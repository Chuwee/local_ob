package es.onebox.internal.utils;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CsvParseUtils {
    public static final char COMMA_SEPARATOR = ',';
    public static final Charset UTF_8 = StandardCharsets.UTF_8;

    public static <T> List<T> fromCSV(InputStream inputStream, Class<T> clazz) {

        MappingStrategy<T> strategy = new MappingStrategy<>();
        strategy.setType(clazz);

        return new CsvToBeanBuilder<T>(new InputStreamReader(inputStream, UTF_8))
                .withType(clazz)
                .withSeparator(COMMA_SEPARATOR)
                .withMappingStrategy(strategy)
                .build()
                .parse();
    }

    public static <T> String toCsv(List<T> items, Class<T> clazz) {

        MappingStrategy<T> strategy = new MappingStrategy<>();
        strategy.setType(clazz);

        StringWriter writer = new StringWriter();

        try {
            new StatefulBeanToCsvBuilder<T>(writer)
                    .withMappingStrategy(strategy)
                    .build()
                    .write(items);
            return writer.toString();
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new OneboxRestException(ApiExternalErrorCode.ERROR_PROCESSING_SALES);
        }
    }
}
