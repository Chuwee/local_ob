package es.onebox.common.datasources.ms.crm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * User: cgalindo
 * Date: 16/10/15
 */
public class CrmOrderDocResponse {

    private String id;

    private String user;

    private String status;

    private String channel;

    private Long channel_id;

    private String channel_url;

    private String delivery_method;

    private Double amount;

    private Double discount_amount;

    private String order_date;

    private String time_zone;

    private Integer products_number;

    private String update_date;

    private Boolean passbook_generated;

    private String language;

    private String invoice_id;

    private Long userB2BId;

    private String userB2BTaxId;

    @JsonProperty("channel_agreements")
    private List<ChannelAgreement> channelAgreements;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getDelivery_method() {
        return delivery_method;
    }

    public void setDelivery_method(String delivery_method) {
        this.delivery_method = delivery_method;
    }

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

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public Integer getProducts_number() {
        return products_number;
    }

    public void setProducts_number(Integer products_number) {
        this.products_number = products_number;
    }

    public String getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(String update_date) {
        this.update_date = update_date;
    }

    public Boolean getPassbook_generated() {
        return passbook_generated;
    }

    public void setPassbook_generated(Boolean passbook_generated) {
        this.passbook_generated = passbook_generated;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getInvoice_id() {
        return invoice_id;
    }

    public void setInvoice_id(String invoice_id) {
        this.invoice_id = invoice_id;
    }

    public Long getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(Long channel_id) {
        this.channel_id = channel_id;
    }

    public String getChannel_url() {
        return channel_url;
    }

    public void setChannel_url(String channel_url) {
        this.channel_url = channel_url;
    }

    public String getTime_zone() {
        return time_zone;
    }

    public void setTime_zone(String time_zone) {
        this.time_zone = time_zone;
    }

    public List<ChannelAgreement> getChannelAgreements() {
        return channelAgreements;
    }

    public void setChannelAgreements(List<ChannelAgreement> channelAgreements) {
        this.channelAgreements = channelAgreements;
    }

    public Long getUserB2BId() {
        return userB2BId;
    }

    public void setUserB2BId(Long userB2BId) {
        this.userB2BId = userB2BId;
    }

    public String getUserB2BTaxId() {
        return userB2BTaxId;
    }

    public void setUserB2BTaxId(String userB2BTaxId) {
        this.userB2BTaxId = userB2BTaxId;
    }
}
