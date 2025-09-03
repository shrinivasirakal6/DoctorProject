package com.DAA.Repo;

import com.DAA.Entities.Appointment;
import com.DAA.Entities.Doctor;
import com.DAA.Entities.Patient;
import com.DAA.Entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByDoctor(Doctor doctor);
    List<Review>findByPatient(Patient patient);
    boolean existsByAppointment(Appointment appointment);
}