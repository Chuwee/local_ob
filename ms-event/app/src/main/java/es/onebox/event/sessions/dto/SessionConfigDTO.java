package es.onebox.event.sessions.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.sessions.dto.external.SessionExternalConfigDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SessionConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7586561883336293156L;

    private Integer sessionId;
    private IdNameDTO entity;
    private Integer maxMembers;
    private RestrictionsDTO restrictions;
    private SessionConfigRefundConditionsDTO sessionRefundConditions;
    private PreSaleConfigDTO preSaleConfig;
    private QueueItConfigDTO queueItConfig;
    private StreamingVendorConfigDTO streamingVendorConfig;
    private SessionPresalesConfigDTO sessionPresalesConfig;
    private SessionDynamicPriceConfigDTO sessionDynamicPriceConfigDTO;
    private SessionExternalConfigDTO externalConfig;
    private List<PriceTypeLimitDTO> priceTypeLimits;
    private CustomersLimitsDTO customersLimits;
    private Boolean seasonTicketMultiticket;

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(Integer maxMembers) {
        this.maxMembers = maxMembers;
    }

    public RestrictionsDTO getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(RestrictionsDTO restrictions) {
        this.restrictions = restrictions;
    }

    public SessionConfigRefundConditionsDTO getSessionRefundConditions() {
        return sessionRefundConditions;
    }

    public void setSessionRefundConditions(SessionConfigRefundConditionsDTO sessionRefundConditions) {
        this.sessionRefundConditions = sessionRefundConditions;
    }

    public PreSaleConfigDTO getPreSaleConfig() {
        return preSaleConfig;
    }

    public void setPreSaleConfig(PreSaleConfigDTO preSaleConfig) {
        this.preSaleConfig = preSaleConfig;
    }

    public QueueItConfigDTO getQueueItConfig() {
        return queueItConfig;
    }

    public void setQueueItConfig(QueueItConfigDTO queueItConfig) {
        this.queueItConfig = queueItConfig;
    }

    public StreamingVendorConfigDTO getStreamingVendorConfig() {
        return streamingVendorConfig;
    }

    public void setStreamingVendorConfig(StreamingVendorConfigDTO streamingVendorConfig) {
        this.streamingVendorConfig = streamingVendorConfig;
    }

    public SessionPresalesConfigDTO getSessionPresalesConfig() {return sessionPresalesConfig;}

    public void setSessionPresalesConfig(SessionPresalesConfigDTO sessionPresalesConfig) {this.sessionPresalesConfig = sessionPresalesConfig;}

    public SessionDynamicPriceConfigDTO getSessionDynamicPriceConfigDTO() {
        return sessionDynamicPriceConfigDTO;
    }

    public void setSessionDynamicPriceConfigDTO(SessionDynamicPriceConfigDTO sessionDynamicPriceConfigDTO) {
        this.sessionDynamicPriceConfigDTO = sessionDynamicPriceConfigDTO;
    }

    public SessionExternalConfigDTO getExternalConfig() {
        return externalConfig;
    }

    public void setExternalConfig(SessionExternalConfigDTO externalConfig) {
        this.externalConfig = externalConfig;
    }

    public IdNameDTO getEntity() {
        return entity;
    }

    public void setEntity(IdNameDTO entity) {
        this.entity = entity;
    }

    public List<PriceTypeLimitDTO> getPriceTypeLimits() {
        return priceTypeLimits;
    }

    public void setPriceTypeLimits(List<PriceTypeLimitDTO> priceTypeLimits) {
        this.priceTypeLimits = priceTypeLimits;
    }

    public CustomersLimitsDTO getCustomersLimits() { return customersLimits; }

    public void setCustomersLimits(CustomersLimitsDTO customersLimits) { this.customersLimits = customersLimits; }

    public Boolean getSeasonTicketMultiticket() {
        return seasonTicketMultiticket;
    }

    public void setSeasonTicketMultiticket(Boolean seasonTicketMultiticket) {
        this.seasonTicketMultiticket = seasonTicketMultiticket;
    }
}
