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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;
    private AppointmentRepository appointmentRepository;
    private ModelMapper modelMapper;
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        doctorRepository = mock(DoctorRepository.class);
        patientRepository = mock(PatientRepository.class);
        appointmentRepository = mock(AppointmentRepository.class);
        modelMapper = new ModelMapper();
        adminService = new AdminService();
        // inject mocks manually since no @Autowired here
        adminService.doctorRepository = doctorRepository;
        adminService.patientRepository = patientRepository;
        adminService.appointmentRepository = appointmentRepository;
        adminService.modelMapper = modelMapper;
    }

    @Test
    void approveDoctor_success() {
        Doctor doctor = new Doctor();
        doctor.setDoctorId(1L);
        doctor.setValidDoctor(false);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(doctorRepository.save(any())).thenReturn(doctor);

        DoctorDTO result = adminService.approveDoctor(1L);

        assertTrue(result.getValidDoctor());
        verify(doctorRepository, times(1)).save(doctor);
    }

    @Test
    void approveDoctor_notFound() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> adminService.approveDoctor(99L));

        assertEquals("doctor with the id not found", ex.getMessage());
    }

    @Test
    void suspendDoctor_success() {
        Doctor doctor = new Doctor();
        doctor.setDoctorId(1L);
        doctor.setValidDoctor(true);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(doctorRepository.save(any())).thenReturn(doctor);

        DoctorDTO result = adminService.suspendDoctor(1L);

        assertFalse(result.getValidDoctor());
    }

    @Test
    void getPendingDoctors_returnsOnlyUnapproved() {
        Doctor d1 = new Doctor();
        d1.setDoctorId(1L);
        d1.setValidDoctor(false);

        Doctor d2 = new Doctor();
        d2.setDoctorId(2L);
        d2.setValidDoctor(true);

        when(doctorRepository.findAll()).thenReturn(Arrays.asList(d1, d2));

        List<DoctorDTO> pending = adminService.getPendingDoctors();

        assertEquals(1, pending.size());
        assertEquals(1L, pending.get(0).getDoctorId());
    }

    @Test
    void getAllPatients_returnsAll() {
        Patient p1 = new Patient();
        p1.setPatientId(1L);
        p1.setName("John");

        when(patientRepository.findAll()).thenReturn(List.of(p1));

        List<PatientDTO> patients = adminService.getAllPatients();

        assertEquals(1, patients.size());
        assertEquals("John", patients.get(0).getName());
    }

    @Test
    void blockPatient_success() {
        Patient p = new Patient();
        p.setPatientId(1L);
        p.setBlocked(false);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(p));

        PatientDTO result = adminService.blockPatient(1L);

        assertTrue(result.isBlocked());
        verify(patientRepository).save(p);
    }

    @Test
    void unblockPatient_success() {
        Patient p = new Patient();
        p.setPatientId(1L);
        p.setBlocked(true);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(p));

        PatientDTO result = adminService.unblockPatient(1L);

        assertFalse(result.isBlocked());
    }

    @Test
    void getAllAppointments_returnsAll() {
        Appointment a = new Appointment();
        a.setAppointmentId(1L);
        a.setDate(LocalDateTime.now());
        a.setStatus(AppointmentStatus.BOOKED);

        when(appointmentRepository.findAll()).thenReturn(List.of(a));

        List<AppointmentDTO> result = adminService.getAllAppointments();

        assertEquals(1, result.size());
        assertEquals("BOOKED", result.get(0).getStatus());
    }

    @Test
    void cancelAppointmentByAdmin_success() {
        Appointment a = new Appointment();
        a.setAppointmentId(1L);
        a.setStatus(AppointmentStatus.BOOKED);


        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(a));

        adminService.cancelAppointmentByAdmin(1L);

        assertEquals(AppointmentStatus.CANCELLED, a.getStatus());
        verify(appointmentRepository).save(a);
    }

    @Test
    void cancelAppointmentByAdmin_notFound() {
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> adminService.cancelAppointmentByAdmin(99L));
    }

    @Test
    void countDoctorsPatientsAppointments() {
        when(doctorRepository.count()).thenReturn(5L);
        when(patientRepository.count()).thenReturn(10L);
        when(appointmentRepository.count()).thenReturn(20L);

        assertEquals(5L, adminService.countDoctors());
        assertEquals(10L, adminService.countPatients());
        assertEquals(20L, adminService.countAppointments());
    }
}

