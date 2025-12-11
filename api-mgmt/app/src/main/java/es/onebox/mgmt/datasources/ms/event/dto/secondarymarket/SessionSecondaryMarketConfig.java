package es.onebox.mgmt.datasources.ms.event.dto.secondarymarket;


import es.onebox.mgmt.secondarymarket.dto.SecondaryMarketType;

import java.io.Serializable;

public class SessionSecondaryMarketConfig extends SecondaryMarketConfig implements Serializable {

    private SecondaryMarketType type;

    public SecondaryMarketType getType() {
        return type;
    }

    public void setType(SecondaryMarketType type) {
        this.type = type;
    }
}
