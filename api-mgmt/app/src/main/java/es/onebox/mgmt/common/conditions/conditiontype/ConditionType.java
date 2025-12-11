package es.onebox.mgmt.common.conditions.conditiontype;

public enum ConditionType {
    CAN_BUY("CanBuyCondition", 1),
    CAN_BOOK("CanBookCondition", 2),
    MAX_SEATS_PER_EVENT("MaxBookedSeatsPerEventCondition", 3),
    BOOKING_EXPIRATION_DAYS("BookingExpirationDaysCondition", 4),
    CLIENT_DISCOUNT("ClientDiscountCondition", 5),
    SHOW_TICKET_PRICE("ShowTicketPriceCondition", 6),
    SHOW_TICKET_CLIENT_DISCOUNT("ShowTicketClientDiscountCondition", 7),
    CLIENT_COMMISSION("ClientCommissionCondition", 8),
    PAYMENT_METHODS("PaymentMethodsCondition", 9),
    CLIENT_DISCOUNT_PERCENTAGE("ClientDiscountPercentageCondition", 10),
    CAN_PUBLISH("CanPublishCondition", 11),
    CAN_INVITE("CanInviteCondition", 12);

    private final String className;
    private final int type;

    ConditionType(String className, int type) {
        this.className = className;
        this.type = type;
    }

    public static ConditionType of(int type) {
        if (type > 0 && type <= values().length) {
            return values()[type - 1];
        } else {
            throw new IllegalArgumentException("the type [" + type + "] does not exists");
        }
    }

    public String getClassName() {
        return this.className;
    }

    public int getType() {
        return this.type;
    }
}
