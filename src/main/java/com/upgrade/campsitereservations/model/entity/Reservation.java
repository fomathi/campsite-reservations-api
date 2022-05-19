package com.upgrade.campsitereservations.model.entity;

import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "reservation")
@Data
public class Reservation implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(columnDefinition = "DATE")
    private LocalDate startDate;

    @Column(columnDefinition = "DATE")
    private LocalDate endDate;

    @ManyToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name="email", nullable=false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Visitor visitor;
}
