package com.denysobukhov.mqtt2dbconnector.validators;

import java.math.BigDecimal;

/**
 * @author Denis Obukhov  / created on 21 Dec 2020
 */
public class TemperatureValidator implements ValidationCondition {
    @Override
    public boolean isValid(BigDecimal v) {
        return v.compareTo(BigDecimal.valueOf(-20)) >= 0 && v.compareTo(BigDecimal.valueOf(40)) <= 0;
    }
}
