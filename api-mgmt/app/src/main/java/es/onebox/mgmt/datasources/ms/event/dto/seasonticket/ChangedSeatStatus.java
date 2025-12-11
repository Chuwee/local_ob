package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;


import java.io.Serializable;

public enum ChangedSeatStatus implements Serializable {

    FREE,
    PROMOTOR_LOCKED,
    KILL;

}