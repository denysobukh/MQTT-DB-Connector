package com.denysobukhov.mqtt2dbconnector;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.TimeUnit;

public class ConnectorApplication {

    static final Object lock = new Object();
    private static final String TOPIC = "sensor/weather";
    private static final String BROKER = "tcp://nas.loc:1883";
    private static final String CLIENT_ID = "messages_processing";

    public static void main(String[] args) throws MqttException, InterruptedException {

        MemoryPersistence persistence = new MemoryPersistence();
        MqttClient mqttClient = null;
        try {
            mqttClient = new MqttClient(BROKER, CLIENT_ID, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(false);

            int attemptDelayInitSec = 1;
            int attemptDelayNextSec = attemptDelayInitSec;

            for (int attempt = 0;; attempt++) {
                try {
                    System.out.println("Connecting to broker (attempt " + attempt + "): " + BROKER);
                    mqttClient.connect(connOpts);
                    System.out.println("Connected");

                    attemptDelayNextSec = attemptDelayInitSec;

                    if (mqttClient.isConnected()) {
                        mqttClient.setManualAcks(false);
                        mqttClient.setCallback(new MqttListener());
                        mqttClient.subscribe(TOPIC, 2);
                        try {
                            synchronized (lock) {
                                System.out.println("Subscribed to " + TOPIC);
                                lock.wait();
                            }
                        } catch (InterruptedException ie) {
                            System.out.println("InterruptedException");
                        }
                    }

                } catch (MqttException me) {
                    System.out.println("Can not connect: " + me.getMessage());
                    System.out.println("Waiting for " + attemptDelayNextSec);
                    TimeUnit.SECONDS.sleep(attemptDelayInitSec);
                    attemptDelayNextSec *= 2;
                }
            }
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        } catch (MessageStoreException e) {
            e.printStackTrace();
        } finally {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
                System.out.println("Disconnected");
            }
        }

        System.exit(0);
    }


}