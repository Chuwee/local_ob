package es.onebox.ms.notification.ie.orderrelease.dto;

import es.onebox.dal.dto.utils.PairIntegerValue;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class OrderDataDTOBuilder implements Serializable {
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
    private List<Integer> sessionIds = new ArrayList<>();
    private List<Long> groupIds = new ArrayList<>();
    private PairIntegerValue eventSession;

    public static OrderDataDTOBuilder builder() {
        return new OrderDataDTOBuilder();
    }

    public OrderDataDTOBuilder setOrderCode(String orderCode) {
        this.orderCode = orderCode;
        return this;
    }

    public OrderDataDTOBuilder setOrderProducts(List<OrderProductDTO> orderProducts) {
        this.orderProducts = orderProducts;
        return this;
    }

    public OrderDataDTOBuilder setOrderDate(ZonedDateTime orderDate) {
        this.orderDate = orderDate;
        return this;
    }

    public OrderDataDTOBuilder setTimeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public OrderDataDTOBuilder setChannelId(Integer channelId) {
        this.channelId = channelId;
        return this;
    }

    public OrderDataDTOBuilder setEntityDTOChannel(EntityDTO entityDTOChannel) {
        this.entityDTOChannel = entityDTOChannel;
        return this;
    }

    public OrderDataDTOBuilder setOrderInvoiceId(Integer orderInvoiceId) {
        this.orderInvoiceId = orderInvoiceId;
        return this;
    }

    public OrderDataDTOBuilder setFinalPrice(Double finalPrice) {
        this.finalPrice = finalPrice;
        return this;
    }

    public OrderDataDTOBuilder setSessionGroups(Map<Integer, List<Long>> sessionGroups) {
        this.sessionGroups = sessionGroups;
        return this;
    }

    public OrderDataDTOBuilder setEventSessions(Map<Integer, Set<Integer>> eventSessions) {
        this.eventSessions = eventSessions;
        return this;
    }

    public OrderDataDTOBuilder setSessionIds(List<Integer> sessionIds) {
        this.sessionIds = sessionIds;
        return this;
    }

    public OrderDataDTOBuilder setGroupIds(List<Long> groupIds) {
        this.groupIds = groupIds;
        return this;
    }

    public OrderDataDTOBuilder setEventSession(PairIntegerValue eventSession) {
        this.eventSession = eventSession;
        return this;
    }

    public OrderDataDTO build() {
        return new OrderDataDTO(orderCode, orderProducts, orderDate, timeZone, channelId, entityDTOChannel, orderInvoiceId,
                finalPrice, sessionGroups, eventSessions, sessionIds, groupIds, eventSession);
    }
}