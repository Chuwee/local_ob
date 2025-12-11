package es.onebox.mgmt.datasources.ms.channel.repositories;

import es.onebox.mgmt.datasources.ms.channel.MsChannelDatasource;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReview;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewConfig;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewConfigResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewConfigUpdate;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewConfigUpdateBulk;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewScope;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewUpdate;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ReviewsConfigFilter;
import jakarta.validation.constraints.Min;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ChannelReviewsRepository {

    private final MsChannelDatasource msChannelDatasource;

    public ChannelReviewsRepository(MsChannelDatasource msChannelDatasource) {
        this.msChannelDatasource = msChannelDatasource;
    }

    public ChannelReview getChannelReview(Integer channelId) {
        return msChannelDatasource.getChannelReview(channelId);
    }

    public void updateChannelReview(Integer channelId, ChannelReviewUpdate channelReviewUpdate) {
        msChannelDatasource.updateChannelReview(channelId, channelReviewUpdate);
    }

    public ChannelReviewConfigResponse getChannelReviewsConfig(Integer channelId, ReviewsConfigFilter filter) {
        return msChannelDatasource.getChannelReviewsConfig(channelId, filter);
    }

    public void updateChannelReviewConfig(Integer channelId, ChannelReviewScope scope, Integer scopeId,
                                          ChannelReviewConfigUpdate request) {
        msChannelDatasource.updateChannelReviewConfig(channelId, scope, scopeId, request);
    }

    public void deleteChannelReviewConfig(Integer channelId, ChannelReviewScope scope, Integer scopeId) {
        msChannelDatasource.deleteChannelReviewConfig(channelId, scope, scopeId);
    }

    public void upsertChannelReviewsConfigBulk(Integer channelId, ChannelReviewScope scope,
                                               ChannelReviewConfigUpdateBulk request) {
        msChannelDatasource.upsertChannelReviewsConfigBulk(channelId, scope, request);
    }
}
