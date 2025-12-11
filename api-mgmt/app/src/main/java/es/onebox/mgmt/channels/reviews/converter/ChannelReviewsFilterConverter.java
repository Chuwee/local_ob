package es.onebox.mgmt.channels.reviews.converter;

import es.onebox.mgmt.channels.reviews.dto.ReviewsConfigFilterDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ReviewsConfigFilter;

public class ChannelReviewsFilterConverter {

    private ChannelReviewsFilterConverter() {
    }

    public static ReviewsConfigFilter toMs(ReviewsConfigFilterDTO in) {
        ReviewsConfigFilter out = new ReviewsConfigFilter();
        out.setQ(in.getQ());
        out.setScope(in.getScope());
        out.setLimit(in.getLimit());
        out.setOffset(in.getOffset());
        return out;
    }
}
