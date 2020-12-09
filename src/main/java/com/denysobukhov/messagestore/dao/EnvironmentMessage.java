package com.denysobukhov.messagestore.dao;


import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.persistence.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.StringJoiner;

@Entity
@Table(name = "weather_sensor_messages")
public class EnvironmentMessage implements Serializable {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Timestamp timestamp;
    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal pressure;
    private BigDecimal voltage;

    /**
     * Creates message from xml string
     *
     * @param mqttMessage
     * @throws MessageException
     */
    public EnvironmentMessage(String mqttMessage) throws MessageException {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(
                    new ByteArrayInputStream(
                            mqttMessage.getBytes()
                    ));
            document.getDocumentElement().normalize();

            NodeList nodeList;
            nodeList = document.getElementsByTagName("message");
            if (nodeList.getLength() > 0) {
                final String timeStr = ((Element) nodeList.item(0)).getAttribute("time");
                timestamp = new Timestamp(SIMPLE_DATE_FORMAT.parse(timeStr).getTime());
            }

            nodeList = document.getElementsByTagName("temperature");
            if (nodeList.getLength() > 0) {
                String value = nodeList.item(0).getTextContent();
                if (value != null) {
                    temperature = new BigDecimal(value);
                }
            }

            nodeList = document.getElementsByTagName("humidity");
            if (nodeList.getLength() > 0) {
                String value = nodeList.item(0).getTextContent();
                if (value != null) {
                    humidity = new BigDecimal(value);
                }
            }

            nodeList = document.getElementsByTagName("pressure");
            if (nodeList.getLength() > 0) {
                String value = nodeList.item(0).getTextContent();
                if (value != null) {
                    pressure = new BigDecimal(value);
                }
            }

            nodeList = document.getElementsByTagName("voltage");
            if (nodeList.getLength() > 0) {
                String value = nodeList.item(0).getTextContent();
                if (value != null) {
                    voltage = new BigDecimal(value);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException | ParseException e) {
            throw new MessageException("cannot init builder", e);
        }
    }


    public EnvironmentMessage(Timestamp timestamp, BigDecimal temperature, BigDecimal humidity, BigDecimal pressure, BigDecimal voltage) {
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        this.voltage = voltage;
    }

    public EnvironmentMessage(MqttMessage mqttMessage) throws MessageException {
        this(mqttMessage.toString());
    }

    public EnvironmentMessage() {
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }

    public BigDecimal getHumidity() {
        return humidity;
    }

    public void setHumidity(BigDecimal humidity) {
        this.humidity = humidity;
    }

    public BigDecimal getPressure() {
        return pressure;
    }

    public void setPressure(BigDecimal pressure) {
        this.pressure = pressure;
    }

    public BigDecimal getVoltage() {
        return voltage;
    }

    public void setVoltage(BigDecimal voltage) {
        this.voltage = voltage;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", EnvironmentMessage.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("timestamp=" + timestamp)
                .add("temperature=" + temperature)
                .add("humidity=" + humidity)
                .add("pressure=" + pressure)
                .add("voltage=" + voltage)
                .toString();
    }

    public class MessageException extends Exception {
        public MessageException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
