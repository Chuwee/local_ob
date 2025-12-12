package es.onebox.channels.catalog.eci;

import es.onebox.channels.catalog.ChannelCatalogContext;
import es.onebox.common.datasources.catalog.dto.ChannelEvent;
import es.onebox.common.datasources.catalog.dto.ChannelEventLanguage;
import es.onebox.common.datasources.ms.channel.dto.config.ChannelConfigDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ECILanguageUtils {

    private static final String JAVA_LANGUAGE_SEPARATOR = "_";
    private static final String ISO_LANGUAGE_SEPARATOR = "-";

    private ECILanguageUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static List<String> getLanguagesPriority(ChannelCatalogContext context, ChannelEvent event) {
        List<String> languages = new ArrayList<>();
        languages.add(toISOLanguage(context.getDefaultLanguage()));
        ChannelEventLanguage eventLanguages = event.getLanguage();
        if (eventLanguages != null) {
            languages.add(eventLanguages.getDefaultLang());
            List<String> others = eventLanguages.getOthers();
            if (others != null) {
                languages.addAll(others);
            }
        }
        return languages.stream()
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
    }

    public static <T> Map<String, String> getI18nTexts(T object, Function<T, Map<String, String>> getter, List<String> languages) {
        return getI18nTexts(object, getter, languages, StringUtils.EMPTY);
    }

    public static <T> Map<String, String> getI18nTexts(T object, Function<T, Map<String, String>> getter, List<String> languages, String defaultValue) {
        String firstLanguage = languages.get(0);
        String firstLanguageWithoutVariation = toLanguageWithoutVariation(firstLanguage);
        List<String> otherLanguages = languages.subList(1, languages.size());
        if (object == null) {
            return Collections.singletonMap(firstLanguageWithoutVariation, defaultValue);
        }
        Map<String, String> i18nTexts = getter.apply(object);
        Map<String, String> result = new LinkedHashMap<>();
        String firstValue = getText(i18nTexts, firstLanguage, defaultValue);
        result.put(firstLanguageWithoutVariation, firstValue);
        otherLanguages.forEach(language -> result.put(toLanguageWithoutVariation(language), getText(i18nTexts, language, firstValue)));
        return result;
    }

    public static <T> String getText(T object, Function<T, Map<String, String>> getter, List<String> languages) {
        return getText(object, getter, languages, StringUtils.EMPTY);
    }

    public static <T> String getText(T object, Function<T, Map<String, String>> getter, List<String> languages, String defaultValue) {
        if (object != null) {
            return getText(getter.apply(object), languages, defaultValue);
        }
        return defaultValue;
    }

    private static String getText(Map<String, String> i18nTexts, List<String> languages, String defaultValue) {
        return languages.stream()
                .map(language -> getText(i18nTexts, language, null))
                .filter(StringUtils::isNotBlank)
                .findFirst()
                .orElse(defaultValue);
    }

    private static String getText(Map<String, String> i18nTexts, String language, String defaultValue) {
        if (i18nTexts == null) {
            return defaultValue;
        }
        String text = i18nTexts.get(language);
        if (StringUtils.isBlank(text)) {
            text = i18nTexts.get(toLanguageWithoutVariation(language));
        }
        if (StringUtils.isBlank(text)) {
            return defaultValue;
        }
        return text;
    }

    private static String toLanguageWithoutVariation(String language) {
        return language.split("[" + ISO_LANGUAGE_SEPARATOR + JAVA_LANGUAGE_SEPARATOR + "]")[0].toLowerCase();
    }

    private static String toISOLanguage(String language) {
        String[] parts = language.split(JAVA_LANGUAGE_SEPARATOR);
        String result = parts[0].toLowerCase();
        if (parts.length > 1) {
            result += ISO_LANGUAGE_SEPARATOR + parts[1].toUpperCase();
        }
        return result;
    }
}
