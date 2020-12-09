package com.denysobukhov.messagestore;


import com.denysobukhov.messagestore.dao.EnvironmentMessage;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConnectorApplicationTest {

    private final String MESSAGE = "<message time=\"2018-10-06 12:42:54 +0300\"><temperature>22.2</temperature><humidity>58.7</humidity><pressure>100529</pressure><voltage>9.1</voltage></message>";

    @Test
    void testMessageConversion() throws Exception {

        MqttListener mqttListener = new MqttListener();
        EnvironmentMessage environmentMessage = new EnvironmentMessage(MESSAGE);

        assertEquals(Timestamp.valueOf("2018-10-06 12:42:54.0").toString(), environmentMessage.getTimestamp().toString());
        assertEquals(new BigDecimal("22.2"), environmentMessage.getTemperature());
        assertEquals(new BigDecimal("58.7"), environmentMessage.getHumidity());
        assertEquals(new BigDecimal("100529"), environmentMessage.getPressure());
        assertEquals(new BigDecimal("9.1"), environmentMessage.getVoltage());
    }

    @Test
    void persistSuccess() throws EnvironmentMessage.MessageException {
        EnvironmentMessage environmentMessage = new EnvironmentMessage(MESSAGE);

    }
}
