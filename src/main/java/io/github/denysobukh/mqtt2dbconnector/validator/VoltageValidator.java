package io.github.denysobukh.mqtt2dbconnector.validator;

import java.math.BigDecimal;

/**
 * Accepts voltage values in the inclusive range 0 to 10 volts.
 *
 * @author Denis Obukhov  / created on 21 Dec 2020
 */
public class VoltageValidator implements ValidationCondition {
    /**
     * Checks whether the voltage value is within the accepted range.
     *
     * @param v voltage value
     * @return {@code true} when the value should be stored
     */
    @Override
    public boolean isValid(BigDecimal v) {
        return v.compareTo(BigDecimal.valueOf(0)) >= 0 && v.compareTo(BigDecimal.valueOf(10)) <= 0;
    }
}
