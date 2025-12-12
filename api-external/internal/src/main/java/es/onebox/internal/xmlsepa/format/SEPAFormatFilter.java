package es.onebox.internal.xmlsepa.format;

public class SEPAFormatFilter {
    public static String filter(String filter) {
        StringBuilder output = new StringBuilder();
        for (char c : filter.toCharArray()) {
            if (c >= '0' && c <= '9'
                || c >= 'a' && c <= 'z'
                || c >= 'A' && c <= 'Z'
            ) {
                output.append(c);
            }
        }
        return output.toString();
    }

    public static String filterBIC(String bic) {
        StringBuilder bicBuilder = new StringBuilder(filter(bic));
        while (bicBuilder.length() < 11) {
            bicBuilder.append("X");
        }
        return bicBuilder.toString();
    }
}