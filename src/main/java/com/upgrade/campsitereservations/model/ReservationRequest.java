package com.upgrade.campsitereservations.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ReservationRequest {

    @Schema(required = true, description = "Visitor email")
    @NotNull
    private String email;
    @Schema(required = true, description = "Visitor full name")
    @NotNull
    private String fullName;
    @Schema(required = true, description = "Reservation start date", format = "yyyy-mm-dd")
    @NotNull
    private String arrivalDate;
    @Schema(required = true, description = "Reservation end date", format = "yyyy-mm-dd")
    @NotNull
    private String departureDate;
}
