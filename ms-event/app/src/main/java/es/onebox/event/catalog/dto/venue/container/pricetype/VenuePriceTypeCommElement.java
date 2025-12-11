package es.onebox.event.catalog.dto.venue.container.pricetype;

import es.onebox.event.catalog.dto.venue.container.VenueCommElement;

import java.io.Serial;
import java.io.Serializable;

public class VenuePriceTypeCommElement extends VenueCommElement implements Serializable {

    @Serial
    private static final long serialVersionUID = 4760653553573312465L;

    public VenuePriceTypeCommElement() {
        super();
    }

    public VenuePriceTypeCommElement(String type, String lang, String value) {
        super(type, lang, value);
    }

}
