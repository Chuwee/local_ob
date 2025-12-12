package es.onebox.internal.automaticsales.eip.process;

import es.onebox.internal.automaticsales.processsales.dto.ProcessSalesConfigurationRequest;
import es.onebox.internal.automaticsales.processsales.enums.AttendantFields;
import es.onebox.message.broker.client.message.AbstractNotificationMessage;

import java.io.Serial;

public class GenerateAutomaticSalesProcesssMessage extends AbstractNotificationMessage {

    @Serial
    private static final long serialVersionUID = 6096055731275647785L;
    private String filename;
    private Boolean sort;
    private Boolean useSeatMappings;
    private Boolean useOBIdsForSeatMappings;
    private Boolean forceMultiTicket;
    private Boolean defaultPurchaseLanguage;
    private Boolean skipAddAttendant;
    private Boolean useLocators;
    private Boolean allowBreakAdjacentSeats;
    private Long channelId;
    private String previewToken;
    private Long sessionId;
    private String receiptEmail;
    private AttendantFields extraFieldValue;
    private Boolean allowSkipNonAdjacentSeats;

    public AttendantFields getExtraFieldValue() {
        return extraFieldValue;
    }

    public void setExtraFieldValue(AttendantFields extraFieldValue) {
        this.extraFieldValue = extraFieldValue;
    }

    public static GenerateAutomaticSalesProcesssMessage of(ProcessSalesConfigurationRequest config, Long sessionId) {
        GenerateAutomaticSalesProcesssMessage message = new GenerateAutomaticSalesProcesssMessage();
        message.setFilename(config.getFilename());
        message.setSort(config.getSort());
        message.setChannelId(config.getChannelId());
        message.setPreviewToken(config.getPreviewToken());
        message.setUseLocators(config.getUseLocators());
        message.setDefaultPurchaseLanguage(config.getDefaultPurchaseLanguage());
        message.setForceMultiTicket(config.getForceMultiTicket());
        message.setAllowBreakAdjacentSeats(config.getAllowBreakAdjacentSeats());
        message.setSkipAddAttendant(config.getSkipAddAttendant());
        message.setUseOBIdsForSeatMappings(config.getUseOBIdsForSeatMappings());
        message.setUseSeatMappings(config.getUseSeatMappings());
        message.setSessionId(sessionId);
        message.setReceiptEmail(config.getReceiptEmail());
        message.setExtraFieldValue(config.getExtraFieldValue());
        message.setAllowSkipNonAdjacentSeats(config.getAllowSkipNonAdjacentSeats());
        return message;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Boolean getSort() {
        return sort;
    }

    public void setSort(Boolean sort) {
        this.sort = sort;
    }

    public Boolean getUseSeatMappings() {
        return useSeatMappings;
    }

    public void setUseSeatMappings(Boolean useSeatMappings) {
        this.useSeatMappings = useSeatMappings;
    }

    public Boolean getUseOBIdsForSeatMappings() {
        return useOBIdsForSeatMappings;
    }

    public void setUseOBIdsForSeatMappings(Boolean useOBIdsForSeatMappings) {
        this.useOBIdsForSeatMappings = useOBIdsForSeatMappings;
    }

    public Boolean getForceMultiTicket() {
        return forceMultiTicket;
    }

    public void setForceMultiTicket(Boolean forceMultiTicket) {
        this.forceMultiTicket = forceMultiTicket;
    }

    public Boolean getDefaultPurchaseLanguage() {
        return defaultPurchaseLanguage;
    }

    public void setDefaultPurchaseLanguage(Boolean defaultPurchaseLanguage) {
        this.defaultPurchaseLanguage = defaultPurchaseLanguage;
    }

    public Boolean getSkipAddAttendant() {
        return skipAddAttendant;
    }

    public void setSkipAddAttendant(Boolean skipAddAttendant) {
        this.skipAddAttendant = skipAddAttendant;
    }

    public Boolean getUseLocators() {
        return useLocators;
    }

    public void setUseLocators(Boolean useLocators) {
        this.useLocators = useLocators;
    }

    public Boolean getAllowBreakAdjacentSeats() {
        return allowBreakAdjacentSeats;
    }

    public void setAllowBreakAdjacentSeats(Boolean allowBreakAdjacentSeats) {
        this.allowBreakAdjacentSeats = allowBreakAdjacentSeats;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getPreviewToken() {
        return previewToken;
    }

    public void setPreviewToken(String previewToken) {
        this.previewToken = previewToken;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getReceiptEmail() {
        return receiptEmail;
    }

    public void setReceiptEmail(String receiptEmail) {
        this.receiptEmail = receiptEmail;
    }

    public Boolean getAllowSkipNonAdjacentSeats() {
        return allowSkipNonAdjacentSeats;
    }

    public void setAllowSkipNonAdjacentSeats(Boolean allowSkipNonAdjacentSeats) {
        this.allowSkipNonAdjacentSeats = allowSkipNonAdjacentSeats;
    }
}
