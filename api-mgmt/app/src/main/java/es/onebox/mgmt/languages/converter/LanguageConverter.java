package es.onebox.mgmt.languages.converter;

import es.onebox.core.serializer.dto.common.CodeDTO;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.entity.dto.MasterdataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LanguageConverter {

    private LanguageConverter() {
    }

    public static CodeDTO fromEntity(MasterdataValue entity) {
        if (entity == null) {
            return null;
        }
        CodeDTO language = new CodeDTO();
        language.setCode(ConverterUtils.toLanguageTag(entity.getCode()));

        return language;
    }

    public static List<CodeDTO> fromEntities(List<MasterdataValue> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }
        return entities.stream().map(LanguageConverter::fromEntity).collect(Collectors.toList());
    }

}
