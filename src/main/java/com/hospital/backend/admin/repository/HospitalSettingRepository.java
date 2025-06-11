package com.hospital.backend.admin.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hospital.backend.admin.entity.HospitalSetting;

/**
 * Repositorio para gestionar las configuraciones del hospital
 */
@Repository
public interface HospitalSettingRepository extends JpaRepository<HospitalSetting, Long> {
    
    /**
     * Busca una configuración por su clave
     * @param key Clave de la configuración
     * @return Configuración encontrada o empty
     */
    Optional<HospitalSetting> findByKey(String key);
    
    /**
     * Verifica si existe una configuración con la clave dada
     * @param key Clave a verificar
     * @return true si existe la configuración
     */
    boolean existsByKey(String key);
    
    /**
     * Busca configuraciones por categoría
     * @param category Categoría a buscar
     * @return Lista de configuraciones de la categoría
     */
    List<HospitalSetting> findByCategory(String category);
    
    /**
     * Busca configuraciones públicas
     * @return Lista de configuraciones públicas
     */
    List<HospitalSetting> findByIsPublicTrue();
    
    /**
     * Busca configuraciones editables
     * @return Lista de configuraciones editables
     */
    List<HospitalSetting> findByIsEditableTrue();
    
    /**
     * Busca configuraciones que son del sistema (no editables)
     * @return Lista de configuraciones del sistema
     */
    List<HospitalSetting> findByIsEditableFalse();
    
    /**
     * Busca configuraciones por categoría y acceso público
     * @param category Categoría a buscar
     * @param isPublic Si son públicas o no
     * @return Lista de configuraciones que cumplen los criterios
     */
    List<HospitalSetting> findByCategoryAndIsPublic(String category, Boolean isPublic);
} 