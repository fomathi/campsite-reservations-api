package com.upgrade.campsitereservations.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "campsite.availability")
@Data
public class AvailabilityConfig {
    private int defaultDateRange;
}
