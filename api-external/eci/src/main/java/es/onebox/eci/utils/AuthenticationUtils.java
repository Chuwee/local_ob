package es.onebox.eci.utils;

import es.onebox.common.datasources.ms.entity.dto.User;
import es.onebox.common.datasources.ms.entity.dto.UserSearchFilter;
import es.onebox.common.datasources.ms.entity.dto.Users;
import es.onebox.common.datasources.ms.entity.enums.UserState;
import es.onebox.common.datasources.oauth2.dto.UserAuthentication;
import es.onebox.core.security.Roles;
import es.onebox.oauth2.resource.context.AuthContextUtils;
import es.onebox.oauth2.resource.utils.TokenParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.function.TriFunction;

import java.util.function.Function;

import static java.util.Collections.singletonList;

public class AuthenticationUtils {

    public static String getToken(String userName, String userPassword, Long channelId, Long entityId,
                                  TriFunction<String, String, Long, String> tokenGetter,
                                  Function<UserSearchFilter, Users> userGetter) {
        if (userName != null && userPassword != null) {
            return tokenGetter.apply(userName, userPassword, channelId);
        }

        UserSearchFilter userFilter = new UserSearchFilter();
        userFilter.setStatus(singletonList(UserState.ACTIVE.getState()));
        userFilter.setRole(Roles.ROLE_CNL_TAQ);
        userFilter.setSingleRole(Boolean.FALSE);
        userFilter.setEntityId(entityId);
        Users users = userGetter.apply(userFilter);
        User user = users == null && CollectionUtils.isEmpty(users.getData())
                ? null
                : users.getData().stream().findAny().orElse(null);
        if (user != null)  {
            return tokenGetter.apply(user.getUsername(), user.getPassword(), channelId);
        }

        return null;
    }

    public static UserAuthentication getUserAuthentication() {

        UserAuthentication userAuthentication = new UserAuthentication();
        userAuthentication.setUser(AuthContextUtils.getUserName());
        userAuthentication.setPassword(AuthContextUtils.getStringAttr(TokenParam.USER_PASS.value()));

        return userAuthentication;
    }
}
