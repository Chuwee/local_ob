package es.onebox.atm.email.config;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;

import java.io.Serial;
import java.util.List;

public class PdfTicketGenerationMessage extends AbstractNotificationMessage {
    @Serial
    private static final long serialVersionUID = 1L;

    public enum SourceApp {
        PORTAL,
        REST
    }

    private String orderCode;
    private List<String> barcodes;
    private String language;
    private boolean emailFromOrder;
    private String targetTicketEmail;
    private String targetReceiptEmail;
    private SourceApp sourceApp;
    private boolean sendTicketEmail;
    private boolean sendReceiptEmail;
    private String customBody;
    private String customSubject;
    private Integer resendUserId;
    private boolean forceRegeneration;
    private Boolean fullRegeneration;
    private Boolean hidePrice;
    private Boolean avoidMerge;
    private Boolean external;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public List<String> getBarcodes() {
        return barcodes;
    }

    public void setBarcodes(List<String> barcodes) {
        this.barcodes = barcodes;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isEmailFromOrder() {
        return emailFromOrder;
    }

    public void setEmailFromOrder(boolean emailFromOrder) {
        this.emailFromOrder = emailFromOrder;
    }

    public String getTargetTicketEmail() {
        return targetTicketEmail;
    }

    public void setTargetTicketEmail(String targetTicketEmail) {
        this.targetTicketEmail = targetTicketEmail;
    }

    public String getTargetReceiptEmail() {
        return targetReceiptEmail;
    }

    public void setTargetReceiptEmail(String targetReceiptEmail) {
        this.targetReceiptEmail = targetReceiptEmail;
    }

    public SourceApp getSourceApp() {
        return sourceApp;
    }

    public void setSourceApp(SourceApp sourceApp) {
        this.sourceApp = sourceApp;
    }

    public boolean isSendTicketEmail() {
        return sendTicketEmail;
    }

    public void setSendTicketEmail(boolean sendTicketEmail) {
        this.sendTicketEmail = sendTicketEmail;
    }

    public boolean isSendReceiptEmail() {
        return sendReceiptEmail;
    }

    public void setSendReceiptEmail(boolean sendReceiptEmail) {
        this.sendReceiptEmail = sendReceiptEmail;
    }

    public String getCustomBody() {
        return customBody;
    }

    public void setCustomBody(String customBody) {
        this.customBody = customBody;
    }

    public String getCustomSubject() {
        return customSubject;
    }

    public void setCustomSubject(String customSubject) {
        this.customSubject = customSubject;
    }

    public Integer getResendUserId() {
        return resendUserId;
    }

    public void setResendUserId(Integer resendUserId) {
        this.resendUserId = resendUserId;
    }

    public boolean isForceRegeneration() {
        return forceRegeneration;
    }

    public void setForceRegeneration(boolean forceRegeneration) {
        this.forceRegeneration = forceRegeneration;
    }

    public Boolean getFullRegeneration() {
        return fullRegeneration;
    }

    public void setFullRegeneration(Boolean fullRegeneration) {
        this.fullRegeneration = fullRegeneration;
    }

    public Boolean getHidePrice() {
        return hidePrice;
    }

    public void setHidePrice(Boolean hidePrice) {
        this.hidePrice = hidePrice;
    }

    public Boolean getAvoidMerge() {
        return avoidMerge;
    }

    public void setAvoidMerge(Boolean avoidMerge) {
        this.avoidMerge = avoidMerge;
    }

    public Boolean getExternal() {
        return external;
    }

    public void setExternal(Boolean external) {
        this.external = external;
    }
}
