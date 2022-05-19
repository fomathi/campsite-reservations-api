package com.upgrade.campsitereservations.model;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;

@Value
@Builder
public final class ApiError {
	private final String message;
}
