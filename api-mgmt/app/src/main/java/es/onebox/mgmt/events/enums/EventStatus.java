package es.onebox.mgmt.events.enums;

import java.util.Arrays;
import java.util.List;

public enum EventStatus {

    PLANNED, IN_PROGRAMMING, READY, NOT_ACCOMPLISHED, CANCELLED, FINISHED;

    public static List<EventStatus> actives() {
        return Arrays.asList(PLANNED, IN_PROGRAMMING, READY);
    }

}
