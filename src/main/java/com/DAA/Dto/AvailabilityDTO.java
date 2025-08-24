package com.DAA.Dto;


import lombok.Data;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class AvailabilityDTO {
    private Long id;
    private DayOfWeek day;
    private LocalTime availableFrom;
    private LocalTime availableTo;
    private Boolean isAvailable;
    private Long doctorId; // instead of embedding full Doctor entity
}

