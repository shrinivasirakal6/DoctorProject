package com.DAA.Repo;

import com.DAA.Entities.Doctor;
import org.springframework.data.domain.Page;       // ✅ CORRECT
import org.springframework.data.domain.Pageable;   // ✅ CORRECT

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


    @Query("""
        SELECT d FROM Doctor d
        LEFT JOIN d.reviews r
        WHERE (:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:specialty IS NULL OR LOWER(d.specialty) LIKE LOWER(CONCAT('%', :specialty, '%')))
        AND (:location IS NULL OR LOWER(d.location) LIKE LOWER(CONCAT('%', :location, '%')))
        AND (:insuranceAccepted IS NULL OR d.insuranceAcceptance = :insuranceAccepted)
        GROUP BY d
        HAVING (:minRating IS NULL OR COALESCE(AVG(r.rating), 0) >= :minRating)
        AND (:minExperience IS NULL OR CAST(d.experience AS int) >= :minExperience)
    """)
    Page<Doctor> searchDoctors(
            @Param("name") String name,
            @Param("specialty") String specialty,
            @Param("location") String location,
            @Param("insuranceAccepted") Boolean insuranceAccepted,
            @Param("minRating") Double minRating,
            @Param("minExperience") Integer minExperience,
            Pageable pageable
    );

    List<Doctor> findByLocation(String location);
}