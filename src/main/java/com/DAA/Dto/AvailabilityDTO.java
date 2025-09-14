package com.DAA.Dto;


import com.DAA.Entities.Doctor;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import lombok.Data;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class AvailabilityDTO {
    private Long id;
    private DayOfWeek day;
    private LocalTime slotStart;
    private LocalTime slotEnd;

    private Boolean isBooked ;
    private long doctorId;
}

