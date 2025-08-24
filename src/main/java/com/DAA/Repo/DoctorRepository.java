package com.DAA.Repo;

import com.DAA.Entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByEmail(String email);
    List<Doctor> findBySpecialtyIgnoreCase(String speciality);
    Optional<Doctor> findByName(String name);
    List<Doctor> findByLocationIgnoreCase(String location);

}