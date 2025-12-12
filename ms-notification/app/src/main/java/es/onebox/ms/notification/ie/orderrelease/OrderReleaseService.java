package es.onebox.ms.notification.ie.orderrelease;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import es.onebox.ms.notification.ie.orderrelease.dto.OrderDataDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import es.flc.ie.model.IEForm;
import es.onebox.ms.notification.datasources.ms.entity.dto.Entities;
import es.onebox.ms.notification.datasources.ms.entity.dto.Entity;
import es.onebox.ms.notification.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.ms.notification.datasources.ms.event.dto.Sessions;
import es.onebox.ms.notification.datasources.ms.event.repository.EventsRepository;
import es.onebox.ms.notification.ie.utils.EntityExternalManagementConfigEndpointType;
import es.onebox.ms.notification.ie.utils.IERequestGenerator;

/**
 * Created by joandf on 27/04/2015.
 */
public class OrderReleaseService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderReleaseService.class);

    @Autowired
    private EventsRepository eventsRepository;
    @Autowired
    private EntitiesRepository entitiesRepository;
    @Autowired
    private IERequestGenerator ieRequestGenerator;

    public void processReleaseToShoppingCart(OrderReleaseMessage orderReleaseMessage) {

        // If the order has something (everything goes ok)
        if (CollectionUtils.isNotEmpty(orderReleaseMessage.getSessionIds())) {

            // creates a Maps to get a fast correspondenccy between events and entities, only entities with external
            // Management
            Map<Integer, Integer> eventToEntityMap = getEventEntityMapFromSessionIds(orderReleaseMessage.getSessionIds());

            // With all this it can make the real notification process
            if (!eventToEntityMap.isEmpty()) {
                OrderDataDTO orderData = new OrderDataDTO();
                orderData.setOrderCode(orderReleaseMessage.getOrderCode());
                this.notifyOrderToEntities(orderData, eventToEntityMap);
            }
        }
    }

    private Map<Integer, Integer> getEventEntityMapFromSessionIds(Set<Integer> sessionIds) {
        Sessions sessions = eventsRepository.getSessions(sessionIds.stream().map(Integer::longValue).toList());

        Map<Integer, Integer> eventEntityIds = sessions.getData().stream()
                .collect(Collectors.toMap(s -> s.getEventId().intValue(), s -> s.getEntityId().intValue(), (e1, e2) -> e1));
        Entities entities = entitiesRepository.getEntities(eventEntityIds.values().stream().distinct().toList());
        List<Integer> externalManagementEntityIds = entities.getData().stream()
                .filter(e -> BooleanUtils.isTrue(e.getAllowExternalManagement())).map(Entity::getId).toList();
        eventEntityIds.entrySet().removeIf(e -> !externalManagementEntityIds.contains(e.getValue()));

        return eventEntityIds;
    }

    // Notifies the order payment cancelation to the entity/ies endpoint.
    private void notifyOrderToEntities(OrderDataDTO orderData, Map<Integer, Integer> eventEntityMap) {

        Set<Integer> entitiesProcessed = new HashSet<>();
        for (Integer entityId : eventEntityMap.values()) {
            if (!entitiesProcessed.contains(entityId)) {
                entitiesProcessed.add(entityId);
                LOG.info("ReleaseToShoppingCart notification- code: {}", orderData.getOrderCode());

                try {
                    ieRequestGenerator.executeCall(entityId,
                            EntityExternalManagementConfigEndpointType.UNBLOCK_SESSIONS, OrderReleaseService.generateIEFormForEntity(orderData));
                } catch (Exception e) {
                    LOG.error("ReleaseToShoppingCart notification failed.  Message: {}", e.getMessage());
                }
            }
        }
    }

    // Generate the IEForm to send to the incompatibility engine
    private static IEForm generateIEFormForEntity(OrderDataDTO orderData) {
        IEForm ieForm = new IEForm();
        ieForm.setOrderCode(orderData.getOrderCode());
        return ieForm;
    }
}
