package es.onebox.mgmt.channels.gateways.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.CodeNameDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class ChannelGatewayDTO extends BaseChannelGatewayDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private List<CodeNameDTO> currencies;
    private Set<SurchargeType> surcharges;
    @JsonProperty("has_benefits")
    private Boolean hasBenefits;
    @JsonProperty("allow_benefits")
    private Boolean allowBenefits;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CodeNameDTO> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<CodeNameDTO> currencies) {
        this.currencies = currencies;
    }

    public Set<SurchargeType> getSurcharges() {
        return surcharges;
    }

    public void setSurcharges(Set<SurchargeType> surcharges) {
        this.surcharges = surcharges;
    }

    public Boolean getHasBenefits() {
        return hasBenefits;
    }

    public void setHasBenefits(Boolean hasBenefits) {
        this.hasBenefits = hasBenefits;
    }

    public Boolean getAllowBenefits() {
        return allowBenefits;
    }

    public void setAllowBenefits(Boolean allowBenefits) {
        this.allowBenefits = allowBenefits;
    }

}
