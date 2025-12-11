package es.onebox.event.common.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;

public class PriceZonesRestrictions extends HashMap<Integer, PriceZoneRestriction> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
