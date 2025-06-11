package com.hospital.backend.admin.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.backend.admin.dto.SettingRequest;
import com.hospital.backend.admin.dto.SettingResponse;
import com.hospital.backend.admin.entity.HospitalSetting;
import com.hospital.backend.admin.repository.HospitalSettingRepository;
import com.hospital.backend.common.exception.BusinessException;
import com.hospital.backend.common.exception.ResourceNotFoundException;
import com.hospital.backend.enums.DataType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para gestionar las configuraciones del hospital
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HospitalSettingService {

    private final HospitalSettingRepository settingRepository;
    
    /**
     * Obtiene todas las configuraciones del hospital
     * @return Lista de configuraciones
     */
    public List<SettingResponse> getAllSettings() {
        return settingRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene todas las configuraciones públicas
     * @return Lista de configuraciones públicas
     */
    public List<SettingResponse> getPublicSettings() {
        return settingRepository.findByIsPublicTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene configuraciones por categoría
     * @param category Categoría a buscar
     * @return Lista de configuraciones de la categoría
     */
    public List<SettingResponse> getSettingsByCategory(String category) {
        return settingRepository.findByCategory(category).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene una configuración por su clave
     * @param key Clave de configuración
     * @return Configuración encontrada
     * @throws ResourceNotFoundException si no existe la configuración
     */
    public SettingResponse getSettingByKey(String key) {
        return settingRepository.findByKey(key)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Configuración no encontrada con clave: " + key));
    }
    
    /**
     * Crea una nueva configuración
     * @param request Datos de la configuración
     * @return Configuración creada
     * @throws BusinessException si ya existe una configuración con la misma clave
     */
    @Transactional
    public SettingResponse createSetting(SettingRequest request) {
        if (settingRepository.existsByKey(request.getKey())) {
            throw new BusinessException("Ya existe una configuración con la clave: " + request.getKey());
        }
        
        HospitalSetting setting = HospitalSetting.builder()
                .key(request.getKey())
                .value(request.getValue())
                .dataType(request.getDataType() != null ? request.getDataType() : DataType.STRING)
                .description(request.getDescription())
                .category(request.getCategory())
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : false)
                .isEditable(request.getIsEditable() != null ? request.getIsEditable() : true)
                .build();
        
        return mapToResponse(settingRepository.save(setting));
    }
    
    /**
     * Actualiza una configuración existente
     * @param key Clave de la configuración
     * @param request Nuevos datos
     * @return Configuración actualizada
     * @throws ResourceNotFoundException si no existe la configuración
     * @throws BusinessException si se intenta modificar una configuración del sistema
     */
    @Transactional
    public SettingResponse updateSetting(String key, SettingRequest request) {
        HospitalSetting setting = settingRepository.findByKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("Configuración no encontrada con clave: " + key));
        
        if (!setting.getIsEditable()) {
            throw new BusinessException("No se puede modificar una configuración del sistema");
        }
        
        if (request.getValue() != null) {
            setting.setValue(request.getValue());
        }
        
        if (request.getDescription() != null) {
            setting.setDescription(request.getDescription());
        }
        
        if (request.getCategory() != null) {
            setting.setCategory(request.getCategory());
        }
        
        if (request.getIsPublic() != null) {
            setting.setIsPublic(request.getIsPublic());
        }
        
        return mapToResponse(settingRepository.save(setting));
    }
    
    /**
     * Elimina una configuración
     * @param key Clave de la configuración
     * @throws ResourceNotFoundException si no existe la configuración
     * @throws BusinessException si se intenta eliminar una configuración del sistema
     */
    @Transactional
    public void deleteSetting(String key) {
        HospitalSetting setting = settingRepository.findByKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("Configuración no encontrada con clave: " + key));
        
        if (!setting.getIsEditable()) {
            throw new BusinessException("No se puede eliminar una configuración del sistema");
        }
        
        settingRepository.delete(setting);
    }
    
    /**
     * Obtiene el valor de una configuración como String
     * @param key Clave de la configuración
     * @return Valor de la configuración
     * @throws ResourceNotFoundException si no existe la configuración
     */
    public String getSettingValue(String key) {
        return settingRepository.findByKey(key)
                .map(HospitalSetting::getValue)
                .orElseThrow(() -> new ResourceNotFoundException("Configuración no encontrada con clave: " + key));
    }
    
    /**
     * Obtiene el valor de una configuración como Boolean
     * @param key Clave de la configuración
     * @return Valor booleano
     * @throws ResourceNotFoundException si no existe la configuración
     * @throws BusinessException si el tipo de dato no es BOOLEAN
     */
    public Boolean getBooleanValue(String key) {
        HospitalSetting setting = settingRepository.findByKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("Configuración no encontrada con clave: " + key));
                
        if (setting.getDataType() != DataType.BOOLEAN) {
            throw new BusinessException("La configuración no es de tipo booleano: " + key);
        }
        
        return Boolean.parseBoolean(setting.getValue());
    }
    
    /**
     * Obtiene el valor de una configuración como Integer
     * @param key Clave de la configuración
     * @return Valor entero
     * @throws ResourceNotFoundException si no existe la configuración
     * @throws BusinessException si el tipo de dato no es INTEGER o hay error de conversión
     */
    public Integer getIntegerValue(String key) {
        HospitalSetting setting = settingRepository.findByKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("Configuración no encontrada con clave: " + key));
                
        if (setting.getDataType() != DataType.INTEGER) {
            throw new BusinessException("La configuración no es de tipo entero: " + key);
        }
        
        try {
            return Integer.parseInt(setting.getValue());
        } catch (NumberFormatException e) {
            throw new BusinessException("Error al convertir el valor a entero: " + key);
        }
    }
    
    /**
     * Inicializa configuraciones por defecto si no existen
     * @param defaultSettings Mapa de configuraciones por defecto (clave -> valor)
     */
    @Transactional
    public void initializeDefaultSettings(Map<String, SettingRequest> defaultSettings) {
        defaultSettings.forEach((key, settingRequest) -> {
            if (!settingRepository.existsByKey(key)) {
                createSetting(settingRequest);
            }
        });
    }
    
    /**
     * Convierte una entidad HospitalSetting a un DTO SettingResponse
     * @param setting Entidad a convertir
     * @return DTO con los datos de la entidad
     */
    private SettingResponse mapToResponse(HospitalSetting setting) {
        return SettingResponse.builder()
                .id(setting.getId())
                .key(setting.getKey())
                .value(setting.getValue())
                .dataType(setting.getDataType())
                .description(setting.getDescription())
                .category(setting.getCategory())
                .isPublic(setting.getIsPublic())
                .isEditable(setting.getIsEditable())
                .createdBy(setting.getCreatedBy())
                .updatedBy(setting.getUpdatedBy())
                .createdAt(setting.getCreatedAt())
                .updatedAt(setting.getUpdatedAt())
                .build();
    }
} 