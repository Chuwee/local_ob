package es.onebox.event.priceengine.taxes.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SessionTaxes implements Serializable {
    @Serial
    private static final long serialVersionUID = -8416565024721193157L;

    private List<SessionTaxInfo> pricesTaxes;
    private List<SessionTaxInfo> invitationTaxes;
    private List<SessionTaxInfo> surchargesTaxes;
    private List<ChannelTaxInfo> channelSurchargesTaxes;

    public List<SessionTaxInfo> getPricesTaxes() {
        return pricesTaxes;
    }

    public void setPricesTaxes(List<SessionTaxInfo> pricesTaxes) {
        this.pricesTaxes = pricesTaxes;
    }

    public List<SessionTaxInfo> getInvitationTaxes() { return invitationTaxes; }

    public void setInvitationTaxes(List<SessionTaxInfo> invitationTaxes) { this.invitationTaxes = invitationTaxes; }

    public List<SessionTaxInfo> getSurchargesTaxes() {
        return surchargesTaxes;
    }

    public void setSurchargesTaxes(List<SessionTaxInfo> surchargesTaxes) {
        this.surchargesTaxes = surchargesTaxes;
    }

    public List<ChannelTaxInfo> getChannelSurchargesTaxes() {
        return channelSurchargesTaxes;
    }

    public void setChannelSurchargesTaxes(List<ChannelTaxInfo> channelSurchargesTaxes) {
        this.channelSurchargesTaxes = channelSurchargesTaxes;
    }
}
