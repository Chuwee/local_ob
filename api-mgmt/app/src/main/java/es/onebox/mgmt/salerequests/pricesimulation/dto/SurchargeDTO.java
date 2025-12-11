package es.onebox.mgmt.salerequests.pricesimulation.dto;

import es.onebox.mgmt.salerequests.pricesimulation.dto.enums.SurchargeTypeDTO;

import java.io.Serializable;

public class SurchargeDTO implements Serializable {

    private static final long serialVersionUID = 7271000322500557155L;

    private Double value;
    private SurchargeTypeDTO type;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public SurchargeTypeDTO getType() {
        return type;
    }

    public void setType(SurchargeTypeDTO type) {
        this.type = type;
    }
}
