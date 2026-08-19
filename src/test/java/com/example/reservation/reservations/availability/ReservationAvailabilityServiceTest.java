package com.example.reservation.reservations.availability;

import com.example.reservation.reservations.ReservationRepository;
import com.example.reservation.reservations.ReservationStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationAvailabilityServiceTest {

    @Mock
    private ReservationRepository repository;

    @InjectMocks
    private ReservationAvailabilityService service;

    private final LocalDate startDate = LocalDate.of(2026, 8, 20);
    private final LocalDate endDate = LocalDate.of(2026, 8, 25);

    @Test
    void isReservationAvailable_whenNoConflicts_returnsTrue() {
        when(repository.findConflictReservations(1L, startDate, endDate, ReservationStatus.APPROVED))
                .thenReturn(List.of());

        boolean result = service.isReservationAvailable(1L, startDate, endDate);

        assertTrue(result);
        verify(repository).findConflictReservations(1L, startDate, endDate, ReservationStatus.APPROVED);
    }

    @Test
    void isReservationAvailable_whenConflictsExist_returnsFalse() {
        when(repository.findConflictReservations(1L, startDate, endDate, ReservationStatus.APPROVED))
                .thenReturn(List.of(5L, 6L));

        boolean result = service.isReservationAvailable(1L, startDate, endDate);

        assertFalse(result);
    }

    @Test
    void isReservationAvailable_whenEndDateBeforeStartDate_throwsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.isReservationAvailable(1L, endDate, startDate)
        );
    }
}