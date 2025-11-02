package com.hieunguyen.podcastai.validation;

import com.hieunguyen.podcastai.enums.ErrorCode;
import com.hieunguyen.podcastai.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@Slf4j
public class GNewsQueryValidator {

    private static final Pattern SPECIAL_CHARS_WITHOUT_QUOTES = Pattern.compile("[!?@#$%^&*+=|\\\\<>:;,\\[\\]{}/~`]");
    
    private static final Pattern DASH_SEPARATOR = Pattern.compile("\\b\\w+\\s+-\\s+\\w+\\b");
    
    private static final Pattern QUOTED_STRING = Pattern.compile("\"[^\"]*\"");

    public String validateAndSanitize(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_SEARCH_INPUT);
        }

        String trimmed = query.trim();

        if (!areBracketsBalanced(trimmed)) {
            log.error("GNews query has unbalanced brackets: {}", query);
            throw new AppException(ErrorCode.INVALID_SEARCH_INPUT);
        }

        String withoutQuotes = removeQuotedStrings(trimmed);
        if (containsInvalidSpecialChars(withoutQuotes)) {
            log.error("GNews query contains special characters without quotes: {}", query);
            throw new AppException(ErrorCode.INVALID_SEARCH_INPUT);
        }

        if (DASH_SEPARATOR.matcher(withoutQuotes).find()) {
            log.error("GNews query contains dash separator without quotes. Use quotes: \"Left - Right\"");
            throw new AppException(ErrorCode.INVALID_SEARCH_INPUT);
        }

        if (!areOperatorsValid(trimmed)) {
            log.error("GNews query has invalid operator format: {}", query);
            throw new AppException(ErrorCode.INVALID_SEARCH_INPUT);
        }

        log.debug("GNews query validated successfully: {}", trimmed);
        return trimmed;
    }

    private boolean areBracketsBalanced(String query) {
        int count = 0;
        for (char c : query.toCharArray()) {
            if (c == '(') {
                count++;
            } else if (c == ')') {
                count--;
                if (count < 0) {
                    return false; // Closing bracket before opening
                }
            }
        }
        return count == 0; // All brackets must be closed
    }

    private String removeQuotedStrings(String query) {
        return QUOTED_STRING.matcher(query).replaceAll("");
    }

    private boolean containsInvalidSpecialChars(String query) {
        // Remove valid operators and brackets before checking (case insensitive)
        Pattern operatorPattern = Pattern.compile("\\b(AND|OR|NOT)\\b", Pattern.CASE_INSENSITIVE);
        String cleaned = operatorPattern.matcher(query).replaceAll("");
        cleaned = cleaned.replaceAll("[\\(\\)]", "")
                .replaceAll("\\s+", " ")
                .trim();
        
        return SPECIAL_CHARS_WITHOUT_QUOTES.matcher(cleaned).find();
    }

    private boolean areOperatorsValid(String query) {
        String trimmed = query.trim();
        String upperQuery = trimmed.toUpperCase();

        if (upperQuery.startsWith("AND") || upperQuery.startsWith("OR")) {
            return false;
        }
        
        if (upperQuery.endsWith("AND") || upperQuery.endsWith("OR") || upperQuery.endsWith("NOT")) {
            return false;
        }

        Pattern doubleOperators = Pattern.compile("\\b(AND|OR|NOT)\\s+(AND|OR|NOT)\\b", Pattern.CASE_INSENSITIVE);
        if (doubleOperators.matcher(query).find()) {
            return false;
        }
        
        return true;
    }

    public boolean isValid(String query) {
        try {
            validateAndSanitize(query);
            return true;
        } catch (AppException e) {
            return false;
        }
    }
}
