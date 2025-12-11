package es.onebox.mgmt.datasources.ms.payment.dto;


import es.onebox.mgmt.channels.gateways.dto.PriceRange;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChannelGatewayConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer channelId;
    private String gatewaySid;
    private String confSid;
    private String internalName;
    private Map<String, String> name = new HashMap<>();
    private Map<String, String> subtitle = new HashMap<>();
    private String description;
    private Integer attempts;
    private boolean refund;
    private boolean showBillingForm;
    private boolean saveCardByDefault;
    private boolean forceRiskEvaluation;
    private boolean allowBenefits;
    private boolean active;
    private boolean byDefault;
    private boolean sendAdditionalData;
    private boolean priceRangeEnabled;
    private PriceRange priceRange;
    private boolean live;
    private Map<String, String> fieldsValues = new HashMap<>();
    private List<String> currencies;
    private List<Surcharge> surcharges;
    private List<TaxInfo> taxes;


    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getGatewaySid() {
        return gatewaySid;
    }

    public void setGatewaySid(String gatewaySid) {
        this.gatewaySid = gatewaySid;
    }

    public String getConfSid() {
        return confSid;
    }

    public void setConfSid(String confSid) {
        this.confSid = confSid;
    }

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    public Map<String, String> getName() {
        return name;
    }

    public void setName(Map<String, String> name) {
        this.name = name;
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

    public boolean isAllowBenefits() {
        return allowBenefits;
    }

    public void setAllowBenefits(boolean allowBenefits) {
        this.allowBenefits = allowBenefits;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isByDefault() {
        return byDefault;
    }

    public void setByDefault(boolean byDefault) {
        this.byDefault = byDefault;
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

    public PriceRange getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(PriceRange priceRange) {
        this.priceRange = priceRange;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public Map<String, String> getFieldsValues() {
        return fieldsValues;
    }

    public void setFieldsValues(Map<String, String> fieldsValues) {
        this.fieldsValues = fieldsValues;
    }

    public Map<String, String> getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(Map<String, String> subtitle) {
        this.subtitle = subtitle;
    }

    public List<String> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<String> currencies) {
        this.currencies = currencies;
    }

    public List<Surcharge> getSurcharges() {
        return surcharges;
    }

    public void setSurcharges(List<Surcharge> surcharges) {
        this.surcharges = surcharges;
    }

    public List<TaxInfo> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<TaxInfo> taxes) {
        this.taxes = taxes;
    }
}
