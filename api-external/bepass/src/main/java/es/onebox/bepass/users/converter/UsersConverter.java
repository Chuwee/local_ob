package es.onebox.bepass.users.converter;

import es.onebox.bepass.auth.BepassAuthContext;
import es.onebox.bepass.datasources.bepass.config.BepassConfig;
import es.onebox.bepass.datasources.bepass.dto.CreateUserRequest;
import es.onebox.bepass.users.dto.CreateUserDTO;

public class UsersConverter {

    private UsersConverter() {
    }

    public static CreateUserRequest toCreateUser(CreateUserDTO in, BepassConfig bepassConfig) {
        return new CreateUserRequest(in.getId(), in.getCallbackUrl(),
                bepassConfig.getPostbackUrl(), in.getName(),
                in.getSurname(), cleanIdCard(in.getIdCard()), in.getIdCardType(), in.getGender(),
                in.getBirthday(), in.getPhone(), in.getEmail(), BepassAuthContext.get().companyId());
    }

    public static String cleanIdCard(String idCard) {
        return idCard.replaceAll("[^A-Za-z0-9]", "");
    }
}
