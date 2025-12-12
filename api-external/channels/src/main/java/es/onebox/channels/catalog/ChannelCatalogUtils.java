package es.onebox.channels.catalog;

import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

public class ChannelCatalogUtils {

    private ChannelCatalogUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static String getEventCardUrl(ChannelCatalogContext context, Long eventId, String language) {
        if (eventId == null) {
            return null;
        }
        LinkoutDefaults linkoutDefaults = LinkoutDefaults.fromEnv(context.getEnv());
        var builder = UriComponentsBuilder.newInstance();
        builder.scheme("https")
                .host(getHost(linkoutDefaults, context));
        if (BooleanUtils.isTrue(context.getV4())) {
            builder.pathSegment(context.getPath(), "events", "" + eventId);
            builder.queryParam("hl", toIsoLanguage(language));
        } else {
            builder.pathSegment(context.getPath(), toJavaLanguage(language), "entradas", "evento", "" + eventId);
        }
        return builder.build().toString();
    }

    public static String getSessionSelectUrl(ChannelCatalogContext context, Long sessionId, Long eventId, String language) {
        if (sessionId == null || eventId == null) {
            return null;
        }
        LinkoutDefaults linkoutDefaults = LinkoutDefaults.fromEnv(context.getEnv());
        var builder = UriComponentsBuilder.newInstance();
        builder.scheme("https")
                .host(getHost(linkoutDefaults, context));
        if (BooleanUtils.isTrue(context.getV4())) {
            builder.pathSegment(context.getPath(), "select", "" + sessionId)
                    .queryParam("hl", toIsoLanguage(language));
        } else {
            builder.pathSegment(context.getPath(), toJavaLanguage(language), "entradas", "evento", "" + eventId, "session", "" + sessionId, "select");
        }
        return builder.build().toString();
    }

    private static String getHost(LinkoutDefaults defaults, ChannelCatalogContext context) {
        if (BooleanUtils.isTrue(context.getV4())) {
            return StringUtils.isNotBlank(context.getCustomDomain()) ? context.getCustomDomain() : defaults.getDomainV4();
        } else {
            return StringUtils.isNotBlank(context.getCustomDomain()) ? context.getCustomDomain() : defaults.getDomainV3();
        }
    }

    public static String toJavaLanguage(String isoLanguage) {
        return isoLanguage.replace("-", "_");
    }

    public static String toIsoLanguage(String javaLanguage) {
        return javaLanguage.replace("_", "-");
    }

    public static Long getEventId(Map parameters) {
        Object eventIdParam = parameters.get("event-id");
        var s = eventIdParam != null ? (String[]) eventIdParam : null;

        if (s != null && StringUtils.isNumeric(s[0])) {
            return Long.parseLong(s[0]);
        } else if (s != null && !StringUtils.isNumeric(s[0])) {
            throw new OneboxRestException(ApiExternalErrorCode.BAD_REQUEST_PARAMETER);
        } else {
            return null;
        }
    }
}
