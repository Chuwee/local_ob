package es.onebox.fifaqatar.config.interceptor;

import es.onebox.fifaqatar.config.context.AppRequestContext;
import es.onebox.fifaqatar.error.InvalidAuthException;
import es.onebox.fifaqatar.adapter.datasource.FeverMeDatasource;
import es.onebox.fifaqatar.adapter.datasource.dto.MeResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FifaSecurityInterceptorTest {


    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private FeverMeDatasource feverMeDatasource;

    private static MockedStatic<AppRequestContext> appRequestContext;

    @InjectMocks
    private FifaQatarSecurityInterceptor fifaQatarSecurityInterceptor;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeAll
    public static void beforeAll() {
        appRequestContext = Mockito.mockStatic(AppRequestContext.class);
    }

    @AfterAll
    public static void afterAll() {
        appRequestContext.close();
    }


    @Test
    void test_whenInvalidHeader_thenThrowException() {
        when(httpServletRequest.getHeader("Authorization")).thenReturn("NOPE");

        assertThrows(InvalidAuthException.class, () -> fifaQatarSecurityInterceptor.preHandle(httpServletRequest, null, null));
    }

    @Test
    void test_whenValidHeaderAndInvalidJWT_thenThrowException() {
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer JWT");
        when(feverMeDatasource.me("JWT")).thenThrow(new RuntimeException());

        assertThrows(InvalidAuthException.class, () -> fifaQatarSecurityInterceptor.preHandle(httpServletRequest, null, null));
    }

    @Test
    void test_whenValidHeaderValidJWT_thenOk() throws Exception {
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer JWT");
        when(feverMeDatasource.me("JWT")).thenReturn(new MeResponseDTO());
        appRequestContext.when(() -> AppRequestContext.setCurrentUser(any())).thenAnswer(invocationOnMock -> null);

        assertTrue(fifaQatarSecurityInterceptor.preHandle(httpServletRequest, null, null));
    }
}
