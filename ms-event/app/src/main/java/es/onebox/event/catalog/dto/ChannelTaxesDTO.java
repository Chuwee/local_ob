package es.onebox.event.catalog.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelTaxesDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<CatalogTaxInfoDTO> surcharges;

    public List<CatalogTaxInfoDTO> getSurcharges() {
        return surcharges;
    }

    public void setSurcharges(List<CatalogTaxInfoDTO> surcharges) {
        this.surcharges = surcharges;
    }
}
