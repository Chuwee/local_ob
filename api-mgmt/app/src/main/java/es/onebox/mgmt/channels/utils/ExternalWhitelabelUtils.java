package es.onebox.mgmt.channels.utils;

import es.onebox.core.utils.common.UrlBuilder;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.common.dto.TimeZone;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ExternalWhitelabelUtils {

    private static final String COMMUNICATION_PROTOCOL = "https://";
    private static final String URL_SEPARATOR = "/";
    private static final String DATE_PARAM = "date";
    private static final String TIME_PARAM = "time";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm";
    public static final String DEFAULT_PATH = "m";

    private ExternalWhitelabelUtils() {
    }

    public static String buildSessionUrl(String domain, String whitelabelPath, String externalReference, String language,
                                         ZonedDateTime date,TimeZone timezone) {
       return buildUrl(domain, whitelabelPath, externalReference, language, date, timezone);
    }

    public static String buildEventUrl(String domain, String whitelabelPath, String externalReference, String language) {
        return buildUrl(domain, whitelabelPath, externalReference, language, null, null);
    }

    private static String buildUrl(String domain, String whitelabelPath, String externalReference, String language,
                                   ZonedDateTime date, TimeZone timezone) {

        if (StringUtils.isEmpty(externalReference)) {
            return null;
        }
        Map<String, String> params = new HashMap<>();
        String finalLanguage = getFinalLanguage(language);

        if(Objects.nonNull(date) && Objects.nonNull(timezone)) {
            final DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            final DateFormat tf = new SimpleDateFormat(TIME_FORMAT);
            df.setTimeZone(java.util.TimeZone.getTimeZone(timezone.getOlsonId()));
            tf.setTimeZone(java.util.TimeZone.getTimeZone(timezone.getOlsonId()));

            Date from = Date.from(date.toInstant());
            String formattedDate = df.format(from);
            String formattedTime = tf.format(from);

            params.put(DATE_PARAM, formattedDate);
            params.put(TIME_PARAM, formattedTime);
        }

        if (StringUtils.isEmpty(whitelabelPath)) {
            return UrlBuilder.builder()
                    .protocol(COMMUNICATION_PROTOCOL)
                    .pathParts(domain, DEFAULT_PATH, externalReference, finalLanguage)
                    .separator(URL_SEPARATOR)
                    .params(params)
                    .build();
        } else {
            return UrlBuilder.builder()
                    .protocol(COMMUNICATION_PROTOCOL)
                    .pathParts(domain, finalLanguage, whitelabelPath, externalReference)
                    .separator(URL_SEPARATOR)
                    .params(params)
                    .build();
        }
    }


    public static String getFinalLanguage(String language) {
        if (language != null) {
            String languageTag = ConverterUtils.toLanguageTag(language);
            return ConverterUtils.toLanguage(languageTag);
        }
        return "";
    }

}
