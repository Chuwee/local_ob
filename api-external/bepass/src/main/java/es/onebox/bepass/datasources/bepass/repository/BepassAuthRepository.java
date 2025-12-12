package es.onebox.bepass.datasources.bepass.repository;

import es.onebox.bepass.datasources.bepass.BepassAuthDatasource;
import es.onebox.bepass.datasources.bepass.dto.AuthResponse;
import es.onebox.cache.annotation.Cached;
import org.springframework.stereotype.Repository;

@Repository
public class BepassAuthRepository {

    private final BepassAuthDatasource bepassAuthDatasource;

    public BepassAuthRepository(BepassAuthDatasource bepassAuthDatasource) {
        this.bepassAuthDatasource = bepassAuthDatasource;
    }

    @Cached(key = "bepass.token", expires = 50 * 60)
    public AuthResponse getToken() {
        return bepassAuthDatasource.getToken();
    }
}
