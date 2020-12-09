package com.denysobukhov.messagestore;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Application {

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

            int n = 1, max_attempts = 10;
            for (; ; ) {
                try {
                    System.out.println("Connecting to broker (attempt " + n + " of " + max_attempts + "): " + BROKER);
                    mqttClient.connect(connOpts);
                    System.out.println("Connected");
                    break;
                } catch (MqttException me) {
                    System.out.println("Can not connect: " + me.getMessage());
                } finally {
                    n++;
                    if (n > max_attempts) {
                        System.out.println("Unable to connect");
                        System.exit(-1);
                    }
                }
            }

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


//            System.out.println("\r\nPress Enter to exit");
//            System.in.read();

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