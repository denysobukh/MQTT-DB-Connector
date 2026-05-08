package io.github.denysobukh.mqtt2dbconnector.validator;

import java.math.BigDecimal;

/**
 * Accepts relative humidity values in the inclusive range 0 to 101 percent.
 *
 * @author Denis Obukhov  / created on 21 Dec 2020
 */
public class HumidityValidator implements ValidationCondition {
    /**
     * Checks whether the humidity value is within the accepted range.
     *
     * @param v humidity value
     * @return {@code true} when the value should be stored
     */
    @Override
    public boolean isValid(BigDecimal v) {
        return v.compareTo(BigDecimal.valueOf(0)) >= 0 && v.compareTo(BigDecimal.valueOf(101)) <= 0;
    }
}
