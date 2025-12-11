package es.onebox.mgmt.channels.reviews.converter;

import es.onebox.mgmt.channels.reviews.dto.ChannelReviewConfigDTO;
import es.onebox.mgmt.channels.reviews.dto.ChannelReviewConfigDetailsDTO;
import es.onebox.mgmt.channels.reviews.dto.ChannelReviewConfigDetailsEventDTO;
import es.onebox.mgmt.channels.reviews.dto.ChannelReviewConfigDetailsSessionDTO;
import es.onebox.mgmt.channels.reviews.dto.ChannelReviewConfigResponseDTO;
import es.onebox.mgmt.channels.reviews.dto.ChannelReviewConfigUpdateBulkDTO;
import es.onebox.mgmt.channels.reviews.dto.ChannelReviewConfigUpdateDTO;
import es.onebox.mgmt.channels.reviews.dto.ChannelReviewDTO;
import es.onebox.mgmt.channels.reviews.dto.ChannelReviewUpdateDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReview;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewConfig;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewConfigResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewConfigUpdate;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewConfigUpdateBulk;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewUpdate;

import java.util.ArrayList;
import java.util.List;

public class ChannelReviewsConverter {

    private ChannelReviewsConverter(){
        throw new UnsupportedOperationException("Cannot instantiate utilities classs");
    }


    public static ChannelReviewDTO toDTO(ChannelReview channelReview) {
        ChannelReviewDTO result = new ChannelReviewDTO();

        result.setEnable(channelReview.getEnable());
        result.setSendCriteria(channelReview.getSendCriteria());
        result.setSendTimeUnit(channelReview.getSendTimeUnit());
        result.setSendTimeValue(channelReview.getSendTimeValue());

        return result;
    }

    public static ChannelReviewUpdate toEntity(ChannelReviewUpdateDTO request) {
        ChannelReviewUpdate result = new ChannelReviewUpdate();

        result.setEnable(request.getEnable());
        result.setSendCriteria(request.getSendCriteria());
        result.setSendTimeUnit(request.getSendTimeUnit());
        result.setSendTimeValue(request.getSendTimeValue());

        return result;
    }

    public static ChannelReviewConfigResponseDTO toDTO(ChannelReviewConfigResponse in) {
        ChannelReviewConfigResponseDTO result = new ChannelReviewConfigResponseDTO();
        result.setMetadata(in.getMetadata());
        List<ChannelReviewConfigDTO> configs = new ArrayList<>();
        in.getData().forEach(config -> configs.add(ChannelReviewsConverter.toDTO(config)));
        result.setData(configs);
        return result;
    }

    private static ChannelReviewConfigDTO toDTO(ChannelReviewConfig config) {
        ChannelReviewConfigDTO result = new ChannelReviewConfigDTO();

        result.setScope(config.getScope());
        result.setScopeId(config.getScopeId());
        result.setSendCriteria(config.getSendCriteria());

        if (config.getDetails() != null) {
            ChannelReviewConfigDetailsDTO details = new ChannelReviewConfigDetailsDTO();
            ChannelReviewConfigDetailsEventDTO eventDetails = new ChannelReviewConfigDetailsEventDTO();
            eventDetails.setName(config.getDetails().getEventName());
            eventDetails.setId(config.getDetails().getEventId());
            details.setEvent(eventDetails);

            if (config.getDetails().getSessionName() != null) {
                ChannelReviewConfigDetailsSessionDTO sessionDetails = new ChannelReviewConfigDetailsSessionDTO();
                sessionDetails.setName(config.getDetails().getSessionName());
                sessionDetails.setStartDate(config.getDetails().getSessionStartDate());
                sessionDetails.setId(config.getDetails().getSessionId());
                details.setSession(sessionDetails);
            }
            result.setDetails(details);
        }

        return result;
    }

    public static ChannelReviewConfigUpdate toEntity(ChannelReviewConfigUpdateDTO request) {
        ChannelReviewConfigUpdate result = new ChannelReviewConfigUpdate();
        result.setSendCriteria(request.getSendCriteria());
        return result;
    }

    public static ChannelReviewConfigUpdateBulk toEntity(ChannelReviewConfigUpdateBulkDTO request) {
        ChannelReviewConfigUpdateBulk result = new ChannelReviewConfigUpdateBulk();
        result.setSendCriteria(request.getSendCriteria());
        result.setScopeIds(request.getScopeIds());
        return result;
    }
}
