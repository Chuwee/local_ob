package es.onebox.mgmt.users.service;


import es.onebox.mgmt.datasources.ms.entity.dto.user.ratelimit.UserRateLimitConfig;
import es.onebox.mgmt.datasources.ms.entity.repository.UserRateLimitRepository;
import es.onebox.mgmt.users.converter.ratelimit.UserRateLimitConverter;
import es.onebox.mgmt.users.dto.ratelimit.UserRateLimitConfigDTO;
import org.springframework.stereotype.Service;

@Service
public class UserRateLimitService {

    private final UserRateLimitRepository repository;


    public UserRateLimitService(UserRateLimitRepository repository) {
        this.repository = repository;
    }

    public UserRateLimitConfigDTO searchRateLimit(Long userId) {
        UserRateLimitConfig userRateLimitConfig = repository.searchRateLimit(userId);
        return UserRateLimitConverter.domainToDTO(userRateLimitConfig);
    }

    public void upsertRateLimit(Long userId, UserRateLimitConfigDTO userRateLimitConfigDTO) {
        UserRateLimitValidationService.validate(userRateLimitConfigDTO);
        UserRateLimitConfig userRateLimitConfig = UserRateLimitConverter.dtoToDomain(
            userRateLimitConfigDTO);
        repository.upsertRateLimit(userId, userRateLimitConfig);
    }

}
