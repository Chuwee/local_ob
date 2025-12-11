package es.onebox.event.catalog.elasticsearch.context;

import es.onebox.event.attendants.dto.EventAttendantsConfigDTO;
import es.onebox.event.attendants.dto.SessionAttendantsConfigDTO;
import es.onebox.event.priceengine.taxes.domain.ChannelTaxInfo;
import es.onebox.event.packs.dto.RelatedPackDTO;
import es.onebox.event.secondarymarket.dto.SessionSecondaryMarketConfigDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;
import java.util.Map;

public class ChannelSessionForEventIndexation extends ChannelSessionPriceZones {

    @Serial
    private static final long serialVersionUID = 1L;

    private CpanelSesionRecord session;
    private CpanelCanalEventoRecord channelEvent;
    private EventAttendantsConfigDTO eventAttendantsConfig;
    private SessionAttendantsConfigDTO sessionAttendantsConfig;
    private List<Long> products;
    private Map<Long, RelatedPackDTO> relatedPacksByPackId;
    private Boolean presales;
    private SessionSecondaryMarketConfigDTO secondaryMarketConfig;
    private List<ChannelTaxInfo> channelSurchargesTaxes;

    public void setSession(CpanelSesionRecord session) {
        this.session = session;
    }

    public CpanelSesionRecord getSession() {
        return session;
    }

    public void setChannelEvent(CpanelCanalEventoRecord channelEvent) {
        this.channelEvent = channelEvent;
    }

    public CpanelCanalEventoRecord getChannelEvent() {
        return channelEvent;
    }


    public EventAttendantsConfigDTO getEventAttendantsConfig() {
        return eventAttendantsConfig;
    }

    public void setEventAttendantsConfig(EventAttendantsConfigDTO eventAttendantsConfig) {
        this.eventAttendantsConfig = eventAttendantsConfig;
    }

    public SessionAttendantsConfigDTO getSessionAttendantsConfig() {
        return sessionAttendantsConfig;
    }

    public void setSessionAttendantsConfig(SessionAttendantsConfigDTO sessionAttendantsConfig) {
        this.sessionAttendantsConfig = sessionAttendantsConfig;
    }

    public List<Long> getProducts() {
        return products;
    }

    public void setProducts(List<Long> products) {
        this.products = products;
    }

    public Boolean getPresales() {
        return presales;
    }

    public void setPresales(Boolean presales) {
        this.presales = presales;
    }

    public SessionSecondaryMarketConfigDTO getSecondaryMarketConfig() {
        return secondaryMarketConfig;
    }

    public void setSecondaryMarketConfig(SessionSecondaryMarketConfigDTO secondaryMarketConfig) {
        this.secondaryMarketConfig = secondaryMarketConfig;
    }

    public Map<Long, RelatedPackDTO> getRelatedPacksByPackId() {
        return relatedPacksByPackId;
    }

    public void setRelatedPacksByPackId(Map<Long, RelatedPackDTO> relatedPacksByPackId) {
        this.relatedPacksByPackId = relatedPacksByPackId;
    }

    public List<ChannelTaxInfo> getChannelSurchargesTaxes() {
        return channelSurchargesTaxes;
    }

    public void setChannelSurchargesTaxes(List<ChannelTaxInfo> channelSurchargesTaxes) {
        this.channelSurchargesTaxes = channelSurchargesTaxes;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
