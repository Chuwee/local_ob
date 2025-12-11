package es.onebox.mgmt.members;

import es.onebox.mgmt.common.restrictions.DynamicBusinessRuleFieldType;

public enum DynamicBusinessRuleFields {
    PERMISSION_WHITE_LIST(DynamicBusinessRuleFieldType.INTEGER, DynamicBusinessRuleFieldContainer.LIST,  null,null, "permissionsWhitelist"),
    PERMISSION_BLACK_LIST(DynamicBusinessRuleFieldType.INTEGER, DynamicBusinessRuleFieldContainer.LIST,  null,null, "permissionsBlacklist"),
    ALLOWED_ROLES(DynamicBusinessRuleFieldType.INTEGER, DynamicBusinessRuleFieldContainer.LIST,  "ROLE_ID", null, "allowedRoles"),
    ALLOWED_CAPACITIES(DynamicBusinessRuleFieldType.INTEGER, DynamicBusinessRuleFieldContainer.LIST,  "CAPACITY_ID", null, "allowedCapacities"),
    EXCLUDED_PERIODICITIES(DynamicBusinessRuleFieldType.INTEGER, DynamicBusinessRuleFieldContainer.LIST,  "PERIODICITY_ID", null, "excludedPeriodicities"),
    COMMENT_ID(DynamicBusinessRuleFieldType.INTEGER, DynamicBusinessRuleFieldContainer.SINGLE,  null, null, "commentId"), // hay que cambiar las clases para que en vez de ser commentChangedSeatId sea commentId
    COMMENT_CONTENT(DynamicBusinessRuleFieldType.STRING, DynamicBusinessRuleFieldContainer.SINGLE,  null, null, "commentContent"), // hay que cambiar las clases para que en vez de ser commentChangedSeatContent sea commentContent
    PAYED_TERMS_EXCLUSIONS(DynamicBusinessRuleFieldType.INTEGER, DynamicBusinessRuleFieldContainer.LIST,  "TERM_ID", null, "payedTermsExclusions"),
    UNPAYED_TERMS_EXCLUSIONS(DynamicBusinessRuleFieldType.INTEGER, DynamicBusinessRuleFieldContainer.LIST,  "TERM_ID", null, "unpayedTermsExclusions"),
    MEMBER_ROLE_RELATIONS(DynamicBusinessRuleFieldType.INTEGER, DynamicBusinessRuleFieldContainer.MAP,  "ROLE_ID", "ROLE_ID", "partnerRolesRelation"),
    ROLE_AGE_RELATIONS(DynamicBusinessRuleFieldType.STRING, DynamicBusinessRuleFieldContainer.MAP,  null, "ROLE_ID", "roleAgeRelation"),
    PERIODICITY_TERM_RELATIONS(DynamicBusinessRuleFieldType.INTEGER, DynamicBusinessRuleFieldContainer.MAP, "PERIODICITY_ID", "TERM_ID","periodicityTermRelation"),
    SUBSCRIPTION_MODES(DynamicBusinessRuleFieldType.INTEGER, DynamicBusinessRuleFieldContainer.LIST, "PERIODICITY_ID", null, "MODALITY_ID"),
    NUMBER_OF_TERMS(DynamicBusinessRuleFieldType.INTEGER, DynamicBusinessRuleFieldContainer.SINGLE, null, null, "numberOfTerms"),
    DISCOUNT_RANGE(DynamicBusinessRuleFieldType.INTEGER, DynamicBusinessRuleFieldContainer.MAP, null, null, "DISCOUNT_RANGE"),
    SENIORITY_THRESHOLD(DynamicBusinessRuleFieldType.INTEGER, DynamicBusinessRuleFieldContainer.SINGLE, null, null, "SENIORITY_THRESHOLD");

    private final DynamicBusinessRuleFieldType fieldType;
    private final DynamicBusinessRuleFieldContainer fieldContainer;
    private final String valueSource;
    private final String valueTarget;
    private final String fieldName;

    DynamicBusinessRuleFields(DynamicBusinessRuleFieldType fieldType, DynamicBusinessRuleFieldContainer fieldContainer,
                              String valueSource, String valueTarget, String fieldName) {
        this.fieldType = fieldType;
        this.fieldContainer = fieldContainer;
        this.valueSource = valueSource;
        this.valueTarget = valueTarget;
        this.fieldName = fieldName;
    }

    public DynamicBusinessRuleFieldType getFieldType() {
        return fieldType;
    }

    public DynamicBusinessRuleFieldContainer getFieldContainer() {
        return fieldContainer;
    }

    public String getValueSource() {
        return valueSource;
    }

    public String getValueTarget() {
        return valueTarget;
    }

    public String getFieldName() {
        return fieldName;
    }
}
