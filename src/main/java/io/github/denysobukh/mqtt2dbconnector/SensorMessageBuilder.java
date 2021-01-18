package io.github.denysobukh.mqtt2dbconnector;

import io.github.denysobukh.mqtt2dbconnector.model.SensorMessage;

import java.util.Set;

/**
 * @author Denis Obukhov  / created on 13 Dec 2020
 */
public interface SensorMessageBuilder {
    Set<SensorMessage> parse(String input) throws BuilderException;

    public class BuilderException extends Throwable {
        public BuilderException(String s, Exception e) {
            super(s, e);
        }
    }
}
