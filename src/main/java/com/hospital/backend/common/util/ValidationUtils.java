// Utilidades para validación de datos del sistema hospitalario
package com.hospital.backend.common.util;

import java.util.regex.Pattern;

public class ValidationUtils {
    
    private static final Pattern DNI_PATTERN = Pattern.compile("^[0-9]{8}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{7,15}$");
    private static final Pattern CMP_PATTERN = Pattern.compile("^[0-9]{5,8}$");
    private static final Pattern CARNET_EXTRANJERIA_PATTERN = Pattern.compile("^[0-9]{9}$");
    private static final Pattern PASSPORT_PATTERN = Pattern.compile("^[A-Z0-9]{6,12}$");
    
    public static boolean isValidDni(String dni) {
        return dni != null && DNI_PATTERN.matcher(dni).matches();
    }
    
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    public static boolean isValidCmp(String cmp) {
        return cmp != null && CMP_PATTERN.matcher(cmp).matches();
    }
    
    public static boolean isValidCarnetExtranjeria(String carnet) {
        return carnet != null && CARNET_EXTRANJERIA_PATTERN.matcher(carnet).matches();
    }
    
    public static boolean isValidPassport(String passport) {
        return passport != null && PASSPORT_PATTERN.matcher(passport).matches();
    }
    
    public static boolean isValidAge(int age) {
        return age >= 0 && age <= 120;
    }
    
    public static boolean isAdult(int age) {
        return age >= 18;
    }
    
    public static boolean isPediatric(int age) {
        return age < 18;
    }
    
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    public static boolean isValidLength(String value, int minLength, int maxLength) {
        if (value == null) return false;
        int length = value.trim().length();
        return length >= minLength && length <= maxLength;
    }
}