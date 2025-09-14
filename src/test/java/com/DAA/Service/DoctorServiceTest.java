package com.DAA.Service;


import com.DAA.Config.JwtUtil;
import com.DAA.Dto.AvailabilityDTO;
import com.DAA.Dto.DoctorDTO;
import com.DAA.Entities.Availability;
import com.DAA.Entities.Doctor;
import com.DAA.Repo.AvailabilityRepository;
import com.DAA.Repo.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DoctorServiceTest {

    private DoctorRepository doctorRepository;
    private ModelMapper modelMapper;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private AvailabilityRepository availabilityRepository;

    private DoctorService doctorService;

    @BeforeEach
    void setUp() {
        doctorRepository = mock(DoctorRepository.class);
        modelMapper = new ModelMapper();
        passwordEncoder = mock(PasswordEncoder.class);
        jwtUtil = mock(JwtUtil.class);
        availabilityRepository = mock(AvailabilityRepository.class);

        doctorService = new DoctorService();
        doctorService.doctorRepository = doctorRepository;
        doctorService.modelMapper = modelMapper;
        doctorService.passwordEncoder = passwordEncoder;
        doctorService.jwtUtil = jwtUtil;
        doctorService.availabilityRepository = availabilityRepository;
    }

    // ---------------- doctorSignUp ----------------
    @Test
    void testDoctorSignUp() {
        DoctorDTO dto = new DoctorDTO();
        dto.setName("Dr. John");
        dto.setEmail("john@example.com");
        dto.setPassword("plainPassword");

        Doctor savedDoctor = new Doctor();
        savedDoctor.setDoctorId(1L);
        savedDoctor.setName("Dr. John");
        savedDoctor.setEmail("john@example.com");
        savedDoctor.setPassword("encodedPassword");
        savedDoctor.setValidDoctor(false);

        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(doctorRepository.save(any(Doctor.class))).thenReturn(savedDoctor);

        DoctorDTO result = doctorService.doctorSignUp(dto);

        assertNotNull(result);
        assertEquals("Dr. John", result.getName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("encodedPassword", savedDoctor.getPassword()); // inside entity
    }

    // ---------------- doctorLogin ----------------
    @Test
    void testDoctorLogin_Success() {
        Doctor doctor = new Doctor();
        doctor.setDoctorId(1L);
        doctor.setEmail("john@example.com");
        doctor.setPassword("encodedPassword");
        doctor.setValidDoctor(true);
        doctor.setName("Dr. John");

        when(doctorRepository.findByEmail("john@example.com")).thenReturn(Optional.of(doctor));
        when(passwordEncoder.matches("plainPassword", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("Dr. John")).thenReturn("jwt-token");

        String token = doctorService.doctorLogin("john@example.com", "plainPassword");

        assertEquals("jwt-token", token);
    }

    @Test
    void testDoctorLogin_DoctorNotFound() {
        when(doctorRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> doctorService.doctorLogin("notfound@example.com", "pwd"));

        assertEquals("Doctor not found!", ex.getMessage());
    }

    @Test
    void testDoctorLogin_NotApproved() {
        Doctor doctor = new Doctor();
        doctor.setValidDoctor(false);
        doctor.setPassword("encodedPassword");

        when(doctorRepository.findByEmail("john@example.com")).thenReturn(Optional.of(doctor));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> doctorService.doctorLogin("john@example.com", "pwd"));

        assertEquals("Doctor is not yet approved by Admin!", ex.getMessage());
    }

    @Test
    void testDoctorLogin_InvalidPassword() {
        Doctor doctor = new Doctor();
        doctor.setValidDoctor(true);
        doctor.setPassword("encodedPassword");

        when(doctorRepository.findByEmail("john@example.com")).thenReturn(Optional.of(doctor));
        when(passwordEncoder.matches("wrongPwd", "encodedPassword")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> doctorService.doctorLogin("john@example.com", "wrongPwd"));

        assertEquals("Invalid credentials!", ex.getMessage());
    }

    // ---------------- addAvailability ----------------
    @Test
    void testAddAvailability_NoOverlap() {
        Doctor doctor = new Doctor();
        doctor.setDoctorId(1L);
        doctor.setName("Dr. John");

        AvailabilityDTO dto = new AvailabilityDTO();
        dto.setDay(DayOfWeek.MONDAY);
        dto.setSlotStart(LocalTime.of(9, 0));
        dto.setSlotEnd(LocalTime.of(10, 0));

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(availabilityRepository.findOverlappingSlots(anyLong(), any(), any(), any()))
                .thenReturn(List.of());
        when(availabilityRepository.save(any(Availability.class)))
                .thenAnswer(invocation -> {
                    Availability a = invocation.getArgument(0);
                    a.setId(100L);
                    return a;
                });

        List<AvailabilityDTO> result = doctorService.addAvailability(1L, dto);

        assertEquals(2, result.size()); // 9:00-9:30 and 9:30-10:00
        assertEquals(DayOfWeek.MONDAY, result.get(0).getDay());
    }

    @Test
    void testAddAvailability_WithOverlap() {
        Doctor doctor = new Doctor();
        doctor.setDoctorId(1L);

        AvailabilityDTO dto = new AvailabilityDTO();
        dto.setDay(DayOfWeek.MONDAY);
        dto.setSlotStart(LocalTime.of(9, 0));
        dto.setSlotEnd(LocalTime.of(10, 0));

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(availabilityRepository.findOverlappingSlots(anyLong(), any(), any(), any()))
                .thenReturn(List.of(new Availability())); // simulate overlap

        List<AvailabilityDTO> result = doctorService.addAvailability(1L, dto);

        assertEquals(0, result.size()); // no slot saved due to overlap
    }

    // ---------------- getAvailabilities ----------------
    @Test
    void testGetAvailabilities() {
        Doctor doctor = new Doctor();
        doctor.setDoctorId(1L);

        Availability availability = new Availability();
        availability.setId(200L);
        availability.setDay(DayOfWeek.TUESDAY);
        availability.setSlotStart(LocalTime.of(11, 0));
        availability.setSlotEnd(LocalTime.of(11, 30));
        availability.setDoctor(doctor);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(availabilityRepository.findByDoctor(doctor)).thenReturn(List.of(availability));

        List<AvailabilityDTO> result = doctorService.getAvailabilities(1L);

        assertEquals(1, result.size());
        assertEquals(DayOfWeek.TUESDAY, result.get(0).getDay());
        assertEquals(LocalTime.of(11, 0), result.get(0).getSlotStart());
    }
}

