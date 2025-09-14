package com.DAA.Service;

import com.DAA.Dto.ReviewDTO;
import com.DAA.Entities.*;
import com.DAA.Repo.AppointmentRepository;
import com.DAA.Repo.DoctorRepository;
import com.DAA.Repo.PatientRepository;
import com.DAA.Repo.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    private ReviewRepository reviewRepository;
    private PatientRepository patientRepository;
    private DoctorRepository doctorRepository;
    private AppointmentRepository appointmentRepository;
    private ModelMapper modelMapper;
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewRepository = mock(ReviewRepository.class);
        patientRepository = mock(PatientRepository.class);
        doctorRepository = mock(DoctorRepository.class);
        appointmentRepository = mock(AppointmentRepository.class);
        modelMapper = new ModelMapper();

        reviewService = new ReviewService();
        reviewService.reviewRepository = reviewRepository;
        reviewService.patientRepository = patientRepository;
        reviewService.doctorRepository = doctorRepository;
        reviewService.appointmentRepository = appointmentRepository;
        reviewService.modelMapper = modelMapper;
    }

    // ---------------- ADD REVIEW ----------------
    @Test
    void testAddReview_Success() {
        Patient patient = new Patient(); patient.setPatientId(1L);
        Doctor doctor = new Doctor(); doctor.setDoctorId(2L);
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(3L);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.COMPLETED);

        ReviewDTO dto = new ReviewDTO();
        dto.setReviewContent("Good service");
        dto.setRating(4);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findById(3L)).thenReturn(Optional.of(appointment));
        when(reviewRepository.existsByAppointment(appointment)).thenReturn(false);
        when(reviewRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ReviewDTO result = reviewService.addReview(1L, 2L, 3L, dto);

        assertEquals(4, result.getRating());
        assertEquals("Good service", result.getReviewContent());
    }

    @Test
    void testAddReview_InvalidRating() {
        ReviewDTO dto = new ReviewDTO();
        dto.setRating(10);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(new Patient()));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(new Doctor()));
        when(appointmentRepository.findById(3L)).thenReturn(Optional.of(new Appointment()));

        assertThrows(RuntimeException.class,
                () -> reviewService.addReview(1L, 2L, 3L, dto));
    }

    @Test
    void testAddReview_InvalidAppointment() {
        Patient patient = new Patient(); patient.setPatientId(1L);
        Doctor doctor = new Doctor(); doctor.setDoctorId(2L);
        Appointment appointment = new Appointment();
        appointment.setPatient(new Patient());
        appointment.setDoctor(new Doctor());
        appointment.setStatus(AppointmentStatus.COMPLETED);

        ReviewDTO dto = new ReviewDTO(); dto.setRating(4);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findById(3L)).thenReturn(Optional.of(appointment));

        assertThrows(RuntimeException.class,
                () -> reviewService.addReview(1L, 2L, 3L, dto));
    }

    @Test
    void testAddReview_NotCompletedAppointment() {
        Patient patient = new Patient(); patient.setPatientId(1L);
        Doctor doctor = new Doctor(); doctor.setDoctorId(2L);
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.BOOKED);

        ReviewDTO dto = new ReviewDTO(); dto.setRating(4);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findById(3L)).thenReturn(Optional.of(appointment));

        assertThrows(RuntimeException.class,
                () -> reviewService.addReview(1L, 2L, 3L, dto));
    }

    @Test
    void testAddReview_AlreadyExists() {
        Patient patient = new Patient(); patient.setPatientId(1L);
        Doctor doctor = new Doctor(); doctor.setDoctorId(2L);
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.COMPLETED);

        ReviewDTO dto = new ReviewDTO(); dto.setRating(5);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findById(3L)).thenReturn(Optional.of(appointment));
        when(reviewRepository.existsByAppointment(appointment)).thenReturn(true);

        assertThrows(RuntimeException.class,
                () -> reviewService.addReview(1L, 2L, 3L, dto));
    }

    // ---------------- UPDATE REVIEW ----------------
    @Test
    void testUpdateReview_Success() {
        Review review = new Review();
        review.setReviewId(1L);
        review.setRating(3);

        ReviewDTO dto = new ReviewDTO();
        dto.setReviewContent("Updated");
        dto.setRating(5);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ReviewDTO result = reviewService.updateReview(1L, dto);

        assertEquals(5, result.getRating());
        assertEquals("Updated", result.getReviewContent());
    }

    @Test
    void testUpdateReview_InvalidRating() {
        ReviewDTO dto = new ReviewDTO();
        dto.setRating(0);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(new Review()));

        assertThrows(RuntimeException.class,
                () -> reviewService.updateReview(1L, dto));
    }

    @Test
    void testUpdateReview_NotFound() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> reviewService.updateReview(99L, new ReviewDTO()));
    }

    // ---------------- DELETE REVIEW ----------------
    @Test
    void testDeleteReview_Success() {
        Review review = new Review(); review.setReviewId(1L);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.deleteReview(1L);

        verify(reviewRepository).delete(review);
    }

    // ---------------- GET REVIEWS ----------------
    @Test
    void testGetReviewsByDoctor() {
        Doctor doctor = new Doctor(); doctor.setDoctorId(2L);
        Review review = new Review(); review.setRating(4);

        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(reviewRepository.findByDoctor(doctor)).thenReturn(List.of(review));

        List<ReviewDTO> result = reviewService.getReviewsByDoctor(2L);

        assertEquals(1, result.size());
        assertEquals(4, result.get(0).getRating());
    }

    @Test
    void testGetReviewsByPatient() {
        Patient patient = new Patient(); patient.setPatientId(1L);
        Review review = new Review(); review.setRating(3);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(reviewRepository.findByPatient(patient)).thenReturn(List.of(review));

        List<ReviewDTO> result = reviewService.getReviewsByPatient(1L);

        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getRating());
    }

    // ---------------- GET AVERAGE RATING ----------------
    @Test
    void testGetAverageRatingForDoctor() {
        Doctor doctor = new Doctor(); doctor.setDoctorId(2L);
        Review r1 = new Review(); r1.setRating(4);
        Review r2 = new Review(); r2.setRating(2);

        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(reviewRepository.findByDoctor(doctor)).thenReturn(List.of(r1, r2));

        double avg = reviewService.getAverageRatingForDoctor(2L);

        assertEquals(3.0, avg);
    }

    @Test
    void testGetAverageRatingForDoctor_NoReviews() {
        Doctor doctor = new Doctor(); doctor.setDoctorId(2L);

        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(reviewRepository.findByDoctor(doctor)).thenReturn(List.of());

        double avg = reviewService.getAverageRatingForDoctor(2L);

        assertEquals(0.0, avg);
    }
}

