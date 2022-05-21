package com.upgrade.campsitereservations.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upgrade.campsitereservations.exception.BadRequestException;
import com.upgrade.campsitereservations.exception.NoAvailabilityException;
import com.upgrade.campsitereservations.model.Availability;
import com.upgrade.campsitereservations.service.AvailabilityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AvailabilityControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    AvailabilityService availabilityService;

    @Nested
    @DisplayName("Given A GetAvailability COMMAND")
    class GetAvailability {

        @Test
        @DisplayName("Get Availability Happy Path")
        void testGetAvailabilityHappyPath() throws Exception {

            when(availabilityService.getAvailability(anyString(), anyString())).thenReturn(buildAvailability());

            mockMvc.perform(get("/availability?from=&to=")
                    .contentType("application/json"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Get Availability and a BadRequestException, then status 400")
        void testGetAvailability_1() throws Exception {

            when(availabilityService.getAvailability(anyString(), anyString())).thenThrow(BadRequestException.class);

            mockMvc.perform(get("/availability?from=&to=")
                    .contentType("application/json"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Get Availability and a NoAvailabilityException, then status 404")
        void testGetAvailability_2() throws Exception {

            when(availabilityService.getAvailability(anyString(), anyString())).thenThrow(NoAvailabilityException.class);

            mockMvc.perform(get("/availability?from=2023-01-01&to==2023-01-05")
                    .contentType("application/json"))
                    .andExpect(status().isNotFound());
        }


        @Test
        @DisplayName("Get Availability and a DateTimeParseException, then status 400")
        void testGetAvailability_3() throws Exception {

            when(availabilityService.getAvailability(anyString(), anyString())).thenThrow(DateTimeParseException.class);

            mockMvc.perform(get("/availability?from=xx&to=yyy")
                    .contentType("application/json"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Get Availability and a SQLTimeoutException, then status 500")
        void testGetAvailability_5() throws Exception {

            when(availabilityService.getAvailability(anyString(), anyString())).thenThrow(NullPointerException.class);

            mockMvc.perform(get("/availability?from=&to=")
                    .contentType("application/json"))
                    .andExpect(status().isInternalServerError());
        }
    }

    private Availability buildAvailability() {
        List<LocalDate> availableDates = new ArrayList<>();
        availableDates.add(LocalDate.now());
        availableDates.add(LocalDate.now().plusDays(1));
        availableDates.add(LocalDate.now().plusDays(2));

        return Availability.builder().availableDates(availableDates).build();
    }
}