package com.DAA.Service;

import com.DAA.Dto.AppointmentDTO;
import com.DAA.Dto.DoctorDTO;
import com.DAA.Dto.PatientDTO;
import com.DAA.Entities.Appointment;
import com.DAA.Entities.AppointmentStatus;
import com.DAA.Entities.Doctor;
import com.DAA.Entities.Patient;
import com.DAA.Repo.AppointmentRepository;
import com.DAA.Repo.DoctorRepository;
import com.DAA.Repo.PatientRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {
    //write a method to approve a doctor by setting valid doctor as true
    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    AppointmentRepository appointmentRepository;

    public DoctorDTO approveDoctor(long doctorId){
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(
                () -> new RuntimeException("doctor with the id not found")
        );

        doctor.setValidDoctor(true);
        doctorRepository.save(doctor);

       return modelMapper.map(doctor,DoctorDTO.class);
    }

    // Suspend a doctor
    public DoctorDTO suspendDoctor(long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(
                () -> new RuntimeException("Doctor with the ID not found")
        );

        doctor.setValidDoctor(false); // suspended
        doctorRepository.save(doctor);

        return modelMapper.map(doctor, DoctorDTO.class);
    }

    // View all pending doctors
    public List<DoctorDTO> getPendingDoctors() {
        return doctorRepository.findAll().stream()
                .filter(d -> !Boolean.TRUE.equals(d.getValidDoctor()))
                .map(d -> modelMapper.map(d, DoctorDTO.class))
                .collect(Collectors.toList());
    }

    // -------------------- Patient Management --------------------

    // View all patients
    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(p -> modelMapper.map(p, PatientDTO.class))
                .collect(Collectors.toList());
    }

    // Block a patient (for misuse, spamming, fraud, etc.)
    public PatientDTO blockPatient(long patientId) {
        Patient patient = patientRepository.findById(patientId).orElseThrow(
                () -> new RuntimeException("Patient with the ID not found")
        );

        patient.setBlocked(true); // assuming you add a field "blocked" in Patient entity
        patientRepository.save(patient);

        return modelMapper.map(patient, PatientDTO.class);
    }

    // Unblock patient
    public PatientDTO unblockPatient(long patientId) {
        Patient patient = patientRepository.findById(patientId).orElseThrow(
                () -> new RuntimeException("Patient with the ID not found")
        );

        patient.setBlocked(false);
        patientRepository.save(patient);

        return modelMapper.map(patient, PatientDTO.class);
    }

    // -------------------- Appointment Oversight --------------------

    // View all appointments
    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(p -> modelMapper.map(p, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

    // Cancel an appointment (admin override in emergencies)
    public void cancelAppointmentByAdmin(long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(
                () -> new RuntimeException("Appointment not found")
        );
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment); // or update status to CANCELLED
    }

    // -------------------- Analytics --------------------

    // Count total doctors
    public long countDoctors() {
        return doctorRepository.count();
    }

    // Count total patients
    public long countPatients() {
        return patientRepository.count();
    }

    // Count total appointments
    public long countAppointments() {
        return appointmentRepository.count();
    }
}

