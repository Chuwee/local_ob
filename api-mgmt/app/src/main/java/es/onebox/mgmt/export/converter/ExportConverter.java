package es.onebox.mgmt.export.converter;

import es.onebox.core.file.exporter.status.model.ExportProcess;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.export.dto.Delivery;
import es.onebox.mgmt.export.dto.ExportFileField;
import es.onebox.mgmt.export.dto.ExportFilter;
import es.onebox.mgmt.export.dto.ExportRequest;
import es.onebox.mgmt.export.dto.ExportResponse;
import es.onebox.mgmt.export.dto.ExportStatusResponse;
import es.onebox.mgmt.export.enums.ReportDeliveryType;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.users.dto.UserSelfDTO;
import org.apache.commons.collections.MapUtils;

import java.util.Map;
import java.util.Objects;

public class ExportConverter {

    private static final String DELIVERY_EMAIL_ADDRESS = "address";

    public static ExportStatusResponse mapResponse(final ExportProcess exportProcess) {
        ExportStatusResponse response = new ExportStatusResponse();
        response.setExportId(exportProcess.getExportId());
        response.setStatus(exportProcess.getStatus());
        response.setUrl(exportProcess.getUrl());
        return response;
    }

    public static <T extends ExportFileField<?>> ExportFilter<T> toFilter(ExportRequest<T> body, UserSelfDTO user) {
        return toFilter(body, user, null, null);
    }

    public static <T extends ExportFileField<?>> ExportFilter<T> toFilter(ExportRequest<T> body, UserSelfDTO user, String timezone,
            String q) {
        return fillFilter(new ExportFilter<>(), body, user, timezone, q);
    }

    public static <T extends ExportFileField<?>> ExportFilter<T> fillFilter(ExportFilter<T> filter, ExportRequest<T> body, UserSelfDTO user, String timezone, String q) {
        filter.setFields(body.getFields());
        filter.setFormat(body.getFormat());
        filter.setEmail(extractEmail(body.getDelivery()));
        filter.setUserId(user.getId());
        filter.setLanguage(ConverterUtils.toLocale(user.getLanguage()));
        filter.setTimeZone(timezone);
        filter.setQ(q);
        filter.setTranslations(body.getTranslations());
        return filter;
    }

    public static ExportResponse toResponse(ExportProcess exportProcess) {
        ExportResponse out = new ExportResponse();
        out.setExportId(exportProcess.getExportId());
        return out;
    }

    public static String extractEmail(Delivery delivery) {
        if (delivery != null && ReportDeliveryType.EMAIL == delivery.getType()) {
            Map<String, Object> properties = delivery.getProperties();
            return resolveEmail(properties);
        }
        return null;
    }

    public static String resolveEmail(Map<String, Object> properties) {
        String email;
        if (MapUtils.isNotEmpty(properties) && Objects.nonNull(properties.get(DELIVERY_EMAIL_ADDRESS))) {
            email = (String) properties.get(DELIVERY_EMAIL_ADDRESS);
        } else {
            email = SecurityUtils.getUsername();
        }
        return email;
    }

}
