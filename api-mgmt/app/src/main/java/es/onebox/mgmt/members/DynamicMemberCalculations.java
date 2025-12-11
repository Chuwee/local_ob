package es.onebox.mgmt.members;

import static es.onebox.mgmt.members.DynamicBusinessRuleFields.ALLOWED_CAPACITIES;
import static es.onebox.mgmt.members.DynamicBusinessRuleFields.EXCLUDED_PERIODICITIES;
import static es.onebox.mgmt.members.DynamicBusinessRuleFields.NUMBER_OF_TERMS;
import static es.onebox.mgmt.members.DynamicBusinessRuleFields.PERIODICITY_TERM_RELATIONS;
import static es.onebox.mgmt.members.MemberOrderType.BUY_SEAT;
import static es.onebox.mgmt.members.MemberOrderType.CHANGE_SEAT;

public enum DynamicMemberCalculations implements DynamicBusinessRuleConfigurable {
    NEW_SEAT_CALCULATOR(BUY_SEAT, "NEW_SEAT_PRICE", "newPriceCalculatorData","NewSeatPeriodicityTermPriceValladolidCalculator", PERIODICITY_TERM_RELATIONS),
    NEW_SEAT_PERIODICITY_TERMS_CALCULATOR(BUY_SEAT,"NEW_SEAT_PRICE", "newPriceCalculatorData","NewSeatPeriodicityTermPriceCalculator", PERIODICITY_TERM_RELATIONS),
    NEW_SEAT_PERIODICITY_CALCULATOR(BUY_SEAT, "NEW_SEAT_PRICE", "newPriceCalculatorData", "NewSeatPeriodicityPriceCalculator", NUMBER_OF_TERMS, PERIODICITY_TERM_RELATIONS),
    PREVIOUS_SEAT_CALCULATOR(CHANGE_SEAT, "PREVIOUS_SEAT_PRICE", "previousPriceCalculatorData", "PreviousSeatCapacityPeriodicityPriceCalculator", ALLOWED_CAPACITIES, EXCLUDED_PERIODICITIES);

    private MemberOrderType orderType;
    private String operationName;
    private String id;
    private String javaClass;
    private DynamicBusinessRuleFields[] fields;

    DynamicMemberCalculations(MemberOrderType orderType, String operationName, String id, String javaClass, DynamicBusinessRuleFields... fields) {
        this.orderType = orderType;
        this.operationName = operationName;
        this.id = id;
        this.javaClass = javaClass;
        this.fields = fields;
    }

    public MemberOrderType getOrderType() {
        return orderType;
    }

    public String getOperationName() {
        return operationName;
    }

    public String getId() {
        return id;
    }

    public String getJavaClass() {
        return javaClass;
    }

    public DynamicBusinessRuleFields[] getFields() {
        return fields;
    }

}
