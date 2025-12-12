package es.onebox.internal.automaticsales.report.converter;

import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.internal.automaticsales.export.dto.Delivery;
import es.onebox.internal.automaticsales.export.enums.ReportDeliveryType;
import org.apache.commons.collections.MapUtils;

import java.util.Map;
import java.util.Objects;

public class ExportConverter {

    private static final String DELIVERY_EMAIL_ADDRESS = "address";

    public static String extractEmail(Delivery delivery) {
        if (delivery != null && ReportDeliveryType.EMAIL == delivery.getType()) {
            Map<String, Object> properties = delivery.getProperties();
            return resolveEmail(properties);
        }
        return null;
    }

    public static String resolveEmail(Map<String, Object> properties) {
        String email = "";
        if (MapUtils.isNotEmpty(properties) && Objects.nonNull(properties.get(DELIVERY_EMAIL_ADDRESS))) {
            email = (String) properties.get(DELIVERY_EMAIL_ADDRESS);
        }
        if(email.equals("")) {
            throw new OneboxRestException(ApiExternalErrorCode.ERROR_VOID_EMAIL);
        }
        return email;
    }

}
