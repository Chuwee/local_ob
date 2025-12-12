package es.onebox.atm.wizard.channel.service;

import es.onebox.atm.cart.ATMVendorConstants;
import es.onebox.atm.wizard.channel.dto.ChannelConfigurationRequest;
import es.onebox.common.datasources.ms.channel.dto.config.ChannelConfigDTO;
import es.onebox.common.datasources.ms.channel.repository.ChannelConfigRepository;
import es.onebox.common.datasources.ms.client.repository.AuthVendorChannelConfigRepository;
import es.onebox.common.datasources.ms.event.dto.BaseEventChannelDTO;
import es.onebox.common.datasources.ms.event.dto.EventChannelInfoDTO;
import es.onebox.common.datasources.ms.event.dto.EventChannelsDTO;
import es.onebox.common.datasources.ms.event.enums.ChannelSubtype;
import es.onebox.common.datasources.ms.event.enums.EventChannelReleaseFlagStatus;
import es.onebox.common.datasources.ms.event.enums.EventChannelSaleFlagStatus;
import es.onebox.common.datasources.ms.event.enums.EventChannelStatus;
import es.onebox.common.datasources.ms.event.enums.EventStatus;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.promotion.dto.CommunicationElementDTO;
import es.onebox.common.datasources.ms.promotion.dto.PromotionDetailDTO;
import es.onebox.common.datasources.ms.promotion.dto.PromotionDiscountConfigDTO;
import es.onebox.common.datasources.ms.promotion.enums.PromotionDiscountType;
import es.onebox.common.datasources.ms.promotion.repository.PromotionChannelRepository;
import es.onebox.core.exception.OneboxRestException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AtmWizardChannelConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AtmWizardChannelConfigService.class);

    private final ChannelConfigRepository channelConfigRepository;
    private final AuthVendorChannelConfigRepository authVendorChannelConfigRepository;
    private final MsEventRepository msEventRepository;
    private final PromotionChannelRepository promotionChannelRepository;

    @Autowired
    public AtmWizardChannelConfigService(final ChannelConfigRepository channelConfigRepository,
                                         final AuthVendorChannelConfigRepository authVendorChannelConfigRepository,
                                         final MsEventRepository msEventRepository,
                                         final PromotionChannelRepository promotionChannelRepository){
        this.channelConfigRepository = channelConfigRepository;
        this.authVendorChannelConfigRepository = authVendorChannelConfigRepository;
        this.msEventRepository = msEventRepository;
        this.promotionChannelRepository = promotionChannelRepository;
    }

    public void setUpChannelConfig(final Long eventId, final ChannelConfigurationRequest request){
        List<Integer> promotionIds = request.getPromotions().stream().distinct().toList();
        List<Long> vendorChannelIds = authVendorChannelConfigRepository.getChannelsByAuthVendor(ATMVendorConstants.ATM_VENDOR_ID);

        List<Long> eventChannelIds = getFilteredEventChannels(eventId, vendorChannelIds);
        LOGGER.info("ATM WIZARD - Writing fake promotions {} of event {} in channels {}", promotionIds, eventId, eventChannelIds);

        eventChannelIds.forEach(channelId -> updateChannelConfigDocument(channelId, promotionIds));

        for(Integer promotionId : promotionIds) {
            PromotionDetailDTO promotionDetailDTO = this.promotionChannelRepository.getEventPromotion(eventId, promotionId.longValue());
            promotionDetailDTO.setShowTicketDiscountName(false);
            PromotionDiscountConfigDTO promotionDiscountConfigDTO = promotionDetailDTO.getDiscount();
            if(promotionDiscountConfigDTO != null) {
                promotionDiscountConfigDTO.setType(PromotionDiscountType.FIXED);
                promotionDiscountConfigDTO.setValue(Double.valueOf("0"));
                promotionDetailDTO.setDiscount(promotionDiscountConfigDTO);

                this.promotionChannelRepository.putEventPromotion(eventId, promotionId.longValue(), promotionDetailDTO);
            }

            List<CommunicationElementDTO> communicationElements = this.promotionChannelRepository.getEventCommunicationElements(eventId, promotionId.longValue());
            if(communicationElements != null && !communicationElements.isEmpty()) {
                for (CommunicationElementDTO communicationElement : communicationElements) {
                    communicationElement.setValue(StringUtils.EMPTY);
                }
                this.promotionChannelRepository.putEventCommunicationElements(eventId, promotionId.longValue(), communicationElements);
            }
        }
    }

    private List<Long> getFilteredEventChannels(Long eventId, List<Long> vendorChannelIds) {
        if(CollectionUtils.isEmpty(vendorChannelIds)){
            return Collections.emptyList();
        }

        EventChannelsDTO eventChannels = msEventRepository.getEventChannels(eventId);
        if(eventChannels.getMetadata().getTotal() == 0L){
            return Collections.emptyList();
        }
        return eventChannels.getData().stream()
                .filter(this::isValidChannelType)
                .filter(this::isValidEventStatus)
                .filter(this::isValidChannelEventStatus)
                .map(BaseEventChannelDTO::getChannel)
                .map(EventChannelInfoDTO::getId)
                .filter(vendorChannelIds::contains)
                .toList();
    }

    private boolean isValidChannelEventStatus(BaseEventChannelDTO eventChannel) {
        EventChannelStatus requestStatus = eventChannel.getStatus().getRequest();
        EventChannelReleaseFlagStatus releaseStatus = eventChannel.getStatus().getRelease();
        EventChannelSaleFlagStatus saleStatus = eventChannel.getStatus().getSale();

        return isValidRequestStatus(requestStatus) &&
                isValidReleaseStatus(releaseStatus) &&
                isValidSaleStatus(saleStatus);
    }

    private boolean isValidSaleStatus(EventChannelSaleFlagStatus saleStatus) {
        return EventChannelSaleFlagStatus.IN_PROGRAMMING.equals(saleStatus) ||
                EventChannelSaleFlagStatus.PLANNED.equals(saleStatus) ||
                EventChannelSaleFlagStatus.SALE.equals(saleStatus) ||
                EventChannelSaleFlagStatus.SALE_PENDING.equals(saleStatus);
    }

    private boolean isValidReleaseStatus(EventChannelReleaseFlagStatus releaseStatus) {
        return EventChannelReleaseFlagStatus.IN_PROGRAMMING.equals(releaseStatus) ||
                EventChannelReleaseFlagStatus.PLANNED.equals(releaseStatus) ||
                EventChannelReleaseFlagStatus.RELEASE_PENDING.equals(releaseStatus) ||
                EventChannelReleaseFlagStatus.RELEASED.equals(releaseStatus);
    }

    private boolean isValidRequestStatus(EventChannelStatus requestStatus) {
        return EventChannelStatus.ACCEPTED.equals(requestStatus);
    }

    private boolean isValidChannelType(BaseEventChannelDTO eventChannel) {
        ChannelSubtype type = eventChannel.getChannel().getType();
        return ChannelSubtype.PORTAL_WEB.equals(type);
    }

    private boolean isValidEventStatus(BaseEventChannelDTO eventChannel){
        EventStatus status = eventChannel.getEvent().getStatus();
        return EventStatus.READY.equals(status) ||
                EventStatus.IN_PROGRAMMING.equals(status) ||
                EventStatus.IN_PROGRESS.equals(status);
    }

    private void updateChannelConfigDocument(Long channelId, List<Integer> promotionIds) {
        try{
            ChannelConfigDTO channelConfig = channelConfigRepository.getChannelConfig(channelId);
            Set<Integer> currentPromotions = new HashSet<>(channelConfig.getCustomPromotionalCodeValidation().getSalesId());
            currentPromotions.addAll(promotionIds);
            channelConfig.getCustomPromotionalCodeValidation().setSalesId(new ArrayList<>(currentPromotions));
            channelConfig.setAllowAutomaticSeatSelection(true);
            channelConfigRepository.updateChannelConfig(channelId,channelConfig);
        }catch(OneboxRestException | NullPointerException ex){
            LOGGER.error("ATM WIZARD - Error writing promotions for channel {} : {}", channelId, ex.getMessage());
        }
    }
}
