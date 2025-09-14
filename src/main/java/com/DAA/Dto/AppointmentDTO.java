package com.DAA.Dto;


import com.DAA.Entities.AppointmentStatus;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class AppointmentDTO {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private LocalDateTime date;
    private LocalTime startTime;
    private LocalTime endTime;
    private AppointmentStatus status; // PENDING, CONFIRMED, CANCELLED
    private LocalDateTime createdAt;
}

