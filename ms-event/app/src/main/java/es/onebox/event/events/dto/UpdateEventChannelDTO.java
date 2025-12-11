package es.onebox.event.events.dto;

import java.io.Serializable;
import java.util.List;

public class UpdateEventChannelDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private UpdateEventChannelSettingsDTO settings;
    private Boolean useAllSaleGroups;
    private List<Long> saleGroups;
    private EventTicketTemplatesDTO ticketTemplates;

    public Boolean getUseAllSaleGroups() {
        return useAllSaleGroups;
    }

    public void setUseAllSaleGroups(Boolean useAllSaleGroups) {
        this.useAllSaleGroups = useAllSaleGroups;
    }

    public UpdateEventChannelSettingsDTO getSettings() {
        return settings;
    }

    public void setSettings(UpdateEventChannelSettingsDTO settings) {
        this.settings = settings;
    }

    public List<Long> getSaleGroups() {
        return saleGroups;
    }

    public void setSaleGroups(List<Long> saleGroups) {
        this.saleGroups = saleGroups;
    }

    public EventTicketTemplatesDTO getTicketTemplates() {
        return ticketTemplates;
    }

    public void setTicketTemplates(EventTicketTemplatesDTO ticketTemplates) {
        this.ticketTemplates = ticketTemplates;
    }
}
