package es.onebox.mgmt.datasources.ms.payment.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class GatewayConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String sid;
    private String name;
    private String description;
    private String paymentURL;
    private boolean retry;
    private Integer maxAttempts;
    private boolean refund;
    private boolean showBillingForm;
    private boolean saveCardByDefault;
    private boolean forceRiskEvaluation;
    private String refundURL;
    private String javaClass;
    private boolean sync;
    private boolean notifyClient;
    private String acknowledgementURL;
    private Map<String, String> customProperties;
    private Boolean shouldUseIframe;
    private Boolean retryRefund;
    private Boolean allowBankTransfer;
    private String informationURL;
    private boolean sendAdditionalData;
    private boolean priceRangeEnabled;
    private boolean live;
    private List<String> mandatoryFormFields;
    private Boolean wallet;
    private Boolean allowBenefits;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getPaymentURL() {
        return paymentURL;
    }

    public void setPaymentURL(String paymentURL) {
        this.paymentURL = paymentURL;
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

    public String getRefundURL() {
        return refundURL;
    }

    public void setRefundURL(String refundURL) {
        this.refundURL = refundURL;
    }

    public String getJavaClass() {
        return javaClass;
    }

    public void setJavaClass(String javaClass) {
        this.javaClass = javaClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public boolean isNotifyClient() {
        return notifyClient;
    }

    public void setNotifyClient(boolean notifyClient) {
        this.notifyClient = notifyClient;
    }

    public String getAcknowledgementURL() {
        return acknowledgementURL;
    }

    public void setAcknowledgementURL(String acknowledgementURL) {
        this.acknowledgementURL = acknowledgementURL;
    }

    public Map<String, String> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(Map<String, String> customProperties) {
        this.customProperties = customProperties;
    }

    public Boolean isShouldUseIframe() {
        return shouldUseIframe;
    }

    public void setShouldUseIframe(Boolean shouldUseIframe) {
        this.shouldUseIframe = shouldUseIframe;
    }

    public Boolean getRetryRefund() {
        return retryRefund;
    }

    public void setRetryRefund(Boolean retryRefund) {
        this.retryRefund = retryRefund;
    }

    public String getInformationURL() {
        return informationURL;
    }

    public void setInformationURL(String informationURL) {
        this.informationURL = informationURL;
    }

    public Boolean getAllowBankTransfer() {
        return allowBankTransfer;
    }

    public void setAllowBankTransfer(Boolean allowBankTransfer) {
        this.allowBankTransfer = allowBankTransfer;
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

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public List<String> getMandatoryFormFields() {
        return mandatoryFormFields;
    }

    public void setMandatoryFormFields(List<String> mandatoryFormFields) {
        this.mandatoryFormFields = mandatoryFormFields;
    }

    public Boolean getWallet() {
        return wallet;
    }

    public void setWallet(Boolean wallet) {
        this.wallet = wallet;
    }

    public Boolean getAllowBenefits() {
        return allowBenefits;
    }

    public void setAllowBenefits(Boolean allowBenefits) {
        this.allowBenefits = allowBenefits;
    }
}
