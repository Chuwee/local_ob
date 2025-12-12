package es.onebox.ms.notification.ie.orderbookingpurchase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import es.flc.ie.model.IEForm;
import es.onebox.dal.dto.couch.order.OrderDTO;
import es.onebox.dal.dto.couch.order.OrderProductDTO;
import es.onebox.ms.notification.datasources.ms.entity.dto.Entities;
import es.onebox.ms.notification.datasources.ms.entity.dto.Entity;
import es.onebox.ms.notification.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.ms.notification.datasources.ms.order.repository.OrdersRepository;
import es.onebox.ms.notification.ie.utils.EntityExternalManagementConfigEndpointType;
import es.onebox.ms.notification.ie.utils.IERequestGenerator;

public class OrderBookingPurchaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderBookingPurchaseService.class);

    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private EntitiesRepository entitiesRepository;

    @Autowired
    private IERequestGenerator ieRequestGenerator;

    private static final int MAX_ATTEMPTS = 5;
    private static final int TIME_TO_SLEEP = 1000;

    public void processNotification(String newOrderCode) {
        int attempts = 0;

        OrderDTO orderData;
        do {
            orderData = ordersRepository.getOrderByCode(newOrderCode);

            if (orderData == null) {
                try {
                    Thread.sleep(TIME_TO_SLEEP);
                } catch (InterruptedException e) {
                    LOGGER.error("Booking purchased notification failed: {}", newOrderCode);
                    Thread.currentThread().interrupt();
                }
            }

            attempts++;
        } while (attempts < MAX_ATTEMPTS && orderData == null);

        //If the order has something (everything goes ok)
        if (orderData == null) {
            LOGGER.error("Booking purchased notification failed: {}", newOrderCode);
        } else if (CollectionUtils.isEmpty(orderData.getProducts())) {
            LOGGER.warn("Booking purchased notification doesn't have products:, code: {}", newOrderCode);
        } else {
            String parentOrderCode = orderData.getRelatedOriginalCode();

            Map<Integer, Integer> eventEntityIds = orderData.getProducts().stream().
                    collect(Collectors.toMap(OrderProductDTO::getEventId, OrderProductDTO::getEventEntityId, (e1, e2) -> e1));
            Entities entities = entitiesRepository.getEntities(eventEntityIds.values().stream().distinct().toList());
            List<Integer> externalManagementEntityIds = entities.getData().stream().
                    filter(e -> BooleanUtils.isTrue(e.getAllowExternalManagement())).
                    map(Entity::getId).toList();
            eventEntityIds.entrySet().removeIf(e -> !externalManagementEntityIds.contains(e.getValue()));

            if (!eventEntityIds.isEmpty()) {
                //With all this it can make the real notification process
                this.notifyOrderToEntities(
                        parentOrderCode,
                        orderData,
                        eventEntityIds,
                        new HashSet<>(eventEntityIds.values())
                );
            }
        }
    }

    private void notifyOrderToEntities(String parentOrderCode, OrderDTO orderData, Map<Integer, Integer> eventEntityMap,
            Collection<Integer> entityIds) {
        entityIds.forEach(entityId -> {

            try {
               ieRequestGenerator.executeCall(entityId, EntityExternalManagementConfigEndpointType.BLOCK_SESSIONS,
                        OrderBookingPurchaseService.generateIEFormForEntity(parentOrderCode, orderData, entityId, eventEntityMap));
            } catch (Exception e) {
                LOGGER.error("Booking purchased notification failed. Message: {}", e.getMessage());
            }


        });
    }

    private static IEForm generateIEFormForEntity(String parentOrderCode, OrderDTO orderData, Integer entityId, Map<Integer, Integer> eventEntityMap) {
        IEForm ieForm = new IEForm();
        ieForm.setOrderCode(orderData.getCode());
        ieForm.setParentOrderCode(parentOrderCode);
        List<Long> currentEventSessionIds;
        for (OrderProductDTO orderProduct : orderData.getProducts()) {
            if (eventEntityMap.get(orderProduct.getEventId()).equals(entityId)) {
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
