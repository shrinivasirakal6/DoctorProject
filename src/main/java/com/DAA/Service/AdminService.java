package com.DAA.Service;

import com.DAA.Dto.DoctorDTO;
import com.DAA.Entities.Doctor;
import com.DAA.Repo.DoctorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    //write a method to approve a doctor by setting valid doctor as true
    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ModelMapper modelMapper;

    public DoctorDTO approveDoctor(long doctorId){
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(
                () -> new RuntimeException("doctor with the id not found")
        );

        doctor.setValidDoctor(true);

       return modelMapper.map(doctor,DoctorDTO.class);
    }
}
