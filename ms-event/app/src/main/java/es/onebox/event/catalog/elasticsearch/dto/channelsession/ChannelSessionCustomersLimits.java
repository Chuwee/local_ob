package es.onebox.event.catalog.elasticsearch.dto.channelsession;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelSessionCustomersLimits implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<ChannelSessionPriceTypeLimit> priceTypeLimits;

    private Integer min;

    private Integer max;

    public List<ChannelSessionPriceTypeLimit> getPriceTypeLimits() {
        return priceTypeLimits;
    }

    public void setPriceTypeLimits(List<ChannelSessionPriceTypeLimit> priceTypeLimits) {
        this.priceTypeLimits = priceTypeLimits;
    }

    public Integer getMin() { return min; }

    public void setMin(Integer min) { this.min = min; }

    public Integer getMax() { return max; }

    public void setMax(Integer max) { this.max = max; }
}
