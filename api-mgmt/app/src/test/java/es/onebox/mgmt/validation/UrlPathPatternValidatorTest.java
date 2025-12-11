package es.onebox.mgmt.validation;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UrlPathPatternValidatorTest {

    private UrlPathPatternValidator validator;
    
    @Mock
    private ConstraintValidatorContext context;
    
    @Mock
    private ConstraintViolationBuilder builder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new UrlPathPatternValidator();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @Test
    void testValidPattern() {
        String validPattern = "/test/**";
        assertTrue(validator.isValid(validPattern, context));
    }

    @Test
    void testEmptyPattern() {
        String emptyPattern = "";
        assertFalse(validator.isValid(emptyPattern, context));
    }

    @Test
    void testInvalidPattern() {
        String invalidPattern = "invalid*pattern";
        assertFalse(validator.isValid(invalidPattern, context));
    }

    @Test
    void testNullPattern() {
        assertFalse(validator.isValid(null, context));
    }

    @Test
    void testPatternWithSpecialCharacters() {
        String validPattern = "/path/??/test/**";
        assertTrue(validator.isValid(validPattern, context));
    }

    @Test
    void testMatchingRegex() {
        String selfMatchingPattern = "/catalog-api/v1/sessions";
        assertTrue(validator.isValid(selfMatchingPattern, context));
    }

    @Test
    void testAntMatchingPattern() {
        String selfMatchingPattern = "/mgmt-api/v1/users/{userId}/rate-limit";
        assertTrue(validator.isValid(selfMatchingPattern, context));
    }

    @Test
    void testInvalidPatternErrorMessage() {
        String invalidPattern = "/test/{invalid}*";
        assertFalse(validator.isValid(invalidPattern, context));
        verify(context).buildConstraintViolationWithTemplate(eq("Invalid URL pattern format"));
    }
}
