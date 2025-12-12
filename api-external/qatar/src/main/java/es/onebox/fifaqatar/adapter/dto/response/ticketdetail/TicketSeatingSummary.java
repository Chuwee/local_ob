package es.onebox.fifaqatar.adapter.dto.response.ticketdetail;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class TicketSeatingSummary implements Serializable {

    @Serial
    private static final long serialVersionUID = 4773149778065925240L;

    @JsonProperty("title")
    private String title;
    @JsonProperty("html_assignations")
    private String htmlAssignations;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHtmlAssignations() {
        return htmlAssignations;
    }

    public void setHtmlAssignations(String htmlAssignations) {
        this.htmlAssignations = htmlAssignations;
    }
}
