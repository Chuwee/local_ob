package es.onebox.mgmt.entities.converter;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.entity.dto.Attribute;
import es.onebox.mgmt.datasources.ms.entity.dto.AttributeTexts;
import es.onebox.mgmt.datasources.ms.entity.dto.AttributeValue;
import es.onebox.mgmt.entities.dto.AttributeDTO;
import es.onebox.mgmt.entities.dto.AttributeTextsDTO;
import es.onebox.mgmt.entities.dto.AttributeValueDTO;
import es.onebox.mgmt.entities.enums.AttributeScope;
import es.onebox.mgmt.entities.enums.AttributeSelectionType;
import es.onebox.mgmt.entities.enums.AttributeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AttributeConverter {

    private AttributeConverter() {
    }

    public static AttributeDTO fromMsEntity(Attribute attribute, Map<Long, String> languages, String attributeValue) {
        AttributeDTO attr = fromMsEntity(attribute, languages);
        if(attributeValue != null) {
            attr.setValue(attributeValue);
        }
        return attr;
    }

    public static AttributeDTO fromMsEntity(Attribute attribute, Map<Long, String> languages) {
        AttributeDTO attributeDTO = new AttributeDTO();
        attributeDTO.setId(attribute.getId());
        attributeDTO.setEntityId(attribute.getEntityId());
        attributeDTO.setName(attribute.getName());
        attributeDTO.setCode(attribute.getCode());
        attributeDTO.setScope(AttributeScope.get(attribute.getScope()));
        attributeDTO.setType(AttributeType.get(attribute.getType()));
        if (AttributeType.DEFINED.getId() == attribute.getType()) {
            attributeDTO.setSelectionType(AttributeSelectionType.get(attribute.getSelectionType()));
        }
        attributeDTO.setMin(attribute.getMin());
        attributeDTO.setMax(attribute.getMax());

        AttributeTexts attributeTexts = attribute.getTexts();
        if (languages != null && attributeTexts != null) {
            attributeDTO.setTexts(new AttributeTextsDTO(new HashMap<>()));
            convertTextsIds(languages, attributeTexts.getName(), attributeDTO.getTexts().getName());

            if (!CommonUtils.isEmpty(attributeTexts.getValues())) {
                attributeDTO.getTexts().setValues(new ArrayList<>());
                for (AttributeValue value : attributeTexts.getValues()) {
                    AttributeValueDTO attributeValue = new AttributeValueDTO(value.getId(), value.getName(), new HashMap<>());
                    convertTextsIds(languages, value.getValue(), attributeValue.getValue());
                    attributeDTO.getTexts().getValues().add(attributeValue);
                }
            }
        }

        return attributeDTO;
    }

    private static void convertTextsIds(Map<Long, String> languages, Map<Long, String> texts, Map<String, String> target) {
        for (Map.Entry<Long, String> text : texts.entrySet()) {
            if (languages.containsKey(text.getKey())) {
                String locale = languages.get(text.getKey());
                String languageTag = ConverterUtils.toLanguageTag(locale);
                target.put(languageTag, text.getValue());
            }
        }
    }

    public static Attribute toMsEntity(AttributeDTO updateAttribute) {
        Attribute attributeDTO = new Attribute();
        attributeDTO.setName(updateAttribute.getName());
        attributeDTO.setCode(updateAttribute.getCode());
        attributeDTO.setMin(updateAttribute.getMin());
        attributeDTO.setMax(updateAttribute.getMax());

        if (updateAttribute.getScope() != null) {
            attributeDTO.setScope(updateAttribute.getScope().getId());
        }
        if (updateAttribute.getType() != null) {
            attributeDTO.setType(updateAttribute.getType().getId());
        }
        if (updateAttribute.getSelectionType() != null) {
            attributeDTO.setSelectionType(updateAttribute.getSelectionType().getId());
        }

        return attributeDTO;
    }
}
