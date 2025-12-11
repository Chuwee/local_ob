package es.onebox.mgmt.datasources.ms.entity.enums;

public enum MemberOrderType {
    RENEWAL, CHANGE_SEAT, BUY_SEAT, NEW_MEMBER, TRANSFER_SEAT, RELEASE_SEAT, RECOVER_SEAT;

    public MemberPeriodType toPeriodType(){
        return switch (this){
            case RENEWAL -> MemberPeriodType.RENEWAL;
            case CHANGE_SEAT -> MemberPeriodType.CHANGE_SEAT;
            case BUY_SEAT -> MemberPeriodType.BUY_SEAT;
            case NEW_MEMBER -> MemberPeriodType.NEW_MEMBER;
            default -> null;
        };
    }
}
