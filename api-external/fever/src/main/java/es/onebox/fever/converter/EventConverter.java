package es.onebox.fever.converter;

import es.onebox.common.datasources.ms.event.dto.EventChannelSurchargesDTO;
import es.onebox.common.datasources.ms.event.dto.EventCommunicationElementDTO;
import es.onebox.common.datasources.ms.event.dto.EventDTO;
import es.onebox.common.datasources.ms.event.dto.EventLanguageDTO;
import es.onebox.common.datasources.ms.event.dto.EventRateDTO;
import es.onebox.common.datasources.ms.event.dto.EventRatesDTO;
import es.onebox.common.datasources.ms.event.dto.EventTemplatePriceDTO;
import es.onebox.common.datasources.ms.event.dto.RangeDTO;
import es.onebox.common.datasources.ms.event.dto.RangeValueDTO;
import es.onebox.common.datasources.ms.event.dto.RateGroupResponseDTO;
import es.onebox.common.datasources.ms.event.dto.SurchargeLimitDTO;
import es.onebox.common.datasources.ms.event.dto.SurchargesDTO;
import es.onebox.common.datasources.ms.venue.dto.MsPriceTypeDTO;
import es.onebox.common.datasources.ms.venue.dto.MsPriceTypeWebCommunicationElementDTO;
import es.onebox.common.datasources.webhook.dto.fever.event.EventChannelSurchargesFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.event.EventCommunicationElementFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.event.EventLanguageFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.event.EventRateFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.event.EventRatesFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.event.EventTemplatePriceFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.event.PriceTypeAdditionalConfigDTO;
import es.onebox.common.datasources.webhook.dto.fever.event.PriceTypeDTO;
import es.onebox.common.datasources.webhook.dto.fever.event.PriceTypeWebCommunicationElementDTO;
import es.onebox.common.datasources.webhook.dto.fever.event.RangeFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.event.RangeValueFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.event.RateGroupResponseFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.event.SurchargeLimitFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.event.SurchargesFeverDTO;
import java.util.ArrayList;
import java.util.List;

public class EventConverter {

  public static List<EventLanguageFeverDTO> toEventLanguageList(EventDTO event) {

    if (event == null) {
      return null;
    }

    List<EventLanguageFeverDTO> languages = new ArrayList<>();

    event.getLanguages().forEach(language -> {
      languages.add(toEventLanguage(language));
    });

    return languages;
  }

  private static EventLanguageFeverDTO toEventLanguage(EventLanguageDTO eventLanguage) {
    EventLanguageFeverDTO newLanguage = new EventLanguageFeverDTO();
    newLanguage.setId(eventLanguage.getId());
    newLanguage.setCode(eventLanguage.getCode());
    newLanguage.setIsDefault(eventLanguage.getDefault());
    return newLanguage;
  }

  public static List<EventCommunicationElementFeverDTO> toEventCommunicationElementDTO(
      List<EventCommunicationElementDTO> eventCommunicationElement) {

    if (eventCommunicationElement == null) {
      return null;
    }

    List <EventCommunicationElementFeverDTO> communicationElements = new ArrayList<>();
    eventCommunicationElement.forEach(communicationElement -> {
      communicationElements.add(toEventCommunicationElementDTO(communicationElement));
    });

    return communicationElements;
  }

  private static EventCommunicationElementFeverDTO toEventCommunicationElementDTO(
      EventCommunicationElementDTO eventCommunicationElement) {

    if (eventCommunicationElement == null) {
      return null;
    }

    EventCommunicationElementFeverDTO newCommunicationElement = new EventCommunicationElementFeverDTO();

    newCommunicationElement.setId(eventCommunicationElement.getId());
    newCommunicationElement.setLanguage(eventCommunicationElement.getLanguage());
    newCommunicationElement.setImageBinary(eventCommunicationElement.getImageBinary());
    newCommunicationElement.setLanguage(eventCommunicationElement.getLanguage());
    newCommunicationElement.setTag(eventCommunicationElement.getTag());
    newCommunicationElement.setTagId(eventCommunicationElement.getTagId());
    newCommunicationElement.setPosition(eventCommunicationElement.getPosition());
    newCommunicationElement.setValue(eventCommunicationElement.getValue());


    return newCommunicationElement;
  }

  public static List<SurchargesFeverDTO> toEventSuchargesFeverDTOList(
      List<SurchargesDTO> surchargeList) {

    if (surchargeList == null) {
      return null;
    }

    List<SurchargesFeverDTO> surcharges = new ArrayList<>();
    surchargeList.forEach(surcharge -> {
      surcharges.add(toSurchargesFeverDTO(surcharge));
    });
    return surcharges;
  }

