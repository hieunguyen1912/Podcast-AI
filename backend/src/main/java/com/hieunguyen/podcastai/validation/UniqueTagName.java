package com.hieunguyen.podcastai.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueTagNameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueTagName {
    
    String message() default "Tag name must be unique";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
