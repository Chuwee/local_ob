package es.onebox.mgmt.entities.friends.converter;

import es.onebox.mgmt.datasources.ms.entity.dto.EntityFriendsConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.FriendLimitException;
import es.onebox.mgmt.datasources.ms.entity.dto.FriendsLimits;
import es.onebox.mgmt.entities.friends.dto.EntityFriendsConfigDTO;
import es.onebox.mgmt.entities.friends.dto.FriendLimitExceptionDTO;
import es.onebox.mgmt.entities.friends.dto.FriendsLimitsDTO;
import org.apache.commons.collections4.CollectionUtils;

public class EntityFriendsConverter {

    private EntityFriendsConverter() {}

    public static EntityFriendsConfigDTO toDTO(EntityFriendsConfig source) {
        EntityFriendsConfigDTO target = new EntityFriendsConfigDTO();
        target.setLimits(toDTO(source.getLimits()));
        target.setFriendsRelationMode(source.getFriendsRelationMode());
        return target;
    }

    public static FriendsLimitsDTO toDTO(FriendsLimits source) {
        FriendsLimitsDTO target = new FriendsLimitsDTO();
        target.setDefaultValue(source.getDefaultValue());
        if (CollectionUtils.isNotEmpty(source.getExceptions())) {
            target.setExceptions(source.getExceptions().stream()
                    .map(EntityFriendsConverter::toDTO)
                    .toList());
        }
        return target;
    }

    public static FriendLimitExceptionDTO toDTO(FriendLimitException source) {
        FriendLimitExceptionDTO target = new FriendLimitExceptionDTO();
        target.setValue(source.getValue());
        target.setId(source.getId());
        target.setName(source.getName());
        return target;
    }

    public static EntityFriendsConfig toMs(EntityFriendsConfigDTO source) {
        EntityFriendsConfig target = new EntityFriendsConfig();
        target.setLimits(toDTO(source.getLimits()));
        target.setFriendsRelationMode(source.getFriendsRelationMode());
        return target;
    }

    public static FriendsLimits toDTO(FriendsLimitsDTO source) {
        FriendsLimits target = new FriendsLimits();
        target.setDefaultValue(source.getDefaultValue());
        if (source.getExceptions() != null) {
            target.setExceptions(source.getExceptions().stream()
                    .map(EntityFriendsConverter::toDTO)
                    .toList());
        }
        return target;
    }

    public static FriendLimitException toDTO(FriendLimitExceptionDTO source) {
        FriendLimitException target = new FriendLimitException();
        target.setValue(source.getValue());
        target.setId(source.getId());
        return target;
    }
}
