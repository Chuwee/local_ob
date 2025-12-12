package es.onebox.fifaqatar.config.translation;

import org.apache.commons.text.StringSubstitutor;

import java.util.Map;

public class TranslationUtils {

    private TranslationUtils() {
        throw new UnsupportedOperationException();
    }

    public static String getText(TranslationKey key, String language, FifaQatarTranslation dictionary) {
        var obj = dictionary.get(key.name());

        return obj != null ? obj.get(resolveLanguage(language)) : "["+key.name()+"]";
    }

    public static String getText(TranslationKey key, String language, FifaQatarTranslation dictionary, Map<String, String> vars) {
        String text = getText(key, language, dictionary);
        StringSubstitutor substitutor = new StringSubstitutor(vars, "{", "}");
        substitutor.setEnableSubstitutionInVariables(true);
        substitutor.setEnableUndefinedVariableException(false);

        return substitutor.replace(text);
    }

    public static String translateSeatingSummary(String language, FifaQatarTranslation dictionary, String gateName, String blockName, String rowName, String seatName) {
        Map<String, String> vars = Map.of(
                "gateName", gateName != null ? gateName : "-",
                "blockName", blockName != null ? blockName : "-",
                "rowName", rowName != null ? rowName : "-",
                "seatName", seatName != null ? seatName : "-");

        return getText(TranslationKey.TICKET_SEATING_SUMMARY, language, dictionary, vars);
    }

    public static String translateOrderItemBreakdownLabel(String language, FifaQatarTranslation dictionary, String currency, String amount) {
        Map<String, String> vars = Map.of(
                "currency", currency,
                "amount", amount);

        return getText(TranslationKey.ORDER_TICKET_PRICE_BREAKDOWN_LABEL, language, dictionary, vars);
    }

    private static String resolveLanguage(String language) {
        var splitted = language.split("-");

        return splitted.length > 1 ? splitted[0] : "en";
    }


}
