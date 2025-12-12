package es.onebox.ms.notification.ie.orderrefund;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import es.flc.ie.model.IEForm;
import es.onebox.dal.dto.couch.enums.OrderState;
import es.onebox.dal.dto.couch.order.OrderDTO;
import es.onebox.dal.dto.couch.order.OrderProductDTO;
import es.onebox.ms.notification.datasources.ms.entity.dto.Entities;
import es.onebox.ms.notification.datasources.ms.entity.dto.Entity;
import es.onebox.ms.notification.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.ms.notification.datasources.ms.order.repository.OrdersRepository;
import es.onebox.ms.notification.ie.utils.EntityExternalManagementConfigEndpointType;
import es.onebox.ms.notification.ie.utils.IERequestGenerator;

/**
 * Created by joandf on 12/03/2015.
 */
public class OrderRefundService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderRefundService.class);

    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private EntitiesRepository entitiesRepository;
    @Autowired
    private IERequestGenerator ieRequestGenerator;

    private static final int MAX_RETRIES = 5;
    private static final int TIME_TO_SLEEP = 1000;

    public void processRefund(String newOrderCode) {
        // gets the orderData to has information about the order
        int tries = 0;

        OrderDTO orderData;
        do {
            orderData = ordersRepository.getOrderByCode(newOrderCode);
            if (orderData == null) {
                try {
                    Thread.sleep(TIME_TO_SLEEP);
                } catch (InterruptedException e) {
                    LOG.error("Refund notification failed: {}", newOrderCode);
                    Thread.currentThread().interrupt();
                }
            }
            tries++;
        } while (tries < MAX_RETRIES && orderData == null);

        // If the order has something (everything goes ok)
        if (orderData == null) {
            LOG.error("Refund notification failed: {}", newOrderCode);
        } else if (orderData.getProducts() == null || orderData.getProducts().isEmpty()) {
            LOG.warn("Refund notification doesn't has products:, code: {}", newOrderCode);
        } else {
            String parentOrderCode = null;
            if (!OrderState.CANCELLED.equals(orderData.getStatus().getState())) {
                parentOrderCode = orderData.getRelatedOriginalCode();
            }

            Map<Integer, Integer> eventEntityIds = orderData.getProducts().stream()
                    .collect(Collectors.toMap(OrderProductDTO::getEventId, OrderProductDTO::getEventEntityId, (e1, e2) -> e1));
            Entities entities = entitiesRepository.getEntities(eventEntityIds.values().stream().distinct().toList());
            List<Integer> externalManagementEntityIds = entities.getData().stream()
                    .filter(e -> BooleanUtils.isTrue(e.getAllowExternalManagement())).map(Entity::getId).toList();
            eventEntityIds.entrySet().removeIf(e -> !externalManagementEntityIds.contains(e.getValue()));

            if (!eventEntityIds.isEmpty()) {
                // With all this it can make the real notification process
                this.notifyOrderToEntities(parentOrderCode, orderData, eventEntityIds, new HashSet<>(eventEntityIds.values()));
            }

        }
    }

    private void notifyOrderToEntities(String parentOrderCode, OrderDTO orderData, Map<Integer, Integer> eventEntityMap,
            Collection<Integer> entityIds) {
        entityIds.forEach(entityId -> {
            LOG.info("Refund notification- code: {}", orderData.getCode());

            try {
                ieRequestGenerator.executeCall(entityId, EntityExternalManagementConfigEndpointType.UNBLOCK_SESSIONS,
                        OrderRefundService.generateIEFormForEntity(parentOrderCode, orderData, entityId, eventEntityMap));

            } catch (Exception e) {
                LOG.error("Refund notification failed. Message: {}", e.getMessage());
            }

        });
    }

    private static IEForm generateIEFormForEntity(String parentOrderCode, OrderDTO orderData, Integer entityId,
            Map<Integer, Integer> eventEntityMap) {
        IEForm ieForm = new IEForm();
        ieForm.setOrderCode(orderData.getCode());
        ieForm.setParentOrderCode(parentOrderCode);
        Set<Integer> addedSessions = new HashSet<>();
        List<Long> currentEventSessionIds;
        for (OrderProductDTO orderProduct : orderData.getProducts()) {
            if (eventEntityMap.get(orderProduct.getEventId()) == entityId && !addedSessions.contains(orderProduct.getSessionId())) {
                if (!ieForm.getArticleWithSessionsMap().containsKey(Long.valueOf(orderProduct.getEventId()))) {
                    ieForm.getArticleWithSessionsMap().put(Long.valueOf(orderProduct.getEventId()), new ArrayList<>());
                }
                currentEventSessionIds = ieForm.getArticleWithSessionsMap().get(Long.valueOf(orderProduct.getEventId()));
                currentEventSessionIds.add(Long.valueOf(orderProduct.getSessionId()));
            }
        }

        return ieForm;
    }
}
