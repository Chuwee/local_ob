package es.onebox.mgmt.members;


import static es.onebox.mgmt.members.DynamicMemberCalculations.NEW_SEAT_CALCULATOR;
import static es.onebox.mgmt.members.DynamicMemberCalculations.NEW_SEAT_PERIODICITY_CALCULATOR;
import static es.onebox.mgmt.members.DynamicMemberCalculations.NEW_SEAT_PERIODICITY_TERMS_CALCULATOR;
import static es.onebox.mgmt.members.DynamicMemberCalculations.PREVIOUS_SEAT_CALCULATOR;
import static es.onebox.mgmt.members.DynamicMemberDiscountsCalculators.PARTNER_SENIORITY_CALCULATOR;
import static es.onebox.mgmt.members.DynamicMemberInferer.PARTNER_ROLES_AGE_INFERER;
import static es.onebox.mgmt.members.DynamicMemberInferer.PARTNER_ROLES_INFERER;
import static es.onebox.mgmt.members.DynamicMemberInferer.SUBSCRIPTION_MODE_PERIODICITY_INFERER;
import static es.onebox.mgmt.members.DynamicMemberValidations.CAPACITY_PERIODICITY_VALIDATOR;
import static es.onebox.mgmt.members.DynamicMemberValidations.PAYMENTS_PERMISSIONS_VALIDATOR;
import static es.onebox.mgmt.members.DynamicMemberValidations.PERMISSION_VALIDATOR;
import static es.onebox.mgmt.members.DynamicMemberValidations.ROLE_TERMS_VALIDATION;

public enum DynamicBusinessRuleTypes {

    VALIDATION(CAPACITY_PERIODICITY_VALIDATOR, ROLE_TERMS_VALIDATION, PERMISSION_VALIDATOR, PAYMENTS_PERMISSIONS_VALIDATOR),
    INFERER(SUBSCRIPTION_MODE_PERIODICITY_INFERER, PARTNER_ROLES_INFERER, PARTNER_ROLES_AGE_INFERER),
    PRICE_CALCULATOR(NEW_SEAT_CALCULATOR, NEW_SEAT_PERIODICITY_TERMS_CALCULATOR, NEW_SEAT_PERIODICITY_CALCULATOR, PREVIOUS_SEAT_CALCULATOR),
    DISCOUNT_CALCULATOR(PARTNER_SENIORITY_CALCULATOR);

    private final Enum[] availableRules;

    DynamicBusinessRuleTypes(Enum... availableRules) {
        this.availableRules = availableRules;
    }

    public Enum[] getAvailableRules() {
        return availableRules;
    }
}
