package com.DAA.Repo;

import com.DAA.Entities.Appointment;
import com.DAA.Entities.AppointmentStatus;
import com.DAA.Entities.Doctor;
import com.DAA.Entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatient(Patient patient);

    List<Appointment> findByDoctor(Doctor doctor);

    List<Appointment> findByDoctorAndDate(Doctor doctor, LocalDate date);
}