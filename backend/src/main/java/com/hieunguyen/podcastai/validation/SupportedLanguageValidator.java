package com.hieunguyen.podcastai.validation;

import com.hieunguyen.podcastai.enums.SupportedLanguage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class SupportedLanguageValidator implements ConstraintValidator<Language, String> {
    private Set<String> supportedLanguages;
    @Override
    public void initialize(Language constraintAnnotation) {
        supportedLanguages = Arrays.stream(SupportedLanguage.values())
                .map(SupportedLanguage::getCode)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return supportedLanguages.contains(value);
    }
}
