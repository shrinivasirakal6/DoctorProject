package com.DAA.Service;


import com.DAA.Config.JwtUtil;
import com.DAA.Dto.DoctorDTO;
import com.DAA.Dto.PatientDTO;
import com.DAA.Entities.Doctor;
import com.DAA.Entities.Patient;
import com.DAA.Repo.AvailabilityRepository;
import com.DAA.Repo.DoctorRepository;
import com.DAA.Repo.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PatientServiceTest {

    private PatientRepository patientRepository;
    private DoctorRepository doctorRepository;
    private AvailabilityRepository availabilityRepository;
    private ModelMapper modelMapper;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;

    private PatientService patientService;

    @BeforeEach
    void setUp() {
        patientRepository = mock(PatientRepository.class);
        doctorRepository = mock(DoctorRepository.class);
        availabilityRepository = mock(AvailabilityRepository.class);
        modelMapper = new ModelMapper();
        passwordEncoder = mock(PasswordEncoder.class);
        jwtUtil = mock(JwtUtil.class);

        patientService = new PatientService();
        patientService.patientRepository = patientRepository;
        patientService.doctorRepository = doctorRepository;
        patientService.availabilityRepository = availabilityRepository;
        patientService.modelMapper = modelMapper;
        patientService.passwordEncoder = passwordEncoder;
        patientService.jwtUtil = jwtUtil;
    }

    // ---------------- patientSignUp ----------------
    @Test
    void testPatientSignUp() {
        PatientDTO dto = new PatientDTO();
        dto.setName("Alice");
        dto.setEmail("alice@example.com");
        dto.setPassword("plainPwd");

        Patient saved = new Patient();
        saved.setPatientId(1L);
        saved.setName("Alice");
        saved.setEmail("alice@example.com");
        saved.setPassword("encodedPwd");

        when(passwordEncoder.encode("plainPwd")).thenReturn("encodedPwd");
        when(patientRepository.save(any(Patient.class))).thenReturn(saved);

        PatientDTO result = patientService.patientSignUp(dto);

        assertNotNull(result);
        assertEquals("Alice", result.getName());
        assertEquals("alice@example.com", result.getEmail());
    }

    // ---------------- patientLogin ----------------
    @Test
    void testPatientLogin_Success() {
        Patient patient = new Patient();
        patient.setPatientId(1L);
        patient.setEmail("alice@example.com");
        patient.setPassword("encodedPwd");
        patient.setName("Alice");

        when(patientRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(patient));
        when(passwordEncoder.matches("plainPwd", "encodedPwd")).thenReturn(true);
        when(jwtUtil.generateToken("Alice")).thenReturn("jwt-token");

        String token = patientService.patientLogin("alice@example.com", "plainPwd");

        assertEquals("jwt-token", token);
    }

    @Test
    void testPatientLogin_PatientNotFound() {
        when(patientRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> patientService.patientLogin("missing@example.com", "pwd"));

        assertEquals("Patient not found with email", ex.getMessage());
    }

    @Test
    void testPatientLogin_InvalidPassword() {
        Patient patient = new Patient();
        patient.setEmail("alice@example.com");
        patient.setPassword("encodedPwd");

        when(patientRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(patient));
        when(passwordEncoder.matches("wrongPwd", "encodedPwd")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> patientService.patientLogin("alice@example.com", "wrongPwd"));

        assertEquals("Invalid credentials!", ex.getMessage());
    }

    // ---------------- getDoctorBySpecialization ----------------
    @Test
    void testGetDoctorBySpecialization() {
        Doctor doc = new Doctor();
        doc.setDoctorId(1L);
        doc.setName("Dr. Smith");
        doc.setSpecialty("Cardiology");

        when(doctorRepository.findBySpecialtyIgnoreCase("Cardiology")).thenReturn(List.of(doc));

        List<DoctorDTO> result = patientService.getDoctorBySpecialization("Cardiology");

        assertEquals(1, result.size());
        assertEquals("Dr. Smith", result.get(0).getName());
    }

    // ---------------- getDoctorsByLocation ----------------
    @Test
    void testGetDoctorsByLocation() {
        Doctor doc = new Doctor();
        doc.setDoctorId(2L);
        doc.setName("Dr. Brown");
        doc.setLocation("New York");

        when(doctorRepository.searchByLocation("New York")).thenReturn(List.of(doc));

        List<DoctorDTO> result = patientService.getDoctorsByLocation("New York");

        assertEquals(1, result.size());
        assertEquals("Dr. Brown", result.get(0).getName());
        assertEquals("New York", result.get(0).getLocation());
    }
}

