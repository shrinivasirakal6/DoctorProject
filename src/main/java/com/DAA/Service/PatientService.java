package com.DAA.Service;

import com.DAA.Config.JwtUtil;
import com.DAA.Dto.DoctorDTO;
import com.DAA.Dto.PatientDTO;

import org.springframework.data.domain.Page;       // ✅ CORRECT
import org.springframework.data.domain.Pageable;   // ✅ CORRECT

import com.DAA.Entities.Doctor;
import com.DAA.Entities.Patient;
import com.DAA.Repo.AvailabilityRepository;
import com.DAA.Repo.DoctorRepository;
import com.DAA.Repo.PatientRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientService {

    @Autowired
    PatientRepository patientRepository;
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    AvailabilityRepository availabilityRepository;

    public PatientDTO patientSignUp(
            PatientDTO dto
    ){

        Patient patient = modelMapper.map(dto, Patient.class);

        patient.setPassword(passwordEncoder.encode(dto.getPassword()));

        Patient saved = patientRepository.save(patient);

        return modelMapper.map(saved, PatientDTO.class);
    }


    //login
    public String patientLogin(String email, String password) {
        Patient patient = patientRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("Patient not found with email")
        );




        // Validate password
        if (!passwordEncoder.matches(password, patient.getPassword())) {
            throw new RuntimeException("Invalid credentials!");
        }

        // Generate JWT token
        return jwtUtil.generateToken(patient.getName());
    }

    //  ------------------DOCTOR SEARCHES---------------------------
    //1.find by doctor speciality
    public List<DoctorDTO> getDoctorBySpecialization(String speciality){
        List<Doctor> doctorList = doctorRepository.findBySpecialtyIgnoreCase(speciality);
        return doctorList.stream()
                .map(doc -> modelMapper.map(doc, DoctorDTO.class))
                .collect(Collectors.toList());
    }

    // 2. Search by location
    public List<DoctorDTO> getDoctorsByLocation(String location) {
//        List<Doctor> doctorList = doctorRepository.findByLocationIgnoreCase(location);
//        return doctorList.stream()
//                .map(doc -> modelMapper.map(doc, DoctorDTO.class))
//                .collect(Collectors.toList());
        List<Doctor> doctorList = doctorRepository.findByLocation(location);
        return doctorList.stream()
                .map(doc -> modelMapper.map(doc, DoctorDTO.class))
                .collect(Collectors.toList());
    }

    // 🔹 Advanced Search
    public Page<DoctorDTO> searchDoctors(
            String name,
            String specialty,
            String location,
            Boolean insuranceAccepted,
            Double minRating,
            Integer minExperience,
            Pageable pageable) {

        return doctorRepository.searchDoctors(name, specialty, location,
                        insuranceAccepted, minRating, minExperience, pageable)
                .map(doc -> modelMapper.map(doc, DoctorDTO.class));

    }

}
