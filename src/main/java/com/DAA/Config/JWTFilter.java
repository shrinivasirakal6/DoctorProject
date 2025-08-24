package com.DAA.Config;


import com.DAA.Entities.Doctor;
import com.DAA.Entities.Patient;
import com.DAA.Repo.DoctorRepository;
import com.DAA.Repo.PatientRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JwtUtil jwtService;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public JWTFilter(JwtUtil jwtService,
                     DoctorRepository doctorRepository,
                     PatientRepository patientRepository) {
        this.jwtService = jwtService;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            String tokenVal = token.substring(7);
            String username = jwtService.extractUsername(tokenVal);

            // Try doctor first
            Optional<Doctor> doctorOpt = doctorRepository.findByName(username);

            if (doctorOpt.isPresent()) {
                Doctor doctor = doctorOpt.get();
//                String role = "ROLE_DOCTOR";
                String role = doctor.getRole();

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                doctor, null,
                                Collections.singleton(new SimpleGrantedAuthority(role)));

                auth.setDetails(new WebAuthenticationDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);

                System.out.println("✅ Authenticated Doctor: " + username);
            } else {
                // Try patient (could be normal patient or admin)
                Optional<Patient> patientOpt = patientRepository.findByName(username);

                if (patientOpt.isPresent()) {
                    Patient patient = patientOpt.get();
                    String role = patient.getRole(); // could be "ROLE_PATIENT" or "ROLE_ADMIN"

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    patient, null,
                                    Collections.singleton(new SimpleGrantedAuthority(role)));

                    auth.setDetails(new WebAuthenticationDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    System.out.println("✅ Authenticated " + role + ": " + username);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}

