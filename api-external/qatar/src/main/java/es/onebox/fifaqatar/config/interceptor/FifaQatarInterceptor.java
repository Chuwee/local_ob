package es.onebox.fifaqatar.config.interceptor;

import es.onebox.fifaqatar.config.config.FifaQatarConfigDocument;
import es.onebox.fifaqatar.config.config.FifaQatarConfigRepository;
import es.onebox.fifaqatar.config.translation.FifaQatarTranslation;
import es.onebox.fifaqatar.config.context.AppRequestContext;
import es.onebox.fifaqatar.config.translation.FifaQatarTranslationRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class FifaQatarInterceptor implements HandlerInterceptor {

    private final String HEADER_LANGUAGE = "accept-language";
    private final String LOCALE_ENGLISH = "en-GB";
    private final String LOCALE_ARAB = "ar-QA";

    private final FifaQatarConfigRepository configRepository;
    private final FifaQatarTranslationRepository translationRepository;

    public FifaQatarInterceptor(FifaQatarConfigRepository configRepository, FifaQatarTranslationRepository translationRepository) {
        this.configRepository = configRepository;
        this.translationRepository = translationRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        FifaQatarConfigDocument fifaQatarConfigDocument = configRepository.getMainConfig();
        AppRequestContext.setMainConfig(fifaQatarConfigDocument);
        String language = resolveLanguage(request);
        AppRequestContext.setLang(language);
        FifaQatarTranslation translations = translationRepository.getTranslations();
        AppRequestContext.setDictionary(translations);

        return true;
    }

    private String resolveLanguage(HttpServletRequest request) {
        String lang = request.getHeader(HEADER_LANGUAGE);
        if (StringUtils.isBlank(lang)) {
            return LOCALE_ENGLISH;
        } else if (lang.startsWith("en")) {
            return LOCALE_ENGLISH;
        } else if (lang.startsWith("ar")) {
            return LOCALE_ARAB;
        } else {
            return LOCALE_ENGLISH;
        }
    }

}
