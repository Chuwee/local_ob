package es.onebox.mgmt.datasources.ms.event.dto.event;

import java.io.Serializable;
import java.util.List;

public class UpdateEventChannel implements Serializable {

    private static final long serialVersionUID = 1L;

    private UpdateEventChannelSettings settings;
    private Boolean useAllSaleGroups;
    private List<Long> saleGroups;
    private EventTicketTemplates ticketTemplates;

    public Boolean getUseAllSaleGroups() {
        return useAllSaleGroups;
    }

    public void setUseAllSaleGroups(Boolean useAllSaleGroups) {
        this.useAllSaleGroups = useAllSaleGroups;
    }

    public UpdateEventChannelSettings getSettings() {
        return settings;
    }

    public void setSettings(UpdateEventChannelSettings settings) {
        this.settings = settings;
    }

    public List<Long> getSaleGroups() {
        return saleGroups;
    }

    public void setSaleGroups(List<Long> saleGroups) {
        this.saleGroups = saleGroups;
    }

    public void setTicketTemplates(EventTicketTemplates ticketTemplates) {
        this.ticketTemplates = ticketTemplates;
    }

    public EventTicketTemplates getTicketTemplates() {
        return ticketTemplates;
    }
}
