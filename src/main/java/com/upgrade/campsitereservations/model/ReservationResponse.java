package com.upgrade.campsitereservations.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationResponse {

    @Schema(required = true, description = "Reservation ID")
    private String id;
    @Schema(required = true, description = "Reservation Start date", format = "yyyy-mm-dd")
    private String from;
    @Schema(required = true, description = "Reservation End date", format = "yyyy-mm-dd")
    private String to;
}
