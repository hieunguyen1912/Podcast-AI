package com.hieunguyen.podcastai.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueCategorySlugValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueCategorySlug {
    
    String message() default "Category slug must be unique";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
