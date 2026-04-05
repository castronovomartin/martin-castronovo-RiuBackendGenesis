package com.challenge.hotel.infrastructure.input.rest.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation to ensure checkIn date is strictly before checkOut date.
 * Applied at class level to access both fields simultaneously.
 */
@Documented
@Constraint(validatedBy = DateRangeValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {

   String message() default "checkIn date must be strictly before checkOut date";

   Class<?>[] groups() default {};

   Class<? extends Payload>[] payload() default {};
}
