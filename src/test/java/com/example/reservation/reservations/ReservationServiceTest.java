package com.example.reservation.reservations;

import com.example.reservation.reservations.availability.ReservationAvailabilityService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
    private ReservationMapper mapper;

    @Mock
    private ReservationAvailabilityService availabilityService;

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
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(reservation);

        Reservation result = service.getReservationById(1L);

        assertEquals(reservation, result);
        verify(repository).findById(1L);
    }

    @Test
    void getReservationById_whenNotFound_throwsException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getReservationById(1L));
    }

    @Test
    void searchAllByFilter_withoutPagination_usesDefaults() {
        ReservationSearchFilter filter = new ReservationSearchFilter(null, null, null, null);
        ReservationEntity entity = entity(1L, ReservationStatus.PENDING);
        Reservation reservation = reservation(1L, ReservationStatus.PENDING);
        when(repository.searchAllByFilter(null, null, Pageable.ofSize(10).withPage(0)))
                .thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(reservation);

        List<Reservation> result = service.searchAllByFilter(filter);

        assertEquals(List.of(reservation), result);
        verify(repository).searchAllByFilter(null, null, Pageable.ofSize(10).withPage(0));
    }

    @Test
    void createReservation_setsPendingStatusAndSaves() {
        Reservation reservation = reservation(null, null);
        ReservationEntity entity = entity(null, null);
        Reservation saved = reservation(1L, ReservationStatus.PENDING);
        when(mapper.toEntity(reservation)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(saved);

        Reservation result = service.createReservation(reservation);

        assertEquals(ReservationStatus.PENDING, entity.getStatus());
        assertEquals(saved, result);
        verify(repository).save(entity);
    }

    @Test
    void createReservation_whenStatusIsSet_throwsException() {
        Reservation reservation = reservation(null, ReservationStatus.APPROVED);

        assertThrows(IllegalArgumentException.class, () -> service.createReservation(reservation));
    }

    @Test
    void createReservation_whenInvalidDates_throwsException() {
        Reservation reservation = new Reservation(null, 10L, 20L, endDate, startDate, null);

        assertThrows(IllegalArgumentException.class, () -> service.createReservation(reservation));
    }

    @Test
    void updateReservation_whenPending_keepsIdAndSaves() {
        ReservationEntity existing = entity(1L, ReservationStatus.PENDING);
        Reservation update = reservation(null, null);
        ReservationEntity entityToSave = entity(null, null);
        Reservation updated = reservation(1L, ReservationStatus.PENDING);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(mapper.toEntity(update)).thenReturn(entityToSave);
        when(repository.save(entityToSave)).thenReturn(entityToSave);
        when(mapper.toDomain(entityToSave)).thenReturn(updated);

        Reservation result = service.updateReservation(1L, update);

        assertEquals(1L, entityToSave.getId());
        assertEquals(ReservationStatus.PENDING, entityToSave.getStatus());
        assertEquals(updated, result);
        verify(repository).save(entityToSave);
    }

    @Test
    void updateReservation_whenNotPending_throwsException() {
        ReservationEntity existing = entity(1L, ReservationStatus.APPROVED);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        assertThrows(
                IllegalStateException.class,
                () -> service.updateReservation(1L, reservation(null, null))
        );
    }

    @Test
    void cancelReservation_whenPending_setsCancelledStatus() {
        ReservationEntity pending = entity(1L, ReservationStatus.PENDING);
        when(repository.findById(1L)).thenReturn(Optional.of(pending));

        service.cancelReservation(1L);

        verify(repository).setStatus(1L, ReservationStatus.CANCELLED);
    }

    @Test
    void cancelReservation_whenAlreadyCancelled_throwsException() {
        ReservationEntity cancelled = entity(1L, ReservationStatus.CANCELLED);
        when(repository.findById(1L)).thenReturn(Optional.of(cancelled));

        assertThrows(IllegalStateException.class, () -> service.cancelReservation(1L));
    }

    @Test
    void approveReservation_whenAvailable_setsApprovedStatus() {
        ReservationEntity pending = entity(1L, ReservationStatus.PENDING);
        Reservation approved = reservation(1L, ReservationStatus.APPROVED);
        when(repository.findById(1L)).thenReturn(Optional.of(pending));
        when(availabilityService.isReservationAvailable(20L, startDate, endDate)).thenReturn(true);
        when(mapper.toDomain(pending)).thenReturn(approved);

        Reservation result = service.approveReservation(1L);

        assertEquals(ReservationStatus.APPROVED, pending.getStatus());
        assertEquals(approved, result);
        verify(repository).save(pending);
    }

    @Test
    void approveReservation_whenConflict_throwsException() {
        ReservationEntity pending = entity(1L, ReservationStatus.PENDING);
        when(repository.findById(1L)).thenReturn(Optional.of(pending));
        when(availabilityService.isReservationAvailable(20L, startDate, endDate)).thenReturn(false);

        assertThrows(IllegalStateException.class, () -> service.approveReservation(1L));
    }

    @Test
    void approveReservation_whenNotPending_throwsException() {
        ReservationEntity approved = entity(1L, ReservationStatus.APPROVED);
        when(repository.findById(1L)).thenReturn(Optional.of(approved));

        assertThrows(IllegalStateException.class, () -> service.approveReservation(1L));
    }
}