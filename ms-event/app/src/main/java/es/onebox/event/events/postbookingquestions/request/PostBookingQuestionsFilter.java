package es.onebox.event.events.postbookingquestions.request;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;

import java.io.Serial;
import java.io.Serializable;

public class PostBookingQuestionsFilter extends BaseRequestFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String q;

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }
}
