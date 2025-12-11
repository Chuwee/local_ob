package es.onebox.mgmt.channels.promotions.service;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.dto.ChannelPromotionDetailDTO;
import es.onebox.mgmt.channels.promotions.ChannelPromotionConverter;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionDiscountDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionEventsDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionPriceTypesDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionSessionsDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionsDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionsFilter;
import es.onebox.mgmt.channels.promotions.dto.CreateChannelPromotionDTO;
import es.onebox.mgmt.channels.promotions.dto.UpdateChannelPromotionDTO;
import es.onebox.mgmt.channels.promotions.dto.UpdateChannelPromotionEventsDTO;
import es.onebox.mgmt.channels.promotions.dto.UpdateChannelPromotionPriceTypesDTO;
import es.onebox.mgmt.channels.promotions.dto.UpdateChannelPromotionSessionsDTO;
import es.onebox.mgmt.channels.promotions.enums.ChannelPromotionDiscountType;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelType;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionDetail;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionEvents;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionPriceTypes;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionSessions;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotions;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.CreateChannelPromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.UpdateChannelPromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.UpdateChannelPromotionEvents;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.UpdateChannelPromotionPriceTypes;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.UpdateChannelPromotionSessions;
import es.onebox.mgmt.datasources.ms.promotion.repository.ChannelPromotionsRepository;
import es.onebox.mgmt.entities.factory.InventoryProviderEnum;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ApiMgmtPromotionErrorCode;

@Service
public class ChannelPromotionsService {

    private final ChannelsHelper channelsHelper;
    private final ChannelPromotionsRepository channelPromotionsRepository;
    private final EntitiesRepository entitiesRepository;
    private final MasterdataService masterdataService;
	private static final Set<ChannelSubtype> ALLOWED_SUBTYPES_LIST = EnumSet.of(ChannelSubtype.PORTAL_WEB,
			ChannelSubtype.BOX_OFFICE_WEB);

    @Autowired
    public ChannelPromotionsService(ChannelsHelper channelsHelper, ChannelPromotionsRepository channelPromotionsRepository,
                                    EntitiesRepository entitiesRepository, MasterdataService masterdataService) {
        this.channelsHelper = channelsHelper;
        this.channelPromotionsRepository = channelPromotionsRepository;
        this.entitiesRepository = entitiesRepository;
        this.masterdataService = masterdataService;
    }

    public ChannelPromotionsDTO getChannelPromotions(Long channelId, ChannelPromotionsFilter filter) {
        channelsHelper.getAndCheckChannel(channelId);

        ChannelPromotions channelPromotions = channelPromotionsRepository.getChannelPromotions(channelId, filter);

        return ChannelPromotionConverter.fromMsPromotions(channelPromotions);
    }

    public ChannelPromotionDetailDTO getChannelPromotion(Long channelId, Long promotionId) {
        ChannelResponse channel = channelsHelper.getAndCheckChannel(channelId);

		Operator cachedOperator = entitiesRepository.getCachedOperator(channel.getEntityId());
        return ChannelPromotionConverter
                .fromMsPromotionDetail(channelPromotionsRepository.getChannelPromotion(channelId, promotionId),
						BooleanUtils.isTrue(cachedOperator.getUseMultiCurrency()),
						masterdataService.getCurrencies(), CurrenciesUtils.getDefaultCurrency(cachedOperator));
    }

	public IdDTO createChannelPromotion(Long channelId, CreateChannelPromotionDTO request) {
		ChannelResponse channelResponse = channelsHelper.getAndCheckChannel(channelId);
		Entity entity = entitiesRepository.getCachedEntity(channelResponse.getEntityId());

		if (entity == null) {
			throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_NOT_FOUND);
        }

		validateChannelType(channelResponse);
		// TODO: Validation for now will be done hardcoded, in near future
		// TODO: is expected to be done from provider config or something like that
		validateItalianCompliance(entity);

