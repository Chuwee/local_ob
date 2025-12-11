package es.onebox.event.events.domain.eventconfig;

import es.onebox.event.events.domain.TicketTemplateExtraPageType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public class TicketTemplateSettings implements Serializable {
    @Serial
    private static final long serialVersionUID = 5373895068385894020L;

    private Set<TicketTemplateExtraPageType> extraPages;

    public Set<TicketTemplateExtraPageType> getExtraPages() {
        return extraPages;
    }

    public void setExtraPages(Set<TicketTemplateExtraPageType> extraPages) {
        this.extraPages = extraPages;
    }
}
