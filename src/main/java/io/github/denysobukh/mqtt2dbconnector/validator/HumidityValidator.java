package io.github.denysobukh.mqtt2dbconnector.validator;

import java.math.BigDecimal;

/**
 * @author Denis Obukhov  / created on 21 Dec 2020
 */
public class HumidityValidator implements ValidationCondition {
    @Override
    public boolean isValid(BigDecimal v) {
        return v.compareTo(BigDecimal.valueOf(0)) >= 0 && v.compareTo(BigDecimal.valueOf(101)) <= 0;
    }
}
