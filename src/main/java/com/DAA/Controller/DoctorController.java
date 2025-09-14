package com.DAA.Controller;

import com.DAA.Config.JwtUtil;
import com.DAA.Dto.AvailabilityDTO;
import com.DAA.Dto.DoctorDTO;
import com.DAA.Dto.LoginDTO;
import com.DAA.Dto.LoginResponse;
import com.DAA.Service.DoctorService;
import com.DAA.Service.OtpService;
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

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private OtpService otpService;

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
    public ResponseEntity<List<AvailabilityDTO>> addAvailability(
            @RequestParam long doctorId,
            @RequestBody AvailabilityDTO dto
    ){
        List<AvailabilityDTO> availabilityDTOS = doctorService.addAvailability(doctorId, dto);
        return new ResponseEntity<>(availabilityDTOS,HttpStatus.OK);
    }

    @DeleteMapping("/{doctorId}/availabilities/{availabilityId}")
    public ResponseEntity<String> deleteAvailability(
            @PathVariable Long doctorId,
            @PathVariable Long availabilityId
    ) {
        doctorService.deleteAvailability(doctorId, availabilityId);
        return ResponseEntity.ok("Availability deleted successfully");
    }

    @GetMapping("/availabilities")
    public ResponseEntity<List<AvailabilityDTO>> getAllAvailabilities(
            @RequestParam long doctorId
    ){
        List<AvailabilityDTO> availabilities = doctorService.getAvailabilities(doctorId);
        return new ResponseEntity<>(availabilities,HttpStatus.OK);
    }

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestParam String phone) {
        otpService.generateOtp(phone);
        return ResponseEntity.ok("OTP sent via SMS");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String phone, @RequestParam String otp) {
        if (otpService.validateOtp(phone, otp)) {
            String token = jwtUtil.generateToken(phone);
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired OTP");
        }
    }

}
