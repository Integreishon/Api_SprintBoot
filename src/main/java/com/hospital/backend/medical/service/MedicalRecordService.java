package com.hospital.backend.medical.service;

import com.hospital.backend.medical.dto.request.CreateMedicalRecordRequest;
import com.hospital.backend.medical.dto.response.MedicalRecordResponse;
import com.hospital.backend.medical.entity.MedicalRecord;
import com.hospital.backend.medical.repository.MedicalRecordRepository;
import com.hospital.backend.appointment.repository.AppointmentRepository;
import com.hospital.backend.user.repository.PatientRepository;
import com.hospital.backend.user.repository.DoctorRepository;
import com.hospital.backend.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Transactional
    public MedicalRecordResponse create(CreateMedicalRecordRequest request) {
        var appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new BusinessException("Cita no encontrada"));
        var patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new BusinessException("Paciente no encontrado"));
        var doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new BusinessException("Doctor no encontrado"));

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setAppointment(appointment);
        medicalRecord.setPatient(patient);
        medicalRecord.setDoctor(doctor);
        medicalRecord.setRecordDate(LocalDateTime.now());
        medicalRecord.setChiefComplaint(request.getChiefComplaint());
        medicalRecord.setSymptoms(request.getSymptoms());
        medicalRecord.setDiagnosis(request.getDiagnosis());
        medicalRecord.setTreatmentPlan(request.getTreatment());
        medicalRecord.setNotes(request.getNotes());
        medicalRecord.setFollowupRequired(request.getFollowupRequired());
        medicalRecord.setFollowupDate(request.getFollowupDate());
        medicalRecord.setSeverity(request.getSeverity());
        medicalRecord.setHeightCm(request.getHeightCm());
        medicalRecord.setWeightKg(request.getWeightKg());
        medicalRecord.setBloodPressure(request.getBloodPressure());
        medicalRecord.setTemperature(request.getTemperature());
        medicalRecord.setHeartRate(request.getHeartRate());
        medicalRecord.setRespiratoryRate(request.getRespiratoryRate());
        medicalRecord.setOxygenSaturation(request.getOxygenSaturation());
        medicalRecord.setAllergies(request.getAllergies());
        
        MedicalRecord savedRecord = medicalRecordRepository.save(medicalRecord);
        return mapToResponse(savedRecord);
    }

    @Transactional(readOnly = true)
    public MedicalRecordResponse getById(Long id) {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Registro médico no encontrado"));
        return mapToResponse(medicalRecord);
    }

    @Transactional(readOnly = true)
    public List<MedicalRecordResponse> getByPatient(Long patientId, String startDate, String endDate) {
        List<MedicalRecord> records = medicalRecordRepository.findByPatientId(patientId, startDate, endDate);
        return records.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MedicalRecordResponse> getByDoctor(Long doctorId, String startDate, String endDate) {
        List<MedicalRecord> records = medicalRecordRepository.findByDoctorId(doctorId, startDate, endDate);
        return records.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MedicalRecordResponse getByAppointment(Long appointmentId) {
        MedicalRecord medicalRecord = medicalRecordRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new BusinessException("Registro médico no encontrado para la cita"));
        return mapToResponse(medicalRecord);
    }

    private MedicalRecordResponse mapToResponse(MedicalRecord medicalRecord) {
        return MedicalRecordResponse.builder()
                .id(medicalRecord.getId())
                .patientId(medicalRecord.getPatient().getId())
                .patientName(medicalRecord.getPatient().getFirstName() + " " + medicalRecord.getPatient().getLastName())
                .patientDocument(medicalRecord.getPatient().getUser().getDni())
                .patientAge(calculateAge(medicalRecord.getPatient().getBirthDate()))
                .doctorId(medicalRecord.getDoctor().getId())
                .doctorName(medicalRecord.getDoctor().getFirstName() + " " + medicalRecord.getDoctor().getLastName())
                .doctorSpecialty(medicalRecord.getDoctor().getSpecialties().stream()
                        .filter(s -> s.getIsPrimary())
                        .findFirst()
                        .map(s -> s.getSpecialty().getName())
                        .orElse(null))
                .appointmentId(medicalRecord.getAppointment().getId())
                .appointmentDate(medicalRecord.getAppointment().getAppointmentDate().atStartOfDay())
                .recordDate(medicalRecord.getRecordDate())
                .chiefComplaint(medicalRecord.getChiefComplaint())
                .symptoms(medicalRecord.getSymptoms())
                .diagnosis(medicalRecord.getDiagnosis())
                .treatmentPlan(medicalRecord.getTreatmentPlan())
                .notes(medicalRecord.getNotes())
                .followupRequired(medicalRecord.getFollowupRequired())
                .followupDate(medicalRecord.getFollowupDate())
                .severity(medicalRecord.getSeverity())
                .severityName(medicalRecord.getSeverity() != null ? medicalRecord.getSeverity().name() : null)
                .heightCm(medicalRecord.getHeightCm())
                .weightKg(medicalRecord.getWeightKg())
                .bmi(calculateBMI(medicalRecord.getHeightCm(), medicalRecord.getWeightKg()))
                .bloodPressure(medicalRecord.getBloodPressure())
                .temperature(medicalRecord.getTemperature())
                .heartRate(medicalRecord.getHeartRate())
                .respiratoryRate(medicalRecord.getRespiratoryRate())
                .oxygenSaturation(medicalRecord.getOxygenSaturation())
                .allergies(medicalRecord.getAllergies())
                .createdAt(medicalRecord.getCreatedAt())
                .createdBy(medicalRecord.getCreatedBy())
                .updatedAt(medicalRecord.getUpdatedAt())
                .updatedBy(medicalRecord.getUpdatedBy())
                .build();
    }

    private Integer calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    private Double calculateBMI(Double heightCm, Double weightKg) {
        if (heightCm == null || weightKg == null) {
            return null;
        }
        double heightM = heightCm / 100;
        return weightKg / (heightM * heightM);
    }
} 