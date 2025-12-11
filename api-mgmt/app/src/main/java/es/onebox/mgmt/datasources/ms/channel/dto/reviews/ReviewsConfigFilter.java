package es.onebox.mgmt.datasources.ms.channel.dto.reviews;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;

import java.io.Serial;

public class ReviewsConfigFilter extends BaseRequestFilter {

    @Serial
    private static final long serialVersionUID = -4508993817940184672L;

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
