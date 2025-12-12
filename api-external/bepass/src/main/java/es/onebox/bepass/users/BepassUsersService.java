package es.onebox.bepass.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.ms.client.dto.AssignationTrigger;
import es.onebox.common.datasources.ms.client.dto.CustomerTypeAutomaticAssignment;
import es.onebox.common.datasources.ms.client.repository.CustomerRepository;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.datasource.http.response.HttpResponse;
import es.onebox.bepass.datasources.bepass.config.BepassConfig;
import es.onebox.bepass.datasources.bepass.dto.CreateUserResponse;
import es.onebox.bepass.datasources.bepass.dto.UserValidationResponse;
import es.onebox.bepass.datasources.bepass.repository.BepassUserRepository;
import es.onebox.bepass.exception.BepassErrorCode;
import es.onebox.bepass.users.converter.UsersConverter;
import es.onebox.bepass.users.dto.CreateUserDTO;
import es.onebox.bepass.users.dto.UserResponseDTO;
import es.onebox.bepass.users.dto.UserValidationNotificationDTO;
import es.onebox.bepass.users.dto.ValidateUserDTO;
import es.onebox.bepass.users.dto.ValidateUserResponseDTO;
import es.onebox.bepass.users.dto.ValidationMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BepassUsersService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BepassUsersService.class);

    private final BepassUserRepository bepassUserRepository;
    private final CustomerRepository customerRepository;
    private final BepassConfig bepassConfig;
    private final ObjectMapper jacksonMapper;

    public BepassUsersService(BepassUserRepository bepassUserRepository, CustomerRepository customerRepository, BepassConfig bepassConfig, ObjectMapper jacksonMapper) {
        this.bepassUserRepository = bepassUserRepository;
        this.customerRepository = customerRepository;
        this.bepassConfig = bepassConfig;
        this.jacksonMapper = jacksonMapper;
    }

    public UserResponseDTO createUser(CreateUserDTO in) {
        HttpResponse response = this.bepassUserRepository.createUser(UsersConverter.toCreateUser(in, bepassConfig));
        CreateUserResponse user = readUser(response);
        return new UserResponseDTO(user.sup(), user.onboardingUrl());
    }

    public ValidateUserResponseDTO validateUser(ValidateUserDTO user) {
        ValidationMethod type = user.getType();
        final String token = switch (type) {
            case ID_DOC -> this.validateByDocument(UsersConverter.cleanIdCard(user.getId()));
            case ID -> this.validateByUserId(UsersConverter.cleanIdCard(user.getId()));
        };
        LOGGER.info("[BEPASS] Validation type {} for user {} completed successfully", type.name(),  user.getId());
        return new ValidateUserResponseDTO(token);
    }

    //TODO: assign customer type without doing login trigger
    public void postback(UserValidationNotificationDTO body) {
        LOGGER.info("[BEPASS] Postback recevided for user {} with token {}" , body.externalId(), body.token());
        this.customerRepository.executeCustomerTypeAssignment(body.externalId(), new CustomerTypeAutomaticAssignment(AssignationTrigger.LOGIN));
    }

    private String validateByDocument(String doc) {
        UserValidationResponse response = this.bepassUserRepository.validateByUserDocumentId(doc);
        return response.getToken();
    }

    private String validateByUserId(String doc) {
        UserValidationResponse response = this.bepassUserRepository.validateByUserId(doc);
        if (response.getToken() == null) {
           throw new OneboxRestException(BepassErrorCode.USER_NOT_FOUND);
        }
        return response.getToken();
    }

    private CreateUserResponse readUser(HttpResponse response) {
        try {
            return jacksonMapper.readValue(response.getBodyAsString(), CreateUserResponse.class);
        } catch (JsonProcessingException e) {
            LOGGER.error("[BEPASS USER] Error reading user response", e);
            throw new OneboxRestException();
        }
    }

}
