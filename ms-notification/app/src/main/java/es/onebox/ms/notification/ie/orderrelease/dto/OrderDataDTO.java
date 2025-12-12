package es.onebox.ms.notification.ie.orderrelease.dto;

import es.onebox.dal.dto.couch.order.BaseOrderDTO;
import es.onebox.dal.dto.utils.PairIntegerValue;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderDataDTO implements Serializable {

    private String orderCode;
    private List<OrderProductDTO> orderProducts = new ArrayList<>();
    private ZonedDateTime orderDate;
    private String timeZone;
    private Integer channelId;
    private EntityDTO entityDTOChannel;
    private Integer orderInvoiceId;
    private Double finalPrice;
    private Map<Integer, List<Long>> sessionGroups = new HashMap<>();
    private Map<Integer, Set<Integer>> eventSessions = new HashMap<>();

    private List<Integer> sessionIds;
    private List<Long> groupIds;
    private PairIntegerValue eventSession;

    public OrderDataDTO() {
    }

    public OrderDataDTO(String orderCode, List<OrderProductDTO> orderProducts, ZonedDateTime orderDate, String timeZone,
                        Integer channelId, EntityDTO entityDTOChannel, Integer orderInvoiceId, Double finalPrice,
                        Map<Integer, List<Long>> sessionGroups, Map<Integer, Set<Integer>> eventSessions,
                        List<Integer> sessionIds, List<Long> groupIds, PairIntegerValue eventSession) {
        this.orderCode = orderCode;
        this.orderProducts = orderProducts;
        this.orderDate = orderDate;
        this.timeZone = timeZone;
        this.channelId = channelId;
        this.entityDTOChannel = entityDTOChannel;
        this.orderInvoiceId = orderInvoiceId;
        this.finalPrice = finalPrice;
        this.sessionGroups = sessionGroups;
        this.eventSessions = eventSessions;
        this.sessionIds = sessionIds;
        this.groupIds = groupIds;
        this.eventSession = eventSession;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public List<OrderProductDTO> getOrderProducts() {
        return orderProducts;
    }

    public void setOrderProducts(List<OrderProductDTO> orderProducts) {
        this.orderProducts = orderProducts;
    }

    public ZonedDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(ZonedDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getOrderInvoiceId() {
        return orderInvoiceId;
    }

    public void setOrderInvoiceId(Integer orderInvoiceId) {
        this.orderInvoiceId = orderInvoiceId;
    }

    public void setSessionIds(List<Integer> sessionIds) {
        this.sessionIds = sessionIds;
    }

    public List<Integer> getSessionIds() {
        return sessionIds;
    }

    public List<Long> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<Long> groupIds) {
        this.groupIds = groupIds;
    }

    public Map<Integer, List<Long>> getSessionGroups() {
        return sessionGroups;
    }

    public void setSessionGroups(Map<Integer, List<Long>> sessionGroups) {
        this.sessionGroups = sessionGroups;
    }

    public Map<Integer, Set<Integer>> getEventSessions() {
        return eventSessions != null ? eventSessions : new HashMap<>();
    }

    public void setEventSessions(Map<Integer, Set<Integer>> eventSessions) {
        this.eventSessions = eventSessions;
    }

    public EntityDTO getEntityDTOChannel() {
        return entityDTOChannel;
    }

    public void setEntityDTOChannel(EntityDTO entityDTOChannel) {
        this.entityDTOChannel = entityDTOChannel;
    }

    public PairIntegerValue getEventSession() {
        return eventSession;
    }

    public void setEventSession(PairIntegerValue eventSession) {
        this.eventSession = eventSession;
    }

    public Double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(Double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public static OrderDataDTO from(BaseOrderDTO order) {

        List<OrderProductDTO> products = order.getProducts().stream().map(OrderProductDTO::from).toList();

        return new OrderDataDTOBuilder().setOrderCode(order.getCode())
                .setOrderProducts(products)
                .setOrderDate(order.getDate().getPurchased())
                .setTimeZone(order.getDate().getTimeZone())
                .setChannelId(order.getOrderData().getChannelId())
                .setEntityDTOChannel(new EntityDTO(order.getOrderData().getChannelEntityId()))
                .setFinalPrice(order.getPrice().getFinalPrice())
                .setOrderInvoiceId(order.getInvoiceData().getInvoiceNumber())
                .setSessionGroups(OrderDataDTO.getMapSesionGroupIds(order))
                .setEventSessions(OrderDataDTO.getMapEventIdsSesionIds(order)).build();
    }

    private static Map<Integer, List<Long>> getMapSesionGroupIds(BaseOrderDTO order) {
        Map<Integer, List<Long>> result = null;
        if(!CollectionUtils.isEmpty(order.getGroups())) {
            result = order.getProducts().stream().collect(
                    Collectors.groupingBy(es.onebox.dal.dto.couch.order.OrderProductDTO::getSessionId,
                            Collectors.mapping(es.onebox.dal.dto.couch.order.OrderProductDTO::getGroupId, Collectors.toList())));
            result.values().forEach(v->v.removeAll(Collections.singleton(null)));
        }
        return result;
    }

    private static Map<Integer, Set<Integer>> getMapEventIdsSesionIds(BaseOrderDTO order) {
        return order.getProducts().stream().collect(
                Collectors.groupingBy(es.onebox.dal.dto.couch.order.OrderProductDTO::getEventId,
                        Collectors.mapping(es.onebox.dal.dto.couch.order.OrderProductDTO::getSessionId, Collectors.toSet())));
    }


}
