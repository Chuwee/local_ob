package es.onebox.common.datasources.ms.entity.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.time.Duration;

public class RequestEntityDTO extends EntityDTO {

    @Serial
    private static final long serialVersionUID = -6754103796031757463L;

    private String corporateColor;
    private Integer avetClubCode;
    private EntityStreaming streaming;
    private EntityInteractiveVenue interactiveVenue;
    private EntityNotifications notifications;
    private Boolean allowVipViews;
    private Boolean allowTicketHidePrice;
    private Boolean allowExternalInvoiceNotification;
    private EntityCustomization customization;
    private WhatsappConfig whatsappConfig;
    private Boolean allowB2BPublishing;
    private Boolean allowInvitations;
    private Boolean enableV4Configs;
    private Duration sessionDuration;

    public String getCorporateColor() {
        return corporateColor;
    }

    public void setCorporateColor(String corporateColor) {
        this.corporateColor = corporateColor;
    }

    public Integer getAvetClubCode() {
        return avetClubCode;
    }

    public void setAvetClubCode(Integer avetClubCode) {
        this.avetClubCode = avetClubCode;
    }

    public EntityStreaming getStreaming() {
        return streaming;
    }

    public void setStreaming(EntityStreaming streaming) {
        this.streaming = streaming;
    }

    public EntityInteractiveVenue getInteractiveVenue() {
        return interactiveVenue;
    }

    public void setInteractiveVenue(EntityInteractiveVenue interactiveVenue) {
        this.interactiveVenue = interactiveVenue;
    }

    public EntityNotifications getNotifications() {
        return notifications;
    }

    public void setNotifications(EntityNotifications notifications) {
        this.notifications = notifications;
    }

    public Boolean getAllowVipViews() {
        return allowVipViews;
    }

    public void setAllowVipViews(Boolean allowVipViews) {
        this.allowVipViews = allowVipViews;
    }

    public Boolean getAllowTicketHidePrice() {
        return allowTicketHidePrice;
    }

    public void setAllowTicketHidePrice(Boolean allowTicketHidePrice) {
        this.allowTicketHidePrice = allowTicketHidePrice;
    }

    public Boolean getAllowExternalInvoiceNotification() {
        return allowExternalInvoiceNotification;
    }

    public void setAllowExternalInvoiceNotification(Boolean allowExternalInvoiceNotification) {
        this.allowExternalInvoiceNotification = allowExternalInvoiceNotification;
    }

    public EntityCustomization getCustomization() {
        return customization;
    }

    public void setCustomization(EntityCustomization customization) {
        this.customization = customization;
    }

    public WhatsappConfig getWhatsappConfig() {
        return whatsappConfig;
    }

    public void setWhatsappConfig(WhatsappConfig whatsappConfig) {
        this.whatsappConfig = whatsappConfig;
    }

    public Boolean getAllowB2BPublishing() {
        return allowB2BPublishing;
    }

    public void setAllowB2BPublishing(Boolean allowB2BPublishing) {
        this.allowB2BPublishing = allowB2BPublishing;
    }

    public Boolean getAllowInvitations() {
        return allowInvitations;
    }

    public void setAllowInvitations(Boolean allowInvitations) {
        this.allowInvitations = allowInvitations;
    }

    public Boolean getEnableV4Configs() {
        return enableV4Configs;
    }

    public void setEnableV4Configs(Boolean enableV4Configs) {
        this.enableV4Configs = enableV4Configs;
    }

    public Duration getSessionDuration() {
        return sessionDuration;
    }

    public void setSessionDuration(Duration sessionDuration) {
        this.sessionDuration = sessionDuration;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
