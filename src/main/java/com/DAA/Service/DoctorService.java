package com.DAA.Service;


import com.DAA.Config.JwtUtil;
import com.DAA.Dto.AvailabilityDTO;
import com.DAA.Dto.DoctorDTO;
import com.DAA.Entities.Availability;
import com.DAA.Entities.Doctor;
import com.DAA.Repo.AvailabilityRepository;
import com.DAA.Repo.DoctorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    public DoctorDTO doctorSignUp(
            DoctorDTO dto
    ){

        Doctor doctor = modelMapper.map(dto, Doctor.class);

        doctor.setPassword(passwordEncoder.encode(dto.getPassword()));
        doctor.setValidDoctor(false);
        Doctor saved = doctorRepository.save(doctor);

       return modelMapper.map(saved, DoctorDTO.class);
    }


    //doctor login
    public String doctorLogin(String email, String password) {
        Optional<Doctor> doctorOpt = doctorRepository.findByEmail(email);

        if (doctorOpt.isEmpty()) {
            throw new RuntimeException("Doctor not found!");
        }

        Doctor doctor = doctorOpt.get();

        // Check if doctor is approved by Admin
        if (!doctor.getValidDoctor()) {
            throw new RuntimeException("Doctor is not yet approved by Admin!");
        }

        // Validate password
        if (!passwordEncoder.matches(password, doctor.getPassword())) {
            throw new RuntimeException("Invalid credentials!");
        }

        // Generate JWT token
        return jwtUtil.generateToken(doctor.getName());
    }

    //doctor can add his availability
    public AvailabilityDTO addAvailability(long doctorId,AvailabilityDTO dto){
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(
                () -> new RuntimeException("Doctor with the id not found")
        );

        Availability availability = modelMapper.map(dto, Availability.class);
        availability.setDoctor(doctor);
        Availability saved = availabilityRepository.save(availability);
      return   modelMapper.map(saved,AvailabilityDTO.class);
    }

    public List<AvailabilityDTO> getAvailabilities(long doctorId){
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        List<Availability> availabilities = availabilityRepository.findByDoctor(doctor);

        return availabilities.stream()
                .map(av -> modelMapper.map(av, AvailabilityDTO.class))
                .collect(Collectors.toList());
    }


}
