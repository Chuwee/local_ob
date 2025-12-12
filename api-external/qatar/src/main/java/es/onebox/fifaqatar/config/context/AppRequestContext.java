package es.onebox.fifaqatar.config.context;

import es.onebox.fifaqatar.config.config.FifaQatarConfigDocument;
import es.onebox.fifaqatar.config.translation.FifaQatarTranslation;
import es.onebox.fifaqatar.adapter.datasource.dto.MeResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class AppRequestContext {

    public static final String MAIN_CONFIG = "MAIN_CONFIG";
    public static final String DICTIONARY = "DICTIONARY";
    public static final String CURRENT_USER = "CURRENT_USER";
    public static final String CURRENT_LANG = "CURRENT_LANG";
    public static final String X_FORWARDED_PROTO_HEADER = "X-Forwarded-Proto";
    public static final String X_FORWARDED_HOST_HEADER = "X-Forwarded-Host";
    public static final String HTTPS = "https";
    public static final String LOCALHOST = "localhost";
    public static final String LOOPBACK_ADDRESS = "127.0.0.1";
    public static final String HTTP = "http";

    private AppRequestContext() {
        throw new UnsupportedOperationException();
    }

    private static HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static void setCurrentUser(MeResponseDTO me) {
        getCurrentRequest().setAttribute(CURRENT_USER, me);
    }

    public static MeResponseDTO getCurrentUser() {
        return (MeResponseDTO) getCurrentRequest().getAttribute(CURRENT_USER);
    }

    public static void setMainConfig(FifaQatarConfigDocument config) {
        getCurrentRequest().setAttribute(MAIN_CONFIG, config);
    }

    public static FifaQatarConfigDocument getMainConfig() {
        return (FifaQatarConfigDocument) getCurrentRequest().getAttribute(MAIN_CONFIG);
    }

    public static void setDictionary(FifaQatarTranslation dictionary) {
        getCurrentRequest().setAttribute(DICTIONARY, dictionary);
    }

    public static FifaQatarTranslation getDictionary() {
        return (FifaQatarTranslation) getCurrentRequest().getAttribute(DICTIONARY);
    }

    public static void setLang(String lang) {
        getCurrentRequest().setAttribute(CURRENT_LANG, lang);
    }

    public static String getCurrentLang() {
        var lang = getCurrentRequest().getAttribute(CURRENT_LANG);

        return lang != null ? (String) lang : null;
    }

    public static String getHost() {
        var request = getCurrentRequest();
        return getHost(request);
    }

    public static String getHost(HttpServletRequest request) {
        var host = request.getHeader(X_FORWARDED_HOST_HEADER);

        return host != null ? host : request.getServerName();
    }

    public static String getProtocol() {
        var request = getCurrentRequest();

        return getProtocol(request);
    }

    public static String getProtocol(HttpServletRequest request) {
        var host = getHost(request);
        var proto = request.getHeader(X_FORWARDED_PROTO_HEADER);
        var protocol = proto != null ? proto : HTTPS;
        if (host != null && (host.contains(LOCALHOST) || host.contains(LOOPBACK_ADDRESS))) {
            protocol = HTTP;
        }

        return protocol;
    }


}
