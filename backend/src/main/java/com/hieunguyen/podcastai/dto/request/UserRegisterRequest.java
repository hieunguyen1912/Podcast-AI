package com.hieunguyen.podcastai.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class UserRegisterRequest {
        
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
        private String username;
        
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid email address")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        private String email;
        
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,100}$", 
                message = "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character")
        private String password;
        
        @NotBlank(message = "Confirm password is required")
        private String confirmPassword;
        
        @Size(max = 50, message = "First name must not exceed 50 characters")
        private String firstName;
        
        @Size(max = 50, message = "Last name must not exceed 50 characters")
        private String lastName;
        
        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must be a valid international format")
        @Size(max = 20, message = "Phone number must not exceed 20 characters")
        private String phoneNumber;
        
        @AssertTrue(message = "Passwords must match")
        public boolean isPasswordMatch() {
            return password != null && password.equals(confirmPassword);
        }
    }
