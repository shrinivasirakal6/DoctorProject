package com.DAA.Controller;

import com.DAA.Dto.DoctorDTO;
import com.DAA.Dto.LoginDTO;
import com.DAA.Dto.LoginResponse;
import com.DAA.Dto.PatientDTO;
import com.DAA.Service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam LoginDTO dto
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


}
