package es.onebox.mgmt.channels.enums;

public enum ChannelSendEmailMode {
    TICKET_AND_RECEIPT(1),
    ONLY_TICKET(2),
    ONLY_RECEIPT(3),
    NONE(4),
    UNIFIED_TICKET(5),
    RECEIPT_AND_PASSBOOK(6);

    private int emailsToBeSend;

    private ChannelSendEmailMode(int emailsToBeSend) {
        this.emailsToBeSend = emailsToBeSend;
    }

    public int getValue() {
        return this.emailsToBeSend;
    }

    public static ChannelSendEmailMode get(int value){
        for (ChannelSendEmailMode sem : ChannelSendEmailMode.values()) {
            if(sem.getValue() == value){
                return sem;
            }
        }
        return null;
    }

}
