package com.denysobukhov.mqtt2dbconnector;

import com.denysobukhov.mqtt2dbconnector.dao.ParameterValue;
import com.denysobukhov.mqtt2dbconnector.dao.SensorMessage;
import com.denysobukhov.mqtt2dbconnector.validators.HumidityValidator;
import com.denysobukhov.mqtt2dbconnector.validators.PressureValidator;
import com.denysobukhov.mqtt2dbconnector.validators.TemperatureValidator;
import com.denysobukhov.mqtt2dbconnector.validators.VoltageValidator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Denis Obukhov  / created on 13 Dec 2020
 */
public class SensorMessageBuilderMqttXmlV1 implements SensorMessageBuilder {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

    @Override
    public Set<SensorMessage> parse(String input) throws BuilderException {
        Set<SensorMessage> messages = new HashSet<>();
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(
                    new ByteArrayInputStream(
                            input.getBytes()
                    ));
            document.getDocumentElement().normalize();

            NodeList nodeList;
            nodeList = document.getElementsByTagName("message");
            if (nodeList.getLength() > 0) {
                final String timeStr = ((Element) nodeList.item(0)).getAttribute("time");
                Timestamp timestamp = new Timestamp(SIMPLE_DATE_FORMAT.parse(timeStr).getTime());
                SensorMessage message = new SensorMessage();
                message.setTimestamp(timestamp);

                List<ParameterValue> parameters = new LinkedList<>();
                message.setParameterValues(parameters);
                Optional.ofNullable(getSensorParameter(document, "temperature"))
                        .filter(v -> new TemperatureValidator().isValid(v.getValue()))
                        .ifPresent(message::addParameterValue);

                Optional.ofNullable(getSensorParameter(document, "humidity"))
                        .filter(v -> new HumidityValidator().isValid(v.getValue()))
                        .ifPresent(message::addParameterValue);

                Optional.ofNullable(getSensorParameter(document, "pressure"))
                        .filter(v -> new PressureValidator().isValid(v.getValue()))
                        .ifPresent(message::addParameterValue);

                Optional.ofNullable(getSensorParameter(document, "voltage"))
                        .filter(v -> new VoltageValidator().isValid(v.getValue()))
                        .ifPresent(message::addParameterValue);

                final String rssiStr = ((Element) nodeList.item(0)).getAttribute("rssi");

                if (!rssiStr.trim().isEmpty()) {
                    try {
                        message.setRssi(Integer.valueOf(rssiStr));
                    } catch (NumberFormatException e) {
                        System.err.printf("Wrong numeric format for rssi parameter: %s%n", rssiStr);
                    }
                }
                final String fromNodeIdStr = ((Element) nodeList.item(0)).getAttribute("from");
                message.setFromNode(fromNodeIdStr);
                final String toNodeIdStr = ((Element) nodeList.item(0)).getAttribute("to");
                message.setToNode(toNodeIdStr);
                messages.add(message);
            }
        } catch (ParserConfigurationException | SAXException | IOException | ParseException e) {
            throw new SensorMessageBuilder.BuilderException("cannot init builder", e);
        }
        return messages;
    }


    private ParameterValue getSensorParameter(Document document, String name) {
        if (document == null) throw new IllegalArgumentException();
        if (name == null) throw new IllegalArgumentException();

        NodeList nodeList;
        nodeList = document.getElementsByTagName(name);
        if (nodeList.getLength() > 0) {
            String value = nodeList.item(0).getTextContent();
            if (value != null) {
                try {
                    return new ParameterValue(name, new BigDecimal(value));
                } catch (NumberFormatException e) {
                    System.err.printf("Wrong numeric format for %s parameter: %s%n", name, value);
                }
            }
        }
        return null;
    }
}
