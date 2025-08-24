package com.DAA.Service;

import com.DAA.Dto.AppointmentDTO;
import com.DAA.Entities.*;
import com.DAA.Repo.AppointmentRepository;
import com.DAA.Repo.AvailabilityRepository;
import com.DAA.Repo.DoctorRepository;
import com.DAA.Repo.PatientRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ModelMapper modelMapper;

    // ---------------- BOOK APPOINTMENT -------------------
    @Transactional
    public AppointmentDTO bookAppointment(Long patientId, Long doctorId, Long availabilityId) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        Availability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new RuntimeException("Availability slot not found"));

        if (availability.getIsBooked()) {
            throw new RuntimeException("Slot already booked!");
        }

        // Mark slot as booked
        availability.setIsBooked(true);
        availabilityRepository.save(availability);

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setDate(LocalDateTime.now());
        appointment.setStartTime(availability.getSlotStart());
        appointment.setEndTime(availability.getSlotEnd());
        appointment.setStatus(AppointmentStatus.BOOKED);

        Appointment saved = appointmentRepository.save(appointment);

        return modelMapper.map(saved, AppointmentDTO.class);
    }

    // ---------------- CANCEL APPOINTMENT -------------------
    @Transactional
    public AppointmentDTO cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Appointment already cancelled!");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);

        // Free up slot
        Availability availability = availabilityRepository.findByDoctorAndDay(
                        appointment.getDoctor(),
                        appointment.getDate().getDayOfWeek()
                ).stream()
                .filter(av -> av.getSlotStart().equals(appointment.getStartTime()) &&
                        av.getSlotEnd().equals(appointment.getEndTime()))
                .findFirst()
                .orElse(null);

        if (availability != null) {
            availability.setIsBooked(false);
            availabilityRepository.save(availability);
        }

        Appointment saved = appointmentRepository.save(appointment);
        return modelMapper.map(saved, AppointmentDTO.class);
    }

    // ---------------- COMPLETE APPOINTMENT -------------------
    public AppointmentDTO completeAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new RuntimeException("Only booked appointments can be marked as completed!");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);

        Appointment saved = appointmentRepository.save(appointment);
        return modelMapper.map(saved, AppointmentDTO.class);
    }

    // ---------------- GET APPOINTMENTS -------------------
    public List<AppointmentDTO> getAppointmentsByPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        return appointmentRepository.findByPatient(patient).stream()
                .map(app -> modelMapper.map(app, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getAppointmentsByDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return appointmentRepository.findByDoctor(doctor).stream()
                .map(app -> modelMapper.map(app, AppointmentDTO.class))
                .collect(Collectors.toList());
    }
}

