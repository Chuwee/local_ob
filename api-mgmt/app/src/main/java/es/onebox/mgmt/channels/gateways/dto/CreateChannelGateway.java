package es.onebox.mgmt.channels.gateways.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CreateChannelGateway implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Valid
    public ChannelGatewayDetailTranslationsDTO translations;
    private String name;
    private String description;
    private Integer attempts;
    private Boolean refund;
    @JsonProperty("show_billing_form")
    private Boolean showBillingForm;
    @JsonProperty("save_card_by_default")
    private Boolean saveCardByDefault;
    @JsonProperty("force_risk_evaluation")
    private Boolean forceRiskEvaluation;
    @JsonProperty("allow_benefits")
    private Boolean allowBenefits;
    @JsonProperty("send_additional_data")
    private Boolean sendAdditionalData;
    @JsonProperty("price_range_enabled")
    private boolean priceRangeEnabled;
    @JsonProperty("price_range")
    private PriceRange priceRange;
    private Boolean live;
    @JsonProperty("field_values")
    private Map<String, String> fieldValues;
    @JsonProperty("currency_codes")
    private List<String> currencies;
    @Valid
    private List<SurchargeDTO> surcharges;
    @Valid
    private List<UpdateTaxInfoDTO> taxes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChannelGatewayDetailTranslationsDTO getTranslations() {
        return translations;
    }

    public void setTranslations(ChannelGatewayDetailTranslationsDTO translations) {
        this.translations = translations;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public Boolean getRefund() {
        return refund;
    }

    public void setRefund(Boolean refund) {
        this.refund = refund;
    }

    public Boolean getShowBillingForm() {
        return showBillingForm;
    }

    public void setShowBillingForm(Boolean showBillingForm) {
        this.showBillingForm = showBillingForm;
    }

    public Boolean getSaveCardByDefault() {
        return saveCardByDefault;
    }

    public void setSaveCardByDefault(Boolean saveCardByDefault) {
        this.saveCardByDefault = saveCardByDefault;
    }

    public Boolean getForceRiskEvaluation() {
        return forceRiskEvaluation;
    }

    public void setForceRiskEvaluation(Boolean forceRiskEvaluation) {
        this.forceRiskEvaluation = forceRiskEvaluation;
    }

    public Boolean getAllowBenefits() {
        return allowBenefits;
    }

    public void setAllowBenefits(Boolean allowBenefits) {
        this.allowBenefits = allowBenefits;
    }

    public Boolean getSendAdditionalData() {
        return sendAdditionalData;
    }

    public void setSendAdditionalData(Boolean sendAdditionalData) {
        this.sendAdditionalData = sendAdditionalData;
    }

    public boolean getPriceRangeEnabled() {
        return priceRangeEnabled;
    }

    public void setPriceRangeEnabled(boolean priceRangeEnabled) {
        this.priceRangeEnabled = priceRangeEnabled;
    }

    public PriceRange getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(PriceRange priceRange) {
        this.priceRange = priceRange;
    }

    public Boolean getLive() {
        return live;
    }

    public void setLive(Boolean live) {
        this.live = live;
    }

    public Map<String, String> getFieldValues() {
        return fieldValues;
    }

    public void setFieldValues(Map<String, String> fieldValues) {
        this.fieldValues = fieldValues;
    }

    public List<String> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<String> currencies) {
        this.currencies = currencies;
    }

    public List<SurchargeDTO> getSurcharges() {
        return surcharges;
    }

    public void setSurcharges(List<SurchargeDTO> surcharges) {
        this.surcharges = surcharges;
    }

    public List<UpdateTaxInfoDTO> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<UpdateTaxInfoDTO> taxes) {
        this.taxes = taxes;
    }
}
