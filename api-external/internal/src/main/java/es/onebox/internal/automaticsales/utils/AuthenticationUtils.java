package es.onebox.internal.automaticsales.utils;

import es.onebox.common.datasources.oauth2.dto.UserAuthentication;
import es.onebox.oauth2.resource.context.AuthContextUtils;
import es.onebox.oauth2.resource.utils.TokenParam;

public class AuthenticationUtils {

    public static UserAuthentication getUserAuthentication() {
        UserAuthentication userAuthentication = new UserAuthentication();
        userAuthentication.setUser(AuthContextUtils.getUserName());
        userAuthentication.setPassword(AuthContextUtils.getStringAttr(TokenParam.USER_PASS.value()));
        return userAuthentication;
    }
}
