package com.DAA.Dto;


import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class AppointmentDTO {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String status; // PENDING, CONFIRMED, CANCELLED
    private LocalDateTime createdAt;
}

