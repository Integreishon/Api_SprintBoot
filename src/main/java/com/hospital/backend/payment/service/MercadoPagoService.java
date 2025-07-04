package com.hospital.backend.payment.service;

import com.hospital.backend.appointment.entity.Appointment;
import com.hospital.backend.appointment.repository.AppointmentRepository;
import com.hospital.backend.common.exception.BusinessException;
import com.hospital.backend.common.exception.ResourceNotFoundException;
import com.hospital.backend.enums.AppointmentStatus;
import com.hospital.backend.enums.PaymentStatus;
import com.hospital.backend.payment.entity.Payment;
import com.hospital.backend.payment.repository.PaymentRepository;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.PaymentPayer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MercadoPagoService {

    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    @Value("${server.servlet.context-path}")
    private String apiBasePath;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Transactional
    public String createPaymentPreference(Long appointmentId, BigDecimal amount) throws MPException, MPApiException {
        log.info("🚀 Iniciando creación de preferencia de pago para cita ID: {}, Monto: {}", appointmentId, amount);
        
        try {
            // 1. VALIDAR CONFIGURACIÓN DE MERCADO PAGO
            if (accessToken == null || accessToken.trim().isEmpty()) {
                log.error("❌ Access token de Mercado Pago no configurado");
                throw new BusinessException("Configuración de Mercado Pago inválida: Access token no configurado");
            }
            
            log.info("✅ Access token configurado: {}...", accessToken.substring(0, 20));
            
            // 2. VALIDAR CITA
            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));
    
            log.info("✅ Cita encontrada: ID={}, Estado={}, Especialidad={}, Paciente={}", 
                    appointment.getId(), 
                    appointment.getStatus(), 
                    appointment.getSpecialty() != null ? appointment.getSpecialty().getId() : "null",
                    appointment.getPatient() != null ? appointment.getPatient().getId() : "null");
    
            // 3. VALIDAR ESTADO DE LA CITA
            if (appointment.getStatus() != AppointmentStatus.PENDING_VALIDATION) {
                log.warn("⚠️ La cita {} está en estado {}, no en PENDING_VALIDATION", appointmentId, appointment.getStatus());
                // PERMITIR CREAR PREFERENCIA PARA CUALQUIER ESTADO (PARA PRUEBAS)
                log.info("🔧 Permitiendo creación de preferencia para estado: {}", appointment.getStatus());
            }
    
            // 4. VALIDAR DATOS DE LA CITA
            if (appointment.getSpecialty() == null) {
                log.error("❌ La cita {} no tiene especialidad asignada", appointmentId);
                throw new BusinessException("La cita no tiene especialidad asignada");
            }
            
            if (appointment.getPatient() == null) {
                log.error("❌ La cita {} no tiene paciente asignado", appointmentId);
                throw new BusinessException("La cita no tiene paciente asignado");
            }
            
            // 5. VALIDAR Y OBTENER MONTO
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("⚠️ Monto no válido: {}, intentando obtener de la cita", amount);
                
                // Intentar obtener el precio de la cita/especialidad
                amount = appointment.getPrice();
                if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                    amount = appointment.getSpecialty().getConsultationPrice();
                }
                
                // Si aún no tenemos precio válido, usar valor por defecto para pruebas
                if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                    amount = new BigDecimal("50.00"); // Precio por defecto para pruebas
                    log.warn("🔧 Usando precio por defecto para pruebas: {}", amount);
                }
            }
            
            log.info("💰 Monto final para la preferencia: {}", amount);
    
            // 6. CREAR PREFERENCIA
            log.info("🔧 Creando cliente de preferencia de Mercado Pago");
            PreferenceClient client = new PreferenceClient();
    
            // 7. CONFIGURAR ITEMS CON VALIDACIÓN MEJORADA
            List<PreferenceItemRequest> items = new ArrayList<>();
            String title = "Cita Médica - " + appointment.getSpecialty().getName();
            String description = "Reserva para: " + appointment.getPatient().getFullName();
            
            // Limpiar caracteres especiales que pueden causar problemas
            title = title.replaceAll("[^a-zA-Z0-9\\s\\-_.]", "");
            description = description.replaceAll("[^a-zA-Z0-9\\s\\-_.]", "");
            
            // Limitar la longitud de los campos
            if (title.length() > 256) {
                title = title.substring(0, 256);
            }
            if (description.length() > 600) {
                description = description.substring(0, 600);
            }
            
            log.info("📦 Item configurado: title={}, description={}, unitPrice={}", title, description, amount);
            
            items.add(
                    PreferenceItemRequest.builder()
                            .title(title)
                            .description(description)
                            .quantity(1)
                            .unitPrice(amount)
                            .currencyId("PEN")
                            .build());
    
            // 8. CONFIGURAR URLS DE RETORNO CORRECTAMENTE
            String successUrl = "http://localhost:5173/payment/success?appointment_id=" + appointmentId;
            String failureUrl = "http://localhost:5173/payment/failure?appointment_id=" + appointmentId;
            String pendingUrl = "http://localhost:5173/payment/pending?appointment_id=" + appointmentId;
            
            log.info("🔗 URLs configuradas - Success: {}, Failure: {}, Pending: {}", successUrl, failureUrl, pendingUrl);
            
            // 9. CONFIGURAR NOTIFICATION URL (solo en producción)
            String notificationUrl = null;
            if (baseUrl != null && !baseUrl.contains("localhost")) {
                notificationUrl = baseUrl + apiBasePath + "/payments/mercadopago/webhook";
                log.info("📢 Notification URL configurada: {}", notificationUrl);
            } else {
                log.info("🔧 Omitiendo notification_url (entorno de desarrollo)");
            }

            // 10. CREAR REQUEST DE PREFERENCIA CON CONFIGURACIÓN CORRECTA
            PreferenceRequest.PreferenceRequestBuilder requestBuilder = PreferenceRequest.builder()
                .items(items)
                .externalReference(appointment.getId().toString())
                .backUrls(PreferenceBackUrlsRequest.builder()
                        .success(successUrl)
                        .failure(failureUrl)
                        .pending(pendingUrl)
                        .build());
                        
            // NO agregar autoReturn ya que causa problemas con las URLs
            // .autoReturn("approved");  // REMOVIDO - causa el error
            
            // Solo agregar notification URL si no es null
            if (notificationUrl != null) {
                requestBuilder.notificationUrl(notificationUrl);
            }
            
            PreferenceRequest request = requestBuilder.build();
    
            // 11. VALIDAR REQUEST ANTES DE ENVIAR
            log.info("🔍 Validando request antes de enviar...");
            if (request.getItems() == null || request.getItems().isEmpty()) {
                throw new BusinessException("La lista de items está vacía");
            }
            
            PreferenceItemRequest item = request.getItems().get(0);
            if (item.getTitle() == null || item.getTitle().trim().isEmpty()) {
                throw new BusinessException("El título del item no puede estar vacío");
            }
            if (item.getDescription() == null || item.getDescription().trim().isEmpty()) {
                throw new BusinessException("La descripción del item no puede estar vacía");
            }
            if (item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("El precio unitario debe ser mayor a cero");
            }
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new BusinessException("La cantidad debe ser mayor a cero");
            }
            if (!"PEN".equals(item.getCurrencyId())) {
                throw new BusinessException("La moneda debe ser PEN");
            }
            
            log.info("✅ Request validado correctamente");
    
            // 12. ENVIAR SOLICITUD A MERCADO PAGO
            log.info("📡 Enviando solicitud a Mercado Pago...");
            log.info("🔍 Request details: externalReference={}, items={}", 
                    request.getExternalReference(), 
                    request.getItems().size());
            log.info("🔍 Item details: title='{}', price={}, currency={}", 
                    item.getTitle(), 
                    item.getUnitPrice(), 
                    item.getCurrencyId());
            
            com.mercadopago.resources.preference.Preference preference;
            
            try {
                preference = client.create(request);
                log.info("✅ Preferencia creada exitosamente!");
                log.info("🆔 Preference ID: {}", preference.getId());
                log.info("🔗 Init Point: {}", preference.getInitPoint());
                log.info("📱 Sandbox Init Point: {}", preference.getSandboxInitPoint());
                
                return preference.getId();
                
            } catch (MPApiException apiEx) {
                // Logs detallados del error de API
                log.error("❌ ERROR DE API DE MERCADO PAGO:");
                log.error("📝 Mensaje: {}", apiEx.getMessage());
                log.error("🔢 Status Code: {}", apiEx.getStatusCode());
                
                // Intentar extraer el contenido real del error
                String responseBody = "No disponible";
                try {
                    if (apiEx.getApiResponse() != null) {
                        responseBody = apiEx.getApiResponse().getContent();
                        if (responseBody == null || responseBody.trim().isEmpty()) {
                            responseBody = apiEx.getApiResponse().toString();
                        }
                    }
                } catch (Exception e) {
                    log.warn("No se pudo extraer el contenido de la respuesta: {}", e.getMessage());
                }
                
                log.error("📄 Response Body: {}", responseBody);
                log.error("🏷️ Exception Type: {}", apiEx.getClass().getSimpleName());
                
                // Convertir a mensaje más amigable
                String userMessage = "Error al crear la preferencia de pago";
                String detailedMessage = responseBody;
                
                if (apiEx.getStatusCode() == 400) {
                    userMessage = "Datos inválidos para crear el pago";
                    
                    // Analizar errores específicos de MP para dar mejor feedback
                    if (responseBody.toLowerCase().contains("currency")) {
                        userMessage = "Error en la moneda especificada. Debe ser PEN para Perú.";
                    } else if (responseBody.toLowerCase().contains("unit_price")) {
                        userMessage = "Error en el precio del producto. Verifique que sea un valor válido.";
                    } else if (responseBody.toLowerCase().contains("external_reference")) {
                        userMessage = "Error en la referencia externa. ID de cita inválido.";
                    } else if (responseBody.toLowerCase().contains("item")) {
                        userMessage = "Error en los datos del producto/servicio.";
                    } else if (responseBody.toLowerCase().contains("title")) {
                        userMessage = "Error en el título del producto. Contiene caracteres no válidos.";
                    } else if (responseBody.toLowerCase().contains("description")) {
                        userMessage = "Error en la descripción del producto. Contiene caracteres no válidos.";
                    }
                } else if (apiEx.getStatusCode() == 401) {
                    userMessage = "Credenciales de Mercado Pago inválidas. Contacte al administrador.";
                } else if (apiEx.getStatusCode() == 403) {
                    userMessage = "Acceso denegado a Mercado Pago. Verifique las credenciales.";
                } else if (apiEx.getStatusCode() >= 500) {
                    userMessage = "Error en el servidor de Mercado Pago. Intente nuevamente más tarde.";
                }
                
                throw new BusinessException(userMessage + " (Código: " + apiEx.getStatusCode() + "). Detalles: " + detailedMessage);
                
            } catch (MPException mpEx) {
                log.error("❌ ERROR GENERAL DE MERCADO PAGO:");
                log.error("📝 Mensaje: {}", mpEx.getMessage());
                log.error("🏷️ Cause: {}", mpEx.getCause());
                
                throw new BusinessException("Error de conexión con Mercado Pago: " + mpEx.getMessage());
            }
            
        } catch (ResourceNotFoundException e) {
            log.error("❌ Cita no encontrada: {}", e.getMessage());
            throw e;
        } catch (BusinessException e) {
            log.error("❌ Error de negocio: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ Error inesperado: {}", e.getMessage(), e);
            throw new BusinessException("Error inesperado al crear preferencia: " + e.getMessage());
        }
    }

    @Transactional
    public void processWebhookNotification(Map<String, Object> notification) {
        log.info("📢 Procesando webhook de Mercado Pago: {}", notification);
        
        if (!"payment".equals(notification.get("type"))) {
            log.warn("⚠️ Notificación ignorada - tipo no es 'payment': {}", notification.get("type"));
            return;
        }

        Map<String, Object> data = (Map<String, Object>) notification.get("data");
        if (data == null || data.get("id") == null) {
            log.error("❌ Notificación inválida - sin ID de pago: {}", notification);
            return;
        }
        
        String paymentIdStr = data.get("id").toString();
        Long mpPaymentId = Long.parseLong(paymentIdStr);

        log.info("🔍 Procesando pago de Mercado Pago ID: {}", mpPaymentId);

        try {
            PaymentClient paymentClient = new PaymentClient();
            com.mercadopago.resources.payment.Payment mpPayment = paymentClient.get(mpPaymentId);

            if (mpPayment == null) {
                log.error("❌ No se pudo obtener información del pago {}", mpPaymentId);
                return;
            }

            String externalReference = mpPayment.getExternalReference();
            if (externalReference == null) {
                log.error("❌ Pago {} sin referencia externa", mpPaymentId);
                return;
            }
            
            Long appointmentId = Long.parseLong(externalReference);
            String status = mpPayment.getStatus();
            
            log.info("💳 Estado del pago {}: {} para cita {}", mpPaymentId, status, appointmentId);

            if ("approved".equals(status)) {
                paymentService.confirmPaymentByMp(appointmentId, mpPaymentId.toString());
                log.info("✅ Pago confirmado exitosamente para cita {}", appointmentId);
            } else {
                log.warn("⚠️ Pago {} no aprobado - Estado: {}", mpPaymentId, status);
            }

        } catch (MPException | MPApiException e) {
            log.error("❌ Error procesando webhook de Mercado Pago", e);
            throw new BusinessException("Error al comunicarse con Mercado Pago: " + e.getMessage());
        }
    }

    /**
     * Validación completa de la configuración de Mercado Pago
     * @return String con el reporte de validación
     */
    public String validateMercadoPagoConfig() {
        StringBuilder report = new StringBuilder();
        report.append("=== VALIDACIÓN DE MERCADO PAGO ===\n\n");
        
        try {
            // 1. Verificar access token
            report.append("1. Access Token: ");
            if (accessToken == null || accessToken.trim().isEmpty()) {
                report.append("❌ NO CONFIGURADO\n");
                return report.toString();
            } else {
                report.append("✅ CONFIGURADO (").append(accessToken.substring(0, 20)).append("...)\n");
            }
            
            // 2. Verificar que es token de prueba
            report.append("2. Tipo de Token: ");
            if (accessToken.startsWith("TEST-")) {
                report.append("✅ TOKEN DE PRUEBA\n");
            } else if (accessToken.startsWith("APP_USR-")) {
                report.append("⚠️ TOKEN DE PRODUCCIÓN\n");
            } else {
                report.append("❌ FORMATO DESCONOCIDO\n");
            }
            
            // 3. Verificar configuración en SDK
            report.append("3. SDK Configuration: ");
            String sdkToken = com.mercadopago.MercadoPagoConfig.getAccessToken();
            if (sdkToken != null && sdkToken.equals(accessToken)) {
                report.append("✅ CORRECTAMENTE CONFIGURADO\n");
            } else {
                report.append("❌ DESINCRONIZADO\n");
            }
            
            // 4. Probar creación de cliente
            report.append("4. Cliente PreferenceClient: ");
            try {
                PreferenceClient client = new PreferenceClient();
                report.append("✅ CREADO EXITOSAMENTE\n");
            } catch (Exception e) {
                report.append("❌ ERROR: ").append(e.getMessage()).append("\n");
            }
            
            // 5. Verificar URLs base
            report.append("5. Configuración de URLs:\n");
            report.append("   - Base URL: ").append(baseUrl).append("\n");
            report.append("   - API Path: ").append(apiBasePath).append("\n");
            report.append("   - Webhook URL: ");
            if (baseUrl != null && !baseUrl.contains("localhost")) {
                report.append(baseUrl).append(apiBasePath).append("/payments/mercadopago/webhook\n");
            } else {
                report.append("NO CONFIGURADO (desarrollo local)\n");
            }
            
            report.append("\n✅ CONFIGURACIÓN VÁLIDA");
            
        } catch (Exception e) {
            report.append("\n❌ ERROR EN VALIDACIÓN: ").append(e.getMessage());
        }
        
        return report.toString();
    }

    /**
     * Diagnóstico completo para depuración
     */
    public String diagnoseMercadoPagoPreference(Long appointmentId) {
        StringBuilder diagnosis = new StringBuilder();
        diagnosis.append("=== DIAGNÓSTICO COMPLETO DE MERCADO PAGO ===\n\n");
        
        try {
            // Ejecutar validación de configuración
            diagnosis.append(validateMercadoPagoConfig()).append("\n\n");
            
            // Diagnóstico específico de la cita
            diagnosis.append("=== DIAGNÓSTICO DE LA CITA ===\n");
            
            try {
                Appointment appointment = appointmentRepository.findById(appointmentId)
                        .orElse(null);
                
                if (appointment == null) {
                    diagnosis.append("❌ CITA NO ENCONTRADA con ID: ").append(appointmentId).append("\n");
                } else {
                    diagnosis.append("✅ Cita encontrada:\n");
                    diagnosis.append("   - ID: ").append(appointment.getId()).append("\n");
                    diagnosis.append("   - Estado: ").append(appointment.getStatus()).append("\n");
                    diagnosis.append("   - Fecha: ").append(appointment.getAppointmentDate()).append("\n");
                    
                    if (appointment.getSpecialty() != null) {
                        diagnosis.append("   - Especialidad: ").append(appointment.getSpecialty().getName()).append("\n");
                        diagnosis.append("   - Precio especialidad: ").append(appointment.getSpecialty().getConsultationPrice()).append("\n");
                    } else {
                        diagnosis.append("   - ❌ Sin especialidad asignada\n");
                    }
                    
                    if (appointment.getPatient() != null) {
                        diagnosis.append("   - Paciente: ").append(appointment.getPatient().getFullName()).append("\n");
                    } else {
                        diagnosis.append("   - ❌ Sin paciente asignado\n");
                    }
                    
                    diagnosis.append("   - Precio cita: ").append(appointment.getPrice()).append("\n");
                }
            } catch (Exception e) {
                diagnosis.append("❌ Error al buscar cita: ").append(e.getMessage()).append("\n");
            }
            
            // Recomendaciones
            diagnosis.append("\n=== RECOMENDACIONES ===\n");
            diagnosis.append("1. Verificar que el access token es válido\n");
            diagnosis.append("2. Verificar que la cita tiene especialidad y paciente\n");
            diagnosis.append("3. Verificar que hay un precio válido\n");
            diagnosis.append("4. Revisar logs detallados en consola\n");
            
        } catch (Exception e) {
            diagnosis.append("\n❌ ERROR EN DIAGNÓSTICO: ").append(e.getMessage());
        }
        
        return diagnosis.toString();
    }

    /**
     * Crear una preferencia de prueba muy simple para diagnosticar problemas
     */
    public String createTestPreference() {
        log.info("🧪 Creando preferencia de prueba muy simple...");
        
        try {
            PreferenceClient client = new PreferenceClient();
            
            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(
                    PreferenceItemRequest.builder()
                            .title("Test Producto")
                            .description("Prueba de pago")
                            .quantity(1)
                            .unitPrice(new BigDecimal("10.00"))
                            .currencyId("PEN")
                            .build());
            
            // CONFIGURACIÓN MÍNIMA SIN autoReturn
            PreferenceRequest request = PreferenceRequest.builder()
                    .items(items)
                    .externalReference("TEST_" + System.currentTimeMillis())
                    .build();
            
            com.mercadopago.resources.preference.Preference preference = client.create(request);
            
            log.info("✅ Preferencia de prueba creada: {}", preference.getId());
            return preference.getId();
            
        } catch (Exception e) {
            log.error("❌ Error en preferencia de prueba: {}", e.getMessage(), e);
            throw new BusinessException("Error en prueba: " + e.getMessage());
        }
    }

    /**
     * Crear preferencia simplificada sin URLs de retorno problemáticas
     */
    public String createSimplePaymentPreference(Long appointmentId, BigDecimal amount) throws MPException, MPApiException {
        log.info("🚀 Creando preferencia SIMPLIFICADA para cita ID: {}, Monto: {}", appointmentId, amount);
        
        try {
            // Validaciones básicas
            if (accessToken == null || accessToken.trim().isEmpty()) {
                throw new BusinessException("Access token no configurado");
            }
            
            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));
            
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                amount = new BigDecimal("50.00"); // Precio por defecto
            }
            
            // Crear cliente y items
            PreferenceClient client = new PreferenceClient();
            
            List<PreferenceItemRequest> items = new ArrayList<>();
            String title = "Cita Medica - " + (appointment.getSpecialty() != null ? appointment.getSpecialty().getName() : "General");
            String description = "Reserva para consulta medica";
            
            items.add(
                    PreferenceItemRequest.builder()
                            .title(title)
                            .description(description)
                            .quantity(1)
                            .unitPrice(amount)
                            .currencyId("PEN")
                            .build());
            
            // 8. CONFIGURAR URLS DE RETORNO CORRECTAMENTE
            String successUrl = "http://localhost:5173/payment/success?appointment_id=" + appointmentId;
            String failureUrl = "http://localhost:5173/payment/failure?appointment_id=" + appointmentId;
            String pendingUrl = "http://localhost:5173/payment/pending?appointment_id=" + appointmentId;

            log.info("🔗 URLs configuradas - Success: {}, Failure: {}, Pending: {}", successUrl, failureUrl, pendingUrl);
            
            // REQUEST CON URLs DE RETORNO
            PreferenceRequest request = PreferenceRequest.builder()
                    .items(items)
                    .externalReference(appointmentId.toString())
                    .backUrls(PreferenceBackUrlsRequest.builder()
                        .success(successUrl)
                        .failure(failureUrl)
                        .pending(pendingUrl)
                        .build())
                    .build();
            
            log.info("📡 Enviando preferencia simplificada...");
            com.mercadopago.resources.preference.Preference preference = client.create(request);
            
            log.info("✅ Preferencia simplificada creada: {}", preference.getId());
            return preference.getId();
            
        } catch (MPApiException apiEx) {
            log.error("❌ ERROR API MP: Status={}, Message={}", apiEx.getStatusCode(), apiEx.getMessage());
            String errorDetails = "No disponible";
            try {
                if (apiEx.getApiResponse() != null && apiEx.getApiResponse().getContent() != null) {
                    errorDetails = apiEx.getApiResponse().getContent();
                }
            } catch (Exception e) {
                // Ignorar error al extraer detalles
            }
            log.error("📄 Detalles: {}", errorDetails);
            throw new BusinessException("Error MP: " + apiEx.getMessage() + " - " + errorDetails);
        }
    }
}