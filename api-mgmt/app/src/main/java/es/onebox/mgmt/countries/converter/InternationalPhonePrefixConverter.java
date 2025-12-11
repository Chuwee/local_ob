package es.onebox.mgmt.countries.converter;

import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.countries.dto.InternationalPhonePrefixDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.MasterdataCountryValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InternationalPhonePrefixConverter {

    private InternationalPhonePrefixConverter(){}

    public static List<InternationalPhonePrefixDTO> fromEntities(List<MasterdataCountryValue> entities) {
        if (CommonUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        return entities.stream().map(InternationalPhonePrefixConverter::fromEntity).collect(Collectors.toList());
    }

    public static InternationalPhonePrefixDTO fromEntity(MasterdataCountryValue entity) {
        InternationalPhonePrefixDTO prefix = fromEntity(new InternationalPhonePrefixDTO(), entity);
        prefix.setValue(entity.getInternationalPhonePrefix());

        return prefix;
    }

    public static <T extends IdNameCodeDTO> T fromEntity(T value, MasterdataCountryValue entity) {
        value.setId(entity.getId());
        value.setCode(entity.getCode());
        value.setName(entity.getName());
        return value;
    }
}
