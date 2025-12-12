package es.onebox.fifaqatar.adapter.mapper;

import es.onebox.common.datasources.common.dto.Session;
import es.onebox.common.datasources.ms.event.dto.response.catalog.event.EventCatalog;
import es.onebox.common.datasources.ms.event.dto.response.catalog.session.SessionCatalog;
import es.onebox.common.datasources.ms.event.dto.response.session.passbook.SessionPassbookCommElement;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.venue.repository.VenueTemplateRepository;
import es.onebox.common.datasources.orderitems.dto.OrderItem;
import es.onebox.common.datasources.orderitems.enums.OrderItemRelatedProductState;
import es.onebox.common.datasources.orders.dto.OrderDetail;
import es.onebox.fifaqatar.config.translation.TranslationKey;
import es.onebox.fifaqatar.config.translation.TranslationUtils;
import es.onebox.fifaqatar.error.TicketDetailNotFoundException;
import es.onebox.fifaqatar.adapter.FifaQatarMappingHelper;
import es.onebox.fifaqatar.adapter.dto.response.TicketPlanExtra;
import es.onebox.fifaqatar.adapter.dto.response.TicketResponse;
import es.onebox.fifaqatar.adapter.dto.response.TicketsResponse;
import es.onebox.fifaqatar.adapter.dto.response.orderdetail.OrderDetailResponse;
import es.onebox.fifaqatar.adapter.dto.response.orderdetail.OrderPrice;
import es.onebox.fifaqatar.adapter.dto.response.orderdetail.OrderPriceBreakdown;
import es.onebox.fifaqatar.adapter.dto.response.orderdetail.OrderTicket;
import es.onebox.fifaqatar.adapter.dto.response.ticketdetail.TicketDetailResponse;
import es.onebox.fifaqatar.adapter.mapping.TicketDetailMapping;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FifaQatarResponseMapper implements TicketMapper, TicketDetailMapper, OrderDetailMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(FifaQatarResponseMapper.class);

    private static final String IMAGE_LANDSCAPE = "IMG_BANNER_WEB";
    private static final String TEXT_COMM_ELEMENT = "TEXT_TITLE_WEB";

    private final MsEventRepository msEventRepository;
    private final VenueTemplateRepository venueTemplateRepository;
    private final FifaQatarMappingHelper fifaQatarMappingHelper;

    public FifaQatarResponseMapper(
            MsEventRepository msEventRepository,
            VenueTemplateRepository venueTemplateRepository,
            FifaQatarMappingHelper fifaQatarMappingHelper) {
        this.venueTemplateRepository = venueTemplateRepository;
        this.fifaQatarMappingHelper = fifaQatarMappingHelper;
        this.msEventRepository = msEventRepository;
    }

    public TicketsResponse map(List<OrderItem> items, MapperContext mapperContext) {
        if (CollectionUtils.isEmpty(items)) {
            TicketsResponse ticketsResponse = new TicketsResponse();
            ticketsResponse.setCount(0);
            ticketsResponse.setResults(Collections.EMPTY_LIST);

            return ticketsResponse;
        }

        var sessionIds = items.stream().map(item -> item.getTicket().getAllocation().getSession().getId()).distinct().collect(Collectors.toList());
        var eventIDs = items.stream().map(item -> item.getTicket().getAllocation().getEvent().getId()).distinct().collect(Collectors.toList());

        Map<Long, SessionCatalog> sessionsMap = new HashMap<>();
        Map<Long, List<SessionPassbookCommElement>> passbookElementsMap = new HashMap<>();
        Map<Long, EventCatalog> eventsMap = new HashMap<>();
        sessionIds.forEach(id -> {
            SessionCatalog sessionCatalog = msEventRepository.getSessionCatalog(id);
            sessionsMap.put(id, sessionCatalog);
            passbookElementsMap.put(id, msEventRepository.getSessionPassbookCommElements(sessionCatalog.getEventId(), sessionCatalog.getSessionId()));
        });
        eventIDs.forEach(id -> {
            eventsMap.put(id, msEventRepository.getEventCatalog(id));
        });

        Map<AbstractMap.SimpleEntry<String, Long>, List<OrderItem>> itemsByOrderAndSession = items.stream()
                .collect(Collectors.groupingBy(p -> new AbstractMap.SimpleEntry<>(
                        p.getOrder().getCode(),
                        p.getTicket().getAllocation().getSession().getId())
                ));

        List<TicketResponse> tickets = itemsByOrderAndSession.entrySet().stream().map(e -> {
            var orderCode = e.getKey().getKey();
            var sessionId = e.getKey().getValue();
            var sessionCatalog = sessionsMap.get(sessionId);

            mapperContext.setSessionCatalog(sessionCatalog);
            mapperContext.setEventCatalog(eventsMap.get(sessionCatalog.getEventId()));
            mapperContext.setSessionPassbookCommElements(passbookElementsMap.get(sessionId));

            return map(new TicketResponse(), orderCode, sessionId.intValue(), e.getValue(), mapperContext);
        }).collect(Collectors.toList());
        var now = ZonedDateTime.now();
        tickets.sort(Comparator.comparingLong(
                ticket -> {
                    ZonedDateTime start = ticket.getSessionStart();
                    ZonedDateTime end = ticket.getSessionEnd();

                    if (end != null && end.isBefore(now)) {
                        return Long.MAX_VALUE;
                    }
                    if (start != null) {
                        return Math.abs(Duration.between(start, now).toMillis());
                    }

                    return Long.MAX_VALUE;
                }));

        TicketsResponse ticketsResponse = new TicketsResponse();
        ticketsResponse.setResults(tickets);
        ticketsResponse.setCount(tickets.size());

        return ticketsResponse;
    }

    private <T extends TicketResponse> T map(T ticketResponse, String orderCode, Integer sessionId, List<OrderItem> items, MapperContext mapperContext) {
        var item = items.get(0);

        TicketDetailMapping ticketDetailMapping = fifaQatarMappingHelper.getByOrderCodeAndSessionId(orderCode, sessionId.longValue());
        Integer orderId = fifaQatarMappingHelper.getOrderMappingByOrderCode(orderCode);


        ticketResponse.setId(ticketDetailMapping != null ? Integer.parseInt(ticketDetailMapping.getId()) : fifaQatarMappingHelper.createTicketDetailMapping(orderCode, sessionId.longValue()));
        ticketResponse.setStatus(1);
        ticketResponse.setOrderId(orderId != null ? orderId : fifaQatarMappingHelper.createOrderDetailMapping(orderCode));
        ticketResponse.setOrderExternalId(orderCode);
        List<OrderItem> ownerItems = MapperUtils.filterOrderItemsByOwnerAndNotTransferred(items, mapperContext.getCurrentCustomer().getUserId());
        List<OrderItem> receivedItems = MapperUtils.filterOrderItemsByReceiver(items, mapperContext.getCurrentCustomer().getUserId());
        if (CollectionUtils.isNotEmpty(receivedItems)) {
            ticketResponse.setNumTickets(receivedItems.size());
        } else {
            ticketResponse.setNumTickets(ownerItems.size());
        }
        ticketResponse.setCanValidate(Boolean.TRUE);
        ticketResponse.setValidateCount(0);

        Session session = item.getTicket().getAllocation().getSession();//TODO como session label el nombre del evento
        SessionCatalog sessionCatalog = mapperContext.getSessionCatalog();
        EventCatalog eventCatalog = mapperContext.getEventCatalog();
        String sessionTitle = buildSessionName(TEXT_COMM_ELEMENT, sessionCatalog, mapperContext.getCurrentLang(), eventCatalog.getEventDefaultLanguage());
        ticketResponse.setPlanName(StringUtils.isNotBlank(sessionTitle) ? sessionTitle : session.getName());
        ticketResponse.setPlanId(session.getId().intValue());
        ZonedDateTime startDate = Instant.ofEpochMilli(sessionCatalog.getBeginSessionDate()).atZone(ZoneId.of("UTC"));
        ticketResponse.setSessionStart(startDate);
        ticketResponse.setSessionEnd(MapperUtils.getSessionEndDate(sessionCatalog));

        ticketResponse.setTimezone(item.getTicket().getAllocation().getVenue().getTimeZone());
        ticketResponse.setPlaceName(item.getTicket().getAllocation().getVenue().getName());

        String sessionPassbookImage = buildSessionPassbookUrl(mapperContext.getCurrentLang(), mapperContext.getSessionPassbookCommElements());
        String sessionImageLandscape = buildSessionImageUrl(IMAGE_LANDSCAPE, sessionCatalog, mapperContext.getCurrentLang(), eventCatalog.getEventDefaultLanguage());
        String eventImageLandscape = buildEventImageUrl(IMAGE_LANDSCAPE, eventCatalog, mapperContext.getCurrentLang(), eventCatalog.getEventDefaultLanguage());
        if (sessionPassbookImage != null) {
            ticketResponse.setPlanCoverImage(sessionPassbookImage);
        } else if (sessionImageLandscape != null) {
            ticketResponse.setPlanCoverImage(sessionImageLandscape);
        } else {
            ticketResponse.setPlanCoverImage(eventImageLandscape);
        }
        ticketResponse.setMultiplePlaces(Boolean.FALSE);

        TicketPlanExtra ticketPlanExtra = new TicketPlanExtra();
        ticketPlanExtra.setDisallowIndexing(Boolean.FALSE);
        ticketPlanExtra.setTimeless(Boolean.FALSE);
        ticketPlanExtra.setUrgency(Boolean.FALSE);
        ticketPlanExtra.setDisallowIndexing(Boolean.FALSE);
        ticketResponse.setPlanExtra(ticketPlanExtra);


        return ticketResponse;
    }

    public TicketDetailResponse mapTicketDetail(List<OrderItem> items, MapperContext mapperContext) {
        if (CollectionUtils.isEmpty(items)) {
            throw new TicketDetailNotFoundException();
        }
        var sessionIds = items.stream().map(item -> item.getTicket().getAllocation().getSession().getId()).distinct().collect(Collectors.toList());
        var eventIDs = items.stream().map(item -> item.getTicket().getAllocation().getEvent().getId()).distinct().collect(Collectors.toList());
        var sessionId = sessionIds.get(0);
        var eventId = eventIDs.get(0);

        var eventCatalog = msEventRepository.getEventCatalog(eventId);
        mapperContext.setEventCatalog(eventCatalog);

        var sessionCatalog = msEventRepository.getSessionCatalog(sessionId);
        mapperContext.setSessionCatalog(sessionCatalog);

        var sessionSecMktConfig = msEventRepository.getSessionSecMktConfig(sessionId);
        mapperContext.setSessionSecMktConfig(sessionSecMktConfig);

        List<SessionPassbookCommElement> sessionPassbookCommElements = msEventRepository.getSessionPassbookCommElements(eventId, sessionId);
        mapperContext.setSessionPassbookCommElements(sessionPassbookCommElements);

        var venue = venueTemplateRepository.getVenue(items.get(0).getTicket().getAllocation().getVenue().getId());
        var ticketDetail = map(new TicketDetailResponse(), items.get(0).getOrder().getCode(), sessionId.intValue(), items, mapperContext);
        List<OrderItem> ownerItems = MapperUtils.filterOrderItemsByOwnerAndNotTransferred(items, mapperContext.getCurrentCustomer().getUserId());
        List<OrderItem> receivedItems = MapperUtils.filterOrderItemsByReceiver(items, mapperContext.getCurrentCustomer().getUserId());

        ticketDetail.setReleaseConditions(mapReleaseCondition(mapperContext));
        if (!mustFillInfo(mapperContext.getCurrentCustomer()) && !isDeliveryActive(mapperContext) && !isSessionFinished(mapperContext.getSessionCatalog())) {
            //Release condition if ownertickets > 50
            if (CollectionUtils.isNotEmpty(ownerItems) && ownerItems.size() > mapperContext.getMainConfig().getMaxBarcodesByTicketDetail().intValue()) {
                ticketDetail.setReleaseConditions(mapMaxTicketCondition(mapperContext, sessionId, ownerItems.get(0).getOrder().getCode()));
                ticketDetail.setCodes(List.of());
            } else {
                ticketDetail.setCodes(mapCodes(CollectionUtils.isNotEmpty(receivedItems) ? receivedItems : ownerItems, mapperContext));
            }
        } else {
            ticketDetail.setCodes(List.of());
        }
        if (!mustFillInfo(mapperContext.getCurrentCustomer())) {
            if (CollectionUtils.isNotEmpty(receivedItems)) {
                ticketDetail.setManagement(mapTransferManagement(mapperContext, sessionId, items.get(0).getOrder().getCode()));
            } else {
                ticketDetail.setManagement(mapManagement(mapperContext, sessionId, items.get(0).getOrder().getCode()));
            }
            if (CollectionUtils.isNotEmpty(ownerItems)) {
                ticketDetail.setSecMktManagement(maSecMktManagement(mapperContext));
            }
        }

        String eventTile = buildEventName(TEXT_COMM_ELEMENT, eventCatalog, mapperContext.getCurrentLang(), eventCatalog.getEventDefaultLanguage());
        ticketDetail.setLabel(StringUtils.isNotBlank(eventTile) ? eventTile : eventCatalog.getEventName());
        ticketDetail.setInstructions(mapInstructions(sessionCatalog, mapperContext.getCurrentLang(), eventCatalog.getEventDefaultLanguage()));
        ticketDetail.setTicketType("session_ticket");
        ticketDetail.setSessionId(sessionId.intValue());
        ticketDetail.setValidationMethod("qr_code");
        ticketDetail.setTransferable(Boolean.FALSE);
        ticketDetail.setTransferred(CollectionUtils.isNotEmpty(receivedItems) ? Boolean.TRUE : Boolean.FALSE);
        ticketDetail.setReschedulable(Boolean.FALSE);
        ticketDetail.setExchangeable(Boolean.FALSE);
        ticketDetail.setTicketsToTransfer(0);
        ticketDetail.setPlaceName(venue.getName());
        var places = mapPlaces(venue, items.get(0).getPrice().getCurrency());
        ticketDetail.setPlaces(mapPlaces(venue, items.get(0).getPrice().getCurrency()));
        ticketDetail.setPlace(places.get(0));
        ticketDetail.setTicketPrice(mapTicketPrice(items));
        var itemPrices = items.stream().map(OrderItem::getPrice).collect(Collectors.toList());
        ticketDetail.setDiscountApplied(mapDiscountApplied(items));
        ticketDetail.setSurchargeApplied(mapSurchargeApplied(itemPrices));
        ticketDetail.setSeatingSummary(CollectionUtils.isNotEmpty(receivedItems) ? mapSeatingSummary(receivedItems, mapperContext) : mapSeatingSummary(ownerItems, mapperContext));
        ticketDetail.setExtraInfo(CollectionUtils.isNotEmpty(receivedItems) ? mapExtraInfo(receivedItems, mapperContext) : mapExtraInfo(ownerItems, mapperContext));

        return ticketDetail;
    }

    public OrderDetailResponse mapOrderDetail(Integer orderId, OrderDetail order, MapperContext mapperContext) {
        OrderDetailResponse response = new OrderDetailResponse();

        response.setId(orderId);
        response.setExternalId(order.getCode());

        var sessionIds = order.getItems().stream().map(item -> item.getTicket().getAllocation().getSession().getId()).distinct().collect(Collectors.toList());
        var eventIDs = order.getItems().stream().map(item -> item.getTicket().getAllocation().getEvent().getId()).distinct().collect(Collectors.toList());

        Map<Long, SessionCatalog> sessionsMap = new HashMap<>();
        Map<Long, EventCatalog> eventsMap = new HashMap<>();

        sessionIds.forEach(id -> {
            sessionsMap.put(id, msEventRepository.getSessionCatalog(id));
        });
        eventIDs.forEach(id -> {
            eventsMap.put(id, msEventRepository.getEventCatalog(id));
        });

        var sessionCatalog = sessionsMap.get(sessionIds.get(0));
        var eventCatalog = msEventRepository.getEventCatalog(sessionCatalog.getEventId());

        response.setPlanId(sessionCatalog.getSessionId().intValue());
        response.setPlanName(sessionCatalog.getSessionName());
        String sessionImageLandscape = buildSessionImageUrl(IMAGE_LANDSCAPE, sessionCatalog, null, eventCatalog.getEventDefaultLanguage());
        String eventImageLandscape = buildEventImageUrl(IMAGE_LANDSCAPE, eventCatalog, null, eventCatalog.getEventDefaultLanguage());
        response.setPlanCoverImage(sessionImageLandscape != null ? sessionImageLandscape : eventImageLandscape);

        response.setPriceBreakdownItems(List.of());

        OrderPriceBreakdown orderPriceBreakdownFooter = new OrderPriceBreakdown();
        orderPriceBreakdownFooter.setLabel(TranslationUtils.getText(TranslationKey.ORDER_PRICE_BREAKDOWN_LABEL, mapperContext.getCurrentLang(), mapperContext.getDictionary()));
        orderPriceBreakdownFooter.setType("total_price");
        orderPriceBreakdownFooter.setSchemaType("price");
        OrderPrice orderPrice = new OrderPrice();
        orderPrice.setAmount(order.getPrice().getFinalPrice());
        orderPrice.setCurrency(order.getPrice().getCurrency());
        orderPriceBreakdownFooter.setPrice(orderPrice);

        response.setPriceBreakdownItemsFooter(List.of(orderPriceBreakdownFooter));

        var orderTickets = order.getItems().stream().map(item -> {
            var orderTicket = new OrderTicket();
            orderTicket.setId(item.getId().intValue());
            orderTicket.setStatus(1); //TODO
            orderTicket.setQuantity(1);
            orderTicket.setExchangeable(Boolean.FALSE);
            orderTicket.setExpressExchangeable(Boolean.FALSE);
            orderTicket.setTransferable(Boolean.FALSE);
            orderTicket.setUpgradeable(Boolean.FALSE);
            orderTicket.setInstructions("");
            orderTicket.setCodes(List.of());

            var sessionId = item.getTicket().getAllocation().getSession().getId();
            var venueId = item.getTicket().getAllocation().getVenue().getId();
            var itemVenue = venueTemplateRepository.getVenue(venueId);
            orderTicket.setSessionId(sessionId.intValue());
            orderTicket.setSessionLabel(item.getTicket().getAllocation().getPriceType().getName() + " - " + item.getTicket().getRate().getName());
            orderTicket.setSessionPlaceName(itemVenue.getName());
            orderTicket.setSessionGooglePlaceId(itemVenue.getGooglePlaceId());
            orderTicket.setSessionCityCode("");
            orderTicket.setStarts(item.getTicket().getAllocation().getSession().getDate().getStart().withZoneSameInstant(ZoneId.of("UTC")));
            orderTicket.setEnd(item.getTicket().getAllocation().getSession().getDate().getStart().plusHours(5).withZoneSameInstant(ZoneId.of("UTC"))); //TODO: agree and end session
            orderTicket.setTimezone(item.getTicket().getAllocation().getVenue().getTimeZone());

            OrderPrice itemPrice = new OrderPrice();
            itemPrice.setAmount(item.getPrice().getFinalAmount());
            itemPrice.setCurrency(item.getPrice().getCurrency());

            orderTicket.setTotalPrice(itemPrice);
            orderTicket.setUnitPrice(itemPrice);
            orderTicket.setSurchargeApplied(mapSurchargeApplied(List.of(item.getPrice())));

            OrderPriceBreakdown orderPriceBreakdown = new OrderPriceBreakdown();
            orderPriceBreakdown.setType("ticket_price");
            orderPriceBreakdown.setSchemaType("price");
            orderPriceBreakdown.setPrice(itemPrice);
            Currency instance = Currency.getInstance(itemPrice.getCurrency());
            String breakdownLabel = TranslationUtils.translateOrderItemBreakdownLabel(mapperContext.getCurrentLang(), mapperContext.getDictionary(), instance.getSymbol(), String.valueOf(itemPrice.getAmount()));
            orderPriceBreakdown.setLabel(breakdownLabel);
            orderTicket.setPriceBreakdownItems(List.of(orderPriceBreakdown));

            return orderTicket;
        }).collect(Collectors.toList());

        response.setTickets(orderTickets);

        return response;
    }
}
