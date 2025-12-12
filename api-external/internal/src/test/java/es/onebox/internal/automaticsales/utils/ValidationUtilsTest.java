package es.onebox.internal.automaticsales.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static es.onebox.internal.automaticsales.processsales.utils.ValidationUtils.checkValidEmail;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ValidationUtils - Email Validation Tests")
class ValidationUtilsTest {

    @Test
    @DisplayName("Should return false for null email")
    void shouldReturnFalseForNullEmail() {
        assertFalse(checkValidEmail(null));
    }

    @Test
    @DisplayName("Should return false for empty email")
    void shouldReturnFalseForEmptyEmail() {
        assertFalse(checkValidEmail(""));
    }

    @Test
    @DisplayName("Should return false for blank email")
    void shouldReturnFalseForBlankEmail() {
        assertFalse(checkValidEmail(" "));
        assertFalse(checkValidEmail("  "));
    }

    @Test
    @DisplayName("Should return true for valid basic email formats")
    void shouldReturnTrueForValidBasicEmails() {
        assertTrue(checkValidEmail("test@example.com"));
        assertTrue(checkValidEmail("user.name@domain.com"));
        assertTrue(checkValidEmail("user+tag@domain.co.uk"));
        assertTrue(checkValidEmail("user123@domain.org"));
        assertTrue(checkValidEmail("user-name@domain.net"));
        assertTrue(checkValidEmail("user_name@domain.info"));
        assertTrue(checkValidEmail("user@subdomain.domain.com"));
        assertTrue(checkValidEmail("user@domain-with-dash.com"));
        assertTrue(checkValidEmail("user@domain123.com"));
        assertTrue(checkValidEmail("user@domain.com"));
        assertTrue(checkValidEmail("a@b.c"));
        assertTrue(checkValidEmail("user@domain.co.uk"));
        assertTrue(checkValidEmail("user@domain.com.au"));
        assertTrue(checkValidEmail("user@domain.museum"));
        assertTrue(checkValidEmail("user@domain.travel"));
    }

    @Test
    @DisplayName("Should return false for invalid email formats")
    void shouldReturnFalseForInvalidEmails() {
        assertFalse(checkValidEmail("invalid-email"));
        assertFalse(checkValidEmail("@domain.com"));
        assertFalse(checkValidEmail("user@"));
        assertFalse(checkValidEmail("user@domain"));
        assertFalse(checkValidEmail("user.domain.com"));
        assertFalse(checkValidEmail("user@.com"));
        assertFalse(checkValidEmail("user@domain."));
        assertFalse(checkValidEmail("user@domain..com"));
        assertFalse(checkValidEmail("user@@domain.com"));
        assertFalse(checkValidEmail("user@domain@com"));
        assertFalse(checkValidEmail("user@domain.com."));
        assertFalse(checkValidEmail(".user@domain.com"));
        assertFalse(checkValidEmail("user.@domain.com"));
        assertFalse(checkValidEmail("user@domain..com"));
        assertFalse(checkValidEmail("user@domain.com.."));
        assertFalse(checkValidEmail("user@domain.com-"));
        assertFalse(checkValidEmail("user@-domain.com"));
        assertFalse(checkValidEmail("user@domain-.com"));
    }

    @Test
    @DisplayName("Should return false for emails with leading or trailing whitespace")
    void shouldReturnFalseForEmailsWithWhitespace() {
        assertFalse(checkValidEmail("user@domain.com "));
        assertFalse(checkValidEmail(" user@domain.com"));
        assertFalse(checkValidEmail(" user@domain.com "));
        assertFalse(checkValidEmail("\tuser@domain.com"));
        assertFalse(checkValidEmail("user@domain.com\t"));
        assertFalse(checkValidEmail("\nuser@domain.com"));
        assertFalse(checkValidEmail("user@domain.com\n"));
    }

    @Test
    @DisplayName("Should return true for emails with uppercase letters (case insensitive)")
    void shouldReturnTrueForEmailsWithUppercase() {
        assertTrue(checkValidEmail("USER@DOMAIN.COM"));
        assertTrue(checkValidEmail("User@Domain.com"));
        assertTrue(checkValidEmail("USER@domain.com"));
        assertTrue(checkValidEmail("user@DOMAIN.com"));
    }

