package io.github.denysobukh.mqtt2dbconnector.service;

import io.github.denysobukh.mqtt2dbconnector.MqttListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
@Service
public class ConnectorService implements ApplicationListener<ApplicationReadyEvent> {

    public static final Object lock = new Object();

    @Value("${mqtt.broker}")
    private String broker;
    @Value("${mqtt.topic}")
    private String topic;
    @Value("${mqtt.clientid}")
    private String clientId;

    private final MqttListener mqttListener;

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