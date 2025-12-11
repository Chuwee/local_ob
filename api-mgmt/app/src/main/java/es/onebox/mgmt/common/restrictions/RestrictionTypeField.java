package es.onebox.mgmt.common.restrictions;

import es.onebox.mgmt.members.DynamicBusinessRuleFieldContainer;

public interface RestrictionTypeField {
    DynamicBusinessRuleFieldType getFieldType();
    DynamicBusinessRuleFieldContainer getFieldContainer();
    StructureContainerDataOrigin getValueSource();
    String getFieldName();
}
