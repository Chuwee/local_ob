package es.onebox.mgmt.datasources.ms.channel.dto;

import es.onebox.mgmt.channels.purchaseconfig.enums.InvoiceGenerationMode;
import es.onebox.mgmt.channels.purchaseconfig.enums.InvoiceRequestType;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelInvoiceSettings implements Serializable {

    @Serial
    private static final long serialVersionUID = -6290467976113951200L;

    private Boolean enabled;
    private List<MandatoryThreshold> mandatoryThresholds;
    private InvoiceGenerationMode invoiceGenerationMode;
    private InvoiceRequestType invoiceRequestType;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<MandatoryThreshold> getMandatoryThresholds() {
        return mandatoryThresholds;
    }

    public void setMandatoryThresholds(List<MandatoryThreshold> mandatoryThresholds) {
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
