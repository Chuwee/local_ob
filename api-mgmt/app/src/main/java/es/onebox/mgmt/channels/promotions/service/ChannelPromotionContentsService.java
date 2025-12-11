package es.onebox.mgmt.channels.promotions.service;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.channelcontents.ChannelContentConverter;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.common.channelcontents.PromotionChannelContentTextType;
import es.onebox.mgmt.common.promotions.dto.PromotionChannelContentTextFilter;
import es.onebox.mgmt.common.promotions.dto.PromotionChannelContentTextListDTO;
import es.onebox.mgmt.datasources.common.dto.BaseCommunicationElement;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.promotion.enums.PromotionTagType;
import es.onebox.mgmt.datasources.ms.promotion.repository.ChannelPromotionsRepository;
import es.onebox.mgmt.exception.ApiMgmtPromotionErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class ChannelPromotionContentsService {

	private static final int NAME_MAX_SIZE = 40;
    private static final int DESCRIPTION_MAX_SIZE = 1000;
    private final MasterdataService masterdataService;
	private final ChannelsHelper channelsHelper;
	private final ChannelPromotionsRepository channelPromotionsRepository;

	@Autowired
	public ChannelPromotionContentsService(MasterdataService masterdataService, ChannelsHelper channelsHelper,
										   ChannelPromotionsRepository channelPromotionsRepository) {
		this.masterdataService = masterdataService;
		this.channelsHelper = channelsHelper;
		this.channelPromotionsRepository = channelPromotionsRepository;
	}

	public ChannelContentTextListDTO<PromotionChannelContentTextType> getChannelPromotionContentTexts(Long channelId,
																									  Long promotionId, PromotionChannelContentTextFilter filter) {
		channelsHelper.getAndCheckChannel(channelId);

		CommunicationElementFilter<PromotionTagType> communicationElementFilter = ChannelContentConverter
				.fromPromotionFilter(filter, masterdataService);

		List<BaseCommunicationElement> comElements = channelPromotionsRepository.getChannelContentTexts(channelId,
				promotionId, communicationElementFilter);

		comElements.sort(Comparator.comparing(BaseCommunicationElement::getLanguage));

		return ChannelContentConverter.fromMsPromotionText(comElements);
	}

	public void updateChannelPromotionContentTexts(Long channelId, Long promotionId,
												   PromotionChannelContentTextListDTO content) {
		ChannelResponse channelResponse = channelsHelper.getAndCheckChannel(channelId);
		Map<Long, String> languagesByIds = masterdataService.getLanguagesByIds();
		for (ChannelContentTextDTO<PromotionChannelContentTextType> element : content.getTexts()) {
            PromotionChannelContentTextType type = element.getType();
            String value = element.getValue();
            validateLength(type, value);
			element.setLanguage(
					ChannelContentsUtils.checkElementLanguageForChannel(channelResponse, languagesByIds, element.getLanguage()));
		}
		channelPromotionsRepository.updateChannelPromotionContentTexts(channelId, promotionId,
				ChannelContentConverter.toMsPromotionText(content.getTexts()));
	}

    private static void validateLength(PromotionChannelContentTextType type, String value) {
        if (PromotionChannelContentTextType.DESCRIPTION.equals(type) && value.length() > DESCRIPTION_MAX_SIZE) {
            throw ExceptionBuilder.build(ApiMgmtPromotionErrorCode.BAD_REQUEST_PARAMETER,
                    "Description must have between 1 and 1000 characters");
        } else if (PromotionChannelContentTextType.NAME.equals(type) && value.length() > NAME_MAX_SIZE) {
            throw ExceptionBuilder.build(ApiMgmtPromotionErrorCode.BAD_REQUEST_PARAMETER,
                    "Name must have between 1 and 40 characters");
        }
    }
}
