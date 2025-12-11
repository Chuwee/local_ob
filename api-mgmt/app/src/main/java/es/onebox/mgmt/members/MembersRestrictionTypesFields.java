package es.onebox.mgmt.members;

import es.onebox.mgmt.common.restrictions.DynamicBusinessRuleFieldType;
import es.onebox.mgmt.common.restrictions.RestrictionTypeField;
import es.onebox.mgmt.common.restrictions.StructureContainerDataOrigin;

import static es.onebox.mgmt.common.restrictions.StructureContainerDataOrigin.ROLE_ID;
import static es.onebox.mgmt.common.restrictions.StructureContainerDataOrigin.SECTOR_ID;

public enum MembersRestrictionTypesFields implements RestrictionTypeField {

    MINIMUM_MAXIMUM(DynamicBusinessRuleFieldType.STRING, DynamicBusinessRuleFieldContainer.SINGLE, StructureContainerDataOrigin.MINIMUM_MAXIMUM, "minimum_maximum"),
    LIMIT_TICKETS_NUMBER(DynamicBusinessRuleFieldType.INTEGER, DynamicBusinessRuleFieldContainer.SINGLE, null, "limit_tickets_number"),
    LIMIT_ROLES(DynamicBusinessRuleFieldType.INTEGER, DynamicBusinessRuleFieldContainer.LIST, ROLE_ID, "limit_roles"),
    RATIO_CONDITION_TICKETS_NUMBER(DynamicBusinessRuleFieldType.INTEGER, DynamicBusinessRuleFieldContainer.SINGLE, null, "ratio_condition_tickets_number"),
    RATIO_ALLOWED_TICKETS_NUMBER(DynamicBusinessRuleFieldType.INTEGER, DynamicBusinessRuleFieldContainer.SINGLE, null, "ratio_allowed_tickets_number"),
    RATIO_ALLOWED_ROLES(DynamicBusinessRuleFieldType.INTEGER, DynamicBusinessRuleFieldContainer.LIST, ROLE_ID, "ratio_allowed_roles"),
    RATIO_CONDITION_ROLES(DynamicBusinessRuleFieldType.INTEGER, DynamicBusinessRuleFieldContainer.LIST, ROLE_ID, "ratio_condition_roles"),
    VENUE_TEMPLATE_SECTORS(DynamicBusinessRuleFieldType.INTEGER, DynamicBusinessRuleFieldContainer.LIST, SECTOR_ID, "venue_template_sectors");


    private final DynamicBusinessRuleFieldType fieldType;
    private final DynamicBusinessRuleFieldContainer fieldContainer;
    private final StructureContainerDataOrigin valueSource;
    private final String fieldName;

    MembersRestrictionTypesFields(DynamicBusinessRuleFieldType fieldType, DynamicBusinessRuleFieldContainer fieldContainer,
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
