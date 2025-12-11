package es.onebox.mgmt.salerequests.gateways.benefit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.payment.dto.benefits.BenefitType;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class BenefitDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -2718051543487353738L;

    @NotNull(message = "field type can not be null")
    private BenefitType type;

    @JsonProperty("bin_groups")
    private List<BinGroupDTO> binGroups;

    @JsonProperty("brand_groups")
    private List<BrandGroupDTO> brandGroups;

    public BenefitType getType() {
        return type;
    }

    public void setType(BenefitType type) {
        this.type = type;
    }

    public List<BinGroupDTO> getBinGroups() {
        return binGroups;
    }

    public void setBinGroups(List<BinGroupDTO> binGroups) {
        this.binGroups = binGroups;
    }

    public List<BrandGroupDTO> getBrandGroups() {
        return brandGroups;
    }

    public void setBrandGroups(List<BrandGroupDTO> brandGroups) {
        this.brandGroups = brandGroups;
    }
}
