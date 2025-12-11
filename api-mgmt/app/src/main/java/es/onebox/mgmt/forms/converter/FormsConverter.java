package es.onebox.mgmt.forms.converter;

import es.onebox.mgmt.datasources.ms.entity.dto.CustomerTypesField;
import es.onebox.mgmt.datasources.ms.entity.dto.Form;
import es.onebox.mgmt.datasources.ms.entity.dto.FormField;
import es.onebox.mgmt.datasources.ms.entity.dto.ValidationRule;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.CustomerType;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.CustomerTypes;
import es.onebox.mgmt.entities.customertypes.dto.CustomerTypeDTO;
import es.onebox.mgmt.entities.customertypes.dto.CustomerTypesDTO;
import es.onebox.mgmt.forms.dto.CustomerTypesFieldDTO;
import es.onebox.mgmt.forms.dto.FormFieldDTO;
import es.onebox.mgmt.forms.dto.FormValidatorInfoDTO;
import es.onebox.mgmt.forms.dto.UpdateFormDTO;
import es.onebox.mgmt.forms.dto.UpdateFormFieldDTO;
import es.onebox.mgmt.forms.dto.ValidationRuleDTO;
import es.onebox.mgmt.forms.enums.ValidationRuleType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FormsConverter {

    private FormsConverter() {
    }

    public static List<List<FormFieldDTO>> toDTO(List<List<FormField>> form, CustomerTypes customerTypes) {
        return form.stream().map(fieldList -> fieldList.stream().map(formField -> toDTO(formField, customerTypes)).toList()).toList();
    }

    private static FormFieldDTO toDTO(FormField in, CustomerTypes customerTypes) {
        FormFieldDTO out = new FormFieldDTO();
        out.setKey(in.getKey());
        out.setMandatory(BooleanUtils.isTrue(in.getRequired()));
        out.setVisible(BooleanUtils.isTrue(in.getVisible()));
        out.setUneditable(BooleanUtils.isTrue(in.getUneditable()));
        out.setUnique(BooleanUtils.isTrue(in.getUnique()));
        out.setExternalField(BooleanUtils.isTrue(in.getExternalField()));
        out.setSize(in.getSize());
        out.setType(in.getType());
        out.setAvailableRules(Arrays.stream(ValidationRuleType.values()).filter(rule -> rule.containsType(in.getType()))
                .map(rule -> new FormValidatorInfoDTO(rule, rule.getValueRequired(), rule.getValueType())).toList());
        out.setAppliedRules(toValidationRulesDTO(in.getRules()));
        out.setCustomerTypes(toCustomerTypesDTO(in.getCustomerTypes(), customerTypes));
        return out;
    }

    private static List<ValidationRuleDTO> toValidationRulesDTO(List<ValidationRule> rules) {
        if (CollectionUtils.isEmpty(rules)) {
            return null;
        }
        return rules.stream().map(FormsConverter::toValidationRuleDTO).toList();
    }

    private static ValidationRuleDTO toValidationRuleDTO(ValidationRule in) {
        ValidationRuleDTO out = new ValidationRuleDTO();
        out.setRule(ValidationRuleType.valueOf(in.getRule().name()));
        out.setValue(in.getValue());
        return out;
    }

    private static CustomerTypesDTO toCustomerTypesDTO(CustomerTypesField customerTypes, CustomerTypes entityCustomerTypes){
        if (customerTypes == null || CollectionUtils.isEmpty(customerTypes.getCodes())
                || Objects.isNull(entityCustomerTypes) || CollectionUtils.isEmpty(entityCustomerTypes.getData())) {
            return null;
        }
        Map<String, CustomerType> entityCustomerTypesMap = entityCustomerTypes.getData().stream()
                .collect(Collectors.toMap(CustomerType::getCode, Function.identity()));

        List<CustomerTypeDTO> out = customerTypes.getCodes().stream().filter(entityCustomerTypesMap::containsKey)
                .map(code -> toCustomerTypeDTO(entityCustomerTypesMap.get(code))).toList();
        return new CustomerTypesDTO(out);
    }

    private static CustomerTypeDTO toCustomerTypeDTO(CustomerType in){
        CustomerTypeDTO out = new CustomerTypeDTO();
        out.setId(in.getId());
        out.setName(in.getName());
        out.setCode(in.getCode());
        return out;
    }

    public static Form toMs(UpdateFormDTO updateFormDTO) {
        return updateFormDTO.stream()
                .map(it -> it.stream().map(FormsConverter::toUpdateFormsField).toList())
                .collect(Collectors.toCollection(Form::new));
    }

    private static FormField toUpdateFormsField(UpdateFormFieldDTO in) {
        FormField out = new FormField();
        out.setKey(in.getKey());
        out.setRequired(in.getMandatory());
        out.setUneditable(in.getUneditable());
        out.setVisible(in.getVisible());
        out.setUnique(in.getUnique());
        out.setSize(in.getSize());
        out.setRules(toUpdateValidationRules(in.getRules()));
        out.setCustomerTypes(toUpdateCustomerTypes(in.getCustomerTypes()));
        return out;
    }

    private static List<ValidationRule> toUpdateValidationRules(List<ValidationRuleDTO> rules) {
        if (CollectionUtils.isEmpty(rules)) {
            return null;
        }
        return rules.stream().map(FormsConverter::toUpdateEntityValidationRule).toList();
    }

    private static ValidationRule toUpdateEntityValidationRule(ValidationRuleDTO in) {
        ValidationRule out = new ValidationRule();
        out.setRule(es.onebox.mgmt.datasources.ms.entity.dto.ValidationRuleType.valueOf(in.getRule().name()));
        out.setValue(in.getValue());
        return out;
    }

    private static CustomerTypesField toUpdateCustomerTypes(CustomerTypesFieldDTO in) {
        if (in == null || CollectionUtils.isEmpty(in.getCodes())) {
            return null;
        }
        CustomerTypesField out = new CustomerTypesField();
        out.setCodes(in.getCodes());
        return out;
    }
}