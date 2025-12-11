package es.onebox.event.attributes;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.datasources.ms.entity.dto.Attribute;
import es.onebox.event.datasources.ms.entity.dto.AttributeSelectionType;
import es.onebox.event.datasources.ms.entity.dto.AttributeValue;
import es.onebox.event.exception.MsEventErrorCode;

import java.util.List;
import java.util.stream.Collectors;

public class AttributeService {

    public static void checkStringValue(Attribute attribute, AttributeRequestValueDTO requestValueDTO) {
        if (requestValueDTO.getValue() != null) {
            if (requestValueDTO.getValue().isEmpty()) {
                return;
            }
            if ((attribute.getMin() != null && attribute.getMin().intValue() > requestValueDTO.getValue().length()) ||
                    (attribute.getMax() != null && requestValueDTO.getValue().length() > attribute.getMax().intValue())) {
                throw OneboxRestException
                        .builder(MsEventErrorCode.ATTRIBUTE_INVALID_VALUE)
                        .setMessage("Attributes id: " + attribute.getId() + ", value: " + requestValueDTO.getValue() + ", string length exceeded")
                        .build();
            }
        }
    }

    public static void checkNumericValue(Attribute attribute, AttributeRequestValueDTO requestValueDTO) {
        if (requestValueDTO.getValue().isEmpty()) {
            return;
        }
        int value;
        try {
            value = Integer.parseInt(requestValueDTO.getValue());
        } catch (Exception e) {
            throw OneboxRestException
                    .builder(MsEventErrorCode.ATTRIBUTE_INVALID_VALUE)
                    .setMessage("Attributes id: " + attribute.getId() + ", value: " + requestValueDTO.getValue())
                    .build();
        }
        if ((attribute.getMin() != null && attribute.getMin().intValue() > value) ||
                (attribute.getMax() != null && attribute.getMax().intValue() < value)) {
            throw OneboxRestException
                    .builder(MsEventErrorCode.ATTRIBUTE_INVALID_VALUE)
                    .setMessage("Attributes id: " + attribute.getId() + ", value: " + requestValueDTO.getValue() + ", numeric range exceeded")
                    .build();
        }
    }

    public void checkDefinedType(Attribute attribute, AttributeRequestValueDTO requestValueDTO) {
        if (AttributeSelectionType.SINGLE.getId() == attribute.getSelectionType()
        && requestValueDTO.getSelected() != null && requestValueDTO.getSelected().size() > 1) {
            throw OneboxRestException
                    .builder(MsEventErrorCode.ATTRIBUTE_INVALID_VALUE)
                    .setMessage("Attributes id: " + attribute.getId() + " is single seleccion")
                    .build();
        }

        List<Long> definedValueIds = attribute.getTexts().getValues().stream()
                .map(AttributeValue::getId)
                .collect(Collectors.toList());
        for (Long selected : requestValueDTO.getSelected()) {
            if (!definedValueIds.contains(selected)) {
                throw OneboxRestException
                        .builder(MsEventErrorCode.ATTRIBUTE_INVALID_VALUE)
                        .setMessage("Attributes id: " + attribute.getId() + ", valueId: " + selected)
                        .build();
            }
        }
    }

}
