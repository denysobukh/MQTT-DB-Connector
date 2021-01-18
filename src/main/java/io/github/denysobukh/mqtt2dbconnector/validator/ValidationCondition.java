package io.github.denysobukh.mqtt2dbconnector.validator;

import java.math.BigDecimal;

/**
 * @author Denis Obukhov  / created on 21 Dec 2020
 */
@FunctionalInterface
public  interface ValidationCondition {
    boolean isValid(final BigDecimal value);
}
