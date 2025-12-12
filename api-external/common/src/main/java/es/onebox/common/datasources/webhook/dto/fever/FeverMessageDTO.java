package es.onebox.common.datasources.webhook.dto.fever;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;
import java.util.Map;

@JsonNaming(SnakeCaseStrategy.class)
public class FeverMessageDTO {

  @JsonProperty("onebox_id")
  private String id;

  private Long eventId;
  private EventUpdate eventUpdate;
  private ProductUpdate productUpdate;
  private OrderDetailDTO orderDetail;
  private EntityDetailDTO entityDetail;
  private UserDetailDTO userDetail;

  @JsonProperty("print_detail")
  private OrderPrintDetailDTO orderPrintDetail;
  private Long sessionId;
  private Long promotionId;
  private Long channelId;
  private Long templateId;
  private Long rateId;
  private Boolean promotionActive;

  private String code;
  private String url;
  @JsonProperty("previous_code")
  private String prevOrderCode;
  private Boolean reimbursement;

  private String name;
  private String surname;
  private String email;
  @JsonProperty("session_ids")
  private List<Integer> sessionIds;
  @JsonProperty("allow_commercial_mailing")
  private Boolean allowCommercialMailing;
  @JsonProperty("required_events")
  private Map<Integer, List<Integer>> requiredEvents;

  @JsonProperty("channel_form")
  private ChannelFormDetailDTO channelFormDetailDTO;

  @JsonProperty("sale_request_status")
  private SaleRequestStatusDTO saleRequestStatusDTO;

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

  public EventUpdate getEventUpdate() {
    return eventUpdate;
  }

  public void setEventUpdate(EventUpdate eventUpdate) {
    this.eventUpdate = eventUpdate;
  }

  public ProductUpdate getProductUpdate() {
    return productUpdate;
  }

  public void setProductUpdate(ProductUpdate productUpdate) {
    this.productUpdate = productUpdate;
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

  public OrderDetailDTO getOrderDetail() {
    return orderDetail;
  }

  public void setOrderDetail(OrderDetailDTO orderDetail) {
    this.orderDetail = orderDetail;
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

  public OrderPrintDetailDTO getOrderPrintDetail() {
    return orderPrintDetail;
  }

  public void setOrderPrintDetail(
      OrderPrintDetailDTO orderPrintDetail) {
    this.orderPrintDetail = orderPrintDetail;
  }

  public ChannelFormDetailDTO getChannelFormDetailDTO() {
    return channelFormDetailDTO;
  }

  public void setChannelFormDetailDTO(ChannelFormDetailDTO channelFormDetailDTO) {
    this.channelFormDetailDTO = channelFormDetailDTO;
  }


  public SaleRequestStatusDTO getSaleRequestStatusDTO() {
    return saleRequestStatusDTO;
  }

  public void setSaleRequestStatusDTO(SaleRequestStatusDTO saleRequestStatusDTO) {
    this.saleRequestStatusDTO = saleRequestStatusDTO;
  }

  public EntityDetailDTO getEntityDetail() {
    return entityDetail;
  }

  public void setEntityDetail(EntityDetailDTO entityDetail) {
    this.entityDetail = entityDetail;
  }

  public UserDetailDTO getUserDetail() {
    return userDetail;
  }

  public void setUserDetail(UserDetailDTO userDetail) {
    this.userDetail = userDetail;
  }

  public Map<Integer, List<Integer>> getRequiredEvents() { return requiredEvents; }

  public void setRequiredEvents(Map<Integer, List<Integer>> requiredEvents) { this.requiredEvents = requiredEvents; }
}
