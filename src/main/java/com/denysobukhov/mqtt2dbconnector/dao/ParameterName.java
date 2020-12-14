package com.denysobukhov.mqtt2dbconnector.dao;

import javax.persistence.*;

/**
 * @author Denis Obukhov  / created on 13 Dec 2020
 */
@Entity
@Table(name = "names")
public class ParameterName {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String name;


    public ParameterName() {
    }

    public ParameterName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
