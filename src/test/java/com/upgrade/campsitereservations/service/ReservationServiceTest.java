package com.upgrade.campsitereservations.service;

import com.upgrade.campsitereservations.component.CampsiteAvailabilityManager;
import com.upgrade.campsitereservations.component.Validator;
import com.upgrade.campsitereservations.config.ReservationConfig;
import com.upgrade.campsitereservations.exception.BadRequestException;
import com.upgrade.campsitereservations.exception.NotFoundException;
import com.upgrade.campsitereservations.model.Availability;
import com.upgrade.campsitereservations.model.ReservationRequest;
import com.upgrade.campsitereservations.model.ReservationResponse;
import com.upgrade.campsitereservations.model.UpdateReservationRequest;
import com.upgrade.campsitereservations.model.entity.Reservation;
import com.upgrade.campsitereservations.model.entity.Visitor;
import com.upgrade.campsitereservations.repository.ReservationRepository;
import com.upgrade.campsitereservations.repository.VisitorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservationServiceTest {
    ReservationConfig config = mock(ReservationConfig.class);
    Validator validator = mock(Validator.class);
    CampsiteAvailabilityManager availabilityManager = mock(CampsiteAvailabilityManager.class);
    ReservationRepository reservationRepository = mock(ReservationRepository.class);
    VisitorRepository visitorRepository = mock(VisitorRepository.class);

    ReservationService service = new ReservationService(config,
                                                        validator,
                                                        availabilityManager,
                                                        reservationRepository,
                                                        visitorRepository);

    @Nested
    @DisplayName("Given an addReservation COMMAND")
    class AddReservation {

        @Test
        @DisplayName("Given an invalid dates format, then throww BadRequestException")
        void addReservation_1() {
            ReservationRequest request = ReservationRequest.builder()
                                .email("test@email.com")
                                .fullName("firstName lastName")
                                .arrivalDate("20-06-2022")
                                .departureDate("2022-06-25")
                                .build();

            BadRequestException exception = assertThrows(BadRequestException.class, ()->{
                service.addReservation(request);
            });

            assertEquals("Invalid date format", exception.getMessage());
        }

        @Test
        @DisplayName("Given an invalid request, then throw BadRequestException")
        void addReservation_2() {
            ReservationRequest request = ReservationRequest.builder()
                    .email("test@email.com")
                    .fullName("firstName lastName")
                    .arrivalDate("2022-06-30")
                    .departureDate("2022-06-25")
                    .build();

            BadRequestException exception = assertThrows(BadRequestException.class, ()->{
                service.addReservation(request);
            });

            assertEquals("Invalid reservation request", exception.getMessage());
        }

        @Test
        @DisplayName("Given an invalid reservation startDate, then throw BadRequestException")
        void addReservation_3() {
            LocalDate today = LocalDate.now();
            LocalDate from = today;
            LocalDate to = from.plusDays(3);
            ReservationRequest request = ReservationRequest.builder()
                    .email("test@email.com")
                    .fullName("firstName lastName")
                    .arrivalDate(from.toString())
                    .departureDate(to.toString())
                    .build();
            when(config.getMinDaysBeforeStart()).thenReturn(1);
            when(config.getMaxDaysBeforeStart()).thenReturn(30);

            BadRequestException exception = assertThrows(BadRequestException.class, ()->{
                service.addReservation(request);
            });

            assertEquals("The campsite can be reserved minimum 1 day(s) ahead of arrival and up to 1 month in advance", exception.getMessage());
        }

        @Test
        @DisplayName("Given a reservation range higher than 4days, then throw BadRequestException")
        void addReservation_5() {
            LocalDate today = LocalDate.now();
            LocalDate from = today.plusDays(1);
            LocalDate to = from.plusDays(5);
            ReservationRequest request = ReservationRequest.builder()
                    .email("test@email.com")
                    .fullName("firstName lastName")
                    .arrivalDate(from.toString())
                    .departureDate(to.toString())
                    .build();
            when(config.getMinDaysBeforeStart()).thenReturn(1);
            when(config.getMaxDaysBeforeStart()).thenReturn(30);
            when(config.getMaxReservationDays()).thenReturn(3);

            BadRequestException exception = assertThrows(BadRequestException.class, ()->{
                service.addReservation(request);
            });

            assertEquals("The campsite can be reserved for max 3 days", exception.getMessage());
        }

        @Test
        @DisplayName("Given a valid reservation request, then add reservation")
        void addReservation_6() {
            LocalDate today = LocalDate.now();
            LocalDate from = today.plusDays(1);
            LocalDate to = from.plusDays(3);
            ReservationRequest request = ReservationRequest.builder()
                    .email("test@email.com")
                    .fullName("firstName lastName")
                    .arrivalDate(from.toString())
                    .departureDate(to.toString())
                    .build();
            when(config.getMinDaysBeforeStart()).thenReturn(1);
            when(config.getMaxDaysBeforeStart()).thenReturn(30);
            when(config.getMaxReservationDays()).thenReturn(3);
            List<LocalDate> dates = new ArrayList<>();
            for(int i=0; i<=3; i++){
                dates.add(from.plusDays(i));
            }
            Availability availability = Availability.builder().availableDates(dates).build();
            when(availabilityManager.findAvailability(from, to)).thenReturn(availability);
            Reservation reservation = new Reservation();
            reservation.setId(1);
            reservation.setStartDate(from);
            reservation.setEndDate(to);
            reservation.setVisitor(new Visitor());
            when(reservationRepository.save(any())).thenReturn(reservation);

            ReservationResponse actual = service.addReservation(request);

            assertEquals(from.toString(), actual.getFrom());
            assertEquals(to.toString(), actual.getTo());
        }

        @Test
        @DisplayName("Given a valid reservation request but date not availability, then throw BadRequest")
        void addReservation_7() {
            LocalDate today = LocalDate.now();
            LocalDate from = today.plusDays(1);
            LocalDate to = from.plusDays(3);
            ReservationRequest request = ReservationRequest.builder()
                    .email("test@email.com")
                    .fullName("firstName lastName")
                    .arrivalDate(from.toString())
                    .departureDate(to.toString())
                    .build();
            when(config.getMinDaysBeforeStart()).thenReturn(1);
            when(config.getMaxDaysBeforeStart()).thenReturn(30);
            when(config.getMaxReservationDays()).thenReturn(3);
            List<LocalDate> dates = new ArrayList<>();
            for(int i=0; i<=2; i++){
                dates.add(from.plusDays(i));
            }
            Availability availability = Availability.builder().availableDates(dates).build();
            when(availabilityManager.findAvailability(from, to)).thenReturn(availability);

            BadRequestException exception = assertThrows(BadRequestException.class, ()->{
                service.addReservation(request);
            });

            assertEquals("Fail to complete the reservation. Verify reservation dates availability", exception.getMessage());
        }

        @Test
        @DisplayName("Given a valid reservation request and visitor already, then add reservation")
        void addReservation_8() {
            LocalDate today = LocalDate.now();
            LocalDate from = today.plusDays(1);
            LocalDate to = from.plusDays(3);
            ReservationRequest request = ReservationRequest.builder()
                    .email("test@email.com")
                    .fullName("firstName lastName")
                    .arrivalDate(from.toString())
                    .departureDate(to.toString())
                    .build();
            when(config.getMinDaysBeforeStart()).thenReturn(1);
            when(config.getMaxDaysBeforeStart()).thenReturn(30);
            when(config.getMaxReservationDays()).thenReturn(3);
            List<LocalDate> dates = new ArrayList<>();
            for(int i=0; i<=3; i++){
                dates.add(from.plusDays(i));
            }
            Availability availability = Availability.builder().availableDates(dates).build();
            when(availabilityManager.findAvailability(from, to)).thenReturn(availability);
            Visitor visitor = new Visitor();
            visitor.setFullName("firstName lastName");
            visitor.setEmail("test@email.com");
            when(visitorRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(visitor));
            Reservation reservation = new Reservation();
            reservation.setId(1);
            reservation.setStartDate(from);
            reservation.setEndDate(to);
            reservation.setVisitor(new Visitor());
            when(reservationRepository.save(any())).thenReturn(reservation);

            ReservationResponse actual = service.addReservation(request);

            assertEquals(from.toString(), actual.getFrom());
            assertEquals(to.toString(), actual.getTo());
        }
    }

    @Nested
    @DisplayName("Given an UpdateReservation COMMAND")
    class UpdateReservation {

        @Test
        @DisplayName("Given a reservation id that doesn't exit, then throw NotFoundException")
        void updateReservation_1() {
            LocalDate today = LocalDate.now();
            LocalDate from = today.plusDays(2);
            LocalDate to = from.plusDays(3);
            UpdateReservationRequest updateRequest = UpdateReservationRequest.builder()
                    .arrivalDate(from.toString())
                    .departureDate(to.toString())
                    .build();

            when(reservationRepository.findById(1)).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class, ()->{
                service.updateReservation(1, updateRequest);
            });

            assertEquals("Reservation not found", exception.getMessage());

        }

        @Test
        @DisplayName("Given a valid update reservation request, then update")
        void updateReservation_2() {
            LocalDate today = LocalDate.now();
            LocalDate from = today.plusDays(2);
            LocalDate to = from.plusDays(3);
            UpdateReservationRequest updateRequest = UpdateReservationRequest.builder()
                    .arrivalDate(from.toString())
                    .departureDate(to.toString())
                    .build();

            Reservation reservation = new Reservation();
            reservation.setId(1);
            reservation.setStartDate(today.plusDays(1));
            reservation.setEndDate(to);

            when(reservationRepository.findById(1)).thenReturn(Optional.of(reservation));
            List<LocalDate> dates = new ArrayList<>();
            for(int i=0; i<=5; i++){
                dates.add(today.plusDays(i));
            }
            Availability availability = Availability.builder().availableDates(dates).build();
            when(availabilityManager.findAvailability(any(), any(), any())).thenReturn(availability);
            Reservation updatedReservation = new Reservation();
            reservation.setId(1);
            reservation.setStartDate(from);
            reservation.setEndDate(to);
            reservation.setVisitor(new Visitor());
            when(reservationRepository.save(any())).thenReturn(updatedReservation);

            ReservationResponse response =  service.updateReservation(1, updateRequest);


            assertEquals(from.toString(), response.getFrom());

        }

        @Test
        @DisplayName("Given a valid update reservation request but no availability, then throw BadRequestException")
        void updateReservation_3() {
            LocalDate today = LocalDate.now();
            LocalDate from = today.plusDays(2);
            LocalDate to = from.plusDays(3);
            UpdateReservationRequest updateRequest = UpdateReservationRequest.builder()
                    .arrivalDate(from.toString())
                    .departureDate(to.toString())
                    .build();

            Reservation reservation = new Reservation();
            reservation.setId(1);
            reservation.setStartDate(today.plusDays(1));
            reservation.setEndDate(to);

            when(reservationRepository.findById(1)).thenReturn(Optional.of(reservation));
            List<LocalDate> dates = new ArrayList<>();
            for(int i=0; i<=2; i++){
                dates.add(today.plusDays(i));
            }
            Availability availability = Availability.builder().availableDates(dates).build();
            when(availabilityManager.findAvailability(any(), any(), any())).thenReturn(availability);

            BadRequestException exception = assertThrows(BadRequestException.class, ()->{
                service.updateReservation(1, updateRequest);
            });

            assertEquals("Fail to complete the reservation. Verify reservation dates availability", exception.getMessage());

        }
    }

    @Nested
    @DisplayName("Given a cancelReservation COMMAND")
    class CancelReservation {

        @Test
        @DisplayName("Test Cancel Reservation")
        void testCancelReservation() {
            LocalDate today = LocalDate.now();
            Reservation reservation = new Reservation();
            reservation.setId(1);
            reservation.setStartDate(today.plusDays(1));
            reservation.setEndDate(today.plusDays(4));

            when(reservationRepository.getById(1)).thenReturn(reservation);
            service.cancelReservation(1);
            verify(reservationRepository, times(1)).delete(reservation);
        }
    }
}