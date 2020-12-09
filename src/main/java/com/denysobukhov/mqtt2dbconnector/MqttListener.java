package com.denysobukhov.mqtt2dbconnector;

import com.denysobukhov.mqtt2dbconnector.dao.EnvironmentMessage;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class MqttListener implements MqttCallback {

    private SessionFactory sessionFactory;

    MqttListener() throws MessageStoreException {
        /*
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.example.xml
                .build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
//            entityManager = sessionFactory.createEntityManager();
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(registry);
            throw new MessageStoreException(e);
        }

        */

        sessionFactory = new Configuration().configure().buildSessionFactory();

    }

    public void connectionLost(Throwable throwable) {
        System.out.println("Connection lost");
        synchronized (ConnectorApplication.lock) {
            ConnectorApplication.lock.notifyAll();
        }
        System.out.println("Signal sent");
    }

    public void messageArrived(String s, MqttMessage mqttMessage) {
        System.out.println("WeatherSensorMessage arrived: " + s + " : " + mqttMessage);

        try {
            EnvironmentMessage message = new EnvironmentMessage(mqttMessage);
            Session session = sessionFactory.openSession();
            session.getTransaction().begin();
            session.persist(message);
            session.getTransaction().commit();
            session.close();

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        System.out.println("Delivery complete");
    }
}
