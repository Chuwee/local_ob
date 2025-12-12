package es.onebox.common.datasources.ms.order.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SeatBuyerDataDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3903407655575216878L;

    private boolean partner;
    private boolean companion;
    private boolean generalPublic;
    private boolean partnerSeasonTicketHolder;
    private String partnerId;
    private Map<String, String> fields;
    private String partnerType;
    private boolean useGenericCharges;
    private boolean autofill;
    private boolean editAutofill;

    public SeatBuyerDataDTO() {
        this.fields = new HashMap();
    }

    public SeatBuyerDataDTO(boolean partner, boolean companion, boolean generalPublic, String partnerId) {
        this.partner = partner;
        this.companion = companion;
        this.generalPublic = generalPublic;
        this.partnerId = partnerId;
        this.fields = new HashMap();
    }

    public boolean isPartner() {
        return partner;
    }

    public void setPartner(boolean partner) {
        this.partner = partner;
    }

    public boolean isCompanion() {
        return companion;
    }

    public void setCompanion(boolean companion) {
        this.companion = companion;
    }

    public boolean isGeneralPublic() {
        return generalPublic;
    }

    public void setGeneralPublic(boolean generalPublic) {
        this.generalPublic = generalPublic;
    }

    public boolean isPartnerSeasonTicketHolder() {
        return partnerSeasonTicketHolder;
    }

    public void setPartnerSeasonTicketHolder(boolean partnerSeasonTicketHolder) {
        this.partnerSeasonTicketHolder = partnerSeasonTicketHolder;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public String getPartnerType() {
        return partnerType;
    }

    public void setPartnerType(String partnerType) {
        this.partnerType = partnerType;
    }

    public boolean isUseGenericCharges() {
        return useGenericCharges;
    }

    public void setUseGenericCharges(boolean useGenericCharges) {
        this.useGenericCharges = useGenericCharges;
    }

    public boolean isAutofill() {
        return autofill;
    }

    public void setAutofill(boolean autofill) {
        this.autofill = autofill;
    }

    public boolean isEditAutofill() {
        return editAutofill;
    }

    public void setEditAutofill(boolean editAutofill) {
        this.editAutofill = editAutofill;
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
