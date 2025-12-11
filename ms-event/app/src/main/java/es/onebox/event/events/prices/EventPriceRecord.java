/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.onebox.event.events.prices;

import es.onebox.event.events.enums.PriceType;
import es.onebox.event.sessions.dto.DynamicPriceTranslationDTO;

import java.util.List;

/**
 * @author ignasi
 */
public class EventPriceRecord {

    private Integer eventId;
    private Integer venueConfigId;
    private String venueConfigName;
    private Integer priceZoneId;
    private String priceZoneCode;
    private String priceZoneDescription;
    private Long priceZonePriority;
    private String priceZoneColor;
    private Byte priceZoneRestrictiveAccess;
    private Integer rateId;
    private String rateName;
    private Boolean rateDefault;
    private Double price;
    private List<DynamicPriceTranslationDTO> dynamicPriceTranslations;
    private PriceType priceType;
    private Integer rateGroupId;
    private String rateGroupName;

    public PriceType getPriceType() {
        return priceType;
    }

    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getVenueConfigId() {
        return venueConfigId;
    }

    public void setVenueConfigId(Integer venueConfigId) {
        this.venueConfigId = venueConfigId;
    }

    public String getVenueConfigName() {
        return venueConfigName;
    }

    public void setVenueConfigName(String venueConfigName) {
        this.venueConfigName = venueConfigName;
    }

    public Integer getPriceZoneId() {
        return priceZoneId;
    }

    public void setPriceZoneId(Integer priceZoneId) {
        this.priceZoneId = priceZoneId;
    }

    public String getPriceZoneCode() {
        return priceZoneCode;
    }

    public void setPriceZoneCode(String priceZoneCode) {
        this.priceZoneCode = priceZoneCode;
    }

    public String getPriceZoneDescription() {
        return priceZoneDescription;
    }

    public void setPriceZoneDescription(String priceZoneDescription) {
        this.priceZoneDescription = priceZoneDescription;
    }

    public Integer getRateId() {
        return rateId;
    }

    public void setRateId(Integer rateId) {
        this.rateId = rateId;
    }

    public String getRateName() {
        return rateName;
    }

    public void setRateName(String rateName) {
        this.rateName = rateName;
    }

    public Boolean getRateDefault() {
        return rateDefault;
    }

    public void setRateDefault(Boolean rateDefault) {
        this.rateDefault = rateDefault;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getPriceZonePriority() {
        return priceZonePriority;
    }

    public void setPriceZonePriority(Long priceZonePriority) {
        this.priceZonePriority = priceZonePriority;
    }

    public String getPriceZoneColor() {
        return priceZoneColor;
    }

    public void setPriceZoneColor(String priceZoneColor) {
        this.priceZoneColor = priceZoneColor;
    }

    public Byte getPriceZoneRestrictiveAccess() {
        return priceZoneRestrictiveAccess;
    }

    public void setPriceZoneRestrictiveAccess(Byte priceZoneRestrictiveAccess) {
        this.priceZoneRestrictiveAccess = priceZoneRestrictiveAccess;
    }

    public Integer getRateGroupId() {
        return rateGroupId;
    }

    public void setRateGroupId(Integer rateGroupId) {
        this.rateGroupId = rateGroupId;
    }

    public String getRateGroupName() {
        return rateGroupName;
    }

    public void setRateGroupName(String rateGroupName) {
        this.rateGroupName = rateGroupName;
    }

    public List<DynamicPriceTranslationDTO> getDynamicPriceTranslations() {
        return dynamicPriceTranslations;
    }

    public void setDynamicPriceTranslations(List<DynamicPriceTranslationDTO> dynamicPriceTranslations) {
        this.dynamicPriceTranslations = dynamicPriceTranslations;
    }
}
