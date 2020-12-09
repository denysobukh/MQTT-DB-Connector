package com.denysobukhov.messagestore;

import com.denysobukhov.messagestore.dao.WeatherSensorMessage;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.xml.parsers.DocumentBuilder;

public class MqttListener implements MqttCallback {

    private SessionFactory sessionFactory;

    private DocumentBuilder documentBuilder;


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
        synchronized (Application.lock) {
            Application.lock.notifyAll();
        }
        System.out.println("Signal sent");
    }

    public void messageArrived(String s, MqttMessage mqttMessage) {
        System.out.println("WeatherSensorMessage arrived: " + s + " : " + mqttMessage);

        try {
            WeatherSensorMessage message = new WeatherSensorMessage(mqttMessage);

            System.out.println(message);
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
