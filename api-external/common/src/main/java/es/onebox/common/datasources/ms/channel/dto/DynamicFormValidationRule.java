package es.onebox.common.datasources.ms.channel.dto;

import es.onebox.common.datasources.webhook.dto.fever.ValidationRuleValueType;
import es.onebox.dal.dto.couch.enums.ChannelFieldType;

import java.io.Serializable;
import java.util.List;

public enum DynamicFormValidationRule implements Serializable {

    MIN_YEAR(List.of(ChannelFieldType.DATE), true, ValidationRuleValueType.INTEGER),
    MAX_YEAR(List.of(ChannelFieldType.DATE), true, ValidationRuleValueType.INTEGER),
    REQUIRED_WHEN_AGE_IS_BIGGER_THAN(List.of(ChannelFieldType.TEXT, ChannelFieldType.NUMBER), true, ValidationRuleValueType.INTEGER),
    ID_FIELD(List.of(ChannelFieldType.NUMBER), true, ValidationRuleValueType.INTEGER),
    REGEX(List.of(ChannelFieldType.TEXT, ChannelFieldType.NUMBER, ChannelFieldType.DATE, ChannelFieldType.EMAIL),
            true, ValidationRuleValueType.REGEX);

    private List<ChannelFieldType> types;
    private Boolean valRequired;
    private ValidationRuleValueType valType;

    private DynamicFormValidationRule(List<ChannelFieldType> types) {
        this.types = types;
    }

    private DynamicFormValidationRule(List<ChannelFieldType> types, Boolean valRequired, ValidationRuleValueType valType) {
        this.types = types;
        this.valRequired = valRequired;
        this.valType = valType;
    }

    public List<ChannelFieldType> getTypes() {
        return types;
    }

    public Boolean isValRequired() {
        return valRequired;
    }

    public ValidationRuleValueType getValType() {
        return valType;
    }

    public Boolean containsType(String compType) {
        return types.stream().anyMatch(type -> type.toString().equals(compType));
    }

    public static DynamicFormValidationRule get(int id) {
        return values()[id];
    }

    @Override
    public String toString() {
        return String.valueOf(types);
    }
}
