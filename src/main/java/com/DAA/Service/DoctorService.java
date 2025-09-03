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

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
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
        if(!passwordEncoder.matches(password, doctor.getPassword())) {
            throw new RuntimeException("Invalid credentials!");
        }

        // Generate JWT token
        return jwtUtil.generateToken(doctor.getName());
    }

    //doctor can add his availability
//    public AvailabilityDTO addAvailability(long doctorId,AvailabilityDTO dto){
//        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(
//                () -> new RuntimeException("Doctor with the id not found")
//        );
//
//        List<Availability> overlaps = availabilityRepository.findOverlappingSlots(
//                doctorId, dto.getDay(), dto.getSlotStart(), dto.getSlotEnd()
//        );
//
//        if (!overlaps.isEmpty()) {
//            throw new RuntimeException("Slot overlaps with an existing availability");
//        }
//
//        Availability availability = modelMapper.map(dto, Availability.class);
//        availability.setDoctor(doctor);
//        Availability saved = availabilityRepository.save(availability);
////        AvailabilityDTO mapped = modelMapper.map(saved, AvailabilityDTO.class);
////        mapped.setDoctorId(doctorId);
////        return mapped;
//        return modelMapper.map(saved, AvailabilityDTO.class);
//    }

    public List<AvailabilityDTO> addAvailability(
            long doctorId,AvailabilityDTO dto
    ) {

        LocalTime start=dto.getSlotStart();
        LocalTime end = dto.getSlotEnd();
        DayOfWeek day = dto.getDay();
        int slotMinutes=30;
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(
                () -> new RuntimeException("Doctor with the id not found")
        );

        List<AvailabilityDTO> savedDtos = new ArrayList<>();
        LocalTime current = start;

        while (current.plusMinutes(slotMinutes).compareTo(end) <= 0) {
            LocalTime slotStart = current;
            LocalTime slotEnd = current.plusMinutes(slotMinutes);

            // check overlaps for each generated slot
            List<Availability> overlaps = availabilityRepository.findOverlappingSlots(
                    doctorId, day, slotStart, slotEnd
            );
            if (!overlaps.isEmpty()) {
                current = current.plusMinutes(slotMinutes);
                continue; // skip overlapping slot
            }

            Availability availability = new Availability();
            availability.setDay(day);
            availability.setSlotStart(slotStart);
            availability.setSlotEnd(slotEnd);
            availability.setDoctor(doctor);
            availability.setIsBooked(false);

            Availability saved = availabilityRepository.save(availability);
            savedDtos.add(modelMapper.map(saved, AvailabilityDTO.class));

            current = current.plusMinutes(slotMinutes);
        }

        return savedDtos;
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
