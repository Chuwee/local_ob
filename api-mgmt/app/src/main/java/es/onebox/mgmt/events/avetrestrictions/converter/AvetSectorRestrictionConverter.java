package es.onebox.mgmt.events.avetrestrictions.converter;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.common.restrictions.RestrictionTypeField;
import es.onebox.mgmt.common.restrictions.dto.ConfigurationStructureFieldDTO;
import es.onebox.mgmt.events.avetrestrictions.dto.AvetSectorRestrictionCreateDTO;
import es.onebox.mgmt.events.avetrestrictions.dto.AvetSectorRestrictionDTO;
import es.onebox.mgmt.events.avetrestrictions.dto.AvetSectorRestrictionDetailDTO;
import es.onebox.mgmt.events.avetrestrictions.dto.AvetSectorRestrictionStructureDTO;
import es.onebox.mgmt.events.avetrestrictions.dto.AvetSectorRestrictionUpdateDTO;
import es.onebox.mgmt.events.avetrestrictions.dto.AvetSectorRestrictionsDTO;
import es.onebox.mgmt.events.avetrestrictions.enums.AvetSectorRestrictionType;
import es.onebox.mgmt.events.avetrestrictions.enums.AvetSectorRestrictionTypeFieldsType;
import es.onebox.mgmt.events.avetrestrictions.enums.TimeLapse;
import es.onebox.mgmt.events.avetrestrictions.enums.TimeUnit;
import es.onebox.mgmt.events.avetrestrictions.mapper.AvetSectorRestriction;
import es.onebox.mgmt.events.avetrestrictions.mapper.AvetSectorRestrictionCreate;
import es.onebox.mgmt.events.avetrestrictions.mapper.AvetSectorRestrictions;
import es.onebox.mgmt.events.avetrestrictions.mapper.UpdateAvetSectorRestriction;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static es.onebox.mgmt.common.restrictions.RestrictionConverter.buildRestrictionField;
import static es.onebox.mgmt.common.restrictions.RestrictionConverter.mapValues;
import static es.onebox.mgmt.events.avetrestrictions.enums.AvetSectorRestrictionType.SESSION_START_TIME;
import static es.onebox.mgmt.events.avetrestrictions.enums.AvetSectorRestrictionTypeFieldsType.TIME_LAPSE;
import static es.onebox.mgmt.events.avetrestrictions.enums.AvetSectorRestrictionTypeFieldsType.TIME_UNIT;

public class AvetSectorRestrictionConverter {
    private AvetSectorRestrictionConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static AvetSectorRestrictionCreate convert(AvetSectorRestrictionCreateDTO avetSectorRestrictionCreateDTO) {
        AvetSectorRestrictionCreate avetSectorRestrictionCreate = new AvetSectorRestrictionCreate();
        avetSectorRestrictionCreate.setType(avetSectorRestrictionCreateDTO.getType());
        avetSectorRestrictionCreate.setName(avetSectorRestrictionCreateDTO.getName());

        return avetSectorRestrictionCreate;
    }

    public static AvetSectorRestrictionsDTO toAvetSectorRestrictionsDTO(AvetSectorRestrictions avetSectorRestrictions) {
        AvetSectorRestrictionsDTO avetSectorRestrictionDTOS = new AvetSectorRestrictionsDTO();

        for (AvetSectorRestriction avetSectorRestriction : avetSectorRestrictions) {
            AvetSectorRestrictionDTO avetSectorRestrictionDTO = toAvetSectorRestrictionDTO(avetSectorRestriction);

            avetSectorRestrictionDTOS.add(avetSectorRestrictionDTO);
        }

        return avetSectorRestrictionDTOS;
    }

    public static AvetSectorRestrictionDTO toAvetSectorRestrictionDTO(AvetSectorRestriction avetSectorRestriction) {
        if (avetSectorRestriction == null) {
            return null;
        }
        AvetSectorRestrictionDTO avetSectorRestrictionDTO = new AvetSectorRestrictionDTO();
        avetSectorRestrictionDTO.setSid(avetSectorRestriction.getSid());
        avetSectorRestrictionDTO.setName(avetSectorRestriction.getName());
        avetSectorRestrictionDTO.setActivated(avetSectorRestriction.getActivated());
        avetSectorRestrictionDTO.setType(avetSectorRestriction.getType());

        return avetSectorRestrictionDTO;
    }

    public static AvetSectorRestrictionDetailDTO toAvetSectorRestrictionDetailDTO(AvetSectorRestriction avetSectorRestriction) {
        if (avetSectorRestriction == null) {
            return null;
        }
        AvetSectorRestrictionDetailDTO avetSectorRestrictionDetailDTO = new AvetSectorRestrictionDetailDTO();
        avetSectorRestrictionDetailDTO.setSid(avetSectorRestriction.getSid());
        avetSectorRestrictionDetailDTO.setName(avetSectorRestriction.getName());
        avetSectorRestrictionDetailDTO.setActivated(avetSectorRestriction.getActivated());
        avetSectorRestrictionDetailDTO.setTranslations(avetSectorRestriction.getTranslations());
        avetSectorRestrictionDetailDTO.setFields(avetSectorRestriction.getFields());
        avetSectorRestrictionDetailDTO.setVenueTemplateSectors(avetSectorRestriction.getVenueTemplateSectors());
        avetSectorRestrictionDetailDTO.setType(avetSectorRestriction.getType());

        return avetSectorRestrictionDetailDTO;
    }

    public static UpdateAvetSectorRestriction toUpdateAvetSectorRestriction(AvetSectorRestrictionType avetSectorRestrictionType,
                                                                            AvetSectorRestrictionUpdateDTO avetSectorRestrictionUpdateDTO) {
        UpdateAvetSectorRestriction updateAvetSectorRestriction = new UpdateAvetSectorRestriction();
        updateAvetSectorRestriction.setActivated(avetSectorRestrictionUpdateDTO.getActivated());
        updateAvetSectorRestriction.setName(avetSectorRestrictionUpdateDTO.getName());
        if (avetSectorRestrictionUpdateDTO.getVenueTemplateSectors() != null) {
            updateAvetSectorRestriction.setVenueTemplateSectors(avetSectorRestrictionUpdateDTO.getVenueTemplateSectors());
        }
        if (avetSectorRestrictionUpdateDTO.getFields() != null) {
            mapFields(avetSectorRestrictionUpdateDTO, updateAvetSectorRestriction, avetSectorRestrictionType);
        }

        return updateAvetSectorRestriction;
    }

    private static void mapFields(AvetSectorRestrictionUpdateDTO avetSectorRestrictionUpdateDTO,
                                  UpdateAvetSectorRestriction updateAvetSectorRestriction,
                                  AvetSectorRestrictionType avetSectorRestrictionType) {
        Map<String, Object> restrictionFields = new HashMap<>();
        for (AvetSectorRestrictionTypeFieldsType avetSectorRestrictionTypeFields : avetSectorRestrictionType.getFields()) {

            String fieldName = avetSectorRestrictionTypeFields.getFieldName();
            if (avetSectorRestrictionUpdateDTO.getFields().containsKey(fieldName)) {
                Object fieldValue = avetSectorRestrictionUpdateDTO.getFields().get(fieldName);
                if (avetSectorRestrictionType.equals(SESSION_START_TIME)) {
                    checkFieldsStructure(fieldName, fieldValue);
                }
                restrictionFields.put(fieldName, fieldValue);
            }
        }
        Map<String, Object> avetSectorRestrictionFields = new HashMap<>(restrictionFields);
        updateAvetSectorRestriction.setFields(avetSectorRestrictionFields);
    }

    public static List<AvetSectorRestrictionStructureDTO> toStructure(AvetSectorRestrictionType avetSectorRestrictionType,
                                                                      Map<String, Object> memberConfigMap) {

        AvetSectorRestrictionStructureDTO avetSectorRestrictionStructureDTO = new AvetSectorRestrictionStructureDTO();
        avetSectorRestrictionStructureDTO.setRestrictionType(AvetSectorRestrictionType.valueOf(avetSectorRestrictionType.name()));

        List<ConfigurationStructureFieldDTO> fields = new ArrayList<>();
        for (RestrictionTypeField restrictionTypeFields : avetSectorRestrictionType.getFields()) {
            buildRestrictionField(memberConfigMap, fields, restrictionTypeFields, mapValues(restrictionTypeFields, memberConfigMap));
        }
        avetSectorRestrictionStructureDTO.setFields(fields);
        return List.of(avetSectorRestrictionStructureDTO);
    }

    public static List<AvetSectorRestrictionStructureDTO> toStructure(AvetSectorRestrictionType[] restrictionTypes,
                                                                      Map<String, Object> memberConfigMap) {
        List<AvetSectorRestrictionStructureDTO> result = new ArrayList<>();
        for (AvetSectorRestrictionType restrictionType : restrictionTypes) {
            List<AvetSectorRestrictionStructureDTO> list = toStructure(restrictionType, memberConfigMap);
            result.addAll(list);
        }
        return result;
    }

    private static void checkFieldsStructure(String fieldName, Object fieldValue) {
        if (fieldName.toUpperCase().equals(TIME_UNIT.name())) {
            String value = ((String) fieldValue).toUpperCase();
            if (!value.equals(TimeUnit.HOURS.name()) &&
                    !value.equals(TimeUnit.MINUTES.name()) &&
                    !value.equals(TimeUnit.DAYS.name())) {
                throw new OneboxRestException(ApiMgmtErrorCode.TIME_RESTRICTION_UNIT_NOT_VALID);
            }
        }
        if (fieldName.toUpperCase().equals(TIME_LAPSE.name())) {
            String value = ((String) fieldValue).toUpperCase();
            if (!(value.equals(TimeLapse.BEFORE.name()) || value.equals(TimeLapse.AFTER.name()))) {
                throw new OneboxRestException(ApiMgmtErrorCode.TIME_RESTRICTION_LAPSE_NOT_VALID);
            }
        }
    }
}
