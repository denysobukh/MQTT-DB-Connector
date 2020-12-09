package com.denysobukhov.messagestore;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class ConnectorApplication {

    static final Object lock = new Object();
    private static final String TOPIC = "#";
    private static final String BROKER = "tcp://nas.loc:1883";
    private static final String CLIENT_ID = "MessageStore";

    public static void main(String[] args) throws MqttException {

        MemoryPersistence persistence = new MemoryPersistence();
        MqttClient mqttClient = null;
        try {
            mqttClient = new MqttClient(BROKER, CLIENT_ID, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            int maxAttempts = 10;
            for (int i = 0; i < maxAttempts; i++) {
                try {
                    System.out.println("Connecting to broker (attempt " + i + " of " + maxAttempts + "): " + BROKER);
                    mqttClient.connect(connOpts);
                    System.out.println("Connected");
                    break;
                } catch (MqttException me) {
                    System.out.println("Can not connect: " + me.getMessage());
                }
            }

            if (mqttClient.isConnected()) {
                mqttClient.setCallback(new MqttListener());
                mqttClient.subscribe(TOPIC);
                System.out.println("Subscribed to " + TOPIC);
                try {
                    synchronized (lock) {
                        lock.wait();
                        System.out.println("Disconnecting");
                    }
                } catch (InterruptedException ie) {
                    System.out.println("InterruptedException");
                }
            } else {
                System.out.println("Cannot connect.");
                System.exit(-1);
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