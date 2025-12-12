package es.onebox.common.datasources.catalog.dto.session.prices;

import java.io.Serial;
import java.io.Serializable;

public class CatalogSurcharge implements Serializable {

    @Serial
    private static final long serialVersionUID = 2435583430471166171L;
    private Double value;
    private SurchargeType type;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public SurchargeType getType() {
        return type;
    }

    public void setType(SurchargeType type) {
        this.type = type;
    }
}
