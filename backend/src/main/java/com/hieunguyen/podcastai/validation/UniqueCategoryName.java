package com.hieunguyen.podcastai.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueCategoryNameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueCategoryName {
    
    String message() default "Category name must be unique";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
