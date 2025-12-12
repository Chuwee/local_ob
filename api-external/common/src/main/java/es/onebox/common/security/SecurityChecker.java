package es.onebox.common.security;

import jakarta.servlet.ServletRequest;

public interface SecurityChecker {
    default boolean check(ServletRequest servletRequest, String clientId) {
        if (validateContext(servletRequest)) {
            authentication();
        }
        return validateContext(servletRequest) && validateClientID(clientId) && validateEntity();
    }

    boolean validateEntity();
    boolean validateContext(ServletRequest servletRequest);
    boolean validateClientID(String clientId);
    void authentication();
}
