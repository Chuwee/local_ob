package es.onebox.event.common.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;

public class RatesRestrictions extends HashMap<Integer, RateRestrictions> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
