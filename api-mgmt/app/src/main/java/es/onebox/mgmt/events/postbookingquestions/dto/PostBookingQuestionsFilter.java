package es.onebox.mgmt.events.postbookingquestions.dto;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;

import java.io.Serial;
import java.io.Serializable;

@MaxLimit(1000)
@DefaultLimit(50)
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