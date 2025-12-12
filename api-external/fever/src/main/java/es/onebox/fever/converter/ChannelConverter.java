package es.onebox.fever.converter;

import es.onebox.common.datasources.ms.channel.dto.ChannelFormField;
import es.onebox.common.datasources.ms.channel.dto.ChannelFormsResponse;
import es.onebox.common.datasources.ms.channel.dto.DynamicFormValidationRule;
import es.onebox.common.datasources.webhook.dto.fever.ChannelFormDetailDTO;
import es.onebox.common.datasources.webhook.dto.fever.ChannelFormFieldDTO;
import es.onebox.common.datasources.webhook.dto.fever.DynamicFormValidatorInfoDTO;
import es.onebox.core.order.utils.common.BuyerDataUtils;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChannelConverter {

    public static ChannelFormDetailDTO mapResponseToDetailDTO(ChannelFormsResponse response) {
        ChannelFormDetailDTO dto = new ChannelFormDetailDTO();
        dto.setBooking(mapFields(response.getBooking()));
        dto.setIssue(mapFields(response.getIssue()));
        dto.setMember(mapFields(response.getMember()));
        dto.setNewMember(mapFields(response.getNewMember()));
        dto.setPurchase(mapFields(response.getPurchase()));
        return dto;
    }

    private static List<ChannelFormFieldDTO> mapFields(List<ChannelFormField> fields) {
        return fields != null ? fields.stream().map(ChannelConverter::mapFieldToFieldDTO).toList() : null;
    }

    private static ChannelFormFieldDTO mapFieldToFieldDTO(ChannelFormField field) {
        ChannelFormFieldDTO dto = new ChannelFormFieldDTO();
        dto.setKey(field.getKey());
        dto.setExternalKey(BuyerDataUtils.getFieldExternalKey(field.getKey()));
        dto.setMandatory(field.getMandatory());
        dto.setVisible(field.getVisible());
        dto.setMutable(field.getMutable());
        dto.setUneditable(field.getUneditable());
        dto.setAvailableRules(Arrays.stream(DynamicFormValidationRule.values()).filter(rule -> rule.containsType(field.getType()))
                .map(rule -> new DynamicFormValidatorInfoDTO(rule, rule.isValRequired(), rule.getValType()))
                .collect(Collectors.toList()));
        return dto;
    }
}
