package es.onebox.event.forms.converter;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.forms.domain.CustomerTypes;
import es.onebox.event.forms.domain.FieldType;
import es.onebox.event.forms.domain.FieldValue;
import es.onebox.event.forms.domain.Form;
import es.onebox.event.forms.domain.FormField;
import es.onebox.event.forms.domain.MasterFormField;
import es.onebox.event.forms.domain.MasterFormSubField;
import es.onebox.event.forms.domain.ValidationRule;
import es.onebox.event.forms.domain.ValidationRuleType;
import es.onebox.event.forms.dto.CustomerTypesDTO;

import es.onebox.event.forms.dto.FormFieldValueDTO;
import es.onebox.event.forms.dto.FormFieldDTO;
import es.onebox.event.forms.dto.ValidationRuleDTO;
import es.onebox.event.forms.dto.UpdateFormDTO;
import es.onebox.event.forms.dto.UpdateFormFieldDTO;
import es.onebox.event.forms.enums.ValidationRuleTypeDTO;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FormConverter {

    private FormConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static List<List<FormFieldDTO>> toDTO(List<List<FormField>> fields,
                                                 Map<String, MasterFormField> masterFields) {
        return fields.stream()
                .map(fieldList -> fieldList.stream()
                        .map(field -> toDTO(field, masterFields))
                        .toList())
                .toList();
    }

    private static FormFieldDTO toDTO(FormField field, Map<String, MasterFormField> masterForm) {
        MasterFormField masterField = masterForm.get(field.getKey());
        FormFieldDTO out = new FormFieldDTO();
        out.setKey(masterField.getKey());
        out.setRequired(field.getRequired());
        out.setVisible(field.getVisible());
        out.setUneditable(field.getUneditable());
        out.setRules(toValidationRulesDTO(field.getValidationRules()));
        out.setSize(field.getSize());
        out.setType(masterField.getType().name());
        out.setValidationType(masterField.getValidationType() != null ? masterField.getValidationType().name() : null);
        out.setCustomerTypes(toCustomerTypesDTO(field.getCustomerTypes()));
        out.setValues(toFieldValuesDTO(masterField.getValues()));
        out.setExternalField(false); // No external fields for season tickets
        if (CollectionUtils.isNotEmpty(masterField.getFields())) {
            out.setFields(masterField.getFields().stream()
                    .map(FormConverter::toSubFieldDTO)
                    .toList());
        }
        return out;
    }

    private static FormFieldDTO toSubFieldDTO(MasterFormSubField subField) {
        FormFieldDTO out = new FormFieldDTO();
        out.setKey(subField.getKey());
        out.setSize(subField.getSize());
        out.setType(subField.getType().name());
        out.setValues(toFieldValuesDTO(subField.getValues()));
        return out;
    }

    private static List<FormFieldValueDTO> toFieldValuesDTO(List<FieldValue> values) {
        if (CollectionUtils.isEmpty(values)) {
            return null;
        }
        return values.stream().map(FormConverter::toFieldValueDTO).toList();
    }

    private static FormFieldValueDTO toFieldValueDTO(FieldValue in) {
        if (in == null) {
            return null;
        }
        FormFieldValueDTO out = new FormFieldValueDTO();
        out.setLabel(in.getLabel());
        out.setValue(in.getValue());
        out.setUnicode(in.getUnicode());
        return out;
    }

    public static List<ValidationRuleDTO> toValidationRulesDTO(List<ValidationRule> validationRules) {
        if (CollectionUtils.isEmpty(validationRules)) {
            return null;
        }
        return validationRules.stream().map(FormConverter::toValidationRuleDTO).toList();
    }

    private static ValidationRuleDTO toValidationRuleDTO(ValidationRule rule) {
        ValidationRuleDTO out = new ValidationRuleDTO();
        out.setRule(ValidationRuleTypeDTO.valueOf(rule.getRule().name()));
        out.setValue(rule.getValue());
        return out;
    }

    private static CustomerTypesDTO toCustomerTypesDTO(CustomerTypes customerTypes) {
        if(customerTypes == null || CollectionUtils.isEmpty(customerTypes.getCodes())){
            return null;
        }
        CustomerTypesDTO out = new CustomerTypesDTO();
        out.setCodes(customerTypes.getCodes());
        return out;
    }

    public static Form toDomain(UpdateFormDTO updateForm, Map<String, MasterFormField> masterFormFields) {
        return updateForm.stream()
                .map(it -> it.stream().map(field -> toDomain(field, masterFormFields.get(field.getKey()))).toList())
                .collect(Collectors.toCollection(Form::new));
    }

    private static FormField toDomain(UpdateFormFieldDTO field, MasterFormField masterField) {
        FormField target = new FormField();
        target.setKey(field.getKey());
        target.setRequired(field.getRequired());
        target.setVisible(field.getVisible());
        target.setUneditable(field.getUneditable());
        target.setValidationRules(toDomain(field.getRules(), masterField.getType()));
        target.setCustomerTypes(toDomain(field.getCustomerTypes()));
        return target;
    }

    private static List<ValidationRule> toDomain(List<ValidationRuleDTO> validationRules, FieldType fieldType) {
        if (CollectionUtils.isEmpty(validationRules)) {
            return null;
        }
        return validationRules.stream().map(validationRule -> toDomain(validationRule, fieldType)).toList();
    }

    private static ValidationRule toDomain(ValidationRuleDTO rule, FieldType fieldType) {
        ValidationRule target = new ValidationRule();
        ValidationRuleType validationRuleType = ValidationRuleType.valueOf(rule.getRule().name());
        if (!validationRuleType.containsType(fieldType)) {
            throw new OneboxRestException(MsEventErrorCode.INVALID_VALIDATION_RULE_FOR_FIELD_TYPE);
        }
        target.setRule(validationRuleType);
        target.setValue(rule.getValue());
        return target;
    }

    private static CustomerTypes toDomain(CustomerTypesDTO customerTypes) {
        if(customerTypes == null || CollectionUtils.isEmpty(customerTypes.getCodes())){
            return null;
        }
        CustomerTypes out = new CustomerTypes();
        out.setCodes(customerTypes.getCodes());
        return out;
    }

    public static UpdateFormFieldDTO toUpdateFormFieldDTO(FormFieldDTO formField) {
        UpdateFormFieldDTO updateFormFieldDTO = new UpdateFormFieldDTO();
        updateFormFieldDTO.setKey(formField.getKey());
        updateFormFieldDTO.setRequired(formField.getRequired());
        updateFormFieldDTO.setVisible(formField.getVisible());
        updateFormFieldDTO.setUneditable(formField.getUneditable());
        updateFormFieldDTO.setSize(formField.getSize());
        updateFormFieldDTO.setRules(formField.getRules());
        return updateFormFieldDTO;
    }
} 