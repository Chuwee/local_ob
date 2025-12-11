package es.onebox.mgmt.entities.externalconfiguration.converter;

import es.onebox.mgmt.datasources.integration.dispatcher.enums.Status;

import static es.onebox.mgmt.entities.externalconfiguration.enums.Status.CONNECTED;
import static es.onebox.mgmt.entities.externalconfiguration.enums.Status.DISABLED;
import static es.onebox.mgmt.entities.externalconfiguration.enums.Status.ERROR;
import static es.onebox.mgmt.entities.externalconfiguration.enums.Status.NOT_CONNECTED;
import static es.onebox.mgmt.entities.externalconfiguration.enums.Status.REFRESHING;


public class StatusConverter {

    private StatusConverter(){
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static es.onebox.mgmt.entities.externalconfiguration.enums.Status toDto(Status status) {
        if (status == null) {
            return null;
        }
        return switch (status) {
            case CONNECTED -> CONNECTED;
            case NOT_CONNECTED -> NOT_CONNECTED;
            case DISABLED -> DISABLED;
            case REFRESHING -> REFRESHING;
            case ERROR -> ERROR;
        };
    }
}
