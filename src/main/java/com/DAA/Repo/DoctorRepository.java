package com.DAA.Repo;

import com.DAA.Entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByEmail(String email);
    List<Doctor> findBySpecialtyIgnoreCase(String speciality);
    Optional<Doctor> findByName(String name);
    List<Doctor> findByLocationIgnoreCase(String location);

    @Query("SELECT d FROM Doctor d WHERE LOWER(d.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<Doctor> searchByLocation(@Param("location") String location);


}