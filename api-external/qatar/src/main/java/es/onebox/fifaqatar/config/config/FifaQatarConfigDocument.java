package es.onebox.fifaqatar.config.config;

import es.onebox.couchbase.annotations.CouchDocument;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@CouchDocument
public class FifaQatarConfigDocument implements Serializable {

    /*
     PRE
     ----
     user - https://panel.oneboxtds.net/users/5255219/register-data/security
     entity - https://panel.oneboxtds.net/entities/20226/general-data/principal-info
     */

    @Serial
    private static final long serialVersionUID = -7097216806997251882L;
    
    private String apiKey;
    private Integer entityId;
    private String barcodeSigningKey;
    private String barcodeUrlHost;
    private String accountProfileUrl;
    private String accountTicketsUrl;
    private String accountTicketsTransferUrl;
    private Boolean secMktEnabled;
    private String accountSecMktUrl;
    private DeliverySettings deliverySettings;
    private List<Long> blacklistedEventIds;
    private List<Long> blacklistedSessionIds;
    private Integer maxBarcodesByTicketDetail;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getBarcodeSigningKey() {
        return barcodeSigningKey;
    }

    public void setBarcodeSigningKey(String barcodeSigningKey) {
        this.barcodeSigningKey = barcodeSigningKey;
    }

    public String getBarcodeUrlHost() {
        return barcodeUrlHost;
    }

    public void setBarcodeUrlHost(String barcodeUrlHost) {
        this.barcodeUrlHost = barcodeUrlHost;
    }

    public String getAccountProfileUrl() {
        return accountProfileUrl;
    }

    public void setAccountProfileUrl(String accountProfileUrl) {
        this.accountProfileUrl = accountProfileUrl;
    }

    public String getAccountTicketsUrl() {
        return accountTicketsUrl;
    }

    public void setAccountTicketsUrl(String accountTicketsUrl) {
        this.accountTicketsUrl = accountTicketsUrl;
    }

    public DeliverySettings getDeliverySettings() {
        return deliverySettings;
    }

    public void setDeliverySettings(DeliverySettings deliverySettings) {
        this.deliverySettings = deliverySettings;
    }

    public List<Long> getBlacklistedEventIds() {
        return blacklistedEventIds;
    }

    public void setBlacklistedEventIds(List<Long> blacklistedEventIds) {
        this.blacklistedEventIds = blacklistedEventIds;
    }

    public Integer getMaxBarcodesByTicketDetail() {
        return maxBarcodesByTicketDetail;
    }

    public void setMaxBarcodesByTicketDetail(Integer maxBarcodesByTicketDetail) {
        this.maxBarcodesByTicketDetail = maxBarcodesByTicketDetail;
    }

    public List<Long> getBlacklistedSessionIds() {
        return blacklistedSessionIds;
    }

    public void setBlacklistedSessionIds(List<Long> blacklistedSessionIds) {
        this.blacklistedSessionIds = blacklistedSessionIds;
    }

    public Boolean getSecMktEnabled() {
        return secMktEnabled;
    }

    public void setSecMktEnabled(Boolean secMktEnabled) {
        this.secMktEnabled = secMktEnabled;
    }

    public String getAccountSecMktUrl() {
        return accountSecMktUrl;
    }

    public void setAccountSecMktUrl(String accountSecMktUrl) {
        this.accountSecMktUrl = accountSecMktUrl;
    }

    public String getAccountTicketsTransferUrl() {
        return accountTicketsTransferUrl;
    }

    public void setAccountTicketsTransferUrl(String accountTicketsTransferUrl) {
        this.accountTicketsTransferUrl = accountTicketsTransferUrl;
    }
}
