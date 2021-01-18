package io.github.denysobukh.mqtt2dbconnector;

import io.github.denysobukh.mqtt2dbconnector.model.ParameterName;
import io.github.denysobukh.mqtt2dbconnector.model.ParameterValue;
import io.github.denysobukh.mqtt2dbconnector.model.SensorMessage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * @author Denis Obukhov  / created on 09 Dec 2020
 */
public class LoadFromFileApplication {

    private SensorMessageBuilder messageBuilder = new SensorMessageBuilderMqttXmlV1();

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length == 0) throw new IllegalArgumentException("no file parameter given");
        final String fileName = args[0];

        final LoadFromFileApplication loadFromFileApplication = new LoadFromFileApplication();
        HashMap<Timestamp, SensorMessage> fileMessages = loadFromFileApplication.getMessages(fileName);
        System.out.printf("found %d messages%n", fileMessages.size());
        System.out.printf("inserted %d records%n", loadFromFileApplication.insertIfNotExist(fileMessages));
    }

    private HashMap<Timestamp, SensorMessage> getMessages(String fileName) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(fileName));
        HashMap<Timestamp, SensorMessage> fileMessages = new HashMap<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                try {
                    Set<SensorMessage> messages = messageBuilder.parse(line);
                    if (!messages.isEmpty()) {
                        final SensorMessage m = messages.iterator().next();
                        fileMessages.putIfAbsent(m.getTimestamp(), m);
                    }
                } catch (SensorMessageBuilder.BuilderException e) {
                    e.printStackTrace();
                }
            }
        }
        return fileMessages;
    }

    private int insertIfNotExist(HashMap<Timestamp, SensorMessage> messages) {

        HashMap<String, ParameterName> nameIdsCache = new HashMap<>();

        final Configuration configuration = new Configuration();
        configuration.configure();
        configuration.setProperty("hibernate.show_sql", "false");
        configuration.setProperty("hibernate.format_sql", "false");
        configuration.setProperty("org.hibernate.SQL", "INFO");
        final SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.openSession();
        // Create CriteriaBuilder
        CriteriaBuilder builder = session.getCriteriaBuilder();
        int insertCounter = 0;
        for (SensorMessage m : messages.values()) {
            // Create CriteriaQuery
            CriteriaQuery<SensorMessage> criteria = builder.createQuery(SensorMessage.class);
            final Root<SensorMessage> root = criteria.from(SensorMessage.class);
            criteria.select(root);
            criteria.where(builder.equal(root.get("timestamp"), m.getTimestamp()));
            List<SensorMessage> existMessage = session.createQuery(criteria).getResultList();
            if (existMessage.size() < 1) {

                session.beginTransaction();

                for (ParameterValue p : m.getParameterValues()) {
                    final ParameterName parameterName = p.getParameterName();

                    ParameterName nameId = nameIdsCache.get(p.getParameterName().getName());

                    if (nameId == null) {
                        Query<ParameterName> query = session.createQuery(
                                "from ParameterName n where n.name=:name", ParameterName.class);
                        query.setParameter("name", parameterName.getName());
                        nameId = query.uniqueResult();
                        if (nameId != null) {
                            nameIdsCache.put(nameId.getName(), nameId);
                        }
                    }

                    if (nameId == null) {
                        session.saveOrUpdate(parameterName);
                    } else {
                        p.setParameterName(nameId);
                    }
                }
                session.persist(m);

                session.getTransaction().commit();
                insertCounter++;
            }
        }
        session.close();
        sessionFactory.close();
        return insertCounter;
    }
}
