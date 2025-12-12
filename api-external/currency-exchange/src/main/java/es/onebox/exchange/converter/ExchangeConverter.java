package es.onebox.exchange.converter;

import es.onebox.exchange.dto.ExchangeResponse;
import es.onebox.exchange.dto.ExchangeResponseDTO;

import java.util.HashMap;

public class ExchangeConverter {

    private ExchangeConverter() {}

    public static ExchangeResponseDTO toDTO(ExchangeResponse source) {
        ExchangeResponseDTO target = new ExchangeResponseDTO();
        HashMap<String, Double> map = toMapWithoutSource(source);
        target.setQuotes(map);
        target.setSource(source.getSource());
        return target;
    }

    private static HashMap<String, Double> toMapWithoutSource(ExchangeResponse source) {
        HashMap<String, Double> map = new HashMap<>();
        source.getQuotes()
                .forEach((key, value) -> map.put(key.replace(source.getSource(), "") ,value));
        return map;
    }

}
