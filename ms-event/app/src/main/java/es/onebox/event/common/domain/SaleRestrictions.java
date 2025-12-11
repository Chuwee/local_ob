package es.onebox.event.common.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SaleRestrictions implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer minItems;
    private List<Integer> minItemsRequiredZones;
    private String channel;

    public Integer getMinItems() {
        return minItems;
    }

    public void setMinItems(Integer minItems) {
        this.minItems = minItems;
    }

    public List<Integer> getMinItemsRequiredZones() {
        return minItemsRequiredZones;
    }

    public void setMinItemsRequiredZones(List<Integer> minItemsRequiredZones) {
        this.minItemsRequiredZones = minItemsRequiredZones;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
