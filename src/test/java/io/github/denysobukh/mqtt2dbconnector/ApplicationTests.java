package io.github.denysobukh.mqtt2dbconnector;

import io.github.denysobukh.mqtt2dbconnector.model.SensorMessage;
import io.github.denysobukh.mqtt2dbconnector.exception.MessageStoreException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
class ApplicationTests {

	private final String MESSAGE = "<message time=\"2018-10-06 12:42:54 +0300\"><temperature>22.2</temperature><humidity>58.7</humidity><pressure>100529</pressure><voltage>9.1</voltage></message>";
	private final SensorMessageBuilder builder = new SensorMessageBuilderMqttXmlV1();

	@Test
	void contextLoads() throws MessageStoreException, SensorMessageBuilder.BuilderException {
		final Set<SensorMessage> messages = builder.parse(MESSAGE);
		assertEquals(messages.size(), 1);
		SensorMessage m = messages.iterator().next();

		assertEquals(Timestamp.valueOf("2018-10-06 12:42:54.0").toString(), m.getTimestamp().toString());
		assertEquals(new BigDecimal("22.2"), m.getParameterValues().stream()
				.filter(v -> v.getParameterName().getName().equals("temperature")).findFirst().get()
				.getValue());
		assertEquals(new BigDecimal("58.7"), m.getParameterValues().stream()
				.filter(v -> v.getParameterName().getName().equals("humidity")).findFirst().get()
				.getValue());
		assertEquals(new BigDecimal("100529"), m.getParameterValues().stream()
				.filter(v -> v.getParameterName().getName().equals("pressure")).findFirst().get()
				.getValue());
		assertEquals(new BigDecimal("9.1"), m.getParameterValues().stream()
				.filter(v -> v.getParameterName().getName().equals("voltage")).findFirst().get()
				.getValue());
	}

}
