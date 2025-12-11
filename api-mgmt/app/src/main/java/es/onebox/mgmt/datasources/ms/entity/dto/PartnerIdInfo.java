package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serializable;

public class PartnerIdInfo implements Serializable {

    private static final long serialVersionUID = -4428371915280929233L;
    private Integer min;
    private Integer max;
    private String partnerType;

    private boolean useGenericCharges;


    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
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
}
