package es.onebox.event.events.customertypes.dao;

public enum AssignationTrigger {
    PURCHASE(3),
    REGISTRATION(2),
    LOGIN(1);

    private final int type;

    AssignationTrigger(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static AssignationTrigger fromValue(int value) {
        for (AssignationTrigger trigger : AssignationTrigger.values()) {
            if (trigger.getType() == value) {
                return trigger;
            }
        }
        return null;
    }
}
