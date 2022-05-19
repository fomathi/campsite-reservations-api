package com.upgrade.campsitereservations.repository;

import com.upgrade.campsitereservations.model.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    @Query(value = "SELECT * FROM campsite_reservation.reservation WHERE start_date BETWEEN NOW()+1 and NOW()+30 AND end_date <= NOW()+30", nativeQuery = true)
    List<Reservation> findNext30DaysReservations();
}
