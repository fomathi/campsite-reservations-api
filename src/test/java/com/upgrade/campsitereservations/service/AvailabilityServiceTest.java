package com.upgrade.campsitereservations.service;

import com.upgrade.campsitereservations.component.CampsiteAvailabilityManager;
import com.upgrade.campsitereservations.component.Validator;
import com.upgrade.campsitereservations.config.AvailabilityConfig;
import com.upgrade.campsitereservations.exception.BadRequestException;
import com.upgrade.campsitereservations.exception.NoAvailabilityException;
import com.upgrade.campsitereservations.model.Availability;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AvailabilityServiceTest {

    AvailabilityConfig availabilityConfig = mock(AvailabilityConfig.class);
    Validator validator = mock(Validator.class);
    CampsiteAvailabilityManager campsiteAvailabilityManager = mock(CampsiteAvailabilityManager.class);

    AvailabilityService service = new AvailabilityService(availabilityConfig, validator, campsiteAvailabilityManager);

    @Nested
    @DisplayName("Given a GetAvailability COMMAND")
    public class GetAvailability {

        @Test
        @DisplayName("Given a valid from and to dates, then return availability")
        void testGetAvailabilityHappyPath() {
            LocalDate from = LocalDate.now();
            LocalDate to = from.plusDays(10);
            List<LocalDate> availableDates = new ArrayList<>();
            for(int i = 1; i<=30; i++) {
                availableDates.add(from.minusDays(2).plusDays(i));
            }
            Availability availabilityDates =  Availability.builder()
                    .availableDates(availableDates)
                    .build();

            when(availabilityConfig.getDefaultDateRange()).thenReturn(30);
            when(campsiteAvailabilityManager.findAvailability(from, to)).thenReturn(availabilityDates);

            Availability availability = service.getAvailability(from.toString(), to.toString());

            assertTrue(availability.getAvailableDates().contains(to));
            assertTrue(availability.getAvailableDates().contains(from));
            assertTrue(availability.getAvailableDates().size() == 30);
        }

        @Test
        @DisplayName("Given a valid from and to dates, only from is in the available dates, then return availability")
        void testGetAvailability_1() {
            LocalDate from = LocalDate.now();
            LocalDate to = from.plusDays(10);
            List<LocalDate> availableDates = new ArrayList<>();
            for(int i = 1; i<=5; i++) {
                availableDates.add(from.minusDays(2).plusDays(i));
            }
            Availability availabilityDates =  Availability.builder()
                    .availableDates(availableDates)
                    .build();

            when(availabilityConfig.getDefaultDateRange()).thenReturn(30);
            when(campsiteAvailabilityManager.findAvailability(from, to)).thenReturn(availabilityDates);

            Availability availability = service.getAvailability(from.toString(), to.toString());

            assertFalse(availability.getAvailableDates().contains(to));
            assertTrue(availability.getAvailableDates().size() == 5);
        }

        @Test
        @DisplayName("Given a valid from and to dates, from is not in the available dates, then return availability")
        void testGetAvailability_2() {
            LocalDate from = LocalDate.now();
            LocalDate to = from.plusDays(10);

            List<LocalDate> availableDates = new ArrayList<>();
            for(int i = 1; i<=10; i++) {
                availableDates.add(from.plusDays(i));
            }
            Availability availabilityDates =  Availability.builder()
                    .availableDates(availableDates)
                    .build();

            when(availabilityConfig.getDefaultDateRange()).thenReturn(30);
            when(campsiteAvailabilityManager.findAvailability(from, to)).thenReturn(availabilityDates);

            Availability availability = service.getAvailability(from.toString(), to.toString());

            assertFalse(availability.getAvailableDates().contains(from));
            assertTrue(availability.getAvailableDates().size() == 10);
        }

        @Test
        @DisplayName("Given a valid from and to dates and no availability was found, then throw NoAvailabilityException")
        void testGetAvailability_3() {
            LocalDate from = LocalDate.now();
            LocalDate to = from.plusDays(10);

            when(availabilityConfig.getDefaultDateRange()).thenReturn(30);
            when(campsiteAvailabilityManager.findAvailability(from, to)).thenThrow(NoAvailabilityException.class);

            assertThrows(NoAvailabilityException.class, ()->{
                service.getAvailability(from.toString(), to.toString());
            });
        }

        @Test
        @DisplayName("Given an invalid date range, then throw BadRequestException")
        void testGetAvailability_4() {
            String from = "2022-06-20";
            String to = "2022-06-05";

            BadRequestException exception = assertThrows(BadRequestException.class, ()->{
                service.getAvailability(from, to);
            });

            assertEquals("Invalid availability request parameters", exception.getMessage());
        }

        @Test
        @DisplayName("Given a valid from date and to is empty")
        void testGetAvailability_5() {
            LocalDate from = LocalDate.now();
            String to = "";
            List<LocalDate> availableDates = new ArrayList<>();
            for(int i = 1; i<=30; i++) {
                availableDates.add(from.minusDays(2).plusDays(i));
            }
            Availability availabilityDates =  Availability.builder()
                    .availableDates(availableDates)
                    .build();

            when(availabilityConfig.getDefaultDateRange()).thenReturn(30);
            when(campsiteAvailabilityManager.findAvailability(from, from.plusDays(30))).thenReturn(availabilityDates);

            Availability availability = service.getAvailability(from.toString(), to);

            assertTrue(availability.getAvailableDates().contains(from));
        }

        @Test
        @DisplayName("Given a valid to date and from is empty")
        void testGetAvailability_6() {
            String from = "";
            LocalDate to = LocalDate.now().plusDays(20);

            List<LocalDate> availableDates = new ArrayList<>();
            for(int i = 1; i<=30; i++) {
                availableDates.add(to.minusDays(30).plusDays(i));
            }
            Availability availabilityDates =  Availability.builder()
                    .availableDates(availableDates)
                    .build();

            when(availabilityConfig.getDefaultDateRange()).thenReturn(30);
            when(campsiteAvailabilityManager.findAvailability(to.minusDays(availabilityConfig.getDefaultDateRange()), to)).thenReturn(availabilityDates);

            Availability availability = service.getAvailability(from, to.toString());

            assertTrue(availability.getAvailableDates().contains(to));
        }

        @Test
        @DisplayName("Given an empty date range, then use default range for availability")
        void testGetAvailability_7() {
            String from = "";
            String to = "";
            LocalDate fromDate = LocalDate.now();
            List<LocalDate> availableDates = new ArrayList<>();
            for(int i = 1; i<=10; i++) {
                availableDates.add(fromDate.plusDays(i));
            }
            Availability availabilityDates =  Availability.builder()
                    .availableDates(availableDates)
                    .build();

            when(availabilityConfig.getDefaultDateRange()).thenReturn(30);
            when(campsiteAvailabilityManager.findAvailability(fromDate, fromDate.plusDays(30))).thenReturn(availabilityDates);

            Availability availability = service.getAvailability(from, to);

            assertFalse(availability.getAvailableDates().contains(fromDate));
        }

        @Test
        @DisplayName("Given an invalid date format, then throw BadRequestException")
        void testGetAvailability_8() {
            String from = "20-06-2022";
            String to = "2022-06-05";

            BadRequestException exception = assertThrows(BadRequestException.class, ()->{
                service.getAvailability(from, to);
            });

            assertEquals("Invalid date format", exception.getMessage());
        }

        @Test
        @DisplayName("Given an invalid date format, then throw BadRequestException")
        void testGetAvailability_9() {
            LocalDate today = LocalDate.now();
            LocalDate from = today.plusDays(31);
            LocalDate to = today.plusDays(35);
            when(campsiteAvailabilityManager.findAvailability(from, to)).thenReturn(Availability.builder()
                                                                                                .availableDates(new ArrayList<>())
                                                                                                .build());

            NoAvailabilityException exception = assertThrows(NoAvailabilityException.class, ()->{
                service.getAvailability(from.toString(), to.toString());
            });

            assertEquals("No campsite is available for the provided request", exception.getMessage());
        }
    }

}