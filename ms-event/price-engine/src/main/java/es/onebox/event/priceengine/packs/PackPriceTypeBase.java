package es.onebox.event.priceengine.packs;

import es.onebox.core.serializer.dto.common.IdNameDTO;

public class PackPriceTypeBase extends IdNameDTO {

    private PackPrice price;

    public PackPrice getPrice() {
        return price;
    }

    public void setPrice(PackPrice price) {
        this.price = price;
    }
}
