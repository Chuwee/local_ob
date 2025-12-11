package es.onebox.mgmt.channels.purchaseconfig.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.purchaseconfig.enums.InvoiceGenerationMode;
import es.onebox.mgmt.channels.purchaseconfig.enums.InvoiceRequestType;
import jakarta.validation.Valid;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelPurchaseConfigInvoiceDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1705237503915394181L;

    @JsonProperty("enabled")
    private Boolean enabled;
    @JsonProperty("mandatory_thresholds")
    private List<@Valid MandatoryThresholdDTO> mandatoryThresholds;
    @JsonProperty("invoice_generation_mode")
    private InvoiceGenerationMode invoiceGenerationMode;
    @JsonProperty("invoice_request_type")
    private InvoiceRequestType invoiceRequestType;


    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<MandatoryThresholdDTO> getMandatoryThresholds() {
        return mandatoryThresholds;
    }

    public void setMandatoryThresholds(List<MandatoryThresholdDTO> mandatoryThresholds) {
        this.mandatoryThresholds = mandatoryThresholds;
    }

    public InvoiceGenerationMode getInvoiceGenerationMode() {
        return invoiceGenerationMode;
    }

    public void setInvoiceGenerationMode(InvoiceGenerationMode invoiceGenerationMode) {
        this.invoiceGenerationMode = invoiceGenerationMode;
    }

    public InvoiceRequestType getInvoiceRequestType() {
        return invoiceRequestType;
    }

    public void setInvoiceRequestType(InvoiceRequestType invoiceRequestType) {
        this.invoiceRequestType = invoiceRequestType;
    }
}
