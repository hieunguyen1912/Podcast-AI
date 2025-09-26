package com.hieunguyen.podcastai.util;

import com.hieunguyen.podcastai.entity.User;
import com.hieunguyen.podcastai.enums.ErrorCode;
import com.hieunguyen.podcastai.exception.AppException;
import com.hieunguyen.podcastai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;


    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        log.info("Authentication: {}", authentication);
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("No authenticated user found in SecurityContext");
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("email");
            if (email == null) {
                log.error("Email not found in JWT token");
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
            
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email from JWT: {}", email);
                    return new AppException(ErrorCode.USER_NOT_FOUND);
                });
            
            log.debug("Current user retrieved from JWT: {}", user.getEmail());
            return user;
        }
        
        log.error("Principal is not of type Jwt: {}", principal.getClass().getName());
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }
    

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
    

    public String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }
    

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() 
               && authentication.getPrincipal() instanceof Jwt;
    }
    

    public User getCurrentUserSafely() {
        try {
            return getCurrentUser();
        } catch (AppException e) {
            return null;
        }
    }
}
