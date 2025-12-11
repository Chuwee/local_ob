package es.onebox.event.datasources.ms.client.dto.conditions;


import java.util.Collection;

public enum ConditionType {

    CAN_BUY("CanBuyCondition", 1, GroupType.CAN_BUY_GROUP, Boolean.class),
    CAN_BOOK("CanBookCondition", 2, GroupType.CAN_BOOK_GROUP, Boolean.class),
    MAX_BOOKED_SEATS_PER_EVENT("MaxBookedSeatsPerEventCondition", 3, GroupType.MAX_SEATS_PER_EVENT_GROUP, Integer.class),
    BOOKING_EXPIRATION_DAYS("BookingExpirationDaysCondition", 4, GroupType.BOOKING_EXPIRATION_DAYS_GROUP, Integer.class),
    CLIENT_DISCOUNT("ClientDiscountCondition", 5, GroupType.CLIENT_DISCOUNT_GROUP, Double.class),
    SHOW_TICKET_PRICE("ShowTicketPriceCondition", 6, GroupType.SHOW_TICKET_PRICE_GROUP, Boolean.class),
    SHOW_TICKET_CLIENT_DISCOUNT("ShowTicketClientDiscountCondition", 7, GroupType.SHOW_TICKET_CLIENT_DISCOUNT_GROUP, Boolean.class),
    CLIENT_COMMISSION("ClientCommissionCondition", 8, GroupType.CLIENT_COMMISSION_GROUP, Double.class),
    PAYMENT_METHODS("PaymentMethodsCondition", 9, GroupType.PAYMENT_METHODS_GROUP, Collection.class),
    CLIENT_DISCOUNT_PERCENTAGE("ClientDiscountPercentageCondition", 10, GroupType.CLIENT_DISCOUNT_GROUP, Double.class),
    CAN_PUBLISH("CanPublishCondition", 11, GroupType.CAN_PUBLISH_GROUP, Boolean.class),
    CAN_INVITE("CanInviteCondition",12, GroupType.CAN_INVITE_GROUP, Boolean.class);

    private String className;
    /**
     * The unique identifier of the condition
     */
    private int type;
    /**
     * The group type identifier of the condition. It is used to group conditions that apply to the same group
     */
    private GroupType groupType;

    private Class<?> valueType;

    ConditionType(String className, int type, GroupType groupType, Class<?> valueType) {
        this.className = className;
        this.type = type;
        this.groupType = groupType;
        this.valueType = valueType;
    }
    public static ConditionType of(int type){
        if (type <= 0 || type > values().length){
            throw new IllegalArgumentException("the type [" + type + "] does not exists");
        }
        return values()[type-1];

    }

    public static GroupType getGroupTypeOf(int type){
        return of(type).getGroupType();
    }

    public String getClassName() {
        return className;
    }

    public int getType() {
        return type;
    }

    public GroupType getGroupType() {
        return groupType;
    }

    public Class<?> getValueType() {
        return valueType;
    }

    /**
     * The enumeration of group of conditions
     */
    public enum GroupType{
        CAN_BUY_GROUP,
        CAN_BOOK_GROUP,
        CAN_PUBLISH_GROUP,
        CAN_INVITE_GROUP,
        MAX_SEATS_PER_EVENT_GROUP,
        BOOKING_EXPIRATION_DAYS_GROUP,
        CLIENT_DISCOUNT_GROUP,
        SHOW_TICKET_PRICE_GROUP,
        SHOW_TICKET_CLIENT_DISCOUNT_GROUP,
        CLIENT_COMMISSION_GROUP,
        PAYMENT_METHODS_GROUP,

    }
}
