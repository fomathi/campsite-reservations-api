package com.upgrade.campsitereservations.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upgrade.campsitereservations.exception.BadRequestException;
import com.upgrade.campsitereservations.exception.NoAvailabilityException;
import com.upgrade.campsitereservations.model.ReservationRequest;
import com.upgrade.campsitereservations.model.ReservationResponse;
import com.upgrade.campsitereservations.model.UpdateReservationRequest;
import com.upgrade.campsitereservations.service.ReservationService;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReservationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ReservationService reservationService;

    @Nested
    @DisplayName("Given A AddReservation COMMAND")
    class AddReservation{

        @Test
        @DisplayName("Add Reservation Happy Path")
        void testAddReservation_1() throws Exception {
            when(reservationService.addReservation(any(ReservationRequest.class))).thenReturn(buildResponse());
            mockMvc.perform(post("/reservation")
                    .content(mapper.writeValueAsString(buildRequest()))
                    .contentType("application/json"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("1"));
        }

        @Test
        @DisplayName("Add reservation and a BadRequestException, then status 400")
        void testAddReservation_2() throws Exception {
            when(reservationService.addReservation(any(ReservationRequest.class))).thenThrow(BadRequestException.class);
            mockMvc.perform(post("/reservation")
                    .content(mapper.writeValueAsString(buildRequest()))
                    .contentType("application/json"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Add reservation and a NoAvailabilityException, then status 404")
        void testAddReservation_3() throws Exception {
            when(reservationService.addReservation(any(ReservationRequest.class))).thenThrow(NoAvailabilityException.class);
            mockMvc.perform(post("/reservation")
                    .content(mapper.writeValueAsString(buildRequest()))
                    .contentType("application/json"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Add reservation and a ConstraintViolationException, then status 409")
        void testAddReservation_4() throws Exception {
            when(reservationService.addReservation(any(ReservationRequest.class))).thenThrow(ConstraintViolationException.class);
            mockMvc.perform(post("/reservation")
                    .content(mapper.writeValueAsString(buildRequest()))
                    .contentType("application/json"))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("Given an UpdateReservation COMMAND")
    class UpdateReservation {

        @Test
        @DisplayName("Update reservation happy path")
        void updateReservation() throws Exception {
            when(reservationService.updateReservation(anyInt(), any(UpdateReservationRequest.class))).thenReturn(buildResponse());
            mockMvc.perform(put("/reservation/1")
                    .content(mapper.writeValueAsString(buildUpdateRequest()))
                    .contentType("application/json"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("1"));
        }
    }

    @Nested
    @DisplayName("Given a cancelReservation COMMAND")
    class CancelReservation {

        @Test
        @DisplayName("Cancel reservation")
        void cancelReservation() throws Exception {
            mockMvc.perform(delete("/reservation/1")
                    .contentType("application/json"))
                    .andExpect(status().isOk());
            verify(reservationService, times(1)).cancelReservation(1);
        }
    }

    private UpdateReservationRequest buildUpdateRequest() {
        LocalDate today = LocalDate.now();
        return UpdateReservationRequest.builder()
                .arrivalDate(today.plusDays(1).toString())
                .departureDate(today.plusDays(4).toString())
                .build();
    }

    private ReservationResponse buildResponse() {
        LocalDate today = LocalDate.now();
        return ReservationResponse.builder()
                .from(today.plusDays(1).toString())
                .to(today.plusDays(4).toString())
                .id("1")
                .build();
    }

    private ReservationRequest buildRequest() {
        LocalDate today = LocalDate.now();
        ReservationRequest request = ReservationRequest.builder()
                .email("test@email.com")
                .fullName("firstName lastName")
                .arrivalDate(today.plusDays(1).toString())
                .departureDate(today.plusDays(4).toString())
                .build();

        return request;
    }
}