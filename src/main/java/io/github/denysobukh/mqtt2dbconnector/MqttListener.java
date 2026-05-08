package io.github.denysobukh.mqtt2dbconnector;

import io.github.denysobukh.mqtt2dbconnector.model.ParameterName;
import io.github.denysobukh.mqtt2dbconnector.model.ParameterValue;
import io.github.denysobukh.mqtt2dbconnector.model.SensorMessage;
import io.github.denysobukh.mqtt2dbconnector.service.ConnectorService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import java.util.Set;

/**
 * Receives MQTT messages from Eclipse Paho, parses them, and persists accepted
 * sensor measurements to the database.
 */
@AllArgsConstructor
@Slf4j
@Component
public class MqttListener implements MqttCallback {

    @PersistenceUnit
    private EntityManagerFactory emf;

    protected SessionFactory getSessionFactory() {
        return emf.unwrap(SessionFactory.class);
    }

    /**
     * Logs MQTT connection loss and wakes the connector service so it can start
     * a reconnect cycle.
     *
     * @param t cause reported by the MQTT client
     */
    public void connectionLost(Throwable t) {
        log.error("Connection lost", t);
        synchronized (ConnectorService.lock) {
            ConnectorService.lock.notifyAll();
        }
    }

    /**
     * Parses one MQTT payload and stores the resulting sensor messages.
     * Existing parameter names are reused to satisfy the unique constraint on
     * {@code parameter_name.name}.
     *
     * @param s topic that delivered the message
     * @param mqttMessage raw MQTT message payload and metadata
     */
    public void messageArrived(String s, MqttMessage mqttMessage) {
        log.info("WeatherSensorMessage arrived: " + s + " : " + mqttMessage);

        try {
            Session session = getSessionFactory().openSession();
            session.getTransaction().begin();
            session.getTransaction().commit();

            SensorMessageBuilder messageBuilder = new SensorMessageBuilderMqttXmlV1();
            final Set<SensorMessage> sensorMessages = messageBuilder.parse(mqttMessage.toString());


            session.getTransaction().begin();
            for (SensorMessage m : sensorMessages) {
                for (ParameterValue p : m.getParameterValues()) {
                    final ParameterName parameterName = p.getParameterName();

                    Query<ParameterName> query = session.createQuery(
                            "from ParameterName n where n.name=:name", ParameterName.class);

                    query.setParameter("name", parameterName.getName());
                    ParameterName nameResult = query.uniqueResult();
                    if (nameResult != null) {
                        p.setParameterName(nameResult);
                    } else {
                        session.persist(parameterName);
                    }
                }
                session.persist(m);
            }
            session.getTransaction().commit();

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Logs completion of MQTT message delivery for outbound messages.
     *
     * @param iMqttDeliveryToken delivery token supplied by the MQTT client
     */
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        log.info("Delivery complete");
    }
}
