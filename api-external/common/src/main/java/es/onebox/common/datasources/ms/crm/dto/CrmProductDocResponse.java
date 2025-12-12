package es.onebox.common.datasources.ms.crm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.ms.order.dto.EventType;

/**
 * User: cgalindo
 * Date: 16/10/15
 */
public class CrmProductDocResponse {

    private Integer id;
    private String sku;
    private String description;
    private String event_description;
    @JsonProperty("event_type")
    private EventType eventType;
    private Integer taxonomy_id;
    private String taxonomy_code;
    private String taxonomy_description;
    private Integer event;
    private Integer session;
    private Integer pack;
    private Double amount;
    private Double discount_amount;
    private Boolean passbook_generated;
    private String update_date;
    @JsonProperty("collective_id")
    private Integer collectiveId;
    @JsonProperty("collective_key")
    private String collectiveKey;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTaxonomy_description() {
        return taxonomy_description;
    }

    public void setTaxonomy_description(String taxonomy_description) {
        this.taxonomy_description = taxonomy_description;
    }

    public String getEvent_description() {
        return event_description;
    }

    public void setEvent_description(String event_description) {
        this.event_description = event_description;
    }

    public Integer getTaxonomy_id() {
        return taxonomy_id;
    }

    public void setTaxonomy_id(Integer taxonomy_id) {
        this.taxonomy_id = taxonomy_id;
    }

    public String getTaxonomy_code() {
        return taxonomy_code;
    }

    public void setTaxonomy_code(String taxonomy_code) {
        this.taxonomy_code = taxonomy_code;
    }

    public Integer getEvent() {
        return event;
    }

    public void setEvent(Integer event) {
        this.event = event;
    }

    public Integer getSession() {
        return session;
    }

    public void setSession(Integer session) {
        this.session = session;
    }

    public Integer getPack() { return pack; }

    public void setPack(Integer pack) { this.pack = pack; }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(Double discount_amount) {
        this.discount_amount = discount_amount;
    }

    public Boolean getPassbook_generated() {
        return passbook_generated;
    }

    public void setPassbook_generated(Boolean passbook_generated) {
        this.passbook_generated = passbook_generated;
    }

    public String getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(String update_date) {
        this.update_date = update_date;
    }

    public Integer getCollectiveId() {
        return collectiveId;
    }

    public void setCollectiveId(Integer collectiveId) {
        this.collectiveId = collectiveId;
    }

    public String getCollectiveKey() {
        return collectiveKey;
    }

    public void setCollectiveKey(String collectiveKey) {
        this.collectiveKey = collectiveKey;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}
