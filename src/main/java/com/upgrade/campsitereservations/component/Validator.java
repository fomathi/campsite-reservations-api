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
}
