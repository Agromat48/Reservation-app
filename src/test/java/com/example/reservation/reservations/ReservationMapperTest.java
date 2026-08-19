package com.example.reservation.reservations;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReservationMapperTest {

    private final ReservationMapper mapper = new ReservationMapper();

    @Test
    void toDomain_mapsAllFields() {
        ReservationEntity entity = new ReservationEntity(
                1L,
                10L,
                20L,
                LocalDate.of(2026, 8, 20),
                LocalDate.of(2026, 8, 25),
                ReservationStatus.PENDING
        );

        Reservation reservation = mapper.toDomain(entity);

        assertEquals(1L, reservation.id());
        assertEquals(10L, reservation.userId());
        assertEquals(20L, reservation.roomId());
        assertEquals(LocalDate.of(2026, 8, 20), reservation.startDate());
        assertEquals(LocalDate.of(2026, 8, 25), reservation.endDate());
        assertEquals(ReservationStatus.PENDING, reservation.status());
    }

    @Test
    void toEntity_mapsAllFields() {
        Reservation reservation = new Reservation(
                1L,
                10L,
                20L,
                LocalDate.of(2026, 8, 20),
                LocalDate.of(2026, 8, 25),
                ReservationStatus.APPROVED
        );

        ReservationEntity entity = mapper.toEntity(reservation);

        assertEquals(1L, entity.getId());
        assertEquals(10L, entity.getUserId());
        assertEquals(20L, entity.getRoomId());
        assertEquals(LocalDate.of(2026, 8, 20), entity.getStartDate());
        assertEquals(LocalDate.of(2026, 8, 25), entity.getEndDate());
        assertEquals(ReservationStatus.APPROVED, entity.getStatus());
    }
}