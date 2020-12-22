package com.denysobukhov.mqtt2dbconnector;

import com.denysobukhov.mqtt2dbconnector.dao.ParameterName;
import com.denysobukhov.mqtt2dbconnector.dao.ParameterValue;
import com.denysobukhov.mqtt2dbconnector.dao.SensorMessage;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.Set;

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
            Session session = sessionFactory.openSession();
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
                        session.saveOrUpdate(parameterName);
                    }
                }
                session.persist(m);
            }
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
