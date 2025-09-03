package com.DAA.Controller;

import com.DAA.Dto.ReviewDTO;
import com.DAA.Service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // ---------------- ADD REVIEW -------------------
    @PostMapping("/add/{patientId}/{doctorId}/{appointmentId}")
    public ResponseEntity<ReviewDTO> addReview(
            @PathVariable Long patientId,
            @PathVariable Long doctorId,
            @PathVariable Long appointmentId,
            @RequestBody ReviewDTO dto) {

        ReviewDTO saved = reviewService.addReview(patientId, doctorId, appointmentId, dto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // ---------------- UPDATE REVIEW -------------------
    @PutMapping("/update/{reviewId}")
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewDTO dto) {

        ReviewDTO updated = reviewService.updateReview(reviewId, dto);
        return ResponseEntity.ok(updated);
    }

    // ---------------- DELETE REVIEW -------------------
    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    // ---------------- GET REVIEWS BY DOCTOR -------------------
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByDoctor(@PathVariable Long doctorId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByDoctor(doctorId);
        return ResponseEntity.ok(reviews);
    }

    // ---------------- GET REVIEWS BY PATIENT -------------------
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByPatient(@PathVariable Long patientId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByPatient(patientId);
        return ResponseEntity.ok(reviews);
    }

    // ---------------- GET AVERAGE RATING -------------------
    @GetMapping("/doctor/{doctorId}/average-rating")
    public ResponseEntity<Double> getAverageRatingForDoctor(@PathVariable Long doctorId) {
        double avgRating = reviewService.getAverageRatingForDoctor(doctorId);
        return ResponseEntity.ok(avgRating);
    }
}

