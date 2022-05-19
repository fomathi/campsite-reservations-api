package com.upgrade.campsitereservations.component;

import com.upgrade.campsitereservations.exception.NoAvailabilityException;
import com.upgrade.campsitereservations.model.Availability;
import com.upgrade.campsitereservations.model.entity.Reservation;
import com.upgrade.campsitereservations.repository.ReservationRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CampsiteAvailabilityManager {
    private static final String NO_AVAILABILITY = "No campsite is available for the provided request";

    private final ReservationRepository reservationRepository;

    public CampsiteAvailabilityManager(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Availability findAvailability(LocalDate from, LocalDate to) {
        List<LocalDate> availableDatesInTheNext30Days = findAvailableDatesInTheNext30Days();
        int availabilityStartIndex = 0;
        int availabilityEndIndex = 0;

        if(availableDatesInTheNext30Days.contains(from) && availableDatesInTheNext30Days.contains(to)) {
            availabilityStartIndex = availableDatesInTheNext30Days.indexOf(from);
            availabilityEndIndex = availableDatesInTheNext30Days.indexOf(to);
        } else if(availableDatesInTheNext30Days.contains(from) && !availableDatesInTheNext30Days.contains(to)) {
            availabilityStartIndex = availableDatesInTheNext30Days.indexOf(from);
            availabilityEndIndex = availableDatesInTheNext30Days.size() - 1;
        } else if (!availableDatesInTheNext30Days.contains(from) && availableDatesInTheNext30Days.contains(to)) {
            availabilityStartIndex = 1;
            availabilityEndIndex = availableDatesInTheNext30Days.indexOf(to);
        } else {
            throw new NoAvailabilityException(NO_AVAILABILITY);
        }

        return buildAvailability(availableDatesInTheNext30Days, availabilityStartIndex, availabilityEndIndex);
    }

    private Availability buildAvailability(List<LocalDate> availabilityDates, int start, int end) {
        List<LocalDate> availabilities = new ArrayList<>();
        for(int i = start; i<=end; i++) {
            availabilities.add(availabilityDates.get(i));
        }
        return Availability.builder().availableDates(availabilities).build();
    }

    private List<LocalDate> findAvailableDatesInTheNext30Days() {
        List<LocalDate> next30daysAvailabilities = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for(int i = 1; i<= 30; i++) {
            next30daysAvailabilities.add(today.plusDays(i));
        }

        List<Reservation> reservations = reservationRepository.findNext30DaysReservations();

        List<LocalDate> next30daysReservations = new ArrayList<>();
        for(Reservation reservation: reservations) {
            int count = 0;
            while(!reservation.getStartDate().plusDays(count).isAfter(reservation.getEndDate())) {
                next30daysReservations.add(reservation.getStartDate().plusDays(count));
                count++;
            }
        }

        List<LocalDate> differences = next30daysAvailabilities.stream()
                .filter(element -> !next30daysReservations.contains(element))
                .collect(Collectors.toList());

        return differences;
    }
}
