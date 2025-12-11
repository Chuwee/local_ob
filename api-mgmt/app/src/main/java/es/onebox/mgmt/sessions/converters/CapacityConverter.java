package es.onebox.mgmt.sessions.converters;

import es.onebox.venue.venuetemplates.VenueMapProto;

import java.io.IOException;
import java.io.InputStream;

public class CapacityConverter {

    public CapacityConverter() {
    }

    public static VenueMapProto.VenueMap fromMsToProto(InputStream proto) throws IOException {
        if (proto == null) {
            return null;
        }
        return VenueMapProto.VenueMap.parseFrom(proto);
    }

}
