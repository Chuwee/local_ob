package es.onebox.mgmt.common.restrictions;

import es.onebox.mgmt.common.restrictions.dto.ConfigurationStructureFieldDTO;
import es.onebox.mgmt.members.DynamicBusinessRuleFieldContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestrictionConverter {
    private RestrictionConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static void buildRestrictionField(Map<String, Object> memberConfigMap, List<ConfigurationStructureFieldDTO> fields, RestrictionTypeField restrictionTypeFields, String s) {
        ConfigurationStructureFieldDTO restrictionStructureFieldDTO = new ConfigurationStructureFieldDTO();
        restrictionStructureFieldDTO.setId(restrictionTypeFields.getFieldName());
        restrictionStructureFieldDTO.setType(DynamicBusinessRuleFieldType.valueOf(restrictionTypeFields.getFieldType().name()));
        restrictionStructureFieldDTO.setContainer(StructureContainer.valueOf(restrictionTypeFields.getFieldContainer().name()));
        if (restrictionTypeFields.getValueSource() != null) {
            restrictionStructureFieldDTO.setSource(restrictionTypeFields.getValueSource());
        }

        if (memberConfigMap != null) {
            String values = s;
            if (values != null) {
                if (restrictionTypeFields.getFieldContainer().equals(DynamicBusinessRuleFieldContainer.MAP)) {
                    Map<String, String> map = new HashMap<>();
                    values = values.replace("{", "");
                    values = values.replace("}", "");
                    String[] elements = values.split(",");
                    for (String elem : elements) {
                        map.put(elem.split("=")[0].trim(), elem.split("=")[1].trim());
                    }
                    restrictionStructureFieldDTO.setValue(map);
                }
                if (restrictionTypeFields.getFieldContainer().equals(DynamicBusinessRuleFieldContainer.LIST)) {
                    String[] elements = values.replace("[", "").replace("]", "").split(",");
                    if (restrictionTypeFields.getFieldType().equals(DynamicBusinessRuleFieldType.INTEGER)) {
                        List<Integer> intElements = new ArrayList<>();
                        for (String elem : elements) {
                            if (!elem.trim().isEmpty()) {
                                intElements.add(Integer.valueOf(elem.trim()));
                            }
                        }
                        restrictionStructureFieldDTO.setValue(intElements);
                    }
                    if (restrictionTypeFields.getFieldType().equals(DynamicBusinessRuleFieldType.STRING)) {
                        List<String> strElements = new ArrayList<>();
                        for (String elem : elements) {
                            if (!elem.trim().isEmpty()) {
                                strElements.add(elem.trim());
                            }
                        }
                        restrictionStructureFieldDTO.setValue(strElements);
                    }
                }
                if (restrictionTypeFields.getFieldContainer().equals(DynamicBusinessRuleFieldContainer.SINGLE)) {
                    restrictionStructureFieldDTO.setValue(values);
                }
            }
        }
        fields.add(restrictionStructureFieldDTO);
    }

    public static String mapValues(RestrictionTypeField dynamicBusinessRuleFields,
                                   Map<String, Object> memberConfigMap) {
        try {
            Map<String, Object> map2 = (Map<String, Object>) memberConfigMap.get(dynamicBusinessRuleFields.getFieldName());
            Map<String, Object> map3 = (Map<String, Object>) map2.get("data");
            return map3.get(dynamicBusinessRuleFields.getFieldName()) != null ? map3.get(dynamicBusinessRuleFields.getFieldName()).toString() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
