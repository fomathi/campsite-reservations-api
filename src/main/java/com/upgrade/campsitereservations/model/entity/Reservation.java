package com.upgrade.campsitereservations.model.entity;

import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

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

    @ElementCollection
    @CollectionTable(name = "reservation_dates",
            uniqueConstraints = {@UniqueConstraint(name="uniqueReservationDates", columnNames = {"dates"})})
    protected List<LocalDate> dates;

    @ManyToOne(fetch= FetchType.LAZY, optional = false)
    @JoinColumn(name="email", nullable=false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Visitor visitor;
}
