package com.example.reservation.reservations;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ReservationMapperTest {

    private ReservationMapper mapper;

    @BeforeEach
    void setUp() {
         mapper = new ReservationMapper();
    }

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

        Reservation domain = mapper.toDomain(entity);

        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, domain.id()),
                () -> Assertions.assertEquals(10L, domain.userId()),
                () -> Assertions.assertEquals(20L, domain.roomId()),
                () -> Assertions.assertEquals(LocalDate.of(2026, 8, 20),
                        domain.startDate()),
                () -> Assertions.assertEquals(LocalDate.of(2026, 8, 25),
                        domain.endDate()),
                () -> Assertions.assertEquals(ReservationStatus.PENDING, domain.status())
        );
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