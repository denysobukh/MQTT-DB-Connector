package io.github.denysobukh.mqtt2dbconnector.model;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author Denis Obukhov  / created on 13 Dec 2020
 */
@Entity
@Table(name = "parameter_value")
public class ParameterValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private SensorMessage sensorMessage;

    @OneToOne(cascade = CascadeType.ALL)
    private ParameterName parameterName;

    private BigDecimal value;

    public ParameterValue() {
    }

    public ParameterValue(String name) {
        this.parameterName = new ParameterName(name);
    }

    public ParameterValue(String name, BigDecimal value) {
        this.parameterName = new ParameterName(name);
        this.value = value;
    }

    public SensorMessage getSensorMessage() {
        return sensorMessage;
    }

    public void setSensorMessage(SensorMessage sensorMessage) {
        this.sensorMessage = sensorMessage;
    }

    public long getId() {
        return id;
    }

    public ParameterName getParameterName() {
        return parameterName;
    }

    public void setParameterName(ParameterName parameterName) {
        this.parameterName = parameterName;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ParameterName getSensorParameterName() {
        return parameterName;
    }

    public void setSensorParameterName(ParameterName parameterName) {
        this.parameterName = parameterName;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
