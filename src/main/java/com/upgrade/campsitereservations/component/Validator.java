package com.upgrade.campsitereservations.component;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class Validator {

    public static boolean isDatesFormatValid(String from, String to) {
        DateTimeFormatter  dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        try {
            if(StringUtils.isNotEmpty(from)) {
                LocalDate.parse(from, dateFormatter);
            }
            if(StringUtils.isNotEmpty(to)) {
                LocalDate.parse(to, dateFormatter);
            }
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

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

    public static boolean isReservationLessThan4Days(String from, String to, int maxReservationDays) {
        LocalDate startDate = LocalDate.parse(from);
        LocalDate endDate = LocalDate.parse(to);
        LocalDate maxDate = startDate.plusDays(maxReservationDays);
        return maxDate.isAfter(endDate) || maxDate.equals(endDate);
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
