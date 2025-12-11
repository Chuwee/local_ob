package es.onebox.mgmt.salerequests.gateways.benefit.dto;

import jakarta.validation.constraints.NotEmpty;

import java.io.Serial;
import java.util.List;

public class BinGroupDTO extends BenefitGroupConfigDTO {

    @Serial
    private static final long serialVersionUID = 2663742913275625446L;

    @NotEmpty(message = "bins can not be empty or null")
    private List<String> bins;

    public List<String> getBins() {
        return bins;
    }

    public void setBins(List<String> bins) {
        this.bins = bins;
    }
}
