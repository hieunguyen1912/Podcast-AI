package com.hieunguyen.podcastai.validation;

import com.hieunguyen.podcastai.repository.CategoryRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueCategoryNameValidator implements ConstraintValidator<UniqueCategoryName, String> {
    
    private final CategoryRepository categoryRepository;
    
    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        if (name == null) {
            return true; // Let @NotBlank handle null values
        }
        
        return !categoryRepository.existsByNameIgnoreCase(name);
    }
}