		return channelPromotionsRepository.createChannelPromotion(channelId,
				ChannelPromotionConverter.toMsCreatePromotion(request));
	}

	private void validateChannelType(ChannelResponse channelResponse) {
		boolean isObPortal = ChannelType.OB_PORTAL.equals(channelResponse.getType());
		if (!isObPortal ||
				!ALLOWED_SUBTYPES_LIST.contains(channelResponse.getSubtype())) {
			throw new OneboxRestException(ApiMgmtErrorCode.PROMOTION_CHANNEL_TYPE_NOT_SUPPORTED);
		}
	}

	private void validateItalianCompliance(Entity entity) {
		List<InventoryProviderEnum> providers = entity.getInventoryProviders();

		if (providers == null || providers.size() != 1) {
			return;
		}

		if (InventoryProviderEnum.ITALIAN_COMPLIANCE.equals(providers.get(0))) {
			throw new OneboxRestException(ApiMgmtErrorCode.PROMOTION_CHANNEL_TYPE_NOT_SUPPORTED);
		}
	}

    public void updateChannelPromotion(Long channelId, Long promotionId, UpdateChannelPromotionDTO request) {
        ChannelResponse channel = channelsHelper.getAndCheckChannel(channelId);

        ChannelPromotionDiscountDTO discount = request.getDiscount();
        if(discount != null && discount.getType() == ChannelPromotionDiscountType.FIXED && discount.getFixedValues() != null) {
            discount.getFixedValues().stream()
                    .filter(amountCurrency -> amountCurrency.getAmount() == null)
                    .findAny()
                    .ifPresent(amountCurrency -> {
                        throw new OneboxRestException(ApiMgmtPromotionErrorCode.CHANNEL_PROMOTION_AMOUNT_MANDATORY);
                    });
        }
        UpdateChannelPromotion requestBody = ChannelPromotionConverter.toMsUpdatePromotion(request,
                BooleanUtils.isTrue(entitiesRepository.getCachedOperator(channel.getEntityId()).getUseMultiCurrency()),
                masterdataService.getCurrencies());
        channelPromotionsRepository.updateChannelPromotion(channelId, promotionId, requestBody);
    }

    public void deleteChannelPromotion(Long channelId, Long promotionId) {
        channelsHelper.getAndCheckChannel(channelId);

        getAndCheckPromotion(channelId, promotionId);

        channelPromotionsRepository.deleteChannelPromotion(channelId, promotionId);
    }

    public void getAndCheckPromotion(Long channelId, Long promotionId) {
        ChannelPromotionDetail channelPromotionDetail = channelPromotionsRepository.getChannelPromotion(channelId, promotionId);
        if(channelPromotionDetail == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PROMOTION_NOT_FOUND);
        }
    }

    public ChannelPromotionEventsDTO getChannelPromotionEvents(Long channelId, Long promotionId) {
        channelsHelper.getAndCheckChannel(channelId);

        ChannelPromotionEvents scopes = channelPromotionsRepository.getChannelPromotionEvents(channelId, promotionId);
        return ChannelPromotionConverter.fromMsPromotionEventScope(scopes);
    }

    public void updateChannelPromotionEvents(Long channelId, Long promotionId, UpdateChannelPromotionEventsDTO request) {
        channelsHelper.getAndCheckChannel(channelId);

        UpdateChannelPromotionEvents scopes = ChannelPromotionConverter.toMsPromotionEventScope(request);
        channelPromotionsRepository.updateChannelPromotionEvents(channelId, promotionId, scopes);
    }

    public ChannelPromotionSessionsDTO getChannelPromotionSessions(Long channelId, Long promotionId) {
        channelsHelper.getAndCheckChannel(channelId);

        ChannelPromotionSessions scopes = channelPromotionsRepository.getChannelPromotionSessions(channelId, promotionId);
        return ChannelPromotionConverter.fromMsPromotionSessionScope(scopes);
    }

    public void updateChannelPromotionSessions(Long channelId, Long promotionId, UpdateChannelPromotionSessionsDTO request) {
        channelsHelper.getAndCheckChannel(channelId);

        UpdateChannelPromotionSessions scopes = ChannelPromotionConverter.toMsPromotionSessionScope(request);
        channelPromotionsRepository.updateChannelPromotionSessions(channelId, promotionId, scopes);
    }

    public IdDTO cloneChannelPromotion(Long channelId, Long promotionId) {
        channelsHelper.getAndCheckChannel(channelId);

        CreateChannelPromotion clonePromotion = ChannelPromotionConverter.toMsClonePromotion(promotionId);
        return channelPromotionsRepository.cloneChannelPromotion(channelId, clonePromotion);
    }

    public ChannelPromotionPriceTypesDTO getChannelPromotionPriceTypes(Long channelId, Long promotionId) {
        channelsHelper.getAndCheckChannel(channelId);
        ChannelPromotionPriceTypes priceTypes = channelPromotionsRepository.getChannelPromotionPriceTypes(channelId, promotionId);
        ChannelPromotionPriceTypesDTO priceTypeDto = new ChannelPromotionPriceTypesDTO();
        if (Objects.nonNull(priceTypes)) {
            priceTypeDto.setType(priceTypes.getType());
            if (CollectionUtils.isNotEmpty(priceTypes.getPriceTypes())) {
                priceTypeDto.setPriceTypes(ChannelPromotionConverter.convertToChannelPromotionPriceTypesDto(priceTypes.getPriceTypes()));
            }
        }
        return priceTypeDto;
    }

    public void updateChannelPromotionPriceTypes(Long channelId, Long promotionId, UpdateChannelPromotionPriceTypesDTO body) {
        channelsHelper.getAndCheckChannel(channelId);
        UpdateChannelPromotionPriceTypes updatePriceTypes = new UpdateChannelPromotionPriceTypes();
        updatePriceTypes.setType(body.getType());
        updatePriceTypes.setPriceTypes(body.getData());
        channelPromotionsRepository.updateChannelPromotionPriceTypes(channelId, promotionId, updatePriceTypes);
    }

}
