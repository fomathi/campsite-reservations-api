package com.upgrade.campsitereservations.service;

import com.upgrade.campsitereservations.component.CampsiteAvailabilityManager;
import com.upgrade.campsitereservations.component.Validator;
import com.upgrade.campsitereservations.config.ReservationConfig;
import com.upgrade.campsitereservations.exception.BadRequestException;
import com.upgrade.campsitereservations.model.Availability;
import com.upgrade.campsitereservations.model.ReservationRequest;
import com.upgrade.campsitereservations.model.ReservationResponse;
import com.upgrade.campsitereservations.model.UpdateReservationRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ReservationService {

    private static final String INVALID_DATE = "Invalid reservation request";
    private static final String INVALID_RESERVATION_START_DATE = "Reservation start date must be  minimum 1 day ahead of arrival and up to 1 month in advance";

    private final ReservationConfig config;
    private final Validator validator;
    private final CampsiteAvailabilityManager availabilityManager;

    public ReservationService(ReservationConfig config, Validator validator, CampsiteAvailabilityManager availabilityManager) {
        this.config = config;
        this.validator = validator;
        this.availabilityManager = availabilityManager;
    }

    public ReservationResponse addReservation(ReservationRequest request) {
        String from = request.getArrivalDate();
        String to = request.getDepartureDate();
        if(!validator.isAvailabilityRequestValid(from, to)) {
            throw new BadRequestException(INVALID_DATE);
        }
        if(!validator.isReservationStartDateValid(from, config.getMinDaysBeforeStart(), config.getMaxDaysBeforeStart())) {
            throw new BadRequestException(INVALID_RESERVATION_START_DATE);
        }

        LocalDate startDate = LocalDate.parse(from);
        LocalDate endDate = LocalDate.parse(to);
        Availability availability = availabilityManager.findAvailability(startDate, endDate);
        //TODO
        return ReservationResponse.builder().build();
    }

    public ReservationResponse updateReservation(int reservationId, UpdateReservationRequest request) {
        //TODO
        return ReservationResponse.builder().build();
    }

    public void cancelReservation(int reservationId) {
        //TODO
    }
}
