package dk.ek.studentmockdtoe24c.dto;

import java.time.LocalDate;
import java.time.LocalTime;

// response DTO
/// from backend to frontend
public record StudentResponseDTO(
        Long id,
        String name,
        LocalDate date,
        LocalTime time
) {
}
