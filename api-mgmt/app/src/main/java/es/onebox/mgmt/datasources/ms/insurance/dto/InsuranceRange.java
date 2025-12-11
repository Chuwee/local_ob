package es.onebox.mgmt.datasources.ms.insurance.dto;

import java.io.Serial;
import java.io.Serializable;

public class InsuranceRange implements Serializable {

    @Serial
    private static final long serialVersionUID = -7624589659013192546L;

    private Integer id;
    private Integer policyId;
    private String name;
    private Double max;
    private Double min;
    private Double chargeFix;
    private Double chargePercent;
    private Double chargeMin;
    private Double chargeMax;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Integer policyId) {
        this.policyId = policyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getChargeFix() {
        return chargeFix;
    }

    public void setChargeFix(Double chargeFix) {
        this.chargeFix = chargeFix;
    }

    public Double getChargePercent() {
        return chargePercent;
    }

    public void setChargePercent(Double chargePercent) {
        this.chargePercent = chargePercent;
    }

    public Double getChargeMin() {
        return chargeMin;
    }

    public void setChargeMin(Double chargeMin) {
        this.chargeMin = chargeMin;
    }

    public Double getChargeMax() {
        return chargeMax;
    }

    public void setChargeMax(Double chargeMax) {
        this.chargeMax = chargeMax;
    }
}
