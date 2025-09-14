package com.DAA.Service;

import com.DAA.Dto.AppointmentDTO;
import com.DAA.Entities.*;
import com.DAA.Repo.AppointmentRepository;
import com.DAA.Repo.AvailabilityRepository;
import com.DAA.Repo.DoctorRepository;
import com.DAA.Repo.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {

    private AppointmentRepository appointmentRepository;
    private AvailabilityRepository availabilityRepository;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;
    private SmsService smsService;
    private ModelMapper modelMapper;

    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        appointmentRepository = mock(AppointmentRepository.class);
        availabilityRepository = mock(AvailabilityRepository.class);
        doctorRepository = mock(DoctorRepository.class);
        patientRepository = mock(PatientRepository.class);
        smsService = mock(SmsService.class);
        modelMapper = new ModelMapper();

        appointmentService = new AppointmentService();
        appointmentService.appointmentRepository = appointmentRepository;
        appointmentService.availabilityRepository = availabilityRepository;
        appointmentService.doctorRepository = doctorRepository;
        appointmentService.patientRepository = patientRepository;
        appointmentService.smsService = smsService;
        appointmentService.modelMapper = modelMapper;
    }

    // ---------------- BOOK APPOINTMENT ----------------
    @Test
    void bookAppointment_success() {
        Patient patient = new Patient();
        patient.setPatientId(1L);
        Doctor doctor = new Doctor();
        doctor.setDoctorId(2L);
        doctor.setName("Dr. Strange");
        Availability availability = new Availability();
        availability.setId(3L);
        availability.setSlotStart(LocalTime.of(10, 0));
        availability.setSlotEnd(LocalTime.of(11, 0));
        availability.setIsBooked(false);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(availabilityRepository.findById(3L)).thenReturn(Optional.of(availability));
        when(appointmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AppointmentDTO dto = appointmentService.bookAppointment(1L, 2L, 3L);

        assertEquals(AppointmentStatus.BOOKED, dto.getStatus());
        verify(availabilityRepository).save(availability);
        verify(smsService).sendSms(anyString(), contains("your appointment is booked"));
    }

    @Test
    void bookAppointment_slotAlreadyBooked() {
        Availability availability = new Availability();
        availability.setIsBooked(true);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(new Patient()));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(new Doctor()));
        when(availabilityRepository.findById(3L)).thenReturn(Optional.of(availability));

        assertThrows(RuntimeException.class,
                () -> appointmentService.bookAppointment(1L, 2L, 3L));
    }

    // ---------------- CANCEL APPOINTMENT ----------------
    @Test
    void cancelAppointment_success() {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(10L);
        appointment.setStatus(AppointmentStatus.BOOKED);
        Availability availability = new Availability();
        availability.setIsBooked(true);
        appointment.setAvailability(availability);

        when(appointmentRepository.findById(10L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AppointmentDTO dto = appointmentService.cancelAppointment(10L);

        assertEquals(AppointmentStatus.CANCELLED, dto.getStatus());
        assertFalse(availability.getIsBooked());
        verify(smsService).sendSms(anyString(), contains("canceled"));
    }

    @Test
    void cancelAppointment_alreadyCancelled() {
        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.CANCELLED);

        when(appointmentRepository.findById(10L)).thenReturn(Optional.of(appointment));

        assertThrows(RuntimeException.class, () -> appointmentService.cancelAppointment(10L));
    }

    // ---------------- COMPLETE APPOINTMENT ----------------
    @Test
    void completeAppointment_success() {
        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.BOOKED);

        when(appointmentRepository.findById(20L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AppointmentDTO dto = appointmentService.completeAppointment(20L);

        assertEquals(AppointmentStatus.COMPLETED, dto.getStatus());
        verify(smsService).sendSms(anyString(), contains("completed"));
    }

    @Test
    void completeAppointment_notBooked() {
        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.CANCELLED);

        when(appointmentRepository.findById(20L)).thenReturn(Optional.of(appointment));

        assertThrows(RuntimeException.class, () -> appointmentService.completeAppointment(20L));
    }

    // ---------------- GET APPOINTMENTS ----------------
    @Test
    void getAppointmentsByPatient_success() {
        Patient patient = new Patient();
        patient.setPatientId(1L);
        Appointment app = new Appointment();
        app.setStatus(AppointmentStatus.BOOKED);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByPatient(patient)).thenReturn(List.of(app));

        List<AppointmentDTO> dtos = appointmentService.getAppointmentsByPatient(1L);

        assertEquals(1, dtos.size());
        assertEquals(AppointmentStatus.BOOKED, dtos.get(0).getStatus());
    }

    @Test
    void getAppointmentsByDoctor_success() {
        Doctor doctor = new Doctor();
        doctor.setDoctorId(2L);
        Appointment app = new Appointment();
        app.setStatus(AppointmentStatus.BOOKED);

        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctorAndStatus(doctor, AppointmentStatus.BOOKED))
                .thenReturn(List.of(app));

        List<AppointmentDTO> dtos = appointmentService.getAppointmentsByDoctor(2L);

        assertEquals(1, dtos.size());
        assertEquals(AppointmentStatus.BOOKED, dtos.get(0).getStatus());
    }
}

