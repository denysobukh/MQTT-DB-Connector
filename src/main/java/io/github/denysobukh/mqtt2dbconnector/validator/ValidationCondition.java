package io.github.denysobukh.mqtt2dbconnector.validator;

import java.math.BigDecimal;

/**
 * Predicate for deciding whether a parsed sensor parameter value should be
 * stored.
 *
 * @author Denis Obukhov  / created on 21 Dec 2020
 */
@FunctionalInterface
public  interface ValidationCondition {
    /**
     * Evaluates a parsed parameter value.
     *
     * @param value numeric value parsed from the MQTT payload
     * @return {@code true} when the value is accepted
     */
    boolean isValid(final BigDecimal value);
}
