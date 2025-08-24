package com.DAA.Controller;

import com.DAA.Dto.AvailabilityDTO;
import com.DAA.Dto.DoctorDTO;
import com.DAA.Dto.LoginDTO;
import com.DAA.Dto.LoginResponse;
import com.DAA.Service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctor")
public class DoctorController {
    @Autowired
    private DoctorService doctorService;

    @PostMapping("/signup")
    public ResponseEntity<DoctorDTO> doctorSignUp(@RequestBody DoctorDTO dto){
        DoctorDTO doctorDTO = doctorService.doctorSignUp(dto);
        return new ResponseEntity<>(doctorDTO, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> doctorLogin(@RequestBody LoginDTO dto){
        String token = doctorService.doctorLogin(dto.getEmail(), dto.getPassword());
        LoginResponse response=new LoginResponse();
        response.setJwtToken(token);
        response.setTokenType("JWT Token");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/availability/add")
    public ResponseEntity<AvailabilityDTO> addAvailability(
            @RequestParam long doctorId,
            @RequestBody AvailabilityDTO dto
    ){
        AvailabilityDTO availabilityDTO = doctorService.addAvailability(doctorId, dto);
        return new ResponseEntity<>(availabilityDTO,HttpStatus.OK);
    }

    @GetMapping("/availabilities")
    public ResponseEntity<List<AvailabilityDTO>> getAllAvailabilities(
            @RequestParam long doctorId
    ){
        List<AvailabilityDTO> availabilities = doctorService.getAvailabilities(doctorId);
        return new ResponseEntity<>(availabilities,HttpStatus.OK);
    }

}
