// Constantes del sistema hospitalario
package com.hospital.backend.common.util;

public class Constants {
    
    // Roles del sistema
    public static final String ROLE_PATIENT = "PATIENT";
    public static final String ROLE_DOCTOR = "DOCTOR";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_SUPERADMIN = "SUPERADMIN";
    
    // Estados de citas
    public static final String APPOINTMENT_SCHEDULED = "scheduled";
    public static final String APPOINTMENT_CONFIRMED = "confirmed";
    public static final String APPOINTMENT_COMPLETED = "completed";
    public static final String APPOINTMENT_CANCELLED = "cancelled";
    public static final String APPOINTMENT_NO_SHOW = "no_show";
    
    // Estados de pago
    public static final String PAYMENT_PENDING = "pending";
    public static final String PAYMENT_PAID = "paid";
    public static final String PAYMENT_REFUNDED = "refunded";
    
    // Tipos de notificación
    public static final String NOTIFICATION_APPOINTMENT_REMINDER = "appointment_reminder";
    public static final String NOTIFICATION_EXAM_RESULTS = "exam_results";
    public static final String NOTIFICATION_APPOINTMENT_CANCELLED = "appointment_cancelled";
    public static final String NOTIFICATION_PRESCRIPTION_READY = "prescription_ready";
    public static final String NOTIFICATION_GENERAL = "general";
    
    // Métodos de entrega de notificaciones
    public static final String DELIVERY_PUSH = "push";
    public static final String DELIVERY_EMAIL = "email";
    public static final String DELIVERY_IN_APP = "in_app";
    
    // Tipos de archivo
    public static final String FILE_TYPE_PDF = "pdf";
    public static final String FILE_TYPE_IMAGE = "image";
    public static final String FILE_TYPE_DOCUMENT = "document";
    
    // Categorías de archivos médicos
    public static final String CATEGORY_LAB_RESULT = "lab_result";
    public static final String CATEGORY_IMAGING = "imaging";
    public static final String CATEGORY_PRESCRIPTION = "prescription";
    public static final String CATEGORY_OTHER = "other";
    
    // Configuraciones por defecto
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final int DEFAULT_APPOINTMENT_DURATION = 30; // minutos
    public static final int MIN_APPOINTMENT_NOTICE_HOURS = 2;
    public static final int APPOINTMENT_REMINDER_HOURS = 24;
    
    // Mensajes comunes
    public static final String MSG_RECORD_NOT_FOUND = "Registro no encontrado";
    public static final String MSG_ACCESS_DENIED = "Acceso denegado";
    public static final String MSG_INVALID_CREDENTIALS = "Credenciales inválidas";
    public static final String MSG_EMAIL_ALREADY_EXISTS = "El email ya está registrado";
    public static final String MSG_OPERATION_SUCCESS = "Operación realizada exitosamente";
    
    // Formatos de fecha
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_FORMAT = "HH:mm";
    
    // Horarios de trabajo
    public static final int WORK_START_HOUR = 8;
    public static final int WORK_END_HOUR = 18;
    public static final int WORK_DAYS_START = 1; // Lunes
    public static final int WORK_DAYS_END = 6;   // Sábado
}