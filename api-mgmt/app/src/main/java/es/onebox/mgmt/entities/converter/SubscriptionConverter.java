package es.onebox.mgmt.entities.converter;

import es.onebox.mgmt.datasources.ms.crm.dto.SubscriptionDTO;
import es.onebox.mgmt.entities.dto.CreateSubscriptionRequestDTO;
import es.onebox.mgmt.entities.dto.EntitySubscription;
import es.onebox.mgmt.entities.dto.Subscription;
import es.onebox.mgmt.entities.dto.SubscriptionListStatus;
import es.onebox.mgmt.entities.dto.SubscriptionRequestFilter;
import es.onebox.mgmt.entities.dto.SubscriptionRequestFilterDTO;
import es.onebox.mgmt.entities.dto.UpdateSubscriptionRequestDTO;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class SubscriptionConverter {

    public static Subscription fromMsCRM(SubscriptionDTO subscriptionDTO) {
        if (subscriptionDTO == null) {
            return null;
        }
        Subscription subscription = new Subscription();
        subscription.setId(subscriptionDTO.getId());
        subscription.setName(subscriptionDTO.getName());
        subscription.setStatus(BooleanUtils.isTrue(subscriptionDTO.getActive())?SubscriptionListStatus.ACTIVE:SubscriptionListStatus.INACTIVE);
        subscription.setDefault(subscriptionDTO.getDefault());
        subscription.setUses(subscriptionDTO.getUses());
        subscription.setDescription(subscriptionDTO.getDescription());
        subscription.setEntity(new EntitySubscription());
        subscription.getEntity().setId(subscriptionDTO.getEntityId());
        subscription.getEntity().setName(subscriptionDTO.getEntityName());

        return subscription;
    }

    public static List<Subscription> fromMsCRM(List<SubscriptionDTO> subscriptionDTOS) {
        return subscriptionDTOS.stream().map(SubscriptionConverter::fromMsCRM).collect(Collectors.toList());
    }

    public static SubscriptionDTO toMsCrm(CreateSubscriptionRequestDTO subscription) {
        SubscriptionDTO subscriptionDTO = new SubscriptionDTO();
        subscriptionDTO.setName(subscription.getName());
        subscriptionDTO.setDescription(subscription.getDescription());
        return subscriptionDTO;
    }

    public static SubscriptionRequestFilter toMs(SubscriptionRequestFilterDTO source) {
        SubscriptionRequestFilter target = new SubscriptionRequestFilter();
        if (source.getStatus() != null) {
            target.setFilterActive(source.getStatus().getActive());
        }
        target.setFilterName(source.getQ());
        target.setEntityAdminId(source.getEntityAdminId());

        if (source.getEntityId().equals(SecurityUtils.getUserOperatorId())) {
            target.setOperatorId(source.getEntityId());
        }

        return target;
    }

    public static SubscriptionDTO toMsCrm(UpdateSubscriptionRequestDTO subscription) {
        SubscriptionDTO subscriptionDTO = new SubscriptionDTO();
        if (subscription.getStatus() != null) {
            subscriptionDTO.setActive(subscription.getStatus().getActive());
        }
        subscriptionDTO.setDefault(subscription.getDefault());
        subscriptionDTO.setName(subscription.getName());
        subscriptionDTO.setDescription(subscription.getDescription());
        return subscriptionDTO;
    }

}
