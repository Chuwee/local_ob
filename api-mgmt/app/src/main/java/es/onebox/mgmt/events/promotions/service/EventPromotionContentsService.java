package es.onebox.mgmt.events.promotions.service;

import es.onebox.core.exception.ExceptionBuilder;
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
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.promotion.enums.PromotionTagType;
import es.onebox.mgmt.datasources.ms.promotion.repository.EventPromotionsRepository;
import es.onebox.mgmt.exception.ApiMgmtPromotionErrorCode;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class EventPromotionContentsService {

	private static final int NAME_MAX_SIZE = 40;
    private static final int DESCRIPTION_MAX_SIZE = 1000;
    private final MasterdataService masterdataService;
	private final ValidationService validationService;
	private final EventPromotionsRepository eventPromotionsRepository;

	@Autowired
	public EventPromotionContentsService(MasterdataService masterdataService, ValidationService validationService,
			EventPromotionsRepository eventPromotionsRepository) {
		this.masterdataService = masterdataService;
		this.validationService = validationService;
		this.eventPromotionsRepository = eventPromotionsRepository;
	}

	public ChannelContentTextListDTO<PromotionChannelContentTextType> getEventPromotionChannelContentTexts(Long eventId,
			Long promotionId, PromotionChannelContentTextFilter filter) {
	    validationService.getAndCheckEvent(eventId);

		CommunicationElementFilter<PromotionTagType> communicationElementFilter = ChannelContentConverter
				.fromPromotionFilter(filter, masterdataService);

		List<BaseCommunicationElement> comElements = eventPromotionsRepository.getChannelContentTexts(eventId,
				promotionId, communicationElementFilter);

		comElements.sort(Comparator.comparing(BaseCommunicationElement::getLanguage));

		return ChannelContentConverter.fromMsPromotionText(comElements);
	}

	public void updateEventPromotionChannelContentTexts(Long eventId, Long promotionId,
			PromotionChannelContentTextListDTO content) {
		Event event = validationService.getAndCheckEvent(eventId);
		Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
		for (ChannelContentTextDTO<PromotionChannelContentTextType> element : content.getTexts()) {
            PromotionChannelContentTextType type = element.getType();
            String value = element.getValue();
            validateLength(type, value);
			element.setLanguage(
					ChannelContentsUtils.checkElementLanguageForEvent(event, languages, element.getLanguage()));
		}
		eventPromotionsRepository.updateChannelContentTexts(eventId, promotionId,
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
