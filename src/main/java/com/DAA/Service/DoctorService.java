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
    DoctorRepository doctorRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    AvailabilityRepository availabilityRepository;

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

    // ---------------- DELETE AVAILABILITY -------------------
    public void deleteAvailability(Long doctorId, Long availabilityId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Availability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new RuntimeException("Availability not found"));

        // Ensure the availability belongs to the doctor
        if (!availability.getDoctor().equals(doctor)) {
            throw new RuntimeException("You cannot delete availability of another doctor");
        }

        // Prevent deleting if the slot is already booked
        if (Boolean.TRUE.equals(availability.getIsBooked())) {
            throw new RuntimeException("Cannot delete a booked availability slot");
        }

        availabilityRepository.delete(availability);
    }


    public DoctorDTO updateDoctorProfile(Long doctorId, DoctorDTO dto) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        doctor.setName(dto.getName());
        doctor.setSpecialty(dto.getSpecialty());
        doctor.setLocation(dto.getLocation());
        doctor.setEducation(dto.getEducation());
        doctor.setExperience(dto.getExperience());
        doctor.setDoctorImg(dto.getDoctorImg());
        // ...etc

        Doctor updated = doctorRepository.save(doctor);
        return modelMapper.map(updated, DoctorDTO.class);
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
