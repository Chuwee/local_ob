package es.onebox.fcb.datasources.peoplesoft.utils;

import org.apache.wss4j.common.ext.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;

public class PasswordCallback implements CallbackHandler {

    private final String username;
    private final String password;

    public PasswordCallback(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void handle(Callback[] callbacks) {
        WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
        if (username.equals(pc.getIdentifier())) {
            pc.setPassword(password);
        }
    }

}