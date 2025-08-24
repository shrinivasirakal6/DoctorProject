package com.DAA.Repo;

import com.DAA.Entities.Availability;
import com.DAA.Entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByDoctor(Doctor doctor);
    List<Availability> findByDayAndIsBookedFalse(DayOfWeek day);


    List<Availability> findByDoctorAndDay(Doctor doctor, DayOfWeek dayOfWeek);
}