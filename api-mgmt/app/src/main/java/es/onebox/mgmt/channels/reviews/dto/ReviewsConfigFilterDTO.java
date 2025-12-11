package es.onebox.mgmt.channels.reviews.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewScope;

import java.io.Serial;

@MaxLimit(100)
@DefaultLimit(10)
public class ReviewsConfigFilterDTO extends BaseRequestFilter {

    @Serial
    private static final long serialVersionUID = -4508993817940184672L;

    @JsonProperty("q")
    private String q;
    private ChannelReviewScope scope;

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public ChannelReviewScope getScope() {
        return scope;
    }

    public void setScope(ChannelReviewScope scope) {
        this.scope = scope;
    }
}
