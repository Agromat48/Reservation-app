package com.example.reservation.reservations.availability;

import com.example.reservation.reservations.ReservationRepository;
import com.example.reservation.reservations.ReservationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationAvailabilityService {

    private final ReservationRepository repository;
    private static final Logger log =  LoggerFactory.getLogger(ReservationAvailabilityService.class);

    public ReservationAvailabilityService(ReservationRepository repository) {
        this.repository = repository;
    }

    public boolean isReservationAvailable(
            Long roomId,
            LocalDate startDate,
            LocalDate endDate
    ){

        if(!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("Start date should be after end date");
        }

        List<Long> conflictsIds = repository.findConflictReservations(
                roomId,
                startDate,
                endDate,
                ReservationStatus.APPROVED
        );

        if(conflictsIds.isEmpty()) return true;

        log.info("Conflicts with ids = {}", conflictsIds);
        return false;
    }
}
