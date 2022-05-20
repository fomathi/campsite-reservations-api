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
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private static final String INVALID_DATE = "Invalid reservation request";
    private static final String INVALID_RESERVATION_START_DATE = "The campsite can be reserved minimum 1 day(s) ahead of arrival and up to 1 month in advance";
    private static final String MAX_RESERVATION_DATES = "The campsite can be reserved for max 3 days";
    private static final String ADD_RESERVATION_FAIL = "Fail to complete the reservation. Verify reservation dates availability";
    private static final String RESERVATION_NOT_FOUND = "Reservation not found";
    private final ReservationConfig config;
    private final Validator validator;
    private final CampsiteAvailabilityManager availabilityManager;
    private final ReservationRepository reservationRepository;
    private final VisitorRepository visitorRepository;

    public ReservationService(ReservationConfig config,
                              Validator validator,
                              CampsiteAvailabilityManager availabilityManager,
                              ReservationRepository reservationRepository,
                              VisitorRepository visitorRepository) {
        this.config = config;
        this.validator = validator;
        this.availabilityManager = availabilityManager;
        this.reservationRepository = reservationRepository;
        this.visitorRepository = visitorRepository;
    }

    @Transactional
    public ReservationResponse addReservation(ReservationRequest request) {
        String from = request.getArrivalDate();
        String to = request.getDepartureDate();
        if(!validator.isAvailabilityRequestValid(from, to)) {
            throw new BadRequestException(INVALID_DATE);
        }
        if(!validator.isReservationStartDateValid(from, config.getMinDaysBeforeStart(), config.getMaxDaysBeforeStart())) {
            throw new BadRequestException(INVALID_RESERVATION_START_DATE);
        }
        if(!validator.isReservationLessThan4Days(from, to, config.getMaxReservationDays())) {
            throw new BadRequestException(MAX_RESERVATION_DATES);
        }

        LocalDate startDate = LocalDate.parse(from);
        LocalDate endDate = LocalDate.parse(to);
        Availability availability = availabilityManager.findAvailability(startDate, endDate);
        List<LocalDate> reservationsDates = getReservationsDates(startDate, endDate);

        if(checkReservationDatesAvailabilities(availability.getAvailableDates(), reservationsDates)) {
            Optional<Visitor> optionalVisitor = visitorRepository.findByEmail(request.getEmail());
            Visitor visitorRecord;
            if(optionalVisitor.isPresent()) {
                visitorRecord = optionalVisitor.get();
            } else {
                Visitor visitor = new Visitor();
                visitor.setEmail(request.getEmail());
                visitor.setFullName(request.getFullName());
                visitorRecord = visitorRepository.save(visitor);
            }

            Reservation reservation = new Reservation();
            reservation.setStartDate(startDate);
            reservation.setEndDate(endDate);
            reservation.setDates(reservationsDates);
            reservation.setVisitor(visitorRecord);

            Reservation reservationRecord  = reservationRepository.save(reservation);
            return ReservationResponse.builder()
                    .id(reservationRecord.getId().toString())
                    .from(from)
                    .to(to)
                    .build();
        }

        throw new BadRequestException(ADD_RESERVATION_FAIL);
    }

    @Transactional
    public ReservationResponse updateReservation(int reservationId, UpdateReservationRequest request) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if(optionalReservation.isPresent()) {
            String from = request.getArrivalDate();
            String to = request.getDepartureDate();
            LocalDate startDate = LocalDate.parse(from);
            LocalDate endDate = LocalDate.parse(to);

            Reservation reservationRecord = optionalReservation.get();
            List<LocalDate> oldReservationsDates = getReservationsDates(reservationRecord.getStartDate(), reservationRecord.getEndDate());
            Availability availability = availabilityManager.findAvailability(startDate, endDate, oldReservationsDates);
            List<LocalDate> reservationsDates = getReservationsDates(startDate, endDate);
            List<LocalDate> availableDates = availability.getAvailableDates();

            if(checkReservationDatesAvailabilities(availableDates, reservationsDates)) {
                reservationRecord.setStartDate(startDate);
                reservationRecord.setEndDate(endDate);
                reservationRecord.setDates(reservationsDates);
                reservationRepository.save(reservationRecord);
                return ReservationResponse.builder()
                        .id(reservationRecord.getId().toString())
                        .from(from)
                        .to(to)
                        .build();
            }
            throw new BadRequestException(ADD_RESERVATION_FAIL);

        } else {
            throw new NotFoundException(RESERVATION_NOT_FOUND);
        }
    }

    public void cancelReservation(int reservationId) {
        Reservation reservation = reservationRepository.getById(reservationId);
        reservationRepository.delete(reservation);
    }

    private List<LocalDate> getReservationsDates(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> reservationsDates = new ArrayList<>();
        int count = 0;
        while(!startDate.plusDays(count).isAfter(endDate)) {
            reservationsDates.add(startDate.plusDays(count));
            count++;
        }
        return reservationsDates;
    }

    private boolean checkReservationDatesAvailabilities(List<LocalDate> availableDates, List<LocalDate> reservationsDates) {
        for(LocalDate date: reservationsDates){
            if(!availableDates.contains(date)) {
                return false;
            }
        }
        return true;
    }
}
