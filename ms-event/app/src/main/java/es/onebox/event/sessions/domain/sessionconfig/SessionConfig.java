package es.onebox.event.sessions.domain.sessionconfig;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.couchbase.annotations.Id;
import es.onebox.event.common.domain.Restrictions;
import es.onebox.event.secondarymarket.domain.SessionSecondaryMarketDates;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@CouchDocument
public class SessionConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 2762538561564147791L;

    @Id
    private Integer sessionId;
    private Integer maxMembers;
    private Long eventId;
    private IdNameDTO entity;
    private PreSaleConfig preSaleConfig;
    private QueueItConfig  queueItConfig;
    private Restrictions restrictions;
    private List<String> sessionLiterals;
    private StreamingVendorConfig streamingVendorConfig;
    private CustomersLimits customersLimits;
    private List<PriceTypeLimit> priceTypeLimits;
    private SessionPassbookConfig sessionPassbookConfig;
    private SessionRefundConditions sessionRefundConditions;
    private SessionSecondaryMarketDates secondaryMarketDates;
    private SessionPresalesConfig sessionPresalesConfig;
    private SessionDynamicPriceConfig sessionDynamicPriceConfig;
    private SessionExternalConfig sessionExternalConfig;
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

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public PreSaleConfig getPreSaleConfig() {
        return preSaleConfig;
    }

    public void setPreSaleConfig(PreSaleConfig preSaleConfig) {
        this.preSaleConfig = preSaleConfig;
    }

    public QueueItConfig getQueueItConfig() {
        return queueItConfig;
    }

    public void setQueueItConfig(QueueItConfig queueItConfig) {
        this.queueItConfig = queueItConfig;
    }

    public Restrictions getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(Restrictions restrictions) {
        this.restrictions = restrictions;
    }

    public List<String> getSessionLiterals() {
        return sessionLiterals;
    }

    public void setSessionLiterals(List<String> sessionLiterals) {
        this.sessionLiterals = sessionLiterals;
    }

    public StreamingVendorConfig getStreamingVendorConfig() {
        return streamingVendorConfig;
    }

    public void setStreamingVendorConfig(StreamingVendorConfig streamingVendorConfig) {
        this.streamingVendorConfig = streamingVendorConfig;
    }

    public CustomersLimits getCustomersLimits() {
        return customersLimits;
    }

    public void setCustomersLimits(CustomersLimits customersLimits) {
        this.customersLimits = customersLimits;
    }

    public List<PriceTypeLimit> getPriceTypeLimits() {
        return priceTypeLimits;
    }

    public void setPriceTypeLimits(List<PriceTypeLimit> priceTypeLimits) {
        this.priceTypeLimits = priceTypeLimits;
    }

    public SessionPassbookConfig getSessionPassbookConfig() {
        return sessionPassbookConfig;
    }

    public void setSessionPassbookConfig(SessionPassbookConfig sessionPassbookConfig) {
        this.sessionPassbookConfig = sessionPassbookConfig;
    }

    public SessionRefundConditions getSessionRefundConditions() {
        return sessionRefundConditions;
    }

    public void setSessionRefundConditions(SessionRefundConditions sessionRefundConditions) {
        this.sessionRefundConditions = sessionRefundConditions;
    }

    public SessionSecondaryMarketDates getSecondaryMarketDates() {
        return secondaryMarketDates;
    }

    public void setSecondaryMarketDates(SessionSecondaryMarketDates secondaryMarketDates) {
        this.secondaryMarketDates = secondaryMarketDates;
    }

    public SessionPresalesConfig getSessionPresalesConfig() {
        return sessionPresalesConfig;
    }

    public void setSessionPresalesConfig(SessionPresalesConfig sessionPresalesConfig) {
        this.sessionPresalesConfig = sessionPresalesConfig;
    }

    public SessionDynamicPriceConfig getSessionDynamicPriceConfig() {
        return sessionDynamicPriceConfig;
    }

    public void setSessionDynamicPriceConfig(SessionDynamicPriceConfig sessionDynamicPriceConfig) {
        this.sessionDynamicPriceConfig = sessionDynamicPriceConfig;
    }

    public SessionExternalConfig getSessionExternalConfig() {
        return sessionExternalConfig;
    }

    public void setSessionExternalConfig(SessionExternalConfig sessionExternalConfig) {
        this.sessionExternalConfig = sessionExternalConfig;
    }

    public IdNameDTO getEntity() {
        return entity;
    }

    public void setEntity(IdNameDTO entity) {
        this.entity = entity;
    }

    public Boolean getSeasonTicketMultiticket() {
        return seasonTicketMultiticket;
    }

    public void setSeasonTicketMultiticket(Boolean seasonTicketMultiticket) {
        this.seasonTicketMultiticket = seasonTicketMultiticket;
    }
}
