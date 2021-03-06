package com.upgrade.campsitereservations.repository;

import com.upgrade.campsitereservations.model.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    @Query(value = "SELECT * FROM campsite_reservation.reservation WHERE start_date BETWEEN NOW() and NOW()+30 AND end_date <= NOW()+30", nativeQuery = true)
    List<Reservation> findNext30DaysReservations();

    Optional<Reservation> findById(Integer id);
}
