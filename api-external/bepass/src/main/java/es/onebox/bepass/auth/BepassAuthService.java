package es.onebox.bepass.auth;

import es.onebox.bepass.datasources.bepass.dto.AuthResponse;
import es.onebox.bepass.datasources.bepass.repository.BepassAuthRepository;
import org.springframework.stereotype.Service;

@Service
public class BepassAuthService {

    private final BepassAuthRepository bepassAuthRepository;

    public BepassAuthService(BepassAuthRepository bepassAuthRepository) {
        this.bepassAuthRepository = bepassAuthRepository;
    }

    public String getToken() {
        AuthResponse token = this.bepassAuthRepository.getToken();
        if (token != null) {
            return token.getApiKey();
        }
        return null;
    }
}
