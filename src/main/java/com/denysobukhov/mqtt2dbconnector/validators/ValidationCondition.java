package com.denysobukhov.mqtt2dbconnector.validators;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author Denis Obukhov  / created on 21 Dec 2020
 */
@FunctionalInterface
public  interface ValidationCondition {
    boolean isValid(final BigDecimal value);
}
