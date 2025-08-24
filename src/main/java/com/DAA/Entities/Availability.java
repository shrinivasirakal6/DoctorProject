package com.DAA.Entities;

import jakarta.persistence.*;
import lombok.*;


import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DayOfWeek day;

    private LocalTime slotStart;
    private LocalTime slotEnd;
    // in Availability.java
    @Version
    private Long version;


    private Boolean isBooked = false;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
}
