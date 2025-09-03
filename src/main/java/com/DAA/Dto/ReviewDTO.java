package com.DAA.Dto;

import com.DAA.Entities.Appointment;
import com.DAA.Entities.Doctor;
import com.DAA.Entities.Patient;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
public class ReviewDTO {
    private long reviewId;

    private String reviewContent;
    private float rating;


    private long patientId;


    private long doctorId;

    private long appointmentId;
}
