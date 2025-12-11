package es.onebox.mgmt.gateways.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class GatewayConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String sid;
    private String name;
    private boolean retry;
    @JsonProperty("max_attempts")
    private Integer maxAttempts;
    private boolean refund;
    @JsonProperty("show_billing_form")
    private boolean showBillingForm;
    @JsonProperty("save_card_by_default")
    private boolean saveCardByDefault;
    @JsonProperty("force_risk_evaluation")
    private boolean forceRiskEvaluation;
    @JsonProperty("send_additional_data")
    private boolean sendAdditionalData;
    @JsonProperty("price_range_enabled")
    private boolean priceRangeEnabled;
    @JsonProperty("allow_benefits")
    private boolean allowBenefits;
    private boolean live;
    private List<String> fields = Collections.emptyList();
    private Boolean wallet;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRetry() {
        return retry;
    }

    public void setRetry(boolean retry) {
        this.retry = retry;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public boolean isRefund() {
        return refund;
    }

    public void setRefund(boolean refund) {
        this.refund = refund;
    }

    public boolean isShowBillingForm() {
        return showBillingForm;
    }

    public void setShowBillingForm(boolean showBillingForm) {
        this.showBillingForm = showBillingForm;
    }

    public boolean isSaveCardByDefault() {
        return saveCardByDefault;
    }

    public void setSaveCardByDefault(boolean saveCardByDefault) {
        this.saveCardByDefault = saveCardByDefault;
    }

    public boolean isForceRiskEvaluation() {
        return forceRiskEvaluation;
    }

    public void setForceRiskEvaluation(boolean forceRiskEvaluation) {
        this.forceRiskEvaluation = forceRiskEvaluation;
    }

    public boolean isSendAdditionalData() {
        return sendAdditionalData;
    }

    public void setSendAdditionalData(boolean sendAdditionalData) {
        this.sendAdditionalData = sendAdditionalData;
    }

    public boolean isPriceRangeEnabled() {
        return priceRangeEnabled;
    }

    public void setPriceRangeEnabled(boolean priceRangeEnabled) {
        this.priceRangeEnabled = priceRangeEnabled;
    }

    public boolean isAllowBenefits() {
        return allowBenefits;
    }

    public void setAllowBenefits(boolean allowBenefits) {
        this.allowBenefits = allowBenefits;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public Boolean getWallet() {
        return wallet;
    }

    public void setWallet(Boolean wallet) {
        this.wallet = wallet;
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
