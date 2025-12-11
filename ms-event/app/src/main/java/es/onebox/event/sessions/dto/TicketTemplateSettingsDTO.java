package es.onebox.event.sessions.dto;


import es.onebox.event.sessions.enums.TicketTemplateExtraPageType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public class TicketTemplateSettingsDTO implements Serializable {
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
