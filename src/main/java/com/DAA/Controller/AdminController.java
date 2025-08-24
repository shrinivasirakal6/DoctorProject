package com.DAA.Controller;

import com.DAA.Dto.DoctorDTO;
import com.DAA.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PutMapping("/doctor/approve")
    public ResponseEntity<DoctorDTO> approveDoctor(
            @RequestParam long doctorId
    ){
        DoctorDTO doctorDTO = adminService.approveDoctor(doctorId);
        return new ResponseEntity<>(doctorDTO, HttpStatus.OK);
    }
}
