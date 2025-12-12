package es.onebox.fever.converter;

import es.onebox.common.datasources.ms.channel.dto.ChannelDTO;
import es.onebox.common.datasources.ms.promotion.dto.CommunicationElementDTO;
import es.onebox.common.datasources.ms.promotion.dto.EventPromotionPriceTypesDTO;
import es.onebox.common.datasources.ms.promotion.dto.EventSessionDTO;
import es.onebox.common.datasources.ms.promotion.dto.PromotionChannelsDTO;
import es.onebox.common.datasources.ms.promotion.dto.PromotionCollectiveDTO;
import es.onebox.common.datasources.ms.promotion.dto.PromotionDetailDTO;
import es.onebox.common.datasources.ms.promotion.dto.PromotionDiscountConfigDTO;
import es.onebox.common.datasources.ms.promotion.dto.PromotionEventSessionsDTO;
import es.onebox.common.datasources.ms.promotion.dto.PromotionLimitDTO;
import es.onebox.common.datasources.ms.promotion.dto.PromotionLimitsDTO;
import es.onebox.common.datasources.ms.promotion.dto.PromotionMaxLimitDTO;
import es.onebox.common.datasources.ms.promotion.dto.PromotionPeriodDTO;
import es.onebox.common.datasources.ms.promotion.dto.PromotionPriceTypeDTO;
import es.onebox.common.datasources.ms.promotion.dto.PromotionRatesDTO;
import es.onebox.common.datasources.webhook.dto.fever.event.CommunicationElementFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.event.EventSessionFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.promotion.EventPromotionPriceTypesFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.promotion.PromotionChannelsFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.promotion.PromotionCollectiveFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.promotion.PromotionDetailFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.promotion.PromotionDiscountConfigFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.promotion.PromotionEventSessionsFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.promotion.PromotionLimitFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.promotion.PromotionLimitsFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.promotion.PromotionMaxLimitFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.promotion.PromotionPeriodFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.promotion.PromotionPriceTypeFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.promotion.PromotionRatesFeverDTO;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PromotionConverter {

  public static PromotionDetailFeverDTO convert(PromotionDetailDTO promotion) {

    if (promotion == null) {
      return null;
    }

    PromotionDetailFeverDTO newPromotionDetail = new PromotionDetailFeverDTO();

    newPromotionDetail.setId(promotion.getId());
    newPromotionDetail.setName(promotion.getName());
    newPromotionDetail.setType(promotion.getType());
    newPromotionDetail.setCollective(convert(promotion.getCollective()));
    newPromotionDetail.setCombinable(promotion.getCombinable());
    newPromotionDetail.setIncludePromoterSurcharges(promotion.getIncludePromoterSurcharges());
    newPromotionDetail.setDiscount(convert(promotion.getDiscount()));
    newPromotionDetail.setLimits(convert(promotion.getLimits()));
    newPromotionDetail.setPresale(promotion.getPresale());
    newPromotionDetail.setStatus(promotion.getStatus());
    newPromotionDetail.setAccesControlRestricted(promotion.getAccesControlRestricted());
    newPromotionDetail.setValidityPeriod(convert(promotion.getValidityPeriod()));
    newPromotionDetail.setShowTicketPriceWithoutDiscount(promotion.getShowTicketPriceWithoutDiscount());
    newPromotionDetail.setIncludeChannelSurcharges(promotion.getIncludeChannelSurcharges());
    newPromotionDetail.setShowTicketDiscountName(promotion.getShowTicketDiscountName());


    return newPromotionDetail;

  }

  private static PromotionCollectiveFeverDTO convert(PromotionCollectiveDTO collective) {

    if (collective == null) {
      return null;
    }

    PromotionCollectiveFeverDTO newCollective = new PromotionCollectiveFeverDTO();
    newCollective.setId(collective.getId());
    newCollective.setSelfManaged(collective.getSelfManaged());
    newCollective.setBoxOfficeValidation(collective.getBoxOfficeValidation());
    newCollective.setType(collective.getType());
    newCollective.setRestrictiveSale(collective.getRestrictiveSale());
    return newCollective;
  }

  private static PromotionDiscountConfigFeverDTO convert(PromotionDiscountConfigDTO config) {

    if (config == null) {
      return null;
    }

    PromotionDiscountConfigFeverDTO newConfig = new PromotionDiscountConfigFeverDTO();

    newConfig.setValue(config.getValue());
    newConfig.setType(config.getType());

    return newConfig;
  }

  private static PromotionLimitsFeverDTO convert(PromotionLimitsDTO limits) {

    if (limits == null) {
      return null;
    }

    PromotionLimitsFeverDTO newLimits = new PromotionLimitsFeverDTO();

    newLimits.setPromotionMaxLimit(convert(limits.getPromotionMaxLimit()));
    newLimits.setPacks(convert(limits.getPacks()));
    newLimits.setEventUserCollectiveMaxLimit(convert(limits.getEventUserCollectiveMaxLimit()));
    newLimits.setPurchaseMaxLimit(convert(limits.getPurchaseMaxLimit()));
    newLimits.setPurchaseMinLimit(convert(limits.getPurchaseMinLimit()));
    newLimits.setSessionMaxLimit(convert(limits.getSessionMaxLimit()));
    newLimits.setSessionUserCollectiveMaxLimit(convert(limits.getSessionUserCollectiveMaxLimit()));

    return newLimits;
  }

  private static PromotionMaxLimitFeverDTO convert(PromotionMaxLimitDTO limits) {

    if (limits == null) {
      return null;
    }

    PromotionMaxLimitFeverDTO newLimit = new PromotionMaxLimitFeverDTO();

    newLimit.setCurrent(limits.getCurrent());
    newLimit.setLimit(limits.getLimit());
    newLimit.setEnabled(limits.getEnabled());

    return newLimit;
  }

  private static PromotionLimitFeverDTO convert(PromotionLimitDTO limit) {

    if (limit == null) {
      return null;
    }

    PromotionLimitFeverDTO newLimit = new PromotionLimitFeverDTO();

    newLimit.setEnabled(limit.getEnabled());
    newLimit.setLimit(limit.getLimit());

    return newLimit;
  }

  private static PromotionPeriodFeverDTO convert(PromotionPeriodDTO period) {

    if (period == null) {
      return null;
    }

    PromotionPeriodFeverDTO newPeriod = new PromotionPeriodFeverDTO();

    newPeriod.setEndDate(period.getEndDate());
    newPeriod.setStartDate(period.getStartDate());
    newPeriod.setType(period.getType());

    return newPeriod;

  }

  public static List<CommunicationElementFeverDTO> convert
      (List<CommunicationElementDTO> communicationElements) {

    if (communicationElements == null) {
      return null;
    }

    List<CommunicationElementFeverDTO> newCommunicationElements = new ArrayList<>();

    communicationElements.forEach(comm -> newCommunicationElements.add(convert(comm)));

    return newCommunicationElements;
  }

  private static CommunicationElementFeverDTO convert(CommunicationElementDTO communicationElement) {

    if (communicationElement == null) {
      return null;
    }

    CommunicationElementFeverDTO newCommunicationElement = new CommunicationElementFeverDTO();

    newCommunicationElement.setLanguage(communicationElement.getLanguage());
    newCommunicationElement.setTag(communicationElement.getTag());
    newCommunicationElement.setValue(communicationElement.getValue());

    return newCommunicationElement;
  }

  public static PromotionChannelsFeverDTO convert(PromotionChannelsDTO channels) {

    if (channels == null) {
      return null;
    }

    PromotionChannelsFeverDTO newChannels = new PromotionChannelsFeverDTO();

    newChannels.setChannels(channels.getChannels());
    newChannels.setType(channels.getType());

    return newChannels;
  }

  public static List<Long> convertToId(List<ChannelDTO> channelList){
    if (channelList == null) {
      return null;
    }
    return channelList.stream().map(ChannelDTO::getId).toList();
  }

  public static PromotionEventSessionsFeverDTO convert(PromotionEventSessionsDTO sessions) {
    if (sessions == null) {
      return null;
    }

    PromotionEventSessionsFeverDTO newSessions = new PromotionEventSessionsFeverDTO();

    newSessions.setSessions(convert(sessions.getSessions()));
    newSessions.setType(sessions.getType());

    return newSessions;
  }

  private static Set<EventSessionFeverDTO> convert(Set<EventSessionDTO> sessions) {
    if (sessions == null) {
      return null;
    }

    Set<EventSessionFeverDTO> newSessions = new HashSet<>();
    sessions.forEach(session -> newSessions.add(convert(session)));

    return newSessions;
  }

  private static EventSessionFeverDTO convert(EventSessionDTO session){
    if (session == null) {
      return null;
    }

    EventSessionFeverDTO newSession = new EventSessionFeverDTO();

    newSession.setId(session.getId());
    newSession.setSessionType(session.getSessionType());
    newSession.setEventId(session.getEventId());
    newSession.setDate(SessionConverter.mapSessionDate(session.getDate()));
    newSession.setName(session.getName());

    return newSession;

  }


  public static EventPromotionPriceTypesFeverDTO convert(EventPromotionPriceTypesDTO priceTypes) {
    if (priceTypes == null) {
      return null;
    }

    EventPromotionPriceTypesFeverDTO newPriceTypes = new EventPromotionPriceTypesFeverDTO();
    newPriceTypes.setType(priceTypes.getType());
    newPriceTypes.setPriceTypes(convertSet(priceTypes.getPriceTypes()));
    return newPriceTypes;
  }

  private static Set<PromotionPriceTypeFeverDTO> convertSet(Set<PromotionPriceTypeDTO> priceTypes) {
    if (priceTypes == null) {
      return null;
    }

    Set<PromotionPriceTypeFeverDTO> newPriceTypes = new HashSet<>();

    priceTypes.forEach(pr -> newPriceTypes.add(convert(pr)));

    return newPriceTypes;
  }

  private static PromotionPriceTypeFeverDTO convert(PromotionPriceTypeDTO priceType) {
    if (priceType == null) {
      return null;
    }

    PromotionPriceTypeFeverDTO newPriceType = new PromotionPriceTypeFeverDTO();
    newPriceType.setId(priceType.getId());
    newPriceType.setName(priceType.getName());
    newPriceType.setVenueTemplateId(priceType.getVenueTemplateId());

    return newPriceType;
  }

  public static PromotionRatesFeverDTO convert(PromotionRatesDTO rates){
    if (rates == null) {
      return null;
    }

    PromotionRatesFeverDTO newRates = new PromotionRatesFeverDTO();

    newRates.setRates(rates.getRates());
    newRates.setType(rates.getType());

    return newRates;
  }

}
