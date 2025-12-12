package es.onebox.common.datasources.webhook.dto.fever;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import es.onebox.common.datasources.webhook.dto.fever.event.EventChannelSurchargesFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.event.EventCommunicationElementFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.event.EventRatesFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.event.EventTemplatePriceFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.event.PriceTypeDTO;
import es.onebox.common.datasources.webhook.dto.fever.event.PriceTypeWebCommunicationElementDTO;
import es.onebox.common.datasources.webhook.dto.fever.event.SurchargesFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.event.CommunicationElementFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.event.EventDetail;
import es.onebox.common.datasources.webhook.dto.fever.promotion.EventPromotionPriceTypesFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.promotion.PromotionChannelsFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.promotion.PromotionEventSessionsFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.promotion.PromotionRatesFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.promotion.PromotionDetailFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.session.SessionDetail;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public class EventUpdate {

  private PromotionDetailFeverDTO promotionDetails;
  private PromotionChannelsFeverDTO promotionChannels;
  private PromotionEventSessionsFeverDTO promotionSessions;
  private EventPromotionPriceTypesFeverDTO promotionPriceTypes;
  private PromotionRatesFeverDTO promotionRates;
  private List<CommunicationElementFeverDTO> promotionChannelDetails;
  private SessionDetail session;
  private EventDetail eventDetails;
  private List<EventCommunicationElementFeverDTO> eventCommunicationElements;
  private List<EventChannelSurchargesFeverDTO> eventChannelSurcharges;
  private List<SurchargesFeverDTO> eventSurcharges;
  private List<EventTemplatePriceFeverDTO> eventTemplatePrices;
  private EventRatesFeverDTO eventRateDetails;
  private List<PriceTypeDTO> eventPriceTypeDetails;
  private List<PriceTypeWebCommunicationElementDTO> eventPriceTypeCommunicationElements;

  public EventUpdate() {}

  public PromotionDetailFeverDTO getPromotionDetails() {
    return promotionDetails;
  }

  public void setPromotionDetails(
      PromotionDetailFeverDTO promotionDetails) {
    this.promotionDetails = promotionDetails;
  }

  public PromotionChannelsFeverDTO getPromotionChannels() {
    return promotionChannels;
  }

  public void setPromotionChannels(
      PromotionChannelsFeverDTO promotionChannelsFeverDTO) {
    this.promotionChannels = promotionChannelsFeverDTO;
  }

  public PromotionEventSessionsFeverDTO getPromotionSessions() {
    return promotionSessions;
  }

  public void setPromotionSessions(
      PromotionEventSessionsFeverDTO promotionSessions) {
    this.promotionSessions = promotionSessions;
  }

  public EventPromotionPriceTypesFeverDTO getPromotionPriceTypes() {
    return promotionPriceTypes;
  }

  public void setPromotionPriceTypes(
      EventPromotionPriceTypesFeverDTO promotionPriceTypes) {
    this.promotionPriceTypes = promotionPriceTypes;
  }

  public PromotionRatesFeverDTO getPromotionRates() {
    return promotionRates;
  }

  public void setPromotionRates(
      PromotionRatesFeverDTO promotionRates) {
    this.promotionRates = promotionRates;
  }

  public List<CommunicationElementFeverDTO> getPromotionChannelDetails() {
    return promotionChannelDetails;
  }

  public void setPromotionChannelDetails(
      List<CommunicationElementFeverDTO> promotionChannelDetails) {
    this.promotionChannelDetails = promotionChannelDetails;
  }

  public SessionDetail getSession() {
    return session;
  }

  public void setSession(SessionDetail session) {
    this.session = session;
  }

  public List<EventCommunicationElementFeverDTO> getEventCommunicationElements() {
    return eventCommunicationElements;
  }

  public void setEventCommunicationElements(
      List<EventCommunicationElementFeverDTO> eventCommunicationElements) {
    this.eventCommunicationElements = eventCommunicationElements;
  }

  public EventDetail getEventDetails() {
    return eventDetails;
  }

  public void setEventDetails(EventDetail eventDetails) {
    this.eventDetails = eventDetails;
  }

  public List<EventChannelSurchargesFeverDTO> getEventChannelSurcharges() {
    return eventChannelSurcharges;
  }

  public void setEventChannelSurcharges(
      List<EventChannelSurchargesFeverDTO> eventChannelSurcharges) {
    this.eventChannelSurcharges = eventChannelSurcharges;
  }

  public List<SurchargesFeverDTO> getEventSurcharges() {
    return eventSurcharges;
  }

  public void setEventSurcharges(
      List<SurchargesFeverDTO> eventSurcharges) {
    this.eventSurcharges = eventSurcharges;
  }

  public List<EventTemplatePriceFeverDTO> getEventTemplatePrices() {
    return eventTemplatePrices;
  }

  public void setEventTemplatePrices(
      List<EventTemplatePriceFeverDTO> eventTemplatePrices) {
    this.eventTemplatePrices = eventTemplatePrices;
  }

  public EventRatesFeverDTO getEventRateDetails() {
    return eventRateDetails;
  }

  public void setEventRateDetails(EventRatesFeverDTO eventRateDetails) {
    this.eventRateDetails = eventRateDetails;
  }

  public List<PriceTypeDTO> getEventPriceTypeDetails() {
    return eventPriceTypeDetails;
  }

  public void setEventPriceTypeDetails(List<PriceTypeDTO> eventPriceTypeDetails) {
    this.eventPriceTypeDetails = eventPriceTypeDetails;
  }

  public List<PriceTypeWebCommunicationElementDTO> getEventPriceTypeCommunicationElements() {
    return eventPriceTypeCommunicationElements;
  }

  public void setEventPriceTypeCommunicationElements(List<PriceTypeWebCommunicationElementDTO> eventPriceTypeCommunicationElements) {
    this.eventPriceTypeCommunicationElements = eventPriceTypeCommunicationElements;
  }
}
