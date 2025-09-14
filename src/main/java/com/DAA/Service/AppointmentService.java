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
    AppointmentRepository appointmentRepository;

    @Autowired
    AvailabilityRepository availabilityRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    SmsService smsService;

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
        appointment.setAvailability(availability);
        appointment.setDay(appointment.getDate().getDayOfWeek());

        Appointment saved = appointmentRepository.save(appointment);

        smsService.sendSms("+919731546865","your appointment is booked with  "+appointment.getDoctor().getName()+"at"+appointment.getStartTime()+"for any queries contact admin +919731546865");

        return modelMapper.map(saved, AppointmentDTO.class);
    }

    // ---------------- CANCEL APPOINTMENT -------------------
    @Transactional
    public AppointmentDTO cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        Doctor doctor = appointment.getDoctor();
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Appointment already cancelled!");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);

        Availability availability = appointment.getAvailability();
        // Free up slot
//        Availability availability = availabilityRepository.findByDoctorAndDay(
//                        appointment.getDoctor(),
//                        appointment.getDate().getDayOfWeek()
//                ).stream()
//                .filter(av -> av.getSlotStart().equals(appointment.getStartTime()) &&
//                        av.getSlotEnd().equals(appointment.getEndTime()))
//                .findFirst()
//                .orElse(null);


        if (availability != null) {
            availability.setIsBooked(false);
            availabilityRepository.save(availability);
        }

//        Availability availability = availabilityRepository.findByDoctorAndDayAndSlotStartAndSlotEnd(
//                        doctor,
//                        appointment.getDate().getDayOfWeek(),
//                        appointment.getStartTime(),
//                        appointment.getEndTime())
//                .orElseThrow(() -> new RuntimeException("Availability not found for this slot"));
//
//        availability.setIsBooked(false);
//        availabilityRepository.save(availability);


        Appointment saved = appointmentRepository.save(appointment);
        smsService.sendSms("+919731546865","your appointment has been canceled");
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
        smsService.sendSms("+919731546865","your appointment has been completed");
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

        return appointmentRepository.findByDoctorAndStatus(doctor, AppointmentStatus.BOOKED)
                .stream()
                .map(app -> modelMapper.map(app, AppointmentDTO.class))
                .collect(Collectors.toList());
    }


    // ---------------- UPCOMING & PAST APPOINTMENTS -------------------

    public List<AppointmentDTO> getUpcomingAppointmentsForPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        LocalDateTime now = LocalDateTime.now();

        return appointmentRepository.findByPatient(patient).stream()
                .filter(app -> app.getDate().isAfter(now) && app.getStatus() == AppointmentStatus.BOOKED)
                .map(app -> modelMapper.map(app, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getPastAppointmentsForPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        LocalDateTime now = LocalDateTime.now();

        return appointmentRepository.findByPatient(patient).stream()
                .filter(app -> app.getDate().isBefore(now) &&
                        (app.getStatus() == AppointmentStatus.COMPLETED ||
                                app.getStatus() == AppointmentStatus.CANCELLED))
                .map(app -> modelMapper.map(app, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getUpcomingAppointmentsForDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        LocalDateTime now = LocalDateTime.now();

        return appointmentRepository.findByDoctorAndStatus(doctor, AppointmentStatus.BOOKED).stream()
                .filter(app -> app.getDate().isAfter(now))
                .map(app -> modelMapper.map(app, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getPastAppointmentsForDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        LocalDateTime now = LocalDateTime.now();

        return appointmentRepository.findByDoctorAndStatus(doctor, AppointmentStatus.COMPLETED).stream()
                .filter(app -> app.getDate().isBefore(now))
                .map(app -> modelMapper.map(app, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

}

