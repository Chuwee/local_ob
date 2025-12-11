package es.onebox.event.catalog.dto.venue.container.tier;

import es.onebox.event.catalog.dto.venue.container.VenueCommElement;

import java.io.Serial;
import java.io.Serializable;

public class VenueTierCommElement extends VenueCommElement implements Serializable {

    @Serial
    private static final long serialVersionUID = 4760653553573312465L;

    public VenueTierCommElement() {
        super();
    }

    public VenueTierCommElement(String type, String lang, String value) {
        super(type, lang, value);
    }

}