    @Test
    @DisplayName("Should handle email with special characters in local part")
    void shouldHandleEmailWithSpecialCharactersInLocalPart() {
        String emailWithSpecialChars = "user+tag!#$%&'*+-/=?^_`{|}~@domain.com";
        assertTrue(checkValidEmail(emailWithSpecialChars), "Email with special characters should be valid");
    }

    @Test
    @DisplayName("Should handle email with numbers in domain")
    void shouldHandleEmailWithNumbersInDomain() {
        String emailWithNumbers = "user@domain123.com";
        assertTrue(checkValidEmail(emailWithNumbers), "Email with numbers in domain should be valid");
    }

    @Test
    @DisplayName("Should handle email with hyphens in domain")
    void shouldHandleEmailWithHyphensInDomain() {
        String emailWithHyphens = "user@domain-with-hyphens.com";
        assertTrue(checkValidEmail(emailWithHyphens), "Email with hyphens in domain should be valid");
    }

    @Test
    @DisplayName("Should handle email with multiple subdomains")
    void shouldHandleEmailWithMultipleSubdomains() {
        String emailWithSubdomains = "user@sub1.sub2.sub3.domain.com";
        assertTrue(checkValidEmail(emailWithSubdomains), "Email with multiple subdomains should be valid");
    }

    @Test
    @DisplayName("Should handle very long but valid email")
    void shouldHandleVeryLongValidEmail() {
        String longEmail = "very.long.email.address.with.many.parts.and.subdomains@very.long.domain.name.with.many.subdomains.com";
        assertTrue(checkValidEmail(longEmail), "Long but valid email should be accepted");
    }

    @Test
    @DisplayName("Should return false for emails with invalid special characters")
    void shouldReturnFalseForEmailsWithInvalidSpecialCharacters() {
        assertFalse(checkValidEmail("user@domain.com@"));
        assertFalse(checkValidEmail("user@domain.com#"));
        assertFalse(checkValidEmail("user@domain.com$"));
        assertFalse(checkValidEmail("user@domain.com%"));
        assertFalse(checkValidEmail("user@domain.com^"));
        assertFalse(checkValidEmail("user@domain.com&"));
        assertFalse(checkValidEmail("user@domain.com*"));
        assertFalse(checkValidEmail("user@domain.com("));
        assertFalse(checkValidEmail("user@domain.com)"));
        assertFalse(checkValidEmail("user@domain.com["));
        assertFalse(checkValidEmail("user@domain.com]"));
        assertFalse(checkValidEmail("user@domain.com{"));
        assertFalse(checkValidEmail("user@domain.com}"));
        assertFalse(checkValidEmail("user@domain.com|"));
        assertFalse(checkValidEmail("user@domain.com\\"));
        assertFalse(checkValidEmail("user@domain.com:"));
        assertFalse(checkValidEmail("user@domain.com;"));
        assertFalse(checkValidEmail("user@domain.com\""));
        assertFalse(checkValidEmail("user@domain.com'"));
        assertFalse(checkValidEmail("user@domain.com<"));
        assertFalse(checkValidEmail("user@domain.com>"));
        assertFalse(checkValidEmail("user@domain.com,"));
        assertFalse(checkValidEmail("user@domain.com/"));
        assertFalse(checkValidEmail("user@domain.com?"));
        assertFalse(checkValidEmail("user@domain.com`"));
        assertFalse(checkValidEmail("user@domain.com~"));
    }

    @Test
    @DisplayName("Should handle edge cases for email validation")
    void shouldHandleEdgeCases() {
        // Test with minimum valid email
        assertTrue(checkValidEmail("a@b.c"));
        
        // Test with dots in local part (but not consecutive dots)
        assertTrue(checkValidEmail("user.name@domain.com"));
        assertFalse(checkValidEmail("user..name@domain.com")); // Consecutive dots are not allowed
        
        // Test with plus sign in local part
        assertTrue(checkValidEmail("user+tag@domain.com"));
        
        // Test with underscores in local part
        assertTrue(checkValidEmail("user_name@domain.com"));
        
        // Test with hyphens in local part
        assertTrue(checkValidEmail("user-name@domain.com"));
        
        // Test with numbers in local part
        assertTrue(checkValidEmail("user123@domain.com"));
    }
} 