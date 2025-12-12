package es.onebox.chelsea;

import es.onebox.chelsea.dao.CategoryCustomerTypesCouchDao;
import es.onebox.chelsea.domain.CategoryCustomerTypesMapping;
import es.onebox.chelsea.domain.CustomerTypeMapping;
import es.onebox.common.datasources.ms.client.dto.Customer;
import es.onebox.common.datasources.ms.client.repository.CustomerRepository;
import es.onebox.common.datasources.ms.entity.dto.CustomerType;
import es.onebox.common.datasources.ms.entity.dto.CustomerTypes;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.datasources.ms.event.dto.CategoryDTO;
import es.onebox.common.datasources.ms.event.dto.EventDTO;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.order.dto.EventType;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;
import es.onebox.common.datasources.ms.order.repository.MsOrderRepository;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChelseaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChelseaService.class);

    private final MsOrderRepository msOrderRepository;
    private final MsEventRepository msEventRepository;
    private final CustomerRepository customerRepository;
    private final EntitiesRepository entitiesRepository;
    private final CategoryCustomerTypesCouchDao categoryCustomerTypesCouchDao;

    @Autowired
    public ChelseaService(MsOrderRepository msOrderRepository,
                          MsEventRepository msEventRepository,
                          CustomerRepository customerRepository,
                          EntitiesRepository entitiesRepository,
                          CategoryCustomerTypesCouchDao categoryCustomerTypesCouchDao) {
        this.msOrderRepository = msOrderRepository;
        this.msEventRepository = msEventRepository;
        this.customerRepository = customerRepository;
        this.entitiesRepository = entitiesRepository;
        this.categoryCustomerTypesCouchDao = categoryCustomerTypesCouchDao;
    }

    public void registerOperation(String code) {
        OrderDTO order = msOrderRepository.getOrderByCode(code);
        if (order != null) {
            Map<Integer, EventDTO> eventsCache = new HashMap<>();
            for (OrderProductDTO product : order.getProducts()) {
                EventDTO event = getEvent(product.getEventId(), product.getEventType(), eventsCache);
                checkEventActions(order, event);
            }
        }

    }

    private void checkEventActions(OrderDTO order, EventDTO event) {

        CategoryDTO customCategory = event.getCustomCategory();
        String customerId = order.getCustomer().getUserId();
        if (customCategory != null && StringUtils.isNotEmpty(customerId)) {
            LOGGER.info("[CHELSEA WEBHOOK] code: {} - customer: {} - Check UPGRADE customer types for event: {} with customCategory: {}",
                    order.getCode(), customerId, event.getId(), customCategory.getCode());

            CategoryCustomerTypesMapping mapping = categoryCustomerTypesCouchDao.get(order.getOrderData().getChannelEntityId().toString());
            CustomerTypeMapping customerUpdateMapping = mapping != null ? mapping.get(customCategory.getCode()) : null;
            if (customerUpdateMapping != null) {
                upgradeCustomer(order, customerId, customerUpdateMapping);
            }
        }
    }

    private void upgradeCustomer(OrderDTO order, String customerId, CustomerTypeMapping updateMapping) {
        Integer entityId = order.getOrderData().getChannelEntityId();
        Customer customer = customerRepository.getCustomer(customerId);
        if (customer != null) {
            CustomerTypes entityCustomerTypes = entitiesRepository.getCustomerTypes(entityId.longValue());
            Set<Long> customerTypes = new HashSet<>();

            if (CollectionUtils.isNotEmpty(customer.getCustomerTypes())) {
                customerTypes = customer.getCustomerTypes().stream()
                        .map(IdNameCodeDTO::getId)
                        .collect(Collectors.toSet());
            }
            if (!updateCustomerType(entityCustomerTypes, customerTypes, updateMapping.getRemove(), false)) {
                return;
            }
            if (!updateCustomerType(entityCustomerTypes, customerTypes, updateMapping.getAdd(), true)) {
                return;
            }
            Customer updateRequest = new Customer();
            updateRequest.setCustomerTypes(customerTypes.stream()
                    .map(id -> new IdNameCodeDTO(id, null, null))
                    .collect(Collectors.toList()));
            customerRepository.updateCustomer(customerId, entityId, updateRequest);

            LOGGER.info("[CHELSEA WEBHOOK] code: {} - customer: {} - UPGRADE customer types - add: {} - remove: {}", order.getCode(),
                    customerId, StringUtils.join(updateMapping.getAdd()), StringUtils.join(updateMapping.getRemove()));
        }
    }

    private static boolean updateCustomerType(CustomerTypes entityCustomerTypes, Set<Long> customerTypes,
                                              List<String> updateTypes, boolean add) {
        if (CollectionUtils.isNotEmpty(updateTypes)) {
            for (String type : updateTypes) {
                CustomerType customerType = entityCustomerTypes.getData().stream()
                        .filter(c -> c.getCode().equals(type)).findAny().orElse(null);
                if (customerType != null) {
                    if (add) {
                        customerTypes.add(customerType.getId());
                    } else {
                        customerTypes.removeIf(c -> c.equals(customerType.getId()));
                    }
                } else {
                    LOGGER.warn("[CHELSEA WEBHOOK] NO mapping found for type with code: {}", type);
                    return false;
                }
            }
        }
        return true;
    }

    private EventDTO getEvent(Integer eventId, EventType eventType, Map<Integer, EventDTO> eventsCache) {
        EventDTO event = eventsCache.get(eventId);
        if (event == null) {
            if (EventType.SEASON_TICKET.equals(eventType)) {
                event = msEventRepository.getSeasonTicket(Long.valueOf(eventId));
            } else {
                event = msEventRepository.getEvent(Long.valueOf(eventId));
            }
            eventsCache.put(eventId, event);
        }
        return event;
    }

}
