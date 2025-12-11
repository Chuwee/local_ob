package es.onebox.event.tags.utils;

public class LanguageUtils {
    private LanguageUtils() {
    }

    /**
     * Converts from languageTag IETF BCP47 to locale (cpanel_idioma format) (es-ES to es_ES)
     */
    public static String toCpanelIdiomaCode(String locale) {
        if (locale == null) {
            return null;
        }
        return locale.replaceAll("-", "_");
    }

    public static String toLocaleCode(String locale) {
        if (locale == null) {
            return null;
        }
        return locale.replaceAll("_", "-");
    }
}
