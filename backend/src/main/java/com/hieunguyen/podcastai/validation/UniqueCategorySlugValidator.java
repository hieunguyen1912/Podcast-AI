package com.hieunguyen.podcastai.validation;

import com.hieunguyen.podcastai.repository.CategoryRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueCategorySlugValidator implements ConstraintValidator<UniqueCategorySlug, String> {
    
    private final CategoryRepository categoryRepository;
    
    @Override
    public boolean isValid(String slug, ConstraintValidatorContext context) {
        if (slug == null) {
            return true; // Let @NotBlank handle null values
        }
        
        return !categoryRepository.existsBySlugIgnoreCase(slug);
    }
}
