package es.onebox.event.catalog.elasticsearch.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class RateChannelRestriction implements Serializable {

    @Serial
    private static final long serialVersionUID = 253609915189981338L;

    private List<Integer> restrictedChannels;

    public List<Integer> getRestrictedChannels() {
        return restrictedChannels;
    }

    public void setRestrictedChannels(List<Integer> restrictedChannels) {
        this.restrictedChannels = restrictedChannels;
    }
}
