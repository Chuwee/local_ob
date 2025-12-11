package es.onebox.mgmt.entities.converter;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.entity.dto.SecurityConfigDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.UpdateSecurityConfigRequestDTO;
import es.onebox.mgmt.entities.dto.EntitySecurityConfigDTO;
import es.onebox.mgmt.entities.dto.ExpirationDTO;
import es.onebox.mgmt.entities.dto.PasswordConfigDTO;
import es.onebox.mgmt.entities.dto.StorageDTO;
import es.onebox.mgmt.entities.dto.UpdateEntitySecurityConfigRequestDTO;
import es.onebox.mgmt.entities.enums.TimeUnit;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;

public class EntitySecurityConverter {
    public static EntitySecurityConfigDTO toEntitySecurityConfigDTO(SecurityConfigDTO securityConfig) {
        if (securityConfig != null) {
            EntitySecurityConfigDTO entitySecurityConfig = new EntitySecurityConfigDTO();
            entitySecurityConfig.setEntityId(securityConfig.getEntityId());
            entitySecurityConfig.setPasswordConfig(toPasswordConfigDTO(securityConfig.getPasswordConfig()));
            return entitySecurityConfig;
        }
        throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_SECURITY_CONFIG_NOT_FOUND);
    }

    private static PasswordConfigDTO toPasswordConfigDTO(es.onebox.mgmt.datasources.ms.entity.dto.PasswordConfigDTO in) {
        if (in != null) {
            PasswordConfigDTO out = new PasswordConfigDTO();
            out.setMaxRetries(in.getMaxRetries());
            out.setExpiration(toExpirationDTO(in.getExpiration()));
            out.setStorage(toStorageDTO(in.getStorage()));
            return out;
        }
        return null;
    }

    private static ExpirationDTO toExpirationDTO(es.onebox.mgmt.datasources.ms.entity.dto.ExpirationDTO in) {
        if (in != null) {
            ExpirationDTO out = new ExpirationDTO();
            out.setEnabled(in.getEnabled());
            out.setType(in.getType() != null ? TimeUnit.valueOf(in.getType().name()) : null);
            out.setAmount(in.getAmount());
            return out;
        }
        return null;
    }


    private static StorageDTO toStorageDTO(es.onebox.mgmt.datasources.ms.entity.dto.StorageDTO in) {
        if (in != null) {
            StorageDTO out = new StorageDTO();
            out.setEnabled(in.getEnabled());
            out.setAmount(in.getAmount());
            return out;
        }
        return null;
    }

    public static UpdateSecurityConfigRequestDTO toUpdateSecurityConfigRequestDTO(UpdateEntitySecurityConfigRequestDTO request) {
        UpdateSecurityConfigRequestDTO updateSecurityConfigRequestDTO = new UpdateSecurityConfigRequestDTO();
        updateSecurityConfigRequestDTO.setPasswordConfig(fromUpdateEntitySecurityConfigRequestDTO(request.getPasswordConfig()));
        return updateSecurityConfigRequestDTO;
    }

    private static es.onebox.mgmt.datasources.ms.entity.dto.PasswordConfigDTO fromUpdateEntitySecurityConfigRequestDTO(PasswordConfigDTO in) {
        es.onebox.mgmt.datasources.ms.entity.dto.PasswordConfigDTO out = new es.onebox.mgmt.datasources.ms.entity.dto.PasswordConfigDTO();
        out.setMaxRetries(in.getMaxRetries());
        out.setExpiration(fromExpirationDTO(in.getExpiration()));
        out.setStorage(fromStorageDTO(in.getStorage()));
        return out;
    }

    private static es.onebox.mgmt.datasources.ms.entity.dto.ExpirationDTO fromExpirationDTO(ExpirationDTO in) {
        if (in != null) {
            es.onebox.mgmt.datasources.ms.entity.dto.ExpirationDTO out = new es.onebox.mgmt.datasources.ms.entity.dto.ExpirationDTO();
            out.setEnabled(in.getEnabled());
            out.setType(in.getType() != null ? es.onebox.mgmt.datasources.ms.entity.enums.TimeUnit.valueOf(in.getType().name()) : null);
            out.setAmount(in.getAmount());
            return out;
        }
        return null;
    }

    private static es.onebox.mgmt.datasources.ms.entity.dto.StorageDTO fromStorageDTO(StorageDTO in) {
        if (in != null) {
            es.onebox.mgmt.datasources.ms.entity.dto.StorageDTO out = new es.onebox.mgmt.datasources.ms.entity.dto.StorageDTO();
            out.setEnabled(in.getEnabled());
            out.setAmount(in.getAmount());
            return out;
        }
        return null;
    }

}
