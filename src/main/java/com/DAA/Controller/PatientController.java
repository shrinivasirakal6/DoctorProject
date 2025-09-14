package com.DAA.Controller;

import com.DAA.Dto.DoctorDTO;
import com.DAA.Dto.LoginDTO;
import com.DAA.Dto.LoginResponse;
import com.DAA.Dto.PatientDTO;
import com.DAA.Service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/patient")
public class PatientController {
    @Autowired
    private PatientService patientService;


    @PostMapping("/signup")
    public ResponseEntity<PatientDTO> patientSignUp(
            @RequestBody PatientDTO dto
    ){
        PatientDTO patientDTO = patientService.patientSignUp(dto);
        return new ResponseEntity<>(patientDTO, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> patientLogin(
            @RequestBody LoginDTO dto
            ){
        String token = patientService.patientLogin(dto.getEmail(), dto.getPassword());
        LoginResponse response =new LoginResponse();
        response.setTokenType("JWT");
        response.setJwtToken(token);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/doctor/specialization")
    public ResponseEntity<List<DoctorDTO>> getDoctorBySpecialization(
            @RequestParam String specialization
    ){
        List<DoctorDTO> doctorBySpecialization = patientService.getDoctorBySpecialization(specialization);
        return new ResponseEntity<>(doctorBySpecialization,HttpStatus.OK);
    }

    @GetMapping("/doctor/location/{location}")
    public ResponseEntity<List<DoctorDTO>> getDoctorsByLocation(
            @PathVariable String location){
        List<DoctorDTO> doctorsByLocation = patientService.getDoctorsByLocation(location);
        return new ResponseEntity<>(doctorsByLocation,HttpStatus.OK);
    }


    @GetMapping("/doctors/search")
    public ResponseEntity<Page<DoctorDTO>> searchDoctors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Boolean insuranceAccepted,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort) {

        // build sort object
        String sortBy = sort[0];
        String direction = sort.length > 1 ? sort[1] : "asc";
        Sort sortOrder = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sortOrder);

        Page<DoctorDTO> result = patientService.searchDoctors(
                name, specialty, location, insuranceAccepted, minRating, minExperience, pageable);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}
