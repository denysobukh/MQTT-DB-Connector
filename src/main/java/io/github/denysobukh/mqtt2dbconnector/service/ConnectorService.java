package io.github.denysobukh.mqtt2dbconnector.service;

import io.github.denysobukh.mqtt2dbconnector.MqttListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Connects to the configured MQTT broker when the Spring application is ready
 * and subscribes the application listener to the configured topic.
 * <p>
 * The service retries failed connections with exponential backoff and uses a
 * persistent MQTT session so queued QoS messages can survive reconnects.
 */
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "mqtt.enabled", havingValue = "true", matchIfMissing = true)
@PropertySource("classpath:application.properties")
@Service
public class ConnectorService implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * Monitor used to block the connector thread while the MQTT subscription is
     * active and wake it after a connection loss.
     */
    public static final Object lock = new Object();

    @Value("${mqtt.broker}")
    private String broker;
    @Value("${mqtt.topic}")
    private String topic;
    @Value("${mqtt.clientid}")
    private String clientId;

    private final MqttListener mqttListener;

    /**
     * Opens the MQTT connection, subscribes to the configured topic, and keeps
     * retrying when the broker cannot be reached.
     *
     * @param event Spring Boot readiness event
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        log.info("connecting {} {} as {}", broker, topic, clientId);

        MemoryPersistence persistence = new MemoryPersistence();
        MqttClient mqttClient = null;
        try {
            mqttClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(false);
            connOpts.setConnectionTimeout(5);
            int attemptDelayInitSec = 1;
            int attemptDelayNextSec = attemptDelayInitSec;

            for (int attempt = 1; ; attempt++) {
                try {
                    log.info("Connecting to broker (attempt " + attempt + "): " + broker);
                    mqttClient.connect(connOpts);
                    log.info("Connected");

                    attemptDelayNextSec = attemptDelayInitSec;

                    if (mqttClient.isConnected()) {
                        mqttClient.setManualAcks(true);
                        mqttClient.setCallback(mqttListener);
                        mqttClient.subscribe(topic, 2);
                        try {
                            synchronized (lock) {
                                log.info("Subscribed to " + topic);
                                lock.wait();
                            }
                        } catch (InterruptedException ie) {
                            log.info("InterruptedException");
                        }
                    }

                } catch (MqttException me) {
                    log.error("Can not connect: {}", me.getCause().getMessage());
                    log.info("Next attempt in {} secs ", attemptDelayNextSec);
                    TimeUnit.SECONDS.sleep(attemptDelayNextSec);
                    attemptDelayNextSec *= 2;
                }
            }
        } catch (MqttException | InterruptedException e) {
            log.error("Cannot connect:", e);
        } finally {
            if (mqttClient != null && mqttClient.isConnected()) {
                try {
                    mqttClient.disconnect();
                } catch (MqttException e) {
                    log.error("Can't disconnect", e);
                }
                log.info("Disconnected");
            }
        }
    }
}
