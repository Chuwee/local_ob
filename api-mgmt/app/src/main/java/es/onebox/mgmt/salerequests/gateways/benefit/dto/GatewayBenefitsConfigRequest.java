package es.onebox.mgmt.salerequests.gateways.benefit.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class GatewayBenefitsConfigRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1471956897802374624L;

    @NotNull(message = "benefits can not be null")
    private List<BenefitDTO> benefits;

    public List<BenefitDTO> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<BenefitDTO> benefits) {
        this.benefits = benefits;
    }
}
