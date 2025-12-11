package es.onebox.mgmt.entities;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.enums.EntityStatus;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.entities.converter.CookieSettingsConverter;
import es.onebox.mgmt.entities.dto.CookieSettingsDTO;
import es.onebox.mgmt.entities.dto.CookieSettingsUpdateDTO;
import es.onebox.mgmt.entities.enums.CookiesChannelEnablingMode;
import es.onebox.mgmt.exception.ApiMgmtEntitiesErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CookieSettingsService {

    private final SecurityManager securityManager;
    private final EntitiesRepository entitiesRepository;


    @Autowired
    public CookieSettingsService(SecurityManager securityManager, EntitiesRepository entitiesRepository){
        this.securityManager = securityManager;
        this.entitiesRepository = entitiesRepository;
    }


    public CookieSettingsDTO getCookieSettings(Long entityId) {
        validateEntity(entityId);
        return CookieSettingsConverter.toDTO(entitiesRepository.getCookieSettings(entityId));
    }

    public void updateCookieSettings(Long entityId, CookieSettingsUpdateDTO cookieSettingsDTO) {
        if (CommonUtils.isTrue(cookieSettingsDTO.getEnableCustomIntegration())
          && CommonUtils.isFalse(cookieSettingsDTO.getAcceptIntegrationConditions())) {
            throw new OneboxRestException(ApiMgmtEntitiesErrorCode.COOKIE_INTEGRATION_REQUIRES_CONDITIONS_AGREEMENT);
        }
        if (CommonUtils.isTrue(cookieSettingsDTO.getEnableCustomIntegration())
          && cookieSettingsDTO.getChannelEnablingMode() == null) {
            throw new OneboxRestException(ApiMgmtEntitiesErrorCode.COOKIE_INTEGRATION_REQUIRES_CHANNEL_ENABLING_MODE);
        }
        if (CookiesChannelEnablingMode.RESTRICTED.equals(cookieSettingsDTO.getChannelEnablingMode())
          && CommonUtils.isEmpty(cookieSettingsDTO.getCustomIntegrationChannelIds())) {
            throw new OneboxRestException(ApiMgmtEntitiesErrorCode.CHANNEL_LIST_NULL_OR_EMPTY);
        }
        validateEntity(entityId);

        entitiesRepository.updateCookieSettings(entityId, CookieSettingsConverter.toMs(cookieSettingsDTO));
    }

    public void validateEntity(Long entityId){
        securityManager.checkEntityAccessible(entityId);

        Entity entity = entitiesRepository.getCachedEntity(entityId);

        if (entity == null || entity.getState().equals(EntityStatus.DELETED)) {
            throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_NOT_FOUND, "No entity found with id: " + entityId, null);
        }
    }
}
