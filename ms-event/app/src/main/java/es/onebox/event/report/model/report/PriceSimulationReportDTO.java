package es.onebox.event.report.model.report;

import es.onebox.core.file.exporter.generator.model.ExportableBean;
import es.onebox.event.report.annotation.PriceSimulationFieldBinder;
import es.onebox.event.report.enums.PriceSimulationField;
import java.io.Serial;

public class PriceSimulationReportDTO implements ExportableBean {

    @Serial
    private static final long serialVersionUID = 1L;

    @PriceSimulationFieldBinder(PriceSimulationField.VENUE_CONFIG_ID)
    private Long venueConfigId;
    @PriceSimulationFieldBinder(PriceSimulationField.VENUE_CONFIG_NAME)
    private String venueConfigName;
    @PriceSimulationFieldBinder(PriceSimulationField.RATE_ID)
    private Long rateId;
    @PriceSimulationFieldBinder(PriceSimulationField.RATE_NAME)
    private String rateName;
    @PriceSimulationFieldBinder(PriceSimulationField.RATE_PRICE_TYPE_ID)
    private Long priceTypeId;
    @PriceSimulationFieldBinder(PriceSimulationField.RATE_PRICE_TYPE_NAME)
    private String priceTypeName;
    @PriceSimulationFieldBinder(PriceSimulationField.RATE_PRICE_TYPE_SIMULATION_PRICE_BASE)
    private Double priceBase;
    @PriceSimulationFieldBinder(PriceSimulationField.RATE_PRICE_TYPE_SIMULATION_PRICE_TOTAL)
    private Double priceTotal;
    @PriceSimulationFieldBinder(PriceSimulationField.RATE_PRICE_TYPE_SIMULATION_PRICE_SURCHARGE_VALUE)
    private Double surchargeValue;
    @PriceSimulationFieldBinder(PriceSimulationField.RATE_PRICE_TYPE_SIMULATION_PRICE_SURCHARGE_TYPE)
    private String surchargeType;
    @PriceSimulationFieldBinder(PriceSimulationField.RATE_PRICE_TYPE_SIMULATION_PRICE_PROMOTION_ID)
    private Long promotionId;
    @PriceSimulationFieldBinder(PriceSimulationField.RATE_PRICE_TYPE_SIMULATION_PRICE_PROMOTION_NAME)
    private String promotionName;
    @PriceSimulationFieldBinder(PriceSimulationField.RATE_PRICE_TYPE_SIMULATION_PRICE_PROMOTION_TYPE)
    private String promotionType;

    public Long getVenueConfigId() {
        return venueConfigId;
    }

    public void setVenueConfigId(Long venueConfigId) {
        this.venueConfigId = venueConfigId;
    }

    public String getVenueConfigName() {
        return venueConfigName;
    }

    public void setVenueConfigName(String venueConfigName) {
        this.venueConfigName = venueConfigName;
    }

    public Long getRateId() {
        return rateId;
    }

    public void setRateId(Long rateId) {
        this.rateId = rateId;
    }

    public String getRateName() {
        return rateName;
    }

    public void setRateName(String rateName) {
        this.rateName = rateName;
    }

    public Long getPriceTypeId() {
        return priceTypeId;
    }

    public void setPriceTypeId(Long priceTypeId) {
        this.priceTypeId = priceTypeId;
    }

    public String getPriceTypeName() {
        return priceTypeName;
    }

    public void setPriceTypeName(String priceTypeName) {
        this.priceTypeName = priceTypeName;
    }

    public Double getPriceBase() {
        return priceBase;
    }

    public void setPriceBase(Double priceBase) {
        this.priceBase = priceBase;
    }

    public Double getPriceTotal() {
        return priceTotal;
    }

    public void setPriceTotal(Double priceTotal) {
        this.priceTotal = priceTotal;
    }

    public Double getSurchargeValue() {
        return surchargeValue;
    }

    public void setSurchargeValue(Double surchargeValue) {
        this.surchargeValue = surchargeValue;
    }

    public String getSurchargeType() {
        return surchargeType;
    }

    public void setSurchargeType(String surchargeType) {
        this.surchargeType = surchargeType;
    }

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public String getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(String promotionType) {
        this.promotionType = promotionType;
    }
}
