package es.onebox.event.attributes;

import es.onebox.jooq.cpanel.tables.records.CpanelAtributosEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelAtributosSesionRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AttributeConverter {

    private AttributeConverter() {
    }

    private static AttributeDTO toDTO(Long id, String value, List<Long> valueIds) {
        AttributeDTO result = new AttributeDTO();
        result.setId(id);
        result.setValue(value);
        result.setSelected(valueIds);
        return result;
    }

    public static List<AttributeDTO> attributestoDTO(List<CpanelAtributosEventoRecord> records) {
        if (records == null || records.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Integer, List<CpanelAtributosEventoRecord>> attributesById = records.stream().collect(Collectors.groupingBy(CpanelAtributosEventoRecord::getIdatributo));

        return attributesById.entrySet().stream().map(entry -> {
            String value = entry.getValue().get(0).getValor();
            List<Long> valueIds = entry.getValue().stream()
                    .filter(cpanelAtributosEventoRecord -> cpanelAtributosEventoRecord.getIdvalor() != null)
                    .map(cpanelAtributosEventoRecord -> cpanelAtributosEventoRecord.getIdvalor().longValue())
                    .collect(Collectors.toList());
            return toDTO(entry.getKey().longValue(), value, valueIds);
            }).collect(Collectors.toList());
    }

    public static List<AttributeDTO> sessionAttributestoDTO(List<CpanelAtributosSesionRecord> records) {
        if (records == null || records.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Integer, List<CpanelAtributosSesionRecord>> attributesById = records.stream().collect(Collectors.groupingBy(CpanelAtributosSesionRecord::getIdatributo));

        return attributesById.entrySet().stream().map(entry -> {
            String value = entry.getValue().get(0).getValor();
            List<Long> valueIds = entry.getValue().stream()
                    .filter(attributeRecord -> attributeRecord.getIdvalor() != null)
                    .map(attributeRecord -> attributeRecord.getIdvalor().longValue())
                    .collect(Collectors.toList());
            return toDTO(entry.getKey().longValue(), value, valueIds);
            }).collect(Collectors.toList());
    }

}
