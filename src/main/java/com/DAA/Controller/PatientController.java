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


//    @GetMapping("/doctors/available")
//    public ResponseEntity<List<DoctorDTO>> getDoctorsByAvailability(
//            @RequestParam String day,
//            @RequestParam String desiredStart,
//            @RequestParam String desiredEnd) {
//
//        // Convert params to proper types
//        DayOfWeek dayOfWeek = DayOfWeek.valueOf(day.toUpperCase());
//        LocalTime startTime = LocalTime.parse(desiredStart);
//        LocalTime endTime = LocalTime.parse(desiredEnd);
//
//        List<DoctorDTO> doctors = patientService.getDoctorsByAvailability(dayOfWeek, startTime, endTime);
//        return new ResponseEntity<>(doctors, HttpStatus.OK);
//    }


}
