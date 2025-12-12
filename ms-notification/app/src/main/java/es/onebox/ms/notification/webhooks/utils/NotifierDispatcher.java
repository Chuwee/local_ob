package es.onebox.ms.notification.webhooks.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.mapper.JsonMapper;
import es.onebox.dal.dto.couch.enums.OrderState;
import es.onebox.dal.dto.couch.enums.OrderType;
import es.onebox.dal.dto.couch.order.OrderDTO;
import es.onebox.ms.notification.common.dto.MemberOrderDTO;
import es.onebox.ms.notification.common.utils.GeneratorUtils;
import es.onebox.ms.notification.datasources.ms.channel.dto.Channel;
import es.onebox.ms.notification.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.ms.notification.datasources.ms.crm.dto.CrmClientDocResponse;
import es.onebox.ms.notification.datasources.ms.crm.dto.CrmClientResponse;
import es.onebox.ms.notification.datasources.ms.crm.dto.CrmOrderContainer;
import es.onebox.ms.notification.datasources.ms.crm.dto.CrmOrderResponse;
import es.onebox.ms.notification.datasources.ms.crm.dto.CrmProductDocResponse;
import es.onebox.ms.notification.datasources.ms.crm.repository.AuditCrmRepository;
import es.onebox.ms.notification.datasources.ms.event.dto.Event;
import es.onebox.ms.notification.datasources.ms.event.dto.Product;
import es.onebox.ms.notification.datasources.ms.event.repository.EventsRepository;
import es.onebox.ms.notification.datasources.ms.order.repository.OrdersRepository;
import es.onebox.ms.notification.exception.MsNotificationErrorCode;
import es.onebox.ms.notification.webhooks.dto.AbandonedPreorderPayloadDTO;
import es.onebox.ms.notification.webhooks.dto.B2BBalancePayloadDTO;
import es.onebox.ms.notification.webhooks.dto.CatalogPayloadDTO;
import es.onebox.ms.notification.webhooks.dto.ChannelPayloadDTO;
import es.onebox.ms.notification.webhooks.dto.EntityPayloadDTO;
import es.onebox.ms.notification.webhooks.dto.EventNotificationMessage;
import es.onebox.ms.notification.webhooks.dto.ItemPayloadDTO;
import es.onebox.ms.notification.webhooks.dto.MemberOrderPayloadDTO;
import es.onebox.ms.notification.webhooks.dto.NotificationConfigDTO;
import es.onebox.ms.notification.webhooks.dto.NotificationMessageDTO;
import es.onebox.ms.notification.webhooks.dto.OrderPayloadDTO;
import es.onebox.ms.notification.webhooks.dto.PayloadRequest;
import es.onebox.ms.notification.webhooks.dto.ProductPayloadDTO;
import es.onebox.ms.notification.webhooks.dto.PromotionPayloadDTO;
import es.onebox.ms.notification.webhooks.dto.UserPayloadDTO;
import es.onebox.ms.notification.webhooks.dto.WrapperDTO;
import es.onebox.ms.notification.webhooks.enums.NotificationAction;
import es.onebox.ms.notification.webhooks.enums.NotificationSubtype;
import es.onebox.ms.notification.webhooks.enums.NotificationType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NotifierDispatcher {

    private final OrdersRepository ordersRepository;
    private final EventsRepository eventsRepository;
    private final AuditCrmRepository auditCrmRepository;
    private final ChannelRepository channelRepository;

    @Value("${onebox.gateway.url}")
    private String gatewayUrl;

    @Autowired
    public NotifierDispatcher(OrdersRepository ordersRepository,
                              EventsRepository eventsRepository,
                              AuditCrmRepository auditCrmRepository,
                              ChannelRepository channelRepository) {
        this.ordersRepository = ordersRepository;
        this.eventsRepository = eventsRepository;
        this.auditCrmRepository = auditCrmRepository;
        this.channelRepository = channelRepository;
    }

    public WrapperDTO getWrapper(EventNotificationMessage message) {
        if (message == null) {
            return null;
        }
        WrapperDTO wrapperDTO = new WrapperDTO();
        switch (message.getEvent()) {
            case "ORDER" -> buildOrderWrapper(message, wrapperDTO);
            case "ITEM" -> buildItemWrapper(message, wrapperDTO);
            case "MEMBERORDER" -> buildMemberOrderWrapper(message, wrapperDTO);
            case "PREORDER" -> buildPreorderWrapper(message, wrapperDTO);
            case "EVENT" -> buildEventWrapper(message, wrapperDTO);
            case "SESSION" -> buildSessionWrapper(message, wrapperDTO);
            case "PROMOTION" -> buildPromotionWrapper(message, wrapperDTO);
            case "CHANNEL" -> buildChannelWrapper(message, wrapperDTO);
            case "B2BBALANCE" -> buildB2BBalanceWrapper(message, wrapperDTO);
            case "PRODUCT" -> buildProductWrapper(message, wrapperDTO);
            case "ENTITY_FVZONE" -> buildEntityFvZoneWrapper(message, wrapperDTO);
            case "USER_FVZONE" -> buildUserFvZoneWrapper(message, wrapperDTO);
            default -> throw OneboxRestException.builder(MsNotificationErrorCode.EVENT_NOT_FOUND).build();
        }

        return wrapperDTO;
    }



    public NotificationMessageDTO generateMessage(WrapperDTO wrapperDTO, NotificationConfigDTO config, NotificationType type) throws JsonProcessingException {
        NotificationMessageDTO messageDTO = new NotificationMessageDTO();

        switch (wrapperDTO.getPayloadRequest().getEvent()) {
            case "ORDER" -> buildOrderMessage(wrapperDTO, config, messageDTO);
            case "ITEM" -> buildItemMessage(wrapperDTO, config, messageDTO);
            case "MEMBERORDER" -> buildMemberOrderMessage(wrapperDTO, config, messageDTO);
            case "PREORDER" -> buildPreorderMessage(wrapperDTO, config, messageDTO);
            case "EVENT", "SESSION" -> buildCatalogMessage(wrapperDTO, config, messageDTO);
            case "PROMOTION" -> buildPromotionMessage(wrapperDTO, config, messageDTO);
            case "CHANNEL" -> buildChannelMessage(wrapperDTO, config, messageDTO);
            case "B2BBALANCE" -> buildB2BBalanceMessage(wrapperDTO, config, messageDTO);
            case "PRODUCT" -> buildProductMessage(wrapperDTO, config, messageDTO);
            case "ENTITY_FVZONE" -> buildEntityFvZoneMessage(wrapperDTO, config, messageDTO);
            case "USER_FVZONE" -> buildUserFvZoneMessage(wrapperDTO, config, messageDTO);
        }

        return messageDTO;
    }


    private void buildOrderWrapper(EventNotificationMessage message, WrapperDTO wrapperDTO) {
        OrderDTO orderDTO = ordersRepository.getOrderByCode(message.getOrderCode());
        wrapperDTO.setEntityId(orderDTO.getOrderData().getChannelEntityId().longValue());
        wrapperDTO.setChannelId(orderDTO.getOrderData().getChannelId().longValue());

        String link = gatewayUrl + "/orders-mgmt-api/v1/orders/";
        OrderPayloadDTO orderPayloadDTO = new OrderPayloadDTO();
        orderPayloadDTO.setOrderCode(orderDTO.getCode());
        orderPayloadDTO.setPrevOrderCode(orderDTO.getRelatedOriginalCode());
        orderPayloadDTO.setEvent(message.getEvent());
        orderPayloadDTO.setReimbursement(message.getReimbursement());
        orderPayloadDTO.setUrl(link);
        if (NotificationAction.PRINT.name().equals(message.getAction())) {
            orderPayloadDTO.setAction(NotificationAction.PRINT);
            orderPayloadDTO.setPrintStatus(message.getPrintStatus());
        } else if (NotificationAction.RELOCATE.name().equals(message.getAction())) {
            orderPayloadDTO.setAction(NotificationAction.RELOCATE);
        } else {
            if (orderDTO.getStatus().getType().equals(OrderType.PURCHASE) && orderDTO.getStatus().getState().equals(OrderState.PAID)) {
                orderPayloadDTO.setAction(NotificationAction.PURCHASE);
                orderPayloadDTO.setReimbursement(null);
            }
            if (orderDTO.getStatus().getType().equals(OrderType.BOOKING) && orderDTO.getStatus().getState().equals(OrderState.PAID)) {
                orderPayloadDTO.setAction(NotificationAction.BOOKING);
            }
            if (orderDTO.getStatus().getType().equals(OrderType.REFUND)) {
                orderPayloadDTO.setAction(NotificationAction.REFUND);
            }
            if (orderDTO.getStatus().getType().equals(OrderType.PURCHASE) && orderDTO.getStatus().getState().equals(OrderState.CANCELLED)) {
                orderPayloadDTO.setAction(NotificationAction.CANCEL);
            }
        }
        if (orderPayloadDTO.getAction() == null) {
            throw OneboxRestException.builder(MsNotificationErrorCode.ORDER_ACTION_NOT_FOUND).build();
        }
        wrapperDTO.setPayloadRequest(orderPayloadDTO);
    }

    private void buildItemWrapper(EventNotificationMessage message, WrapperDTO wrapperDTO) {
        OrderDTO orderDTO = ordersRepository.getOrderByCode(message.getOrderCode());
        wrapperDTO.setEntityId(orderDTO.getOrderData().getChannelEntityId().longValue());
        wrapperDTO.setChannelId(orderDTO.getOrderData().getChannelId().longValue());

        String link = gatewayUrl + "/orders-mgmt-api/v1/orders/";
        ItemPayloadDTO itemPayloadDTO = new ItemPayloadDTO();
        itemPayloadDTO.setOrderCode(orderDTO.getCode());
        itemPayloadDTO.setItemId(message.getItemId());
        itemPayloadDTO.setEvent(message.getEvent());
        itemPayloadDTO.setUrl(link);
        itemPayloadDTO.setAction(NotificationAction.TRANSFER);
        if (message.getNotificationSubtype() != null) {
            itemPayloadDTO.setNotificationSubtype(NotificationSubtype.valueOf(message.getNotificationSubtype()));
        }
        if (itemPayloadDTO.getAction() == null) {
            throw OneboxRestException.builder(MsNotificationErrorCode.ORDER_ACTION_NOT_FOUND).build();
        }
        wrapperDTO.setPayloadRequest(itemPayloadDTO);
    }

    private void buildMemberOrderWrapper(EventNotificationMessage message, WrapperDTO wrapperDTO) {
        MemberOrderDTO memberOrderDTO = ordersRepository.getCouchbaseMemberOrderByCode(message.getOrderCode());
        wrapperDTO.setEntityId(memberOrderDTO.getEntityId());
        wrapperDTO.setChannelId(memberOrderDTO.getChannelId());

        String link = gatewayUrl + "/orders-mgmt-api/v1/member-orders/";
        MemberOrderPayloadDTO memberOrderPayloadDTO = new MemberOrderPayloadDTO();
        memberOrderPayloadDTO.setMemberOrderCode(memberOrderDTO.getCode());
        memberOrderPayloadDTO.setEvent(message.getEvent());
        memberOrderPayloadDTO.setUrl(link);
        switch (memberOrderDTO.getType()) {
            case RENEWAL, CHANGE_SEAT, BUY_SEAT -> memberOrderPayloadDTO.setAction(NotificationAction.PURCHASE);
            case TRANSFER_SEAT, RELEASE_SEAT, RECOVER_SEAT ->
                    memberOrderPayloadDTO.setAction(NotificationAction.UPDATE);
            default -> throw OneboxRestException.builder(MsNotificationErrorCode.MEMBER_ORDER_ACTION_NOT_FOUND).build();
        }
        wrapperDTO.setPayloadRequest(memberOrderPayloadDTO);
    }

    private void buildPreorderWrapper(EventNotificationMessage message, WrapperDTO wrapperDTO) {
        Long entityId = message.getId();
        CrmOrderResponse auditCrmOrders = auditCrmRepository.getAuditCrmOrders(message.getOrderCode(), entityId);
        if (CollectionUtils.isEmpty(auditCrmOrders.getPurchases())) {
            throw OneboxRestException.builder(MsNotificationErrorCode.PREORDER_ACTION_NOT_FOUND).build();
        }

        CrmOrderContainer crmOrder = auditCrmOrders.getPurchases().get(0);
        String buyerEmail = crmOrder.getOrder().getUser();

        AbandonedPreorderPayloadDTO payload = new AbandonedPreorderPayloadDTO();
        payload.setOrderCode(crmOrder.getOrder().getId());
        payload.setEmail(buyerEmail);
        payload.setSessionIds(crmOrder.getProducts().stream().map(CrmProductDocResponse::getSession).toList());
        payload.setEvent(message.getEvent());
        payload.setAction(NotificationAction.ABANDONED);

        CrmClientResponse auditCrmBuyers = auditCrmRepository.getAuditCrmBuyers(buyerEmail, entityId);
        if (!CollectionUtils.isEmpty(auditCrmBuyers.getBuyers())) {
            CrmClientDocResponse crmBuyer = auditCrmBuyers.getBuyers().get(0);
            payload.setName(crmBuyer.getName());
            payload.setSurname(crmBuyer.getSurname());
            payload.setAllowCommercialMailing(crmBuyer.getNewsletter_agreement());
        }

        wrapperDTO.setEntityId(entityId);
        wrapperDTO.setPayloadRequest(payload);
    }

    private void buildEventWrapper(EventNotificationMessage message, WrapperDTO wrapperDTO) {

        Event event = eventsRepository.getEvent(message.getId());

        if (event == null) {
            throw OneboxRestException.builder(MsNotificationErrorCode.EVENT_NOT_FOUND).build();
        }
        wrapperDTO.setEntityId(event.getEntityId());
        wrapperDTO.setPayloadRequest(buildCatalogPayload(message));
    }

    private void buildSessionWrapper(EventNotificationMessage message, WrapperDTO wrapperDTO) {
        Long entityId;

        if (message.getNotificationSubtype().equals(NotificationSubtype.SESSION_DELETED.name())) {
            entityId = eventsRepository.getEvent(message.getEventId()).getEntityId();
        } else {
            entityId = eventsRepository.getSession(message.getId()).getEntityId();
        }

        wrapperDTO.setEntityId(entityId);
        wrapperDTO.setPayloadRequest(buildCatalogPayload(message));
    }

    private void buildPromotionWrapper(EventNotificationMessage message, WrapperDTO wrapperDTO) {
        Event event = eventsRepository.getEvent(message.getEventId());
        wrapperDTO.setEntityId(event.getEntityId());
        wrapperDTO.setPayloadRequest(buildPromotionPayload(message));
    }

    private void buildChannelWrapper(EventNotificationMessage message, WrapperDTO wrapperDTO) {
        Channel channel = channelRepository.getChannel(message.getId());
        wrapperDTO.setEntityId(channel.getEntityId());
        wrapperDTO.setPayloadRequest(buildChannelPayload(message));
    }

    private void buildB2BBalanceWrapper(EventNotificationMessage message, WrapperDTO wrapperDTO) {
        wrapperDTO.setEntityId(message.getId());

        B2BBalancePayloadDTO b2BBalancePayloadDTO = new B2BBalancePayloadDTO();
        b2BBalancePayloadDTO.setMovementId(message.getB2bMovementId());
        b2BBalancePayloadDTO.setAction(NotificationAction.valueOf(message.getAction()));
        b2BBalancePayloadDTO.setEvent(message.getEvent());

        wrapperDTO.setPayloadRequest(b2BBalancePayloadDTO);
    }

    private void buildProductWrapper(EventNotificationMessage message, WrapperDTO wrapperDTO) {
        Product product = eventsRepository.getProduct(message.getId());

        if (product == null) {
            throw OneboxRestException.builder(MsNotificationErrorCode.PRODUCT_NOT_FOUND).build();
        }
        wrapperDTO.setEntityId(product.getEntity().getId());
        wrapperDTO.setPayloadRequest(buildProductPayload(message));
    }

    private ProductPayloadDTO buildProductPayload(EventNotificationMessage message) {
        ProductPayloadDTO productPayloadDTO = new ProductPayloadDTO();
        productPayloadDTO.setId(message.getId());
        productPayloadDTO.setEvent(message.getEvent());
        productPayloadDTO.setChannelId(message.getChannelId());
        productPayloadDTO.setAction(NotificationAction.CATALOG);
        if (message.getEventId() != null) {
            productPayloadDTO.setEventId(message.getEventId());
        }
        if (message.getNotificationSubtype() != null) {
            productPayloadDTO.setNotificationSubtype(NotificationSubtype.valueOf(message.getNotificationSubtype()));
        }
        return productPayloadDTO;
    }

    private void buildEntityFvZoneWrapper(EventNotificationMessage message, WrapperDTO wrapperDTO) {
        wrapperDTO.setEntityId(message.getId());

        EntityPayloadDTO payload = new EntityPayloadDTO();
        payload.setId(message.getId());
        payload.setEvent(message.getEvent());
        payload.setAction(NotificationAction.valueOf(message.getAction()));

        wrapperDTO.setPayloadRequest(payload);
    }

    private void buildUserFvZoneWrapper(EventNotificationMessage message, WrapperDTO wrapperDTO) {
        wrapperDTO.setEntityId(message.getId());

        UserPayloadDTO payload = new UserPayloadDTO();
        payload.setId(message.getId());
        payload.setEvent(message.getEvent());
        payload.setAction(NotificationAction.valueOf(message.getAction()));

        wrapperDTO.setPayloadRequest(payload);
    }

    private static CatalogPayloadDTO buildCatalogPayload(EventNotificationMessage message) {
        CatalogPayloadDTO catalogPayloadDTO = new CatalogPayloadDTO();
        catalogPayloadDTO.setId(message.getId());
        catalogPayloadDTO.setEvent(message.getEvent());
        catalogPayloadDTO.setTemplateId(message.getTemplateId());
        catalogPayloadDTO.setRateId(message.getRateId());
        catalogPayloadDTO.setChannelId(message.getChannelId());
        catalogPayloadDTO.setPriceTypeId(message.getPriceTypeId());
        catalogPayloadDTO.setEventId(message.getEventId());
        catalogPayloadDTO.setAction(NotificationAction.CATALOG);
        if (message.getNotificationSubtype() != null) {
            catalogPayloadDTO.setNotificationSubtype(NotificationSubtype.valueOf(message.getNotificationSubtype()));
        }

        Optional.ofNullable(message.getPromotionId()).ifPresent(catalogPayloadDTO::setPromotionId);

        return catalogPayloadDTO;
    }


    private static PromotionPayloadDTO buildPromotionPayload(EventNotificationMessage message) {
        PromotionPayloadDTO promotionPayloadDTO = new PromotionPayloadDTO();
        promotionPayloadDTO.setEvent(message.getEvent());
        promotionPayloadDTO.setAction(NotificationAction.valueOf(message.getAction()));
        promotionPayloadDTO.setEventId(message.getEventId());
        promotionPayloadDTO.setPromotionId(message.getPromotionId());
        promotionPayloadDTO.setSessionId(message.getSessionId());
        promotionPayloadDTO.setPromotionActive(message.isPromotionActive());
        return promotionPayloadDTO;
    }

    private PayloadRequest buildChannelPayload(EventNotificationMessage message) {
        ChannelPayloadDTO channelPayloadDTO = new ChannelPayloadDTO();
        channelPayloadDTO.setId(message.getId());
        channelPayloadDTO.setSubtype(NotificationSubtype.valueOf(message.getNotificationSubtype()));
        channelPayloadDTO.setAction(NotificationAction.valueOf(message.getAction()));
        channelPayloadDTO.setEvent(message.getEvent());
        channelPayloadDTO.setEventId(message.getEventId());
        return channelPayloadDTO;
    }

    private static void buildOrderMessage(WrapperDTO wrapperDTO, NotificationConfigDTO config, NotificationMessageDTO messageDTO) throws JsonProcessingException {
        OrderPayloadDTO orderPayloadDTO = (OrderPayloadDTO) wrapperDTO.getPayloadRequest();
        OrderPayloadDTO newOrderPayloadDTO = new OrderPayloadDTO();
        newOrderPayloadDTO.setOrderCode(orderPayloadDTO.getOrderCode());
        newOrderPayloadDTO.setPrevOrderCode(orderPayloadDTO.getPrevOrderCode());
        newOrderPayloadDTO.setUrl(orderPayloadDTO.getUrl() + orderPayloadDTO.getOrderCode());
        newOrderPayloadDTO.setReimbursement(orderPayloadDTO.getReimbursement());
        newOrderPayloadDTO.setPrintStatus(orderPayloadDTO.getPrintStatus());
        messageDTO.setConfId(config.getDocumentId());
        messageDTO.setEvent(orderPayloadDTO.getEvent());
        messageDTO.setAction(orderPayloadDTO.getAction().name());
        messageDTO.setCode(orderPayloadDTO.getOrderCode());
        messageDTO.setReimbursement(orderPayloadDTO.getReimbursement());
        messageDTO.setPayload(newOrderPayloadDTO);
        messageDTO.setSignature(generateSignature(config.getApiKey(), newOrderPayloadDTO));
        NotificationType type = NotificationType.valueOf(orderPayloadDTO.getEvent());
        if (orderPayloadDTO.getPrevOrderCode() != null && config.getEvents().containsKey(type) && config.getEvents().get(type).contains("UPDATE")) {
            messageDTO.setPrevCode(orderPayloadDTO.getPrevOrderCode());
            OrderPayloadDTO prevPayloadDTO = new OrderPayloadDTO();
            prevPayloadDTO.setOrderCode(orderPayloadDTO.getPrevOrderCode());
            prevPayloadDTO.setUrl(orderPayloadDTO.getUrl() + orderPayloadDTO.getPrevOrderCode());
            messageDTO.setPrevPayload(prevPayloadDTO);
            messageDTO.setPrevSignature(generateSignature(config.getApiKey(), prevPayloadDTO));
        }
    }

    private static void buildItemMessage(WrapperDTO wrapperDTO, NotificationConfigDTO config, NotificationMessageDTO messageDTO) throws JsonProcessingException {
        ItemPayloadDTO itemPayloadDTO = (ItemPayloadDTO) wrapperDTO.getPayloadRequest();
        ItemPayloadDTO newItemPayloadDTO = new ItemPayloadDTO();
        newItemPayloadDTO.setUrl(itemPayloadDTO.getUrl() + itemPayloadDTO.getOrderCode());
        newItemPayloadDTO.setOrderCode(itemPayloadDTO.getOrderCode());
        newItemPayloadDTO.setItemId(itemPayloadDTO.getItemId());
        if (itemPayloadDTO.getNotificationSubtype()!= null) {
            newItemPayloadDTO.setNotificationSubtype(itemPayloadDTO.getNotificationSubtype());
            messageDTO.setNotificationSubtype(itemPayloadDTO.getNotificationSubtype().name());
        }
        messageDTO.setConfId(config.getDocumentId());
        messageDTO.setEvent(itemPayloadDTO.getEvent());
        messageDTO.setAction(itemPayloadDTO.getAction().name());
        messageDTO.setCode(itemPayloadDTO.getOrderCode());
        messageDTO.setItemId(itemPayloadDTO.getItemId());
        messageDTO.setPayload(newItemPayloadDTO);
        messageDTO.setSignature(generateSignature(config.getApiKey(), newItemPayloadDTO));
    }

    private static void buildMemberOrderMessage(WrapperDTO wrapperDTO, NotificationConfigDTO config, NotificationMessageDTO messageDTO) throws JsonProcessingException {
        MemberOrderPayloadDTO memberOrderPayloadDTO = (MemberOrderPayloadDTO) wrapperDTO.getPayloadRequest();
        MemberOrderPayloadDTO newMemberOrderPayloadDTO = new MemberOrderPayloadDTO();
        newMemberOrderPayloadDTO.setMemberOrderCode(memberOrderPayloadDTO.getMemberOrderCode());
        newMemberOrderPayloadDTO.setUrl(memberOrderPayloadDTO.getUrl() + memberOrderPayloadDTO.getMemberOrderCode());
        messageDTO.setConfId(config.getDocumentId());
        messageDTO.setEvent(memberOrderPayloadDTO.getEvent());
        messageDTO.setAction(memberOrderPayloadDTO.getAction().name());
        messageDTO.setCode(memberOrderPayloadDTO.getMemberOrderCode());
        messageDTO.setPayload(newMemberOrderPayloadDTO);
        messageDTO.setSignature(generateSignature(config.getApiKey(), newMemberOrderPayloadDTO));
    }

    private void buildPreorderMessage(WrapperDTO wrapperDTO, NotificationConfigDTO config, NotificationMessageDTO messageDTO) throws JsonProcessingException {
        AbandonedPreorderPayloadDTO payload = (AbandonedPreorderPayloadDTO) wrapperDTO.getPayloadRequest();
        messageDTO.setConfId(config.getDocumentId());
        messageDTO.setEvent(payload.getEvent());
        messageDTO.setAction(payload.getAction().name());
        messageDTO.setCode(payload.getOrderCode());
        messageDTO.setPayload(payload);
        messageDTO.setSignature(generateSignature(config.getApiKey(), payload));
    }

    private static void buildCatalogMessage(WrapperDTO wrapperDTO, NotificationConfigDTO config, NotificationMessageDTO messageDTO) throws JsonProcessingException {
        CatalogPayloadDTO catalogPayloadDTO = (CatalogPayloadDTO) wrapperDTO.getPayloadRequest();
        CatalogPayloadDTO newCatalogPayloadDTO = new CatalogPayloadDTO();
        newCatalogPayloadDTO.setId(catalogPayloadDTO.getId());
        newCatalogPayloadDTO.setChannelId(catalogPayloadDTO.getChannelId());
        newCatalogPayloadDTO.setRateId(catalogPayloadDTO.getRateId());
        newCatalogPayloadDTO.setTemplateId(catalogPayloadDTO.getTemplateId());
        newCatalogPayloadDTO.setPriceTypeId(catalogPayloadDTO.getPriceTypeId());
        newCatalogPayloadDTO.setEventId(catalogPayloadDTO.getEventId());
        Optional.ofNullable(catalogPayloadDTO.getPromotionId()).ifPresent(newCatalogPayloadDTO::setPromotionId);
        messageDTO.setConfId(config.getDocumentId());
        messageDTO.setAction(catalogPayloadDTO.getAction().name());
        if (catalogPayloadDTO.getNotificationSubtype()!= null) {
           messageDTO.setNotificationSubtype(catalogPayloadDTO.getNotificationSubtype().name());
        }
        messageDTO.setCode(catalogPayloadDTO.getId().toString());
        switch (catalogPayloadDTO.getEvent()) {
            case "EVENT" -> messageDTO.setEvent("EVENT_UPDATE");
            case "SESSION" -> messageDTO.setEvent("SESSION_UPDATE");
            default -> messageDTO.setEvent(catalogPayloadDTO.getEvent());
        }
        messageDTO.setPayload(newCatalogPayloadDTO);
        messageDTO.setSignature(generateSignature(config.getApiKey(), newCatalogPayloadDTO));
    }

    private static void buildPromotionMessage(WrapperDTO wrapperDTO, NotificationConfigDTO config, NotificationMessageDTO messageDTO) throws JsonProcessingException {
        PromotionPayloadDTO promotionPayloadDTO = (PromotionPayloadDTO) wrapperDTO.getPayloadRequest();
        messageDTO.setConfId(config.getDocumentId());
        messageDTO.setEvent(promotionPayloadDTO.getEvent());
        messageDTO.setAction(promotionPayloadDTO.getAction().name());
        messageDTO.setPayload(promotionPayloadDTO);
        messageDTO.setSignature(generateSignature(config.getApiKey(), promotionPayloadDTO));
    }

    private void buildChannelMessage(WrapperDTO wrapperDTO, NotificationConfigDTO config, NotificationMessageDTO messageDTO) throws JsonProcessingException{
        ChannelPayloadDTO channelPayloadDTO = (ChannelPayloadDTO) wrapperDTO.getPayloadRequest();
        messageDTO.setConfId(config.getDocumentId());
        messageDTO.setNotificationSubtype(channelPayloadDTO.getSubtype().name());
        messageDTO.setAction(channelPayloadDTO.getAction().name());
        messageDTO.setPayload(channelPayloadDTO);
        messageDTO.setSignature(generateSignature(config.getApiKey(), channelPayloadDTO));
        messageDTO.setEvent(channelPayloadDTO.getEvent());
    }

    private void buildB2BBalanceMessage(WrapperDTO wrapperDTO, NotificationConfigDTO config, NotificationMessageDTO messageDTO) throws JsonProcessingException{
        B2BBalancePayloadDTO b2BBalancePayloadDTO = (B2BBalancePayloadDTO) wrapperDTO.getPayloadRequest();
        messageDTO.setConfId(config.getDocumentId());
        messageDTO.setAction(b2BBalancePayloadDTO.getAction().name());
        messageDTO.setPayload(b2BBalancePayloadDTO);
        messageDTO.setSignature(generateSignature(config.getApiKey(), b2BBalancePayloadDTO));
        messageDTO.setEvent(b2BBalancePayloadDTO.getEvent());
    }

    private void buildEntityFvZoneMessage(WrapperDTO wrapperDTO, NotificationConfigDTO config, NotificationMessageDTO messageDTO) throws JsonProcessingException{
        EntityPayloadDTO entityPayloadDTO = (EntityPayloadDTO) wrapperDTO.getPayloadRequest();
        messageDTO.setConfId(config.getDocumentId());
        messageDTO.setPayload(entityPayloadDTO);
        messageDTO.setSignature(generateSignature(config.getApiKey(), entityPayloadDTO));
        messageDTO.setEvent(entityPayloadDTO.getEvent());
        messageDTO.setAction(entityPayloadDTO.getAction().name());
    }

    private static void buildProductMessage (WrapperDTO wrapperDTO, NotificationConfigDTO config, NotificationMessageDTO messageDTO) throws JsonProcessingException {
        ProductPayloadDTO productPayloadDTO = (ProductPayloadDTO) wrapperDTO.getPayloadRequest();
        messageDTO.setConfId(config.getDocumentId());
        messageDTO.setNotificationSubtype(productPayloadDTO.getNotificationSubtype().name());
        messageDTO.setAction(productPayloadDTO.getAction().name());
        messageDTO.setPayload(productPayloadDTO);
        messageDTO.setSignature(generateSignature(config.getApiKey(), productPayloadDTO));
        messageDTO.setEvent(productPayloadDTO.getEvent());
    }

    private void buildUserFvZoneMessage(WrapperDTO wrapperDTO, NotificationConfigDTO config, NotificationMessageDTO messageDTO) throws JsonProcessingException{
        UserPayloadDTO userPayloadDTO = (UserPayloadDTO) wrapperDTO.getPayloadRequest();
        messageDTO.setConfId(config.getDocumentId());
        messageDTO.setPayload(userPayloadDTO);
        messageDTO.setSignature(generateSignature(config.getApiKey(), userPayloadDTO));
        messageDTO.setEvent(userPayloadDTO.getEvent());
        messageDTO.setAction(userPayloadDTO.getAction().name());
    }

    private static String generateSignature(String apiKey, PayloadRequest payloadRequest) throws JsonProcessingException {
        return GeneratorUtils.getHashSHA256(JsonMapper.jacksonMapper().writeValueAsString(payloadRequest) + apiKey);
    }
}
