package es.onebox.event.catalog.dao.couch;

import es.onebox.couchbase.annotations.Id;

import java.io.Serializable;
import java.util.List;

public class ChannelSessionPricesDocument implements Serializable {

    @Id(index = 1)
    private Long channelId;
    @Id(index = 2)
    private Long sessionId;
    private CatalogVenueConfigPricesSimulation simulation;
    private List<CatalogSessionTaxInfo> taxes;
    private List<CatalogSessionTaxInfo> invitationTaxes;
    private List<CatalogSessionTaxInfo> surchargesTaxes;
    private List<CatalogSessionTaxInfo> channelSurchargesTaxes;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public CatalogVenueConfigPricesSimulation getSimulation() {
        return simulation;
    }

    public void setSimulation(CatalogVenueConfigPricesSimulation simulation) {
        this.simulation = simulation;
    }

    public List<CatalogSessionTaxInfo> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<CatalogSessionTaxInfo> taxes) {
        this.taxes = taxes;
    }

    public List<CatalogSessionTaxInfo> getInvitationTaxes() { return invitationTaxes; }

    public void setInvitationTaxes(List<CatalogSessionTaxInfo> invitationTaxes) { this.invitationTaxes = invitationTaxes; }

    public List<CatalogSessionTaxInfo> getSurchargesTaxes() {
        return surchargesTaxes;
    }

    public void setSurchargesTaxes(List<CatalogSessionTaxInfo> surchargesTaxes) {
        this.surchargesTaxes = surchargesTaxes;
    }

    public List<CatalogSessionTaxInfo> getChannelSurchargesTaxes() {
        return channelSurchargesTaxes;
    }

    public void setChannelSurchargesTaxes(List<CatalogSessionTaxInfo> channelSurchargesTaxes) {
        this.channelSurchargesTaxes = channelSurchargesTaxes;
    }
}
