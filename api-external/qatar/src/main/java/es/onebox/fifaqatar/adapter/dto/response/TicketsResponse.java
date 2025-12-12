package es.onebox.fifaqatar.adapter.dto.response;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class TicketsResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 5307840705816171778L;

    private Integer count;
    private String next;
    private String previous;
    private List<TicketResponse> results;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public List<TicketResponse> getResults() {
        return results;
    }

    public void setResults(List<TicketResponse> results) {
        this.results = results;
    }
}
