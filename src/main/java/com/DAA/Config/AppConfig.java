package com.DAA.Config;

import com.DAA.Dto.AppointmentDTO;
import com.DAA.Dto.AvailabilityDTO;
import com.DAA.Entities.Appointment;
import com.DAA.Entities.Availability;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper mm = new ModelMapper();
        mm.typeMap(Appointment.class, AppointmentDTO.class).addMappings(m -> {
            m.map(src -> src.getPatient().getPatientId(), AppointmentDTO::setPatientId);
            m.map(src -> src.getDoctor().getDoctorId(), AppointmentDTO::setDoctorId);
        });
        mm.typeMap(Availability.class, AvailabilityDTO.class).addMappings(m ->
                m.map(src -> src.getDoctor().getDoctorId(), AvailabilityDTO::setDoctorId)
        );
        return mm;
    }
}
