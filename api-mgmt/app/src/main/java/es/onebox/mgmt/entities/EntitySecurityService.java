package es.onebox.mgmt.entities;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitySecurityRepository;
import es.onebox.mgmt.entities.converter.EntitySecurityConverter;
import es.onebox.mgmt.entities.dto.EntitySecurityConfigDTO;
import es.onebox.mgmt.entities.dto.UpdateEntitySecurityConfigRequestDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntitySecurityService {
    private static final String EXPIRATION_FIELD = "expiration";
    private static final String RETRIES_FIELD = "max_retries";
    private static final String STORAGE_FIELD = "storage";
    private static final Long STORAGE_MAX = 5L;
    private static final Long RETRIES_MAX = 10L;


    private final SecurityManager securityManager;
    private final EntitySecurityRepository entitySecurityRepository;

    @Autowired
    EntitySecurityService(SecurityManager securityManager, EntitySecurityRepository entitySecurityRepository) {
        this.securityManager = securityManager;
        this.entitySecurityRepository = entitySecurityRepository;
    }

    public EntitySecurityConfigDTO getEntitySecurityConfig(Long entityId) {
        securityManager.checkEntityAccessible(entityId);

        return EntitySecurityConverter.toEntitySecurityConfigDTO(entitySecurityRepository.getEntitySecurityConfig(entityId));
    }

    public void updateEntitySecurityConfig(Long entityId, UpdateEntitySecurityConfigRequestDTO request) {
        securityManager.checkEntityAccessible(entityId);
        validateUpdateRequest(entityId, request);
        entitySecurityRepository.updateEntitySecurityConfig(entityId, EntitySecurityConverter.toUpdateSecurityConfigRequestDTO(request));
    }

    private void validateUpdateRequest(Long entityId, UpdateEntitySecurityConfigRequestDTO request) {
        EntitySecurityConfigDTO config = getEntitySecurityConfig(entityId);
        if (request.getPasswordConfig().getExpiration() != null) {
            if (Boolean.TRUE.equals(request.getPasswordConfig().getExpiration().getEnabled())
                    && request.getPasswordConfig().getExpiration().getAmount() == null
                    && (config.getPasswordConfig().getExpiration() == null || config.getPasswordConfig().getExpiration().getAmount() == null) ) {
                throw ExceptionBuilder.build(ApiMgmtErrorCode.ENTITY_SECURITY_CONFIG_MISSING_AMOUNT, EXPIRATION_FIELD);
            }
            if (request.getPasswordConfig().getExpiration().getAmount() < 1L) {
                throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_SECURITY_CONFIG_INVALID_EXPIRATION);
            }

        }
        if (request.getPasswordConfig().getStorage() != null) {
            if (Boolean.TRUE.equals(request.getPasswordConfig().getStorage().getEnabled())
                    && request.getPasswordConfig().getStorage().getAmount() == null
                    && (config.getPasswordConfig().getStorage() == null || config.getPasswordConfig().getStorage().getAmount() == null) ) {
                throw ExceptionBuilder.build(ApiMgmtErrorCode.ENTITY_SECURITY_CONFIG_MISSING_AMOUNT, STORAGE_FIELD);
            }
            if (request.getPasswordConfig().getStorage().getAmount() < 1L
                    || request.getPasswordConfig().getStorage().getAmount() > STORAGE_MAX) {
                throw ExceptionBuilder.build(ApiMgmtErrorCode.ENTITY_SECURITY_CONFIG_INVALID_VALUE, STORAGE_FIELD, STORAGE_MAX);
            }
        }
        if (request.getPasswordConfig().getMaxRetries() != null
                && (request.getPasswordConfig().getMaxRetries() < 0L || request.getPasswordConfig().getMaxRetries() > RETRIES_MAX)) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.ENTITY_SECURITY_CONFIG_INVALID_VALUE, RETRIES_FIELD, RETRIES_MAX);
        }
    }
}
