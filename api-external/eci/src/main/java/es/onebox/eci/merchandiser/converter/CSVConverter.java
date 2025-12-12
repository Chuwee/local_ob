package es.onebox.eci.merchandiser.converter;

import java.util.Map;

public class CSVConverter {

    private CSVConverter() {
    }

    public static String mapToCSV(Map<String, Long> values) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Long> events : values.entrySet()) {
            result.append(events.getKey());
            result.append(",");
            result.append(events.getValue());
            result.append(System.lineSeparator());
        }
        return result.toString();
    }
}
