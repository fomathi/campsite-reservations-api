package com.upgrade.campsitereservations.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class UpdateReservationRequest {
    @Schema(required = true, description = "Reservation start date", format = "yyyy-mm-dd")
    @NotNull
    private String arrivalDate;
    @Schema(required = true, description = "Reservation end date", format = "yyyy-mm-dd")
    @NotNull
    private String departureDate;
}
    