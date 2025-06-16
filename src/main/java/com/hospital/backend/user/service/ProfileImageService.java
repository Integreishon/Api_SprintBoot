package com.hospital.backend.user.service;

import com.hospital.backend.common.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Servicio para gestión de imágenes de perfil de doctores
 */
@Service
@Slf4j
public class ProfileImageService {

    @Value("${app.upload.directory:uploads}")
    private String uploadDirectory;
    
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String PROFILE_IMAGES_DIR = "doctors/profile-images";

    /**
     * Guarda imagen de perfil de doctor
     */
    public String saveProfileImage(MultipartFile file, Long doctorId) {
        validateImageFile(file);
        
        try {
            // Crear directorio si no existe
            Path uploadPath = Paths.get(uploadDirectory, PROFILE_IMAGES_DIR);
            Files.createDirectories(uploadPath);
            
            // Generar nombre único para el archivo
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String filename = "doctor-" + doctorId + "-" + UUID.randomUUID().toString() + "." + extension;
            
            // Guardar archivo
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            
            // Retornar URL relativa para almacenar en BD
            String imageUrl = "/" + PROFILE_IMAGES_DIR + "/" + filename;
            log.info("Imagen de perfil guardada para doctor {}: {}", doctorId, imageUrl);
            
            return imageUrl;
            
        } catch (IOException e) {
            log.error("Error al guardar imagen de perfil para doctor {}", doctorId, e);
            throw new ValidationException("Error al guardar la imagen de perfil");
        }
    }
    
    /**
     * Elimina imagen de perfil anterior si existe
     */
    public void deleteProfileImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }
        
        try {
            Path filePath = Paths.get(uploadDirectory, imageUrl.substring(1)); // Remover '/' inicial
            Files.deleteIfExists(filePath);
            log.info("Imagen de perfil eliminada: {}", imageUrl);
        } catch (IOException e) {
            log.warn("No se pudo eliminar la imagen de perfil: {}", imageUrl, e);
        }
    }
    
    /**
     * Valida que el archivo sea una imagen válida
     */
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("Debe seleccionar una imagen");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ValidationException("La imagen no puede superar los 5MB");
        }
        
        String extension = getFileExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new ValidationException("Solo se permiten imágenes JPG, JPEG y PNG");
        }
        
        // Validar que sea realmente una imagen
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ValidationException("El archivo debe ser una imagen válida");
        }
    }
    
    /**
     * Obtiene la extensión del archivo
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new ValidationException("El archivo debe tener una extensión válida");
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
