package io.github.denysobukh.mqtt2dbconnector.validator;

import java.math.BigDecimal;

/**
 * Accepts temperature values in the inclusive range -20 to 40 degrees Celsius.
 *
 * @author Denis Obukhov  / created on 21 Dec 2020
 */
public class TemperatureValidator implements ValidationCondition {
    /**
     * Checks whether the temperature value is within the accepted range.
     *
     * @param v temperature value in degrees Celsius
     * @return {@code true} when the value should be stored
     */
    @Override
    public boolean isValid(BigDecimal v) {
        return v.compareTo(BigDecimal.valueOf(-20)) >= 0 && v.compareTo(BigDecimal.valueOf(40)) <= 0;
    }
}
