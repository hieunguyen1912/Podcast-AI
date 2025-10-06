package com.hieunguyen.podcastai.validation;

import com.hieunguyen.podcastai.repository.TagRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueTagNameValidator implements ConstraintValidator<UniqueTagName, String> {
    
    private final TagRepository tagRepository;
    
    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        if (name == null) {
            return true; // Let @NotBlank handle null values
        }
        
        // For update operations, we need to check if the name exists for other entities
        // This is a simplified approach - in a real scenario, you might need to pass the current entity ID
        return !tagRepository.existsByNameIgnoreCase(name);
    }
}
