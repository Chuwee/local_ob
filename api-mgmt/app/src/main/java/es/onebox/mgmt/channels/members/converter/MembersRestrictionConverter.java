package es.onebox.mgmt.channels.members.converter;

import es.onebox.mgmt.channels.enums.RestrictionType;
import es.onebox.mgmt.common.restrictions.dto.ConfigurationStructureFieldDTO;
import es.onebox.mgmt.common.restrictions.dto.RestrictionsStructureDTO;
import es.onebox.mgmt.members.MembersRestrictionTypesFields;
import es.onebox.mgmt.members.RestrictionTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static es.onebox.mgmt.common.restrictions.RestrictionConverter.buildRestrictionField;
import static es.onebox.mgmt.common.restrictions.RestrictionConverter.mapValues;

public class MembersRestrictionConverter {

    private MembersRestrictionConverter() {
    }

    public static List<RestrictionsStructureDTO> toStructure(RestrictionTypes restrictionTypes,
                                                             Map<String, Object> memberConfigMap) {

        RestrictionsStructureDTO restrictionsStructureDTO = new RestrictionsStructureDTO();
        restrictionsStructureDTO.setRestrictionType(RestrictionType.valueOf(restrictionTypes.name()));

        List<ConfigurationStructureFieldDTO> fields = new ArrayList<>();
        for(MembersRestrictionTypesFields restrictionTypesField : restrictionTypes.getFields()) {
            buildRestrictionField(memberConfigMap, fields, restrictionTypesField, mapValues(restrictionTypesField, memberConfigMap));
        }
        restrictionsStructureDTO.setFields(fields);
        return List.of(restrictionsStructureDTO);
    }

    public static List<RestrictionsStructureDTO> toStructure(RestrictionTypes[] restrictionTypes,
                                                             Map<String, Object> memberConfigMap) {
        List<RestrictionsStructureDTO> result = new ArrayList<>();
        for(RestrictionTypes restrictionType : restrictionTypes) {
            List<RestrictionsStructureDTO> list = toStructure(restrictionType, memberConfigMap);
            result.addAll(list);
        }
        return result;
    }
}
