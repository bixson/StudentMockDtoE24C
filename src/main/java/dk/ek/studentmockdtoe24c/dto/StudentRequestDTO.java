package dk.ek.studentmockdtoe24c.dto;

import java.time.LocalDate;
import java.time.LocalTime;

// request DTO
/// from frontend to backend
public record StudentRequestDTO(
        String name,
        String password,
        LocalDate date,
        LocalTime time
) {
}
