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
@Table(
        name = "availability",
        uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "day", "slot_start", "slot_end"})
)
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)

    @Column(name = "day")
    private DayOfWeek day;

    @Column(name = "slot_start")
    private LocalTime slotStart;

    @Column(name = "slot_end")
    private LocalTime slotEnd;

    @Version
    private Long version;


    private Boolean isBooked = false;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
}
