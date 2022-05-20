package com.upgrade.campsitereservations.component;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class Validator {

    public static boolean isAvailabilityRangeProvided(String from, String to) {
        return StringUtils.isNotEmpty(from) && StringUtils.isNotEmpty(to);
    }

    public static boolean onlyFromDateIsProvided(String from, String to) {
        return StringUtils.isNotEmpty(from) && StringUtils.isEmpty(to);
    }

    public static boolean onlyToDateIsProvided(String from, String to) {
        return StringUtils.isEmpty(from) && StringUtils.isNotEmpty(to);
    }

    public static boolean isAvailabilityRequestValid(String from, String to) {
        return LocalDate.parse(from).isBefore(LocalDate.parse(to));
    }

    public static boolean isReservationStartDateValid(String from, int minDaysBeforeStart, int maxDaysBeforeStart) {
        LocalDate today = LocalDate.now();
        return isReservationStartDateValidateMinDateBeforeStart(from, today, minDaysBeforeStart) &&
                isReservationStartDateValidateMaxDateBeforeStart(from, today, maxDaysBeforeStart);

    }

    private static boolean isReservationStartDateValidateMinDateBeforeStart(String from, LocalDate today, int minDaysBeforeStart) {
        return today.plusDays(minDaysBeforeStart).isBefore(LocalDate.parse(from)) ||
                today.plusDays(minDaysBeforeStart).equals(LocalDate.parse(from));
    }

    private static boolean isReservationStartDateValidateMaxDateBeforeStart(String from, LocalDate today, int maxDaysBeforeStart) {
        return LocalDate.parse(from).isBefore(today.plusDays(maxDaysBeforeStart)) ||
                LocalDate.parse(from).equals(today.plusDays(maxDaysBeforeStart));
    }
}
