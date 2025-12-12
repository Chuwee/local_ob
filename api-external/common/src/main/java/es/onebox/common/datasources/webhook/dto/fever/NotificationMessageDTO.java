package es.onebox.common.datasources.webhook.dto.fever;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class NotificationMessageDTO {


  private String id;

  private Long eventId;
  private Long sessionId;
  private Long promotionId;
  private Long channelId;
  private Long templateId;
  private Long rateId;
  private Boolean promotionActive;
  private Long priceTypeId;

  private String code;
  private String url;
  @JsonProperty("previous_code")
  private String prevOrderCode;
  private Boolean reimbursement;
  @JsonProperty("print_status")
  private String printStatus;


  private String name;
  private String surname;
  private String email;
  @JsonProperty("session_ids")
  private List<Integer> sessionIds;
  @JsonProperty("allow_commercial_mailing")
  private Boolean allowCommercialMailing;


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Long getEventId() {
    return eventId;
  }

  public void setEventId(Long eventId) {
    this.eventId = eventId;
  }


  public Long getSessionId() {
    return sessionId;
  }

  public void setSessionId(Long sessionId) {
    this.sessionId = sessionId;
  }

  public Long getPromotionId() {
    return promotionId;
  }

  public void setPromotionId(Long promotionId) {
    this.promotionId = promotionId;
  }

  public Boolean getPromotionActive() {
    return promotionActive;
  }

  public void setPromotionActive(Boolean promotionActive) {
    this.promotionActive = promotionActive;
  }

  public Long getPriceTypeId() {
    return priceTypeId;
  }

  public void setPriceTypeId(Long priceTypeId) {
    this.priceTypeId = priceTypeId;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getPrevOrderCode() {
    return prevOrderCode;
  }

  public void setPrevOrderCode(String prevOrderCode) {
    this.prevOrderCode = prevOrderCode;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public List<Integer> getSessionIds() {
    return sessionIds;
  }

  public void setSessionIds(List<Integer> sessionIds) {
    this.sessionIds = sessionIds;
  }

  public Boolean getAllowCommercialMailing() {
    return allowCommercialMailing;
  }

  public void setAllowCommercialMailing(Boolean allowCommercialMailing) {
    this.allowCommercialMailing = allowCommercialMailing;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Boolean getReimbursement() {
    return reimbursement;
  }

  public void setReimbursement(Boolean reimbursement) {
    this.reimbursement = reimbursement;
  }

  public Long getChannelId() {
    return channelId;
  }

  public void setChannelId(Long channelId) {
    this.channelId = channelId;
  }

  public Long getTemplateId() {
    return templateId;
  }

  public void setTemplateId(Long templateId) {
    this.templateId = templateId;
  }

  public Long getRateId() {
    return rateId;
  }

  public void setRateId(Long rateId) {
    this.rateId = rateId;
  }

  public String getPrintStatus() {
    return printStatus;
  }

  public void setPrintStatus(String printStatus) {
    this.printStatus = printStatus;
  }
}