  private static SurchargesFeverDTO toSurchargesFeverDTO(SurchargesDTO surcharge) {

    if (surcharge == null) {
      return null;
    }

    SurchargesFeverDTO newSurcharge = new SurchargesFeverDTO();
    newSurcharge.setRanges(toRangeFeverDTOList(surcharge.getRanges()));
    newSurcharge.setLimit(toSurchargeLimitFeverDTO(surcharge.getLimit()));
    newSurcharge.setType(surcharge.getType());
    newSurcharge.setAllowChannelUseAlternativeCharges(
        surcharge.getAllowChannelUseAlternativeCharges()
    );

    return newSurcharge;
  }
  private static SurchargeLimitFeverDTO toSurchargeLimitFeverDTO(
      SurchargeLimitDTO surchargeLimit) {

    if (surchargeLimit == null) {
      return null;
    }

    SurchargeLimitFeverDTO surchargeLimitFever = new SurchargeLimitFeverDTO();
    surchargeLimitFever.setEnabled(surchargeLimit.getEnabled());
    surchargeLimitFever.setMax(surchargeLimit.getMax());
    surchargeLimitFever.setMin(surchargeLimit.getMin());
    return surchargeLimitFever;
  }

  private static List<RangeFeverDTO> toRangeFeverDTOList(List<RangeDTO> rangeList) {

    if (rangeList == null) {
      return null;
    }

    List<RangeFeverDTO> newRangeList = new ArrayList<>();
    rangeList.forEach(range -> {
      newRangeList.add(toRangeFeverDTO(range));
    });
    return newRangeList;
  }

  private static RangeFeverDTO toRangeFeverDTO(RangeDTO range) {

    if (range == null) {
      return null;
    }

    RangeFeverDTO newRange = new RangeFeverDTO();
    newRange.setFrom(range.getFrom());
    newRange.setTo(range.getTo());
    newRange.setCurrencyId(range.getCurrencyId());
    newRange.setValues(toRangeValueFeverDTO(range.getValues()));
    return newRange;
  }

  private static RangeValueFeverDTO toRangeValueFeverDTO(RangeValueDTO rangeValue) {

    if (rangeValue == null) {
      return null;
    }

    RangeValueFeverDTO newRangeValue = new RangeValueFeverDTO();
    newRangeValue.setFixed(rangeValue.getFixed());
    newRangeValue.setMax(rangeValue.getMax());
    newRangeValue.setMin(rangeValue.getMin());
    newRangeValue.setPercentage(rangeValue.getPercentage());

    return newRangeValue;
  }

  public static List<EventChannelSurchargesFeverDTO> toEventChannelSurchargesFeverDTOList(
      List<EventChannelSurchargesDTO> eventChannelSurcharges) {

    if (eventChannelSurcharges == null) {
      return null;
    }

    List<EventChannelSurchargesFeverDTO> newEventChannelSurcharges = new ArrayList<>();

    eventChannelSurcharges.forEach(surcharges -> {
      newEventChannelSurcharges.add(toEventChannelSurchargesFeverDTO(surcharges));
    });
    return newEventChannelSurcharges;
  }

  private static EventChannelSurchargesFeverDTO toEventChannelSurchargesFeverDTO(EventChannelSurchargesDTO surcharge) {

    if (surcharge == null) {
      return null;
    }

    EventChannelSurchargesFeverDTO newEventSurcharges = new EventChannelSurchargesFeverDTO();

    newEventSurcharges.setEnabledRanges(surcharge.getEnabledRanges());
    newEventSurcharges.setLimit(toSurchargeLimitFeverDTO(surcharge.getLimit()));
    newEventSurcharges.setType(surcharge.getType());
    newEventSurcharges.setAllowChannelUseAlternativeCharges(surcharge.getAllowChannelUseAlternativeCharges());
    newEventSurcharges.setRanges(toRangeFeverDTOList(surcharge.getRanges()));
    return newEventSurcharges;

  }

  public static List<EventTemplatePriceFeverDTO> toEventTemplatePriceFeverDTOList(
      List<EventTemplatePriceDTO> eventTemplatePriceList) {

    if (eventTemplatePriceList == null) {
      return null;
    }
    List<EventTemplatePriceFeverDTO> newEventTemplatePriceList = new ArrayList<>();
    eventTemplatePriceList.forEach(templates -> {
      newEventTemplatePriceList.add(toEventTemplatePriceFeverDTO(templates));
    });
    return newEventTemplatePriceList;
  }



