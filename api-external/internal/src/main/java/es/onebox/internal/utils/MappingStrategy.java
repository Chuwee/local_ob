package es.onebox.internal.utils;

import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvChainedException;
import com.opencsv.exceptions.CsvFieldAssignmentException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.util.Arrays;
import java.util.Objects;

public class MappingStrategy<T> extends HeaderColumnNameMappingStrategy<T> {

    @Override
    public T populateNewBean(String[] line) throws CsvFieldAssignmentException, CsvChainedException {
        Arrays.setAll(line, value -> Objects.equals(line[value], "null") ? null : line[value] != null ? line[value].trim() : "");
        return super.populateNewBean(line);
    }

    @Override
    public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
        String[] header = super.generateHeader(bean);
        return Arrays.stream(header)
                .map(String::toLowerCase)
                .toArray(String[]::new);
    }
}
