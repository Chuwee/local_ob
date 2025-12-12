package es.onebox.fifaqatar.adapter.mapper;

import es.onebox.common.datasources.ms.event.dto.response.catalog.CommunicationElement;
import es.onebox.common.datasources.ms.event.dto.response.catalog.session.SessionCatalog;
import es.onebox.common.datasources.orderitems.dto.OrderItem;
import es.onebox.common.datasources.orderitems.dto.transfer.OrderItemTransfer;
import es.onebox.common.datasources.orderitems.enums.OrderItemRelatedProductState;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapperUtils {

    private MapperUtils() {
        throw new UnsupportedOperationException();
    }

    public static String findCommElement(String elementTag, List<CommunicationElement> communicationElements, String lang, String eventDefaultLanguage) {
        lang = lang != null ? lang.replaceAll("-", "_") : lang;
        if (CollectionUtils.isEmpty(communicationElements)) {
            return null;
        }
        Map<String, CommunicationElement> collect = communicationElements.stream().filter(c -> c.getTag().equals(elementTag) && c.getPosition().equals(1))
                .collect(Collectors.toMap(CommunicationElement::getLanguageCode, Function.identity()));
        if (MapUtils.isNotEmpty(collect) && collect.get(lang) != null) {
            return elementTag.startsWith("IMG") ? collect.get(lang).getUrl() : collect.get(lang).getValue();
        } else if (MapUtils.isNotEmpty(collect) && collect.get(eventDefaultLanguage) != null) {
            return elementTag.startsWith("IMG") ? collect.get(eventDefaultLanguage).getUrl() : collect.get(eventDefaultLanguage).getValue();
        } else {
            return null;
        }
    }

    public static List<OrderItem> filterOrderItemsByOwnerAndNotTransferred(List<OrderItem> items, String customerId) {
        return items.stream()
                .filter(item -> {
                    String itemCustomerId = item.getUserId();
                    OrderItemTransfer transfer = item.getTransfer();
                    boolean isTransferred = transfer != null && "TRANSFERRED".equals(transfer.getStatus());

                    return customerId.equals(itemCustomerId) && !isTransferred;
                }).collect(Collectors.toList());
    }

    public static List<OrderItem> filterOrderItemsByReceiver(List<OrderItem> items, String receiverCustomerId) {
        return items.stream()
                .filter(item -> {
                    OrderItemTransfer transfer = item.getTransfer();

                    return transfer != null && transfer.getReceiver() != null && receiverCustomerId.equals(transfer.getReceiver().getCustomerId());
                }).collect(Collectors.toList());
    }

    public static boolean isSessionInProgress(SessionCatalog session) {
        return isSessionStarted(session) && !isSessionFinished(session);
    }

    public static boolean isSessionStarted(SessionCatalog session) {
        ZonedDateTime startDate = Instant.ofEpochMilli(session.getBeginSessionDate()).atZone(ZoneId.of("UTC"));
        var now = ZonedDateTime.now(startDate.getZone());

        return now.isAfter(startDate);
    }

    public static boolean isSessionFinished(SessionCatalog session) {
        ZonedDateTime endDate = getSessionEndDate(session);
        var now = ZonedDateTime.now(endDate.getZone());

        return now.isAfter(endDate);
    }

    public static ZonedDateTime getSessionEndDate(SessionCatalog session) {
        ZonedDateTime endDate = Instant.ofEpochMilli(session.getEndSessionDate()).atZone(ZoneId.of("UTC"));
        ZonedDateTime realEndDate = Instant.ofEpochMilli(session.getRealEndSessionDate()).atZone(ZoneId.of("UTC"));

        return realEndDate.isAfter(endDate) ? realEndDate.plusHours(5) : endDate.plusHours(5);
    }
}
