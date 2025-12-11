package es.onebox.mgmt.datasources.integration.avetconfig.dto;

import java.io.Serial;
import java.io.Serializable;

public class MatchPriceDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4L;

    private final Integer priceId;
    private final Integer matchPriceId;
    private final Float price;

    private MatchPriceDTO() {
        this(null, null, null);
    }

    public MatchPriceDTO(Integer priceId, Integer matchPriceId, Float price) {
        this.priceId = priceId;
        this.matchPriceId = matchPriceId;
        this.price = price;
    }

    public Integer getPriceId() {
        return priceId;
    }

    public Integer getMatchPriceId() {
        return matchPriceId;
    }

    public Float getPrice() {
        return price;
    }
}