  private static EventTemplatePriceFeverDTO toEventTemplatePriceFeverDTO(EventTemplatePriceDTO eventTemplatePrice) {

    if (eventTemplatePrice == null) {
      return null;
    }

    EventTemplatePriceFeverDTO newEventTemplatePrice = new EventTemplatePriceFeverDTO();
    newEventTemplatePrice.setPrice(eventTemplatePrice.getPrice());
    newEventTemplatePrice.setPriceType(eventTemplatePrice.getPriceType());
    newEventTemplatePrice.setPriceTypeCode(eventTemplatePrice.getPriceTypeCode());
    newEventTemplatePrice.setPriceTypeId(eventTemplatePrice.getPriceTypeId());
    newEventTemplatePrice.setPriceTypeDescription(eventTemplatePrice.getPriceTypeDescription());
    newEventTemplatePrice.setRateGroupId(eventTemplatePrice.getRateGroupId());
    newEventTemplatePrice.setRateGroupName(eventTemplatePrice.getRateGroupName());
    newEventTemplatePrice.setRateId(eventTemplatePrice.getRateId());
    newEventTemplatePrice.setRateName(eventTemplatePrice.getRateName());

    return newEventTemplatePrice;
  }

  public static EventRatesFeverDTO toEventRatesFeverDTO(EventRatesDTO eventRates){

    if (eventRates == null) {
      return null;
    }


    EventRatesFeverDTO newEventRates = new EventRatesFeverDTO();

    newEventRates.setData(toEventRateFeverDTOList(eventRates.getData()));

    return newEventRates;
  }

  private static List<EventRateFeverDTO> toEventRateFeverDTOList(List<EventRateDTO> eventRates) {

    if (eventRates == null) {
      return null;
    }

    List<EventRateFeverDTO> newEventRates = new ArrayList<>();
    eventRates.forEach(eventRateDTO -> {
      newEventRates.add(toEventRateFeverDTO(eventRateDTO));
    });

    return newEventRates;
  }

  private static EventRateFeverDTO toEventRateFeverDTO(EventRateDTO eventRate) {

    EventRateFeverDTO newEventRate = new EventRateFeverDTO();

    if (eventRate == null) {
      return null;
    }
    newEventRate.setId(eventRate.getId());
    newEventRate.setRateGroup(toRateGroupResponseFeverDTO(eventRate.getRateGroup()));
    newEventRate.setDefaultRate(eventRate.getDefaultRate());
    newEventRate.setDescription(eventRate.getDescription());
    newEventRate.setName(eventRate.getName());
    newEventRate.setRestrictive(eventRate.getRestrictive());
    newEventRate.setTranslations(eventRate.getTranslations());

    return newEventRate;
  }

  private static RateGroupResponseFeverDTO toRateGroupResponseFeverDTO(
      RateGroupResponseDTO rateGroupResponse) {

    RateGroupResponseFeverDTO newRateGroupResponse = new RateGroupResponseFeverDTO();
    if(rateGroupResponse != null) {
    newRateGroupResponse.setId(rateGroupResponse.getId());
    newRateGroupResponse.setName(rateGroupResponse.getName());
    } else {
      return null;
    }
    return newRateGroupResponse;
  }

  public static PriceTypeDTO toPriceTypeDTO(MsPriceTypeDTO msPriceTypeDTO) {
    PriceTypeDTO priceTypeDTO = new PriceTypeDTO();
    priceTypeDTO.setId(msPriceTypeDTO.getId());
    priceTypeDTO.setName(msPriceTypeDTO.getName());
    priceTypeDTO.setColor(msPriceTypeDTO.getColor());
    priceTypeDTO.setDefault(msPriceTypeDTO.getDefault());
    priceTypeDTO.setCode(msPriceTypeDTO.getCode());
    priceTypeDTO.setPriority(msPriceTypeDTO.getPriority());
    if(msPriceTypeDTO.getAdditionalConfig() != null) {
      PriceTypeAdditionalConfigDTO additionalConfig = new PriceTypeAdditionalConfigDTO();
      additionalConfig.setRestrictiveAccess(msPriceTypeDTO.getAdditionalConfig().getRestrictiveAccess());
      additionalConfig.setGateId(msPriceTypeDTO.getAdditionalConfig().getGateId());
      priceTypeDTO.setAdditionalConfig(additionalConfig);
    }

    return priceTypeDTO;
  }

  public static List<PriceTypeWebCommunicationElementDTO> toEventPriceTypeWebCommunicationElements(List<MsPriceTypeWebCommunicationElementDTO> priceTypeCommunicationElements) {
    List<PriceTypeWebCommunicationElementDTO> commElements = new ArrayList<>();
    if (priceTypeCommunicationElements != null && !priceTypeCommunicationElements.isEmpty()){
      priceTypeCommunicationElements.forEach(msCommElement -> {
        commElements.add(toEventPriceTypeWebCommunicationElement(msCommElement));
      });
    }
    return commElements;

  }

  private static PriceTypeWebCommunicationElementDTO toEventPriceTypeWebCommunicationElement(MsPriceTypeWebCommunicationElementDTO msCommElement) {
    PriceTypeWebCommunicationElementDTO commElement = new PriceTypeWebCommunicationElementDTO();
    commElement.setType(msCommElement.getType());
    commElement.setLang(msCommElement.getLang());
    commElement.setValue(msCommElement.getValue());
    return commElement;
  }
}
