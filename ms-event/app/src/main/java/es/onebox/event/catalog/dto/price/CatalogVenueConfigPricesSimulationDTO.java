package es.onebox.event.catalog.dto.price;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.catalog.dto.CatalogTaxInfoDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CatalogVenueConfigPricesSimulationDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1065007986451059183L;

    @JsonProperty("venue_config")
    private IdNameDTO venueConfig;
    private List<CatalogRateDTO> rates;
    private List<CatalogTaxInfoDTO> taxes;
    private List<CatalogTaxInfoDTO> invitationTaxes;
    private List<CatalogTaxInfoDTO> surchargesTaxes;
    private List<CatalogTaxInfoDTO> channelSurchargesTaxes;


    public IdNameDTO getVenueConfig() {
        return venueConfig;
    }

    public void setVenueConfig(IdNameDTO venueConfig) {
        this.venueConfig = venueConfig;
    }

    public List<CatalogRateDTO> getRates() {
        return rates;
    }

    public void setRates(List<CatalogRateDTO> rates) {
        this.rates = rates;
    }

    public List<CatalogTaxInfoDTO> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<CatalogTaxInfoDTO> taxes) { this.taxes = taxes; }

    public List<CatalogTaxInfoDTO> getInvitationTaxes() { return invitationTaxes; }

    public void setInvitationTaxes(List<CatalogTaxInfoDTO> invitationTaxes) { this.invitationTaxes = invitationTaxes; }

    public List<CatalogTaxInfoDTO> getSurchargesTaxes() {
        return surchargesTaxes;
    }

    public void setSurchargesTaxes(List<CatalogTaxInfoDTO> surchargesTaxes) {
        this.surchargesTaxes = surchargesTaxes;
    }

    public List<CatalogTaxInfoDTO> getChannelSurchargesTaxes() {
        return channelSurchargesTaxes;
    }

    public void setChannelSurchargesTaxes(List<CatalogTaxInfoDTO> channelSurchargesTaxes) {
        this.channelSurchargesTaxes = channelSurchargesTaxes;
    }
}
