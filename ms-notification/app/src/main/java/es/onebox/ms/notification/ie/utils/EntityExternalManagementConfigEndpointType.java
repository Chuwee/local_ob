package es.onebox.ms.notification.ie.utils;

/**
 * Created by joandf on 17/03/2015.
 */
public enum EntityExternalManagementConfigEndpointType {

    IS_ENABLED(1),
    BLOCK_SESSIONS(2),
    NEW_SESSION(3),
    REMOVE_ACTIVITY(4),
    REMOVE_SESSIONS(5),
    UNBLOCK_SESSIONS(6),
    EXTERNAL_URL(7),
    SESSION_MODIFIED(8),
    OPERATION_CANCELLED(9);

    private int id;

    EntityExternalManagementConfigEndpointType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

}
