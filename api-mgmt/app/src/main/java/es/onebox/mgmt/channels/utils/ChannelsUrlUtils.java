package es.onebox.mgmt.channels.utils;

import es.onebox.core.utils.common.UrlBuilder;
import es.onebox.mgmt.common.BaseLinkDTO;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.HashUtils;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChannelsUrlUtils {

    private static final String COMMUNICATION_PROTOCOL = "https://";
    private static final String URL_SEPARATOR = "/";
    private static final String LANGUAGE_PARAM = "hl";
    private static final String SELECT = "select";
    private static final String ADMIN = "admin";
    private static final String CATALOG = "catalog";
    private static final String SESSION = "session";
    private static final String EVENTS = "events";
    private static final String PRODUCTS = "products";
    private static final String SESSION_PREVIEW_TOKEN = "sessionPreviewToken";
    private static final String PACK = "pack";
    private static final String PACKS = "packs";
    private static final String ATTENDEES = "attendees";

    private ChannelsUrlUtils() {
    }

    public static String selectUrlByChannelConfig(Boolean v4Enabled, String urlChannel, String urlPortal) {
        if (BooleanUtils.isTrue(v4Enabled)) {
            return urlChannel;
        } else {
            return urlPortal;
        }
    }

    public static String buildUrlByChannels(String urlPortal, String urlIntegration, Long sessionId, String language, Boolean isEvent, Boolean isPreview) {
        Map<String, String> params = new HashMap<>();
        String finalLanguage = getFinalLanguage(language);
        String path = Boolean.TRUE.equals(isEvent) ? EVENTS : SELECT;

        if(StringUtils.isNotEmpty(finalLanguage)){
            params.put(LANGUAGE_PARAM, language);
        }

        if(Boolean.TRUE.equals(isPreview)) {
            String hash = HashUtils.encodeHashIds(sessionId);
            params.put(SESSION_PREVIEW_TOKEN, hash);
        }

        return UrlBuilder.builder()
                .protocol(COMMUNICATION_PROTOCOL)
                .pathParts(urlPortal, urlIntegration, path, sessionId.toString())
                .separator(URL_SEPARATOR)
                .params(params)
                .build();
    }

    public static String buildUrlAttendeesByChannels(String urlPortal, String channelUrl, String language) {
        Map<String, String> params = new HashMap<>();
        String finalLanguage = getFinalLanguage(language);

        if(StringUtils.isNotEmpty(finalLanguage)){
            params.put(LANGUAGE_PARAM, language);
        }

        return UrlBuilder.builder()
                .protocol(COMMUNICATION_PROTOCOL)
                .pathParts(urlPortal, channelUrl, ATTENDEES)
                .separator(URL_SEPARATOR)
                .params(params)
                .build();
    }

    public static String buildUrlProductsBySession(String urlChannel, String urlIntegration, Long sessionId,
                                                   String language, Boolean isPreview, Integer channelSubtype) {
        Map<String, String> params = new HashMap<>();
        String finalLanguage = getFinalLanguage(language);

        if (StringUtils.isNotEmpty(finalLanguage)) {
            params.put(LANGUAGE_PARAM, language);
        }
        if (Boolean.TRUE.equals(isPreview)) {
            String hash = HashUtils.encodeHashIds(sessionId);
            params.put(SESSION_PREVIEW_TOKEN, hash);
        }

        if (ChannelSubtype.PORTAL_B2B.equals(ChannelSubtype.getById(channelSubtype))) {
            return buildUrlProductsForB2bChannelBySession(urlChannel, urlIntegration, sessionId, params);
        }
        return buildUrlProductsBySession(urlChannel, urlIntegration, sessionId, params);
    }

    private static String buildUrlProductsForB2bChannelBySession(String urlChannel, String urlIntegration, Long sessionId, Map<String, String> params) {
        return UrlBuilder.builder()
                .protocol(COMMUNICATION_PROTOCOL)
                .pathParts(urlChannel, urlIntegration, ADMIN, CATALOG, SESSION, sessionId.toString(), PRODUCTS)
                .separator(URL_SEPARATOR)
                .params(params)
                .build();
    }

    private static String buildUrlProductsBySession(String urlChannel, String urlIntegration, Long sessionId, Map<String, String> params) {
        return UrlBuilder.builder()
                .protocol(COMMUNICATION_PROTOCOL)
                .pathParts(urlChannel, urlIntegration, SELECT, sessionId.toString(), PRODUCTS)
                .separator(URL_SEPARATOR)
                .params(params)
                .build();
    }

    public static String buildUrlByChannels(String urlChannel, String urlIntegration) {
        return UrlBuilder.builder()
                .protocol(COMMUNICATION_PROTOCOL)
                .pathParts(urlChannel, urlIntegration)
                .separator(URL_SEPARATOR)
                .build();
    }

    public static String buildUrlByChannelsPreview(String urlChannel, String urlIntegration, String previewTokenKey, String previewTokenValue) {
        return UrlBuilder.builder()
                .protocol(COMMUNICATION_PROTOCOL)
                .pathParts(urlChannel, urlIntegration)
                .separator(URL_SEPARATOR)
                .params(Collections.singletonMap(previewTokenKey, previewTokenValue))
                .build();
    }

    public static BaseLinkDTO buildUrlByChannelsToBaseLink(String urlPortal, String urlIntegration, Long sessionId, String language) {
        BaseLinkDTO result = new BaseLinkDTO();
        result.setLanguage(language);

        Map<String,String> params = new HashMap<>();
        String finalLanguage = getFinalLanguage(language);
        if(StringUtils.isNotEmpty(finalLanguage)){
            params.put(LANGUAGE_PARAM, language);
        }

        String linkBuilder = UrlBuilder.builder()
                .protocol(COMMUNICATION_PROTOCOL)
                .pathParts(urlPortal, urlIntegration, SELECT, sessionId)
                .separator(URL_SEPARATOR)
                .params(params)
                .build();

        result.setLink(linkBuilder);
        return result;
    }

    public static String getFinalLanguage(String language) {
        if (language != null) {
            return ConverterUtils.toLanguageTag(language);
        }
        return "";
    }

    public static String buildUrlByChannelsPackDetail(String urlChannel, String urlIntegration, Long packId, String language, ChannelSubtype subtype) {
        if (ChannelSubtype.PORTAL_B2B.equals(subtype)) {
            return buildUrlByB2BChannelsPack(urlChannel, urlIntegration, packId, language);
        }
        return buildUrlByChannelsPack(urlChannel, urlIntegration, packId, language, EVENTS);
    }

    public static String buildUrlByChannelsPack(String urlChannel, String urlIntegration, Long packId, String language, ChannelSubtype subtype) {
        if (ChannelSubtype.PORTAL_B2B.equals(subtype)) {
            return buildUrlByB2BChannelsPack(urlChannel, urlIntegration, packId, language);
        }
        return buildUrlByChannelsPack(urlChannel, urlIntegration, packId, language, SELECT);
    }

    private static String buildUrlByChannelsPack(String urlChannel, String urlIntegration, Long packId, String language, String packLink) {
        return UrlBuilder.builder()
                .protocol(COMMUNICATION_PROTOCOL)
                .pathParts(urlChannel, urlIntegration, packLink, PACK, packId)
                .separator(URL_SEPARATOR)
                .params(Map.of(LANGUAGE_PARAM, language))
                .build();
    }

    private static String buildUrlByB2BChannelsPack(String urlChannel, String urlIntegration, Long packId, String language) {
        return UrlBuilder.builder()
                .protocol(COMMUNICATION_PROTOCOL)
                .pathParts(urlChannel, urlIntegration, ADMIN, PACKS, packId)
                .separator(URL_SEPARATOR)
                .params(Map.of(LANGUAGE_PARAM, language))
                .build();
    }
}
