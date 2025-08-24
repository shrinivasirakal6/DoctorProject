package com.DAA.Service;

import com.DAA.Config.JwtUtil;
import com.DAA.Dto.DoctorDTO;
import com.DAA.Dto.PatientDTO;
import com.DAA.Entities.Availability;
import com.DAA.Entities.Doctor;
import com.DAA.Entities.Patient;
import com.DAA.Repo.AvailabilityRepository;
import com.DAA.Repo.DoctorRepository;
import com.DAA.Repo.PatientRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import java.util.stream.Collectors;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AvailabilityRepository availabilityRepository;

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



        // Check if doctor is approved by Admin


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
        List<Doctor> doctorList = doctorRepository.findByLocationIgnoreCase(location);
        return doctorList.stream()
                .map(doc -> modelMapper.map(doc, DoctorDTO.class))
                .collect(Collectors.toList());
    }

    // 3. Search doctors by availability (day + time slot)
    public List<DoctorDTO> getDoctorsByAvailability(DayOfWeek day, LocalTime desiredStart, LocalTime desiredEnd) {
        List<Availability> availabilities = availabilityRepository.findByDayAndIsBookedFalse(day);

        List<Doctor> doctors = availabilities.stream()
                .filter(av -> !av.getSlotStart().isAfter(desiredStart) && !av.getSlotEnd().isBefore(desiredEnd))
                .map(Availability::getDoctor)
                .distinct()
                .collect(Collectors.toList());

        return doctors.stream()
                .map(doc -> modelMapper.map(doc, DoctorDTO.class))
                .collect(Collectors.toList());
    }
}
