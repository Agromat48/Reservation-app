package com.example.reservation.reservations;

import com.example.reservation.reservations.availability.ReservationAvailabilityService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository repository;

    @Mock
    private ReservationAvailabilityService availabilityService;

    @Mock
    private ReservationMapper mapper;

    @InjectMocks
    private ReservationService service;

    private final LocalDate startDate = LocalDate.of(2026, 8, 20);
    private final LocalDate endDate = LocalDate.of(2026, 8, 25);

    private ReservationEntity entity(Long id, ReservationStatus status) {
        return new ReservationEntity(id, 10L, 20L, startDate, endDate, status);
    }

    private Reservation reservation(Long id, ReservationStatus status) {
        return new Reservation(id, 10L, 20L, startDate, endDate, status);
    }

    @Test
    void getReservationById_returnsReservation() {
        ReservationEntity entity = entity(1L, ReservationStatus.PENDING);
        Reservation reservation = reservation(1L, ReservationStatus.PENDING);
        Mockito.when(repository.findById(1L))
                .thenReturn(Optional.of(entity));
        Mockito.when(mapper.toDomain(entity))
                .thenReturn(reservation);

        Reservation reservationById = service.getReservationById(1L);

        Assertions.assertEquals(reservationById, reservation);
    }

    @Test
    void getReservationById_whenNotFound_throwsException() {
        Mockito.when(repository.findById(1L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.getReservationById(1L));
    }

    @Test
    void searchAllByFilter_withoutPagination_usesDefaults() {
        ReservationSearchFilter filter = new ReservationSearchFilter(20L, 10L, null, null);
        ReservationEntity entity = entity(1L, ReservationStatus.PENDING);
        Reservation reservation = reservation(1L, ReservationStatus.PENDING);
        Pageable defaultPageable = Pageable.ofSize(10).withPage(0);

        Mockito.when(repository.searchAllByFilter(20L, 10L, defaultPageable))
                .thenReturn(List.of(entity));
        Mockito.when(mapper.toDomain(entity))
                .thenReturn(reservation);

        List<Reservation> result = service.searchAllByFilter(filter);

        Assertions.assertEquals(List.of(reservation), result);

        Mockito.verify(repository).searchAllByFilter(20L, 10L, defaultPageable);
    }

    @Test
    void createReservation_setsPendingStatusAndSaves() {
        Reservation reservation = reservation(null, null);
        Reservation reservationPending = reservation(1L, ReservationStatus.PENDING);
        ReservationEntity entityToSave = entity(null, null);
        ReservationEntity savedEntity = entity(1L, ReservationStatus.PENDING);

        when(mapper.toEntity(reservation)).thenReturn(entityToSave);
        when(repository.save(entityToSave)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(reservationPending);

        Reservation result = service.createReservation(reservation);

        assertEquals(ReservationStatus.PENDING, entityToSave.getStatus());
        assertEquals(reservationPending, result);

        verify(repository).save(entityToSave);
    }

    @Test
    void createReservation_whenStatusIsSet_throwsException() {
        Reservation reservation = reservation(1L, ReservationStatus.PENDING);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createReservation(reservation));

        Assertions.assertEquals("Status should be empty", exception.getMessage());

        Mockito.verify(repository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void createReservation_whenInvalidDates_throwsException() {
        Reservation reservation = new Reservation(
                null,
                1L,
                2L,
                endDate,
                startDate,
                null
                );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createReservation(reservation));

        Assertions.assertEquals("Start date should be after end date", exception.getMessage());

        Mockito.verify(repository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void updateReservation_whenPending_keepsIdAndSaves() {
        ReservationEntity existing = entity(1L, ReservationStatus.PENDING);
        Reservation update = reservation(null, null);
        ReservationEntity entityToSave = entity(null, null);
        Reservation updated = reservation(1L, ReservationStatus.PENDING);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(mapper.toEntity(update)).thenReturn(entityToSave);
        Mockito.when(repository.save(entityToSave)).thenReturn(entityToSave);
        Mockito.when(mapper.toDomain(entityToSave)).thenReturn(updated);

        Reservation result = service.updateReservation(1L, update);

        Assertions.assertEquals(1L, entityToSave.getId());
        Assertions.assertEquals(ReservationStatus.PENDING, entityToSave.getStatus());
        Assertions.assertEquals(updated, result);

        Mockito.verify(repository).save(entityToSave);
    }

    @Test
    void updateReservation_whenNotPending_throwsException() {
        ReservationEntity existing = entity(1L, ReservationStatus.APPROVED);
        Reservation update = reservation(null, null);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(existing));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> service.updateReservation(1L, update));

        Assertions.assertEquals("Cannot modify reservation: status = APPROVED", exception.getMessage());

        Mockito.verify(repository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void updateReservation_whenNotFound_throwsException() {
        Reservation update = reservation(null, null);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.updateReservation(1L, update));

        Assertions.assertEquals("Reservation with id 1 not found!", exception.getMessage());

        Mockito.verify(repository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void updateReservation_whenInvalidDates_throwsException() {
        ReservationEntity existing = entity(1L, ReservationStatus.PENDING);
        Reservation update = new Reservation(null, 10L, 20L, endDate, startDate, null);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(existing));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.updateReservation(1L, update));

        Assertions.assertEquals("Start date should be after end date", exception.getMessage());

        Mockito.verify(repository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void cancelReservation_whenPending_setsCancelledStatus() {
        ReservationEntity pending = entity(1L, ReservationStatus.PENDING);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(pending));

        service.cancelReservation(1L);

        Mockito.verify(repository).setStatus(1L, ReservationStatus.CANCELLED);
    }

    @Test
    void cancelReservation_whenAlreadyCancelled_throwsException() {
        ReservationEntity cancelled = entity(1L, ReservationStatus.CANCELLED);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(cancelled));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> service.cancelReservation(1L));

        Assertions.assertEquals("Cannot cancel the reservation. Reservation was already cancelled",
                exception.getMessage());

        Mockito.verify(repository, Mockito.times(0)).setStatus(Mockito.any(), Mockito.any());
    }

    @Test
    void cancelReservation_whenNotFound_throwsException() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.cancelReservation(1L));

        Assertions.assertEquals("Reservation with id 1 not found!", exception.getMessage());

        Mockito.verify(repository, Mockito.times(0)).setStatus(Mockito.any(), Mockito.any());
    }

    @Test
    void cancelReservation_whenApproved_throwsException() {
        ReservationEntity approved = entity(1L, ReservationStatus.APPROVED);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(approved));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> service.cancelReservation(1L));

        Assertions.assertEquals("Cannot cancel reservation: status = APPROVED", exception.getMessage());

        Mockito.verify(repository, Mockito.times(0)).setStatus(Mockito.any(), Mockito.any());
    }

    @Test
    void approveReservation_whenNotFound_throwsException() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.approveReservation(1L));

        Assertions.assertEquals("Reservation with id 1 not found!", exception.getMessage());

        Mockito.verify(repository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void approveReservation_whenAvailable_setsApprovedStatus() {
        ReservationEntity pending = entity(1L, ReservationStatus.PENDING);
        Reservation approved = reservation(1L, ReservationStatus.APPROVED);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(pending));
        Mockito.when(availabilityService.isReservationAvailable(20L, startDate, endDate)).thenReturn(true);
        Mockito.when(mapper.toDomain(pending)).thenReturn(approved);

        Reservation result = service.approveReservation(1L);

        Assertions.assertEquals(ReservationStatus.APPROVED, pending.getStatus());
        Assertions.assertEquals(approved, result);

        Mockito.verify(repository).save(pending);
    }

    @Test
    void approveReservation_whenConflict_throwsException() {
        ReservationEntity pending = entity(1L, ReservationStatus.PENDING);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(pending));
        Mockito.when(availabilityService.isReservationAvailable(20L, startDate, endDate)).thenReturn(false);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> service.approveReservation(1L));

        Assertions.assertEquals("Cannot approve reservation because of conflict", exception.getMessage());

        Mockito.verify(repository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void approveReservation_whenNotPending_throwsException() {
        ReservationEntity approved = entity(1L, ReservationStatus.APPROVED);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(approved));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> service.approveReservation(1L));

        Assertions.assertEquals("Cannot approve reservation: status = APPROVED", exception.getMessage());

        Mockito.verify(repository, Mockito.times(0)).save(Mockito.any());
    }
}
