package com.DAA.Repo;

import com.DAA.Entities.Availability;
import com.DAA.Entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByDoctor(Doctor doctor);
    List<Availability> findByDayAndIsBookedFalse(DayOfWeek day);

    @Query("SELECT a FROM Availability a " +
            "WHERE a.doctor.id = :doctorId " +
            "AND a.day = :day " +
            "AND ( (a.slotStart < :newEnd) AND (a.slotEnd > :newStart) )")
    List<Availability> findOverlappingSlots(
            @Param("doctorId") Long doctorId,
            @Param("day") DayOfWeek day,
            @Param("newStart") LocalTime newStart,
            @Param("newEnd") LocalTime newEnd
    );

//    @Query("SELECT a FROM Availability a WHERE a.doctor = :doctor AND a.day = :day AND a.slotStart = :start AND a.slotEnd = :end")
//    Optional<Availability> findExactSlot(Doctor doctor, DayOfWeek day, LocalTime start, LocalTime end);

    Optional<Availability> findByDoctorAndDayAndSlotStartAndSlotEnd(
            Doctor doctor,
            DayOfWeek day,
            LocalTime slotStart,
            LocalTime slotEnd
    );

    List<Availability> findByDoctorAndDay(Doctor doctor, DayOfWeek dayOfWeek);
}