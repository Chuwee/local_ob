package es.onebox.mgmt.events.avetrestrictions.enums;

import es.onebox.mgmt.common.restrictions.DynamicBusinessRuleFieldType;
import es.onebox.mgmt.common.restrictions.RestrictionTypeField;
import es.onebox.mgmt.common.restrictions.StructureContainerDataOrigin;
import es.onebox.mgmt.members.DynamicBusinessRuleFieldContainer;

import static es.onebox.mgmt.common.restrictions.StructureContainerDataOrigin.CAPACITY_ID;
import static es.onebox.mgmt.common.restrictions.StructureContainerDataOrigin.ONE_ALL_QUALIFIER;
import static es.onebox.mgmt.common.restrictions.StructureContainerDataOrigin.ROLE_ID;
import static es.onebox.mgmt.common.restrictions.StructureContainerDataOrigin.SECTOR_ID;
import static es.onebox.mgmt.common.restrictions.StructureContainerDataOrigin.TERM_ID;

public enum AvetSectorRestrictionTypeFieldsType implements RestrictionTypeField {
    LIMIT_ROLES(DynamicBusinessRuleFieldType.INTEGER, DynamicBusinessRuleFieldContainer.LIST, ROLE_ID, "limit_roles"),
    PAID_TERMS(DynamicBusinessRuleFieldType.INTEGER, DynamicBusinessRuleFieldContainer.LIST, TERM_ID, "paid_terms"),
    ONE_ALL_OF(DynamicBusinessRuleFieldType.STRING, DynamicBusinessRuleFieldContainer.SINGLE, ONE_ALL_QUALIFIER, "one_of_all_of"),
    PASS_CAPACITY_ID(DynamicBusinessRuleFieldType.INTEGER, DynamicBusinessRuleFieldContainer.SINGLE, CAPACITY_ID, "pass_capacity_id"),
    UNAVAILABLE_PASS_IN_SECTORS(DynamicBusinessRuleFieldType.INTEGER, DynamicBusinessRuleFieldContainer.LIST, SECTOR_ID, "unavailable_pass_in_sectors"),
    OBSERVATION_ID(DynamicBusinessRuleFieldType.INTEGER, DynamicBusinessRuleFieldContainer.SINGLE, null, "observation_id"),
    OBSERVATION_TEXT(DynamicBusinessRuleFieldType.STRING, DynamicBusinessRuleFieldContainer.SINGLE, null, "observation_text"),
    TIME_DIGIT(DynamicBusinessRuleFieldType.INTEGER, DynamicBusinessRuleFieldContainer.SINGLE, null, "time_digit"),
    TIME_UNIT(DynamicBusinessRuleFieldType.STRING, DynamicBusinessRuleFieldContainer.SINGLE, StructureContainerDataOrigin.TIME_UNIT, "time_unit"),
    TIME_LAPSE(DynamicBusinessRuleFieldType.STRING, DynamicBusinessRuleFieldContainer.SINGLE, StructureContainerDataOrigin.TIME_LAPSE, "time_lapse");

    private final DynamicBusinessRuleFieldType fieldType;
    private final DynamicBusinessRuleFieldContainer fieldContainer;
    private final StructureContainerDataOrigin valueSource;
    private final String fieldName;

    AvetSectorRestrictionTypeFieldsType(DynamicBusinessRuleFieldType fieldType,
                                        DynamicBusinessRuleFieldContainer fieldContainer,
                                        StructureContainerDataOrigin valueSource, String fieldName) {
        this.fieldType = fieldType;
        this.fieldContainer = fieldContainer;
        this.valueSource = valueSource;
        this.fieldName = fieldName;
    }

    public DynamicBusinessRuleFieldType getFieldType() {
        return fieldType;
    }

    public DynamicBusinessRuleFieldContainer getFieldContainer() {
        return fieldContainer;
    }

    public StructureContainerDataOrigin getValueSource() {
        return valueSource;
    }

    public String getFieldName() {
        return fieldName;
    }
}
