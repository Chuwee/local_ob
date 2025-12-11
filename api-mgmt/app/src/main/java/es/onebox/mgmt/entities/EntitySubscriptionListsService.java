package es.onebox.mgmt.entities;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.EntityTypes;
import es.onebox.core.security.Roles;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.crm.dto.SubscriptionDTO;
import es.onebox.mgmt.datasources.ms.crm.repository.SubscriptionListsRepository;
import es.onebox.mgmt.entities.converter.SubscriptionConverter;
import es.onebox.mgmt.entities.dto.CreateSubscriptionRequestDTO;
import es.onebox.mgmt.entities.dto.Subscription;
import es.onebox.mgmt.entities.dto.SubscriptionRequestFilter;
import es.onebox.mgmt.entities.dto.SubscriptionRequestFilterDTO;
import es.onebox.mgmt.entities.dto.UpdateSubscriptionRequestDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntitySubscriptionListsService {

    private final SubscriptionListsRepository subscriptionListsRepository;
    private final SecurityManager securityManager;

    @Autowired
    public EntitySubscriptionListsService(SubscriptionListsRepository subscriptionListsRepository, SecurityManager securityManager) {
        this.subscriptionListsRepository = subscriptionListsRepository;
        this.securityManager = securityManager;
    }

    public List<Subscription> getSubscriptionLists(SubscriptionRequestFilterDTO filter) {
        securityManager.checkEntityAccessible(filter);
        if (filter.getEntityId() == null) {
            filter.setEntityId(SecurityUtils.getUserEntityId());
        }
        SubscriptionRequestFilter requestFilter = SubscriptionConverter.toMs(filter);
        List<SubscriptionDTO> subscriptionDTOS =
                subscriptionListsRepository.getSubscriptionLists(requestFilter, filter.getEntityId());
        return SubscriptionConverter.fromMsCRM(subscriptionDTOS);
    }

    public Subscription getSubscriptionList(Integer subscriptionListId) {
        Long entityId = SecurityUtils.getUserEntityId();
        Long entityAdminId = null;
        Long operatorId = null;
        if(SecurityUtils.hasAnyRole(Roles.ROLE_ENT_ADMIN)){
            entityAdminId = entityId;
        }
        else if (SecurityUtils.getUserOperatorId() == entityId) {
            operatorId = entityId;
        }
        SubscriptionDTO subscriptionDTO =
                subscriptionListsRepository.getSubscriptionList(entityId, operatorId, subscriptionListId, entityAdminId);
        return SubscriptionConverter.fromMsCRM(subscriptionDTO);
    }

    public IdDTO addSubscriptionLists(CreateSubscriptionRequestDTO subscription) {
        if ((SecurityUtils.hasEntityType(EntityTypes.OPERATOR) || SecurityUtils.hasAnyRole(Roles.ROLE_ENT_ADMIN)) &&
                (subscription.getEntityId() == null || SecurityUtils.getUserEntityId() == subscription.getEntityId().longValue())) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
        }
        Long entityId = SecurityUtils.getUserEntityId();
        if (subscription.getEntityId() != null) {
            securityManager.checkEntityAccessible(subscription.getEntityId());
            entityId = subscription.getEntityId();
        }
        SubscriptionDTO subscriptionDTO = subscriptionListsRepository.addSubscriptionLists(entityId, SubscriptionConverter.toMsCrm(subscription));
        return new IdDTO(subscriptionDTO.getId().longValue());
    }

    public void updateSubscriptionLists(Long subscriptionListId, UpdateSubscriptionRequestDTO subscription) {
        Subscription subscriptionList = getAndCheckSubscriptionList(subscriptionListId.intValue());
        subscriptionListsRepository.updateSubscriptionLists(subscriptionList.getEntity().getId(), subscriptionListId, SubscriptionConverter.toMsCrm(subscription));
    }

    public void deleteSubscriptionLists(Long subscriptionListId) {
        Subscription subscriptionList = getAndCheckSubscriptionList(subscriptionListId.intValue());
        subscriptionListsRepository.deleteSubscriptionLists(subscriptionList.getEntity().getId(), subscriptionListId);
    }

    private Subscription getAndCheckSubscriptionList(Integer subscriptionListId) {
        Subscription subscription = getSubscriptionList(subscriptionListId);
        securityManager.checkEntityAccessible(subscription.getEntity().getId());
        return subscription;
    }

}
