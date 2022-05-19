package com.upgrade.campsitereservations.model.entity;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "visitor")
@Data
public class Visitor {

    @Id
    @Column(name = "email")
    private String email;

    @Column
    private String fullName;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="email")
    private Set<Reservation> reservations;

}
