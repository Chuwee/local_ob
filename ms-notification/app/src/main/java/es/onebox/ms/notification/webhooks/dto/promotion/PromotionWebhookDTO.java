package es.onebox.ms.notification.webhooks.dto.promotion;

import java.io.Serial;
import java.io.Serializable;

public class PromotionWebhookDTO implements Serializable {

  @Serial
  private static final long serialVersionUID = 3L;

  private PromotionDetailDTO promotionDetails;
  private UpdateEventPromotionChannelsDTO eventPromotionChannelsDTO;
  private UpdatePromotionSessionsDTO eventPromotionSessionsDTO;
  private UpdateEventPromotionPriceTypesDTO eventPromotionPriceTypesDTO;
  private UpdateEventPromotionRatesDTO eventPromotionRatesDTO;
  private Boolean promotionDeleted;


  public PromotionWebhookDTO(PromotionDetailDTO promotionDetails,
      UpdateEventPromotionChannelsDTO eventPromotionChannelsDTO,
      UpdatePromotionSessionsDTO eventPromotionSessionsDTO,
      UpdateEventPromotionPriceTypesDTO eventPromotionPriceTypesDTO,
      UpdateEventPromotionRatesDTO eventPromotionRatesDTO, Boolean promotionDeleted) {
    this.promotionDetails = promotionDetails;
    this.eventPromotionChannelsDTO = eventPromotionChannelsDTO;
    this.eventPromotionSessionsDTO = eventPromotionSessionsDTO;
    this.eventPromotionPriceTypesDTO = eventPromotionPriceTypesDTO;
    this.eventPromotionRatesDTO = eventPromotionRatesDTO;
    this.promotionDeleted = promotionDeleted;
  }

  public PromotionWebhookDTO() {
  }

  public PromotionDetailDTO getPromotionDetails() {
    return promotionDetails;
  }

  public void setPromotionDetails(PromotionDetailDTO promotionDetails) {
    this.promotionDetails = promotionDetails;
  }

  public UpdateEventPromotionChannelsDTO getEventPromotionChannelsDTO() {
    return eventPromotionChannelsDTO;
  }

  public void setEventPromotionChannelsDTO(
      UpdateEventPromotionChannelsDTO eventPromotionChannelsDTO) {
    this.eventPromotionChannelsDTO = eventPromotionChannelsDTO;
  }

  public UpdatePromotionSessionsDTO getEventPromotionSessionsDTO() {
    return eventPromotionSessionsDTO;
  }

  public void setEventPromotionSessionsDTO(
      UpdatePromotionSessionsDTO promotionSessionsDTO) {
    this.eventPromotionSessionsDTO = promotionSessionsDTO;
  }

  public UpdateEventPromotionPriceTypesDTO getEventPromotionPriceTypesDTO() {
    return eventPromotionPriceTypesDTO;
  }

  public void setEventPromotionPriceTypesDTO(
      UpdateEventPromotionPriceTypesDTO eventPromotionPriceTypesDTO) {
    this.eventPromotionPriceTypesDTO = eventPromotionPriceTypesDTO;
  }

  public UpdateEventPromotionRatesDTO getEventPromotionRatesDTO() {
    return eventPromotionRatesDTO;
  }

  public void setEventPromotionRatesDTO(
      UpdateEventPromotionRatesDTO eventPromotionRatesDTO) {
    this.eventPromotionRatesDTO = eventPromotionRatesDTO;
  }

  public Boolean isPromotionDeleted() {
    return promotionDeleted;
  }

  public void setPromotionDeleted(Boolean promotionDeleted) {
    this.promotionDeleted = promotionDeleted;
  }
}
