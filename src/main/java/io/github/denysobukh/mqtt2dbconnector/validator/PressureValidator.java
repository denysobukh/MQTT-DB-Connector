package io.github.denysobukh.mqtt2dbconnector.validator;

import java.math.BigDecimal;

/**
 * Accepts atmospheric pressure values in the inclusive range 96,000 to 110,000
 * pascals.
 *
 * @author Denis Obukhov  / created on 21 Dec 2020
 */
public class PressureValidator implements ValidationCondition{
    /**
     * Checks whether the pressure value is within the accepted range.
     *
     * @param v pressure value in pascals
     * @return {@code true} when the value should be stored
     */
    @Override
    public boolean isValid(BigDecimal v) {
        return v.compareTo(BigDecimal.valueOf(96000)) >= 0 && v.compareTo(BigDecimal.valueOf(110000)) <= 0;
    }
}
