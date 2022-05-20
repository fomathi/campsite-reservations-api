package com.upgrade.campsitereservations.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "campsite.reservation")
@Data
public class ReservationConfig {
    private int maxReservationDays;
    private int minDaysBeforeStart;
    private int maxDaysBeforeStart;
}
