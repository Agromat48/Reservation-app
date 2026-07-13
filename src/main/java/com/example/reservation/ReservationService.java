package com.example.reservation;

import org.springframework.stereotype.Service;

@Service
public class ReservationService {
    public String getReservationById() {
        System.out.println("log 1");
        return "reservation-1";
    }
}
