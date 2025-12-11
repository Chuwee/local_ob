package es.onebox.event.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.amqp.webhook.WebhookService;
import es.onebox.event.common.amqp.webhook.dto.enums.NotificationSubtype;
import es.onebox.event.events.converter.EventConverter;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dto.EventDTO;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.request.EventSearchFilter;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.converter.ProductEventConverter;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dao.ProductEventDao;
import es.onebox.event.products.dao.ProductSessionDao;
import es.onebox.event.products.domain.ProductEventRecord;
import es.onebox.event.products.domain.ProductSessionRecord;
import es.onebox.event.products.dto.AddProductEventsDTO;
import es.onebox.event.products.dto.ProductEventsDTO;
import es.onebox.event.products.dto.ProductEventsFilterDTO;
import es.onebox.event.products.dto.UpdateProductEventDTO;
import es.onebox.event.products.enums.ProductEventStatus;
import es.onebox.event.products.enums.SelectionType;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelProductEventRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductEventService {

    private final ProductDao productDao;
    private final EventDao eventDao;
    private final ProductSessionDao productSessionDao;
    private final ProductEventDao productEventDao;
    private final RefreshDataService refreshDataService;
    private final WebhookService webhookService;

    @Autowired
    public ProductEventService(ProductDao productDao, EventDao eventDao, ProductEventDao productEventDao,
                               ProductSessionDao productSessionDao, RefreshDataService refreshDataService, WebhookService webhookService) {
        this.productDao = productDao;
        this.eventDao = eventDao;
        this.productSessionDao = productSessionDao;
        this.productEventDao = productEventDao;
        this.refreshDataService = refreshDataService;
        this.webhookService = webhookService;
    }

    @MySQLRead
    public ProductEventsDTO getProductEvents(Long productId, ProductEventsFilterDTO filter) {
        CpanelProductRecord cpanelProductRecord = productDao.findById(productId.intValue());
        if (cpanelProductRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }
        List<ProductEventRecord> productEventRecords = productEventDao.searchProductEvents(productId.intValue(), filter, false);

        return ProductEventConverter.toEntity(productEventRecords);
    }

    @MySQLWrite
    public ProductEventsDTO addProductEvent(Long productId, AddProductEventsDTO addProductEventsDTO) {
        CpanelProductRecord cpanelProductRecord = productDao.findById(productId.intValue());
        if (cpanelProductRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }

        Map<Integer, Integer> currentEventStatusMap = productEventDao.findByProductId(productId.intValue(), true)
                .stream()
                .collect(Collectors.toMap(
                        CpanelProductEventRecord::getEventid,
                        CpanelProductEventRecord::getStatus
                ));

        List<EventDTO> availableEvents = filterAvailableEvents(addProductEventsDTO.getEventIds());

        if (availableEvents.isEmpty()) {
            throw new OneboxRestException(MsEventErrorCode.EVENTS_NOT_FOUND_OR_INVALID_EVENT_STATE);
        }

        for (EventDTO availableEvent : availableEvents) {
            Integer eventId = availableEvent.getId().intValue();

            if (currentEventStatusMap.containsKey(eventId)) {
                Integer productEventStatus = currentEventStatusMap.get(eventId);

                if (productEventStatus.equals(ProductEventStatus.DELETED.getId())) {
                    productEventDao.updateStatus(productId,
                            eventId.longValue(),
                            ProductEventStatus.INACTIVE);
                    continue;
                }
                throw new OneboxRestException(MsEventErrorCode.PRODUCT_EVENT_ALREADY_EXISTS);
            }

            CpanelProductEventRecord cpanelProductEventRecord = new CpanelProductEventRecord();
            cpanelProductEventRecord.setProductid(productId.intValue());
            cpanelProductEventRecord.setSessionsselectiontype(SelectionType.ALL.getId());
            cpanelProductEventRecord.setEventid(eventId);
            cpanelProductEventRecord.setStatus(ProductEventStatus.INACTIVE.getId());
            productEventDao.insert(cpanelProductEventRecord);
        }

        postUpdateProduct(productId, addProductEventsDTO.getEventIds());

        return ProductEventConverter.toEntity(productEventDao.findByProductId(productId.intValue(), false));
    }

    @MySQLWrite
    public void deleteProductEvent(Long productId, Long eventId) {
        CpanelProductRecord cpanelProductRecord = productDao.findById(productId.intValue());
        if (cpanelProductRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }

        productEventDao.updateStatus(productId, eventId, ProductEventStatus.DELETED);
        postUpdateProduct(productId, List.of(eventId));
    }


    @MySQLWrite
    public void updateProductEvent(Long productId, Long eventId, UpdateProductEventDTO updateProductEventDTO) {
        CpanelProductRecord cpanelProductRecord = productDao.findById(productId.intValue());
        if (cpanelProductRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_NOT_FOUND);
        }

        if (updateProductEventDTO.getStatus() != null &&
                updateProductEventDTO.getStatus().equals(ProductEventStatus.DELETED)) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_EVENT_STATE_NOT_VALID);
        }

        ProductEventRecord productEventRecord =
                productEventDao.findByProductIdAndEventId(productId, eventId);

        //Activate product event only if it is possible
        if (shouldActivateProductEvent(productEventRecord, updateProductEventDTO)) {
            checkProductEventActivation(productId, eventId, productEventRecord);
        }
        productEventDao.updateStatus(productId, productEventRecord.getEventid().longValue(), updateProductEventDTO.getStatus());

        postUpdateProduct(productId, List.of(eventId));
    }

    private boolean shouldActivateProductEvent(ProductEventRecord productEventRecord, UpdateProductEventDTO dto) {
        return !productEventRecord.getStatus().equals(ProductEventStatus.ACTIVE.getId())
                && dto.getStatus() != null
                && dto.getStatus().equals(ProductEventStatus.ACTIVE);
    }

    private void checkProductEventActivation(Long productId, Long eventId, ProductEventRecord productEventRecord) {
        if (!productEventRecord.getSessionsselectiontype().equals(SelectionType.ALL.getId())) {
            List<ProductSessionRecord> sessions = productSessionDao.findProductSessionsByProductId(productId.intValue(), eventId.intValue());
            if (sessions == null || sessions.isEmpty()) {
                throw new OneboxRestException(MsEventErrorCode.PRODUCT_SESSIONS_REQUIRED);
            }
        }
    }

    private List<EventDTO> filterAvailableEvents(List<Long> eventsToFilter) {
        EventSearchFilter eventSearchFilter = new EventSearchFilter();
        eventSearchFilter.setId(eventsToFilter);

        List<EventStatus> eventStatusList = List.of(EventStatus.PLANNED, EventStatus.IN_PROGRAMMING, EventStatus.READY);
        eventSearchFilter.setStatus(eventStatusList);

        return eventDao.findEvents(eventSearchFilter).entrySet().stream().map(EventConverter::fromEntity).toList();
    }

    private void postUpdateProduct(Long productId, List<Long> eventIds) {
        // update products catalog
        refreshDataService.refreshProduct(productId);

        // update channel-session published products
        Set<Integer> validatedEvents = productSessionDao.findRelatedEvents(productId, eventIds, null);
        if (validatedEvents != null) {
            for (Integer eventId : validatedEvents) {
                refreshDataService.refreshEvent(eventId.longValue(), "productEventService.postUpdateProduct");
            }
        }

        Map<Long, List<Long>> publishedSessions = productSessionDao.findPublishedSessions(productId, null, eventIds, null);
        if (MapUtils.isNotEmpty(publishedSessions)) {
            for (Map.Entry<Long, List<Long>> sessionsByEvent : publishedSessions.entrySet()) {
                refreshDataService.refreshSessions(sessionsByEvent.getKey(), sessionsByEvent.getValue(), "productEventService.postUpdateProduct");
            }
        }

        webhookService.sendProductNotification(productId, NotificationSubtype.PRODUCT_EVENTS);
    }
}
