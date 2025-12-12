package es.onebox.internal.automaticsales.processsales.utils;

import es.onebox.common.datasources.ms.order.dto.EventType;
import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;
import es.onebox.common.datasources.ms.order.dto.OrderTicketDataDTO;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class ValidationUtils {

    private static final String EMAIL_REGEXP = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";

    private ValidationUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static boolean validateSeatsPositions (List<OrderProductDTO> products) {
        ProductDTOPositionComparator comparator = new ProductDTOPositionComparator();
        products.sort(comparator);
        return IntStream.range(1, products.size())
                .allMatch(i -> {
                    OrderProductDTO current = products.get(i);
                    OrderProductDTO last = products.get(i - 1);
                    if (current.getEventType() != null && Objects.equals(current.getEventType(), EventType.ACTIVITY)) {
                        return true;
                    }
                    OrderTicketDataDTO ticketData = current.getTicketData();
                    if (ticketData.getNotNumberedAreaId() == null && ticketData.getRowOrder() != null) {
                        ticketData.setRowOrder(ticketData.getRowOrder() - 1);
                        int res = comparator.compare(last, current);
                        ticketData.setRowOrder(ticketData.getRowOrder() + 1);
                        return res == 0;
                    } else {
                        return comparator.compare(last, current) == 0;
                    }
                });
    }

    public static boolean checkValidEmail(String email) {
        if (email == null) {
            return false;
        } else {
            Pattern p = Pattern.compile(EMAIL_REGEXP, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(email);
            return m.matches();
        }
    }
}
