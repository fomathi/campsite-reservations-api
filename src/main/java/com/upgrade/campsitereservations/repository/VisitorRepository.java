package com.upgrade.campsitereservations.repository;

import com.upgrade.campsitereservations.model.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VisitorRepository extends JpaRepository<Visitor, String> {
    Optional<Visitor> findByEmail(String email);
}
