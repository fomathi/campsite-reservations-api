package com.upgrade.campsitereservations.component;

import com.upgrade.campsitereservations.exception.NoAvailabilityException;
import com.upgrade.campsitereservations.model.Availability;
import com.upgrade.campsitereservations.model.entity.Reservation;
import com.upgrade.campsitereservations.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CampsiteAvailabilityManagerTest {

    private final ReservationRepository reservationRepository = mock(ReservationRepository.class);

    CampsiteAvailabilityManager availabilityManager = new CampsiteAvailabilityManager(reservationRepository);

    @Nested
    @DisplayName("Given a FindAvailability COMMAND")
    class FindAvailability {

        @Test
        @DisplayName("Given a valid date range, then return the campsite availability")
        void testFindAvailability_1() {
            LocalDate from = LocalDate.now().plusDays(2);
            LocalDate to = from.plusDays(10);
            when(reservationRepository.findNext30DaysReservations()).thenReturn(buildReservationsList());

            Availability availability = availabilityManager.findAvailability(from, to);

            assertEquals(5, availability.getAvailableDates().size());
            assertTrue(availability.getAvailableDates().contains(from));
            assertTrue(availability.getAvailableDates().contains(to));
        }

        @Test
        @DisplayName("Given a valid date range but end date is not available, then return the campsite availability with default range")
        void testFindAvailability_2() {
            LocalDate from = LocalDate.now().plusDays(2);
            LocalDate to = from.plusDays(7);
            when(reservationRepository.findNext30DaysReservations()).thenReturn(buildReservationsList());

            Availability availability = availabilityManager.findAvailability(from, to);

            assertEquals(23, availability.getAvailableDates().size());
            assertTrue(availability.getAvailableDates().contains(from));
            assertFalse(availability.getAvailableDates().contains(to));
        }

        @Test
        @DisplayName("Given a valid date range but start date is not available, then return the campsite availability with default range")
        void testFindAvailability_3() {
            LocalDate from = LocalDate.now().plusDays(5);
            LocalDate to = from.plusDays(10);
            when(reservationRepository.findNext30DaysReservations()).thenReturn(buildReservationsList());

            Availability availability = availabilityManager.findAvailability(from, to);

            assertEquals(8, availability.getAvailableDates().size());
            assertFalse(availability.getAvailableDates().contains(from));
            assertTrue(availability.getAvailableDates().contains(to));
        }

        @Test
        @DisplayName("Given a range date with No availability, then return empty availability array")
        void testFindAvailability_4() {
            LocalDate from = LocalDate.now().plusDays(31);
            LocalDate to = from.plusDays(3);
            when(reservationRepository.findNext30DaysReservations()).thenReturn(buildReservationsList());

            Availability availability = availabilityManager.findAvailability(from, to);

            assertEquals(0, availability.getAvailableDates().size());
        }

        @Test
        @DisplayName("Given a valid date range, then return the campsite availability")
        void testFindAvailability_5() {
            LocalDate from = LocalDate.now().plusDays(2);
            LocalDate to = from.plusDays(10);
            List<LocalDate> oldReservationsDates = new ArrayList<>();
            oldReservationsDates.add(from.minusDays(2));
            when(reservationRepository.findNext30DaysReservations()).thenReturn(buildReservationsList());

            Availability availability = availabilityManager.findAvailability(from, to, oldReservationsDates);

            assertEquals(6, availability.getAvailableDates().size());
            assertTrue(availability.getAvailableDates().contains(from));
            assertTrue(availability.getAvailableDates().contains(to));
            assertTrue(availability.getAvailableDates().contains(from.minusDays(2)));
        }

        private List<Reservation> buildReservationsList() {
            List<Reservation> reservations = new ArrayList<>();

            Reservation reservation1 = new Reservation();
            reservation1.setId(1);
            reservation1.setStartDate(LocalDate.now().plusDays(5));
            reservation1.setEndDate(LocalDate.now().plusDays(8));
            reservations.add(reservation1);

            Reservation reservation2 = new Reservation();
            reservation2.setId(1);
            reservation2.setStartDate(LocalDate.now().plusDays(9));
            reservation2.setEndDate(LocalDate.now().plusDays(10));
            reservations.add(reservation2);

            return reservations;
        }
    }
}