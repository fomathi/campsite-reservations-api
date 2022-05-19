package com.upgrade.campsitereservations.service;

import com.upgrade.campsitereservations.component.CampsiteAvailabilityManager;
import com.upgrade.campsitereservations.component.Validator;
import com.upgrade.campsitereservations.config.AvailabilityConfig;
import com.upgrade.campsitereservations.exception.BadRequestException;
import com.upgrade.campsitereservations.model.Availability;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AvailabilityService {
    private static final String INVALID_DATE = "Invalid availability request parameters";
    private final AvailabilityConfig availabilityConfig;
    private final Validator validator;
    private final CampsiteAvailabilityManager campsiteAvailabilityManager;


    public AvailabilityService(AvailabilityConfig availabilityConfig, Validator validator, CampsiteAvailabilityManager campsiteAvailabilityManager) {
        this.availabilityConfig = availabilityConfig;
        this.validator = validator;
        this.campsiteAvailabilityManager = campsiteAvailabilityManager;
    }

    public Availability getAvailability(String from, String to) {
        if (validator.isAvailabilityRangeProvided(from, to)) {
            if(!validator.isAvailabilityRequestValid(from,to)) {
                throw new BadRequestException(INVALID_DATE);
            }
            LocalDate fromToDate = LocalDate.parse(from);
            LocalDate toToDate = LocalDate.parse(to);
            return campsiteAvailabilityManager.findAvailability(fromToDate, toToDate);
        } else if(validator.onlyFromDateIsProvided(from, to)) {
            LocalDate fromToDate = LocalDate.parse(from);
            return campsiteAvailabilityManager.findAvailability(fromToDate, fromToDate.plusDays(availabilityConfig.getDefaultDateRange()));
        } else if(validator.onlyToDateIsProvided(from, to)) {
            LocalDate toToDate = LocalDate.parse(to);
            return campsiteAvailabilityManager.findAvailability(toToDate.minusDays(availabilityConfig.getDefaultDateRange()), toToDate);
        } else {
            LocalDate availableFrom = LocalDate.now();
            LocalDate availableUntil = availableFrom.plusDays(availabilityConfig.getDefaultDateRange());
            return campsiteAvailabilityManager.findAvailability(availableFrom, availableUntil);
        }

    }

}
