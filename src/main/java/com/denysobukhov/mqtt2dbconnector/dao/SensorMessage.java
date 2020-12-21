package com.denysobukhov.mqtt2dbconnector.dao;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Denis Obukhov  / created on 13 Dec 2020
 */
@Entity
@Table(name = "messages")
public class SensorMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Integer rssi;
    private Timestamp timestamp;
    private String fromNode;
    private String toNode;

    @OneToMany(
            mappedBy = "sensorMessage",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ParameterValue> parameterValues = new ArrayList<>();

    public SensorMessage() {
    }

    public List<ParameterValue> getParameterValues() {
        return parameterValues;
    }

    public void setParameterValues(List<ParameterValue> parameterValues) {
        this.parameterValues = parameterValues;
    }

    public String getFromNode() {
        return fromNode;
    }

    public void setFromNode(String fromNode) {
        this.fromNode = fromNode;
    }

    public String getToNode() {
        return toNode;
    }

    public void setToNode(String toNode) {
        this.toNode = toNode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getRssi() {
        return rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }


    public void addParameterValue(ParameterValue value) {
        parameterValues.add(value);
        value.setSensorMessage(this);
    }

    public void removeParameterValue(ParameterValue value) {
        parameterValues.remove(value);
        value.setSensorMessage(null);
    }
}
