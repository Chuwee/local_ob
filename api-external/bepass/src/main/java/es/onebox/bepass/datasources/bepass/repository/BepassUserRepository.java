package es.onebox.bepass.datasources.bepass.repository;

import es.onebox.datasource.http.response.HttpResponse;
import es.onebox.bepass.auth.BepassAuthService;
import es.onebox.bepass.datasources.bepass.BepassUsersDatasource;
import es.onebox.bepass.datasources.bepass.dto.CreateUserRequest;
import es.onebox.bepass.datasources.bepass.dto.UserValidationResponse;
import org.springframework.stereotype.Service;

@Service
public class BepassUserRepository {

    private final BepassUsersDatasource bepassUsersDatasource;
    private final BepassAuthService authService;

    public BepassUserRepository(BepassUsersDatasource bepassUsersDatasource, BepassAuthService authService) {
        this.bepassUsersDatasource = bepassUsersDatasource;
        this.authService = authService;
    }

    public HttpResponse createUser(CreateUserRequest body) {
        String token = authService.getToken();
        return this.bepassUsersDatasource.createUser(token, body);
    }

    public UserValidationResponse validateByUserDocumentId(String document) {
        String token = authService.getToken();
        return this.bepassUsersDatasource.validateByDocument(token, document);
    }

    public UserValidationResponse validateByUserId(String id) {
        String token = authService.getToken();
        return this.bepassUsersDatasource.validateByUserId(token, id);
    }
}
