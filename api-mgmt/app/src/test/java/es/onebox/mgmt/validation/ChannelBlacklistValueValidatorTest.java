package es.onebox.mgmt.validation;


import es.onebox.mgmt.channels.blacklists.enums.ChannelBlacklistType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class ChannelBlacklistValueValidatorTest {

    private ChannelBlacklistValueValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintViolationBuilder builder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new ChannelBlacklistValueValidator();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @Test
    void testValidNIF() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURI()).thenReturn(ChannelBlacklistType.NIF.name());

        ServletRequestAttributes mockAttributes = mock(ServletRequestAttributes.class);
        when(mockAttributes.getRequest()).thenReturn(mockRequest);

        try (MockedStatic<RequestContextHolder> mockedStatic = mockStatic(RequestContextHolder.class)) {
            mockedStatic.when(RequestContextHolder::getRequestAttributes).thenReturn(mockAttributes);

            // DNI
            assertTrue(validator.isValid("12345678Z", context));
            assertTrue(validator.isValid("     12345678Z      ", context));
            assertTrue(validator.isValid("12345678z", context));
            assertFalse(validator.isValid("12345678C", context));
            // NIE
            assertTrue(validator.isValid("X1234567L", context));
            assertTrue(validator.isValid("Y1234567X", context));
            assertTrue(validator.isValid("Z1234567R", context));
            assertFalse(validator.isValid("X1234567A", context));
            assertFalse(validator.isValid("Y1234567A", context));
            assertFalse(validator.isValid("Z1234567A", context));
            // PASSPORT
            assertTrue(validator.isValid("AB123456C", context));
            assertFalse(validator.isValid("123456789123456789", context));
        }
    }


    @Test
    void testValidEMAIL() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURI()).thenReturn(ChannelBlacklistType.EMAIL.name());

        ServletRequestAttributes mockAttributes = mock(ServletRequestAttributes.class);
        when(mockAttributes.getRequest()).thenReturn(mockRequest);

        try (MockedStatic<RequestContextHolder> mockedStatic = mockStatic(RequestContextHolder.class)) {
            mockedStatic.when(RequestContextHolder::getRequestAttributes).thenReturn(mockAttributes);

            // DNI
            assertTrue(validator.isValid("email@email.com", context));
            assertTrue(validator.isValid("     email@email.com      ", context));
            assertFalse(validator.isValid("email.com", context));
            assertFalse(validator.isValid("@email.com", context));
            assertFalse(validator.isValid("email@email", context));
            assertFalse(validator.isValid("email@", context));
        }
    }
}