package es.onebox.mgmt.currencies;

import es.onebox.core.serializer.dto.common.CodeDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;

import java.util.List;
import java.util.stream.Collectors;

public class CurrencyConverter {

    private CurrencyConverter() {
    }

    public static List<CodeDTO> toDTO(List<Currency> in) {
        return in.stream().map(CurrencyConverter::toDTO).collect(Collectors.toList());
    }

    public static CodeDTO toDTO(Currency in) {
        return new CodeDTO(in.getCode());
    }
}
