package es.onebox.mgmt.channels.reviews;

import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.reviews.converter.ChannelReviewsConverter;
import es.onebox.mgmt.channels.reviews.converter.ChannelReviewsFilterConverter;
import es.onebox.mgmt.channels.reviews.dto.ChannelReviewConfigResponseDTO;
import es.onebox.mgmt.channels.reviews.dto.ChannelReviewConfigUpdateBulkDTO;
import es.onebox.mgmt.channels.reviews.dto.ChannelReviewConfigUpdateDTO;
import es.onebox.mgmt.channels.reviews.dto.ChannelReviewDTO;
import es.onebox.mgmt.channels.reviews.dto.ChannelReviewUpdateDTO;
import es.onebox.mgmt.channels.reviews.dto.ReviewsConfigFilterDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReview;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewConfigResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewConfigUpdate;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewConfigUpdateBulk;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewScope;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewUpdate;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ReviewsConfigFilter;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelReviewsRepository;
import org.springframework.stereotype.Service;

@Service
public class ChannelReviewsService {

    private final ChannelReviewsRepository channelReviewsRepository;
    private final ChannelsHelper channelsHelper;


    public ChannelReviewsService(ChannelReviewsRepository channelReviewsRepository, ChannelsHelper channelsHelper) {
        this.channelReviewsRepository = channelReviewsRepository;
        this.channelsHelper = channelsHelper;
    }

    public ChannelReviewDTO getChannelReview(Integer channelId) {
        this.channelsHelper.getAndCheckChannel(channelId.longValue());
        ChannelReview channelReview = channelReviewsRepository.getChannelReview(channelId);
        return ChannelReviewsConverter.toDTO(channelReview);
    }

    public void updateChannelReview(Integer channelId, ChannelReviewUpdateDTO request) {
        this.channelsHelper.getAndCheckChannel(channelId.longValue());
        ChannelReviewUpdate channelReviewUpdate = ChannelReviewsConverter.toEntity(request);
        channelReviewsRepository.updateChannelReview(channelId, channelReviewUpdate);
    }

    public ChannelReviewConfigResponseDTO getChannelReviewsConfig(Integer channelId, ReviewsConfigFilterDTO filter) {
        this.channelsHelper.getAndCheckChannel(channelId.longValue());
        ReviewsConfigFilter filterOut = ChannelReviewsFilterConverter.toMs(filter);
        ChannelReviewConfigResponse channelReviewsConfig = channelReviewsRepository.getChannelReviewsConfig(channelId, filterOut);
        return ChannelReviewsConverter.toDTO(channelReviewsConfig);
    }

    public void updateChannelReviewConfig(Integer channelId, ChannelReviewScope scope, Integer scopeId,
                                          ChannelReviewConfigUpdateDTO request) {
        this.channelsHelper.getAndCheckChannel(channelId.longValue());
        ChannelReviewConfigUpdate reviewConfigUpdate = ChannelReviewsConverter.toEntity(request);
        channelReviewsRepository.updateChannelReviewConfig(channelId, scope, scopeId, reviewConfigUpdate);
    }

    public void deleteChannelReviewConfig(Integer channelId, ChannelReviewScope scope, Integer scopeId) {
        this.channelsHelper.getAndCheckChannel(channelId.longValue());
        channelReviewsRepository.deleteChannelReviewConfig(channelId, scope, scopeId);
    }

    public void upsertChannelReviewsConfigBulk(Integer channelId, ChannelReviewScope scope, ChannelReviewConfigUpdateBulkDTO request) {
        this.channelsHelper.getAndCheckChannel(channelId.longValue());
        ChannelReviewConfigUpdateBulk reviewConfigUpdate = ChannelReviewsConverter.toEntity(request);
        channelReviewsRepository.upsertChannelReviewsConfigBulk(channelId, scope, reviewConfigUpdate);
    }
}
