package es.onebox.mgmt.salerequests.gateways.benefit.dto;

import jakarta.validation.constraints.NotEmpty;

import java.io.Serial;
import java.util.List;

public class BrandGroupDTO extends BenefitGroupConfigDTO {

    @Serial
    private static final long serialVersionUID = 2663742913275625446L;

    @NotEmpty(message = "brands can not be empty or null")
    private List<String> brands;

    public List<String> getBrands() {
        return brands;
    }

    public void setBrands(List<String> brands) {
        this.brands = brands;
    }
}
