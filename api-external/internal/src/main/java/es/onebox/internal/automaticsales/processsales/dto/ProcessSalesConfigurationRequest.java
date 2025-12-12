package es.onebox.internal.automaticsales.processsales.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.internal.automaticsales.processsales.enums.AttendantFields;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;


public class ProcessSalesConfigurationRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -140206237704925240L;

    private String filename;
    private Boolean sort;
    @JsonProperty("use_seat_mappings")
    private Boolean useSeatMappings;
    @JsonProperty("use_ob_ids_for_seat_mappings")
    private Boolean useOBIdsForSeatMappings;
    @JsonProperty("force_multi_ticket")
    private Boolean forceMultiTicket;
    @JsonProperty("default_purchase_language")
    private Boolean defaultPurchaseLanguage;
    @JsonProperty("skip_add_attendant")
    private Boolean skipAddAttendant;
    @JsonProperty("use_locators")
    private Boolean useLocators;
    @JsonProperty("allow_break_adjacent_seats")
    private Boolean allowBreakAdjacentSeats;
    @JsonProperty("channel_id")
    private Long channelId;
    @JsonProperty("preview_token")
    private String previewToken;
    @JsonProperty("receipt_email")
    private String receiptEmail;
    @JsonProperty("extra_field_value")
    private AttendantFields extraFieldValue;
    @JsonProperty("allow_skip_non_adjacent_seats")
    private Boolean allowSkipNonAdjacentSeats;

    public AttendantFields getExtraFieldValue() {
        return extraFieldValue;
    }

    public void setExtraFieldValue(AttendantFields extraFieldValue) {
        this.extraFieldValue = extraFieldValue;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Boolean isSort() {
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

    public Boolean getSort() {
        return sort;
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
