package es.onebox.mgmt.members;


import static es.onebox.mgmt.members.MembersRestrictionTypesFields.LIMIT_ROLES;
import static es.onebox.mgmt.members.MembersRestrictionTypesFields.LIMIT_TICKETS_NUMBER;
import static es.onebox.mgmt.members.MembersRestrictionTypesFields.MINIMUM_MAXIMUM;
import static es.onebox.mgmt.members.MembersRestrictionTypesFields.RATIO_ALLOWED_ROLES;
import static es.onebox.mgmt.members.MembersRestrictionTypesFields.RATIO_ALLOWED_TICKETS_NUMBER;
import static es.onebox.mgmt.members.MembersRestrictionTypesFields.RATIO_CONDITION_ROLES;
import static es.onebox.mgmt.members.MembersRestrictionTypesFields.RATIO_CONDITION_TICKETS_NUMBER;

public enum RestrictionTypes {

    LIMIT_TYPE(MINIMUM_MAXIMUM, LIMIT_TICKETS_NUMBER, LIMIT_ROLES),

    RATIO_TYPE(RATIO_CONDITION_TICKETS_NUMBER, RATIO_ALLOWED_TICKETS_NUMBER, RATIO_ALLOWED_ROLES, RATIO_CONDITION_ROLES);

    private MembersRestrictionTypesFields[] fields;

    RestrictionTypes(MembersRestrictionTypesFields... fields) {
        this.fields = fields;
    }

    public MembersRestrictionTypesFields[] getFields() {
        return fields;
    }
}
