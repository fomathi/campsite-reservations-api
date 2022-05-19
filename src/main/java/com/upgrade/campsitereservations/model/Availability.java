package com.upgrade.campsitereservations.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class Availability {
    @Schema(required = true, description = "Campsite availability dates", format = "yyyy-mm-dd")
    private List<LocalDate> availableDates;
}
