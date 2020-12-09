package com.denysobukhov.mqtt2dbconnector;

import com.denysobukhov.mqtt2dbconnector.dao.EnvironmentMessage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * @author Denis Obukhov  / created on 09 Dec 2020
 */
public class LoadFromFileApplication {

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length == 0) throw new IllegalArgumentException("no file parameter given");
        final String fileName = args[0];

        final LoadFromFileApplication loadFromFileApplication = new LoadFromFileApplication();
        HashMap<Timestamp, EnvironmentMessage> fileMessages = loadFromFileApplication.getMessages(fileName);
        System.out.printf("found %d messages%n", fileMessages.size());
        System.out.printf("inserted %d records%n", loadFromFileApplication.insertIfNotExist(fileMessages));
    }

    private HashMap<Timestamp, EnvironmentMessage> getMessages(String fileName) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(fileName));
        HashMap<Timestamp, EnvironmentMessage> fileMessages = new HashMap<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                try {
                    EnvironmentMessage message = new EnvironmentMessage(line);
                    fileMessages.putIfAbsent(message.getTimestamp(), message);
                } catch (EnvironmentMessage.MessageException e) {
                    e.printStackTrace();
                }
            }
        }
        return fileMessages;
    }

    private int insertIfNotExist(HashMap<Timestamp, EnvironmentMessage> messages) {
        final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();
        // Create CriteriaBuilder
        CriteriaBuilder builder = session.getCriteriaBuilder();
        int insertCounter = 0;
        for (EnvironmentMessage message : messages.values()) {
            // Create CriteriaQuery
            CriteriaQuery<EnvironmentMessage> criteria = builder.createQuery(EnvironmentMessage.class);
            final Root<EnvironmentMessage> root = criteria.from(EnvironmentMessage.class);
            criteria.select(root);
            criteria.where(builder.equal(root.get("timestamp"), message.getTimestamp()));
            List<EnvironmentMessage> existMessage = session.createQuery(criteria).getResultList();
            if (existMessage.size() < 1) {
                session.beginTransaction();
                session.saveOrUpdate(message);
                session.getTransaction().commit();
                insertCounter++;
            }
        }
        session.close();
        sessionFactory.close();
        return insertCounter;
    }
}
