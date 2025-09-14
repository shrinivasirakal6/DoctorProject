package com.DAA.Controller;

import com.DAA.Dto.AppointmentDTO;
import com.DAA.Service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/book/{patientId}/{doctorId}/{availabilityId}")
    public ResponseEntity<AppointmentDTO> bookAppointment(
            @PathVariable long patientId,
            @PathVariable long doctorId,
            @PathVariable long availabilityId
    ){
        AppointmentDTO appointmentDTO = appointmentService.bookAppointment(patientId, doctorId, availabilityId);
        return new ResponseEntity<>(appointmentDTO, HttpStatus.OK);
    }

    @PutMapping("/cancel/{appointmentId}")
    public ResponseEntity<AppointmentDTO> cancelAppointment(
            @PathVariable long appointmentId
    ){
        AppointmentDTO appointmentDTO = appointmentService.cancelAppointment(appointmentId);
        return new ResponseEntity<>(appointmentDTO,HttpStatus.OK);
    }

    @PutMapping("/complete/{appointmentId}")
    public ResponseEntity<AppointmentDTO> completeAppointment(
            @PathVariable long appointmentId
    ){
        AppointmentDTO appointmentDTO = appointmentService.completeAppointment(appointmentId);
        return new ResponseEntity<>(appointmentDTO,HttpStatus.OK);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByPatient(
            @PathVariable long patientId
    ){
        List<AppointmentDTO> appointmentsByPatient = appointmentService.getAppointmentsByPatient(patientId);
        return new ResponseEntity<>(appointmentsByPatient,HttpStatus.OK);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByDoctor(
            @PathVariable Long doctorId){
        List<AppointmentDTO> appointmentsByDoctor = appointmentService.getAppointmentsByDoctor(doctorId);
        return new ResponseEntity<>(appointmentsByDoctor,HttpStatus.OK);
    }

    // ---------------- UPCOMING & PAST APPOINTMENTS -------------------

    // Patient upcoming
    @GetMapping("/patient/{patientId}/upcoming")
    public ResponseEntity<List<AppointmentDTO>> getUpcomingAppointmentsForPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getUpcomingAppointmentsForPatient(patientId));
    }

    // Patient past
    @GetMapping("/patient/{patientId}/past")
    public ResponseEntity<List<AppointmentDTO>> getPastAppointmentsForPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getPastAppointmentsForPatient(patientId));
    }

    // Doctor upcoming
    @GetMapping("/doctor/{doctorId}/upcoming")
    public ResponseEntity<List<AppointmentDTO>> getUpcomingAppointmentsForDoctor(
            @PathVariable Long doctorId) {
        return ResponseEntity.ok(appointmentService.getUpcomingAppointmentsForDoctor(doctorId));
    }

    // Doctor past
    @GetMapping("/doctor/{doctorId}/past")
    public ResponseEntity<List<AppointmentDTO>> getPastAppointmentsForDoctor(
            @PathVariable Long doctorId) {
        return ResponseEntity.ok(appointmentService.getPastAppointmentsForDoctor(doctorId));
    }

}
