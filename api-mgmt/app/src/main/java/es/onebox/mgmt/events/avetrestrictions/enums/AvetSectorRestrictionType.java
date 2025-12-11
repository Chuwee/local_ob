package es.onebox.mgmt.events.avetrestrictions.enums;

import static es.onebox.mgmt.events.avetrestrictions.enums.AvetSectorRestrictionTypeFieldsType.LIMIT_ROLES;
import static es.onebox.mgmt.events.avetrestrictions.enums.AvetSectorRestrictionTypeFieldsType.OBSERVATION_ID;
import static es.onebox.mgmt.events.avetrestrictions.enums.AvetSectorRestrictionTypeFieldsType.OBSERVATION_TEXT;
import static es.onebox.mgmt.events.avetrestrictions.enums.AvetSectorRestrictionTypeFieldsType.ONE_ALL_OF;
import static es.onebox.mgmt.events.avetrestrictions.enums.AvetSectorRestrictionTypeFieldsType.PAID_TERMS;
import static es.onebox.mgmt.events.avetrestrictions.enums.AvetSectorRestrictionTypeFieldsType.PASS_CAPACITY_ID;
import static es.onebox.mgmt.events.avetrestrictions.enums.AvetSectorRestrictionTypeFieldsType.TIME_DIGIT;
import static es.onebox.mgmt.events.avetrestrictions.enums.AvetSectorRestrictionTypeFieldsType.TIME_LAPSE;
import static es.onebox.mgmt.events.avetrestrictions.enums.AvetSectorRestrictionTypeFieldsType.TIME_UNIT;
import static es.onebox.mgmt.events.avetrestrictions.enums.AvetSectorRestrictionTypeFieldsType.UNAVAILABLE_PASS_IN_SECTORS;

public enum AvetSectorRestrictionType {
    ROLE(LIMIT_ROLES),
    PAYMENT(PAID_TERMS, ONE_ALL_OF),
    OBSERVATION(OBSERVATION_ID, OBSERVATION_TEXT),
    SECTOR_PASS(PASS_CAPACITY_ID, UNAVAILABLE_PASS_IN_SECTORS),
    SESSION_START_TIME(TIME_DIGIT, TIME_UNIT, TIME_LAPSE);

    private final AvetSectorRestrictionTypeFieldsType[] fields;

    AvetSectorRestrictionType(AvetSectorRestrictionTypeFieldsType... fields) {
        this.fields = fields;
    }

    public AvetSectorRestrictionTypeFieldsType[] getFields() {
        return fields;
    }
}
