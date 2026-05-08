package io.github.denysobukh.mqtt2dbconnector.model;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Database entity representing one message received from a sensor node.
 * <p>
 * A message stores routing metadata and owns a collection of measured parameter
 * values such as temperature, humidity, pressure, and voltage.
 *
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


    /**
     * Adds a parameter value and updates the reverse entity relationship.
     *
     * @param value parameter value to attach to this message
     */
    public void addParameterValue(ParameterValue value) {
        parameterValues.add(value);
        value.setSensorMessage(this);
    }

    /**
     * Removes a parameter value and clears the reverse entity relationship.
     *
     * @param value parameter value to detach from this message
     */
    public void removeParameterValue(ParameterValue value) {
        parameterValues.remove(value);
        value.setSensorMessage(null);
    }
}
