package com.DAA.Service;


import com.DAA.Dto.ReviewDTO;
import com.DAA.Entities.*;
import com.DAA.Repo.AppointmentRepository;
import com.DAA.Repo.DoctorRepository;
import com.DAA.Repo.PatientRepository;
import com.DAA.Repo.ReviewRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ModelMapper modelMapper;

    // ---------------- ADD REVIEW -------------------
    public ReviewDTO addReview(Long patientId, Long doctorId, Long appointmentId, ReviewDTO dto) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        // Ensure that appointment belongs to this patient & doctor
        if (!appointment.getPatient().equals(patient) || !appointment.getDoctor().equals(doctor)) {
            throw new RuntimeException("Invalid appointment for this review");
        }

        // Only completed appointments can be reviewed
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Only completed appointments can be reviewed");
        }

        if (reviewRepository.existsByAppointment(appointment)) {
            throw new RuntimeException("Review already submitted for this appointment");
        }

        Review review = modelMapper.map(dto, Review.class);
        review.setPatient(patient);
        review.setDoctor(doctor);
        review.setAppointment(appointment);

        Review saved = reviewRepository.save(review);
        return modelMapper.map(saved, ReviewDTO.class);
    }

    // ---------------- UPDATE REVIEW -------------------
    public ReviewDTO updateReview(Long reviewId, ReviewDTO dto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
        review.setReviewContent(dto.getReviewContent());
        review.setRating(dto.getRating());

        Review saved = reviewRepository.save(review);
        return modelMapper.map(saved, ReviewDTO.class);
    }

    // ---------------- DELETE REVIEW -------------------
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        reviewRepository.delete(review);
    }

    // ---------------- GET REVIEWS -------------------
    public List<ReviewDTO> getReviewsByDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return reviewRepository.findByDoctor(doctor).stream()
                .map(r -> modelMapper.map(r, ReviewDTO.class))
                .collect(Collectors.toList());
    }

    public List<ReviewDTO> getReviewsByPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        return reviewRepository.findByPatient(patient).stream()
                .map(r -> modelMapper.map(r, ReviewDTO.class))
                .collect(Collectors.toList());
    }

    // ---------------- GET AVERAGE RATING -------------------
    public double getAverageRatingForDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        List<Review> reviews = reviewRepository.findByDoctor(doctor);

        if (reviews.isEmpty()) return 0.0;

        return reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
    }
}

