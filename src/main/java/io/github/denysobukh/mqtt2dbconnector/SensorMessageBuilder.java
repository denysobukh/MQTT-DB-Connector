package io.github.denysobukh.mqtt2dbconnector;

import io.github.denysobukh.mqtt2dbconnector.model.SensorMessage;

import java.util.Set;

/**
 * Converts an incoming MQTT payload into one or more sensor messages.
 *
 * @author Denis Obukhov  / created on 13 Dec 2020
 */
public interface SensorMessageBuilder {
    /**
     * Parses a raw MQTT payload.
     *
     * @param input message payload to parse
     * @return parsed sensor messages; empty when the payload does not contain a
     * supported message element
     * @throws BuilderException when the payload cannot be parsed
     */
    Set<SensorMessage> parse(String input) throws BuilderException;

    /**
     * Signals that a payload could not be converted to sensor messages.
     */
    public class BuilderException extends Throwable {
        /**
         * Creates a parser exception with context and the original cause.
         *
         * @param s message describing the parse failure
         * @param e original parser exception
         */
        public BuilderException(String s, Exception e) {
            super(s, e);
        }
    }
}
