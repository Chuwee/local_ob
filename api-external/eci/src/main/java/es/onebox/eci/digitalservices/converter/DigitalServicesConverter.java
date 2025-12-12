package es.onebox.eci.digitalservices.converter;

import es.onebox.common.datasources.common.enums.OrderType;
import es.onebox.common.datasources.ms.entity.dto.CountryDTO;
import es.onebox.common.datasources.ms.entity.dto.EntityDTO;
import es.onebox.common.datasources.orders.dto.OrderDetail;
import es.onebox.common.datasources.orders.dto.OrderDetailItem;
import es.onebox.eci.digitalservices.dto.Order;

import java.time.format.DateTimeFormatter;
import java.util.List;


public class DigitalServicesConverter {
    public static final String PLATFORM_NIF = "B65315954";
    public static final String PLATFORM_NAME = "Onebox Iberica S.L.";
    public static final String PLATFORM_SERVER_COUNTRY_ISO2 = "IE";

    public static Order convert(OrderDetail orderDetail, EntityDTO entityDTO, List<CountryDTO> countries) {
        Order order = new Order();
        order.setCode(orderDetail.getCode());
        order.setRefundRelatedCode(getRelatedRefundCode(orderDetail));
        order.setEventName(getEventName(orderDetail));
        order.setPromoterName(entityDTO.getName());
        order.setPromoterCIF(entityDTO.getNif());
        order.setPromoterAddress(entityDTO.getAddress());
        order.setPromoterCountry(getPromoterCountry(entityDTO.getCountryId(), countries));
        order.setPlatformCIF(PLATFORM_NIF);
        order.setPlatformName(PLATFORM_NAME);
        order.setPlatformServerCountry(PLATFORM_SERVER_COUNTRY_ISO2);
        order.setTransactionId(getTransactionId(orderDetail));
        order.setEventDate(getSessionDate(orderDetail));
        return order;
    }

    private static String getPromoterCountry(Integer countryId, List<CountryDTO> countries) {
        CountryDTO country = countries.stream()
                .filter(countryDTO -> countryDTO.getId().equals(countryId))
                .findFirst().orElse(null);
        if (country != null) {
            return country.getCode();
        } else {
            return null;
        }
    }

    private static String getEventName(OrderDetail orderDetail) {
        OrderDetailItem item = orderDetail.getItems().stream().findFirst().orElse(null);
        if (item != null) {
            return item.getTicket().getAllocation().getEvent().getName();
        }
        return null;
    }

    private static String getSessionDate(OrderDetail orderDetail) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        OrderDetailItem item = orderDetail.getItems().stream().findFirst().orElse(null);
        if (item != null && item.getTicket().getAllocation().getSession().getDate() != null) {
            return item.getTicket().getAllocation().getSession().getDate().getStart().format(formatter);
        }
        return null;
    }

    private static String getTransactionId(OrderDetail orderDetail) {
        if (orderDetail.getPaymentDetail() != null && orderDetail.getPaymentDetail().getCustomInfo() != null) {
            return orderDetail.getPaymentDetail().getCustomInfo().getPgpSaleTransactionId();
        }
        return null;
    }

    private static String getRelatedRefundCode(OrderDetail orderDetail) {
        if (orderDetail.getType().equals(OrderType.REFUND)) {
            OrderDetailItem item = orderDetail.getItems().stream().findFirst().orElse(null);
            if (item != null && item.getPreviousOrder() != null) {
                return item.getPreviousOrder().getCode();
            }
        }
        return null;
    }
}
