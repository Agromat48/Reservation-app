package com.example.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ErrorResponceDto (
        String message,
        String detailedMessage,
        LocalDateTime errorTime
) {
}
