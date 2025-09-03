package com.DAA.Controller;

import com.DAA.Dto.AppointmentDTO;
import com.DAA.Dto.DoctorDTO;
import com.DAA.Dto.PatientDTO;
import com.DAA.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping("/doctor/suspend")
    public ResponseEntity<DoctorDTO> suspendDoctor(
            @PathVariable long doctorId){
        DoctorDTO doctorDTO = adminService.suspendDoctor(doctorId);
        return new ResponseEntity<>(doctorDTO,HttpStatus.OK);
    }

    @GetMapping("/pendingDoctors")
    public ResponseEntity<List<DoctorDTO>> getPendingDoctors(){
        List<DoctorDTO> pendingDoctors = adminService.getPendingDoctors();
        return new ResponseEntity<>(pendingDoctors,HttpStatus.OK);
    }

    @GetMapping("/patients")
    public ResponseEntity<List<PatientDTO>> getAllPatients(){
        List<PatientDTO> allPatients = adminService.getAllPatients();
        return new ResponseEntity<>(allPatients,HttpStatus.OK);
    }

    @PutMapping("/patient/block/{patientId}")
    public ResponseEntity<PatientDTO> blockPatient(long patientId){
        PatientDTO patientDTO = adminService.blockPatient(patientId);
        return new ResponseEntity<>(patientDTO,HttpStatus.OK);
    }

    @PutMapping("/patient/unblock/{patientId}")
    public ResponseEntity<PatientDTO> unblockPatient(@PathVariable long patientId) {
        PatientDTO patientDTO = adminService.unblockPatient(patientId);
        return new ResponseEntity<>(patientDTO, HttpStatus.OK);
    }

    // -------------------- Appointment Oversight --------------------

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentDTO>> getAllAppointments() {
        List<AppointmentDTO> appointments = adminService.getAllAppointments();
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @PutMapping("/appointments/cancel/{appointmentId}")
    public ResponseEntity<String> cancelAppointmentByAdmin(@PathVariable long appointmentId) {
        adminService.cancelAppointmentByAdmin(appointmentId);
        return new ResponseEntity<>("Appointment cancelled successfully", HttpStatus.OK);
    }

    // -------------------- Analytics --------------------

    @GetMapping("/count/doctors")
    public ResponseEntity<Long> countDoctors() {
        return ResponseEntity.ok(adminService.countDoctors());
    }

    @GetMapping("/count/patients")
    public ResponseEntity<Long> countPatients() {
        return ResponseEntity.ok(adminService.countPatients());
    }

    @GetMapping("/count/appointments")
    public ResponseEntity<Long> countAppointments() {
        return ResponseEntity.ok(adminService.countAppointments());
    }
}
