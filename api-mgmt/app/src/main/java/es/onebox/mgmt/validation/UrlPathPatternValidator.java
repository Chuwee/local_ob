package es.onebox.mgmt.validation;

import es.onebox.mgmt.validation.annotation.UrlPathPattern;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.AntPathMatcher;

public class UrlPathPatternValidator implements ConstraintValidator<UrlPathPattern, String> {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final String URL_PATTERN_REGEX = "^/([a-zA-Z0-9?_-]+|\\*{1,2}|\\{[a-zA-Z0-9_-]+})(/([a-zA-Z0-9?_-]+|\\*{1,2}|\\{[a-zA-Z0-9_-]+}))*$";

    @Override
    public boolean isValid(String pattern, ConstraintValidatorContext context) {
        if (pattern == null || pattern.trim().isEmpty()) {
            return notValidPath(context);
        }

        if (!pattern.matches(URL_PATTERN_REGEX)) {
            return notValidPath(context);
        }

        if (!pathMatcher.match("/**", pattern)) {
            return notValidPath(context);
        }

        return true;
    }

    private static boolean notValidPath(ConstraintValidatorContext context) {
        ValidationUtils.addViolation(context, "Invalid URL pattern format");
        return false;
    }
}
