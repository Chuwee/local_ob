package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelWhitelabelThankYouPageDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8433639264999715511L;

    @JsonProperty("show_purchase_conditions")
    private Boolean showPurchaseConditions;
    private List<ChannelWhitelabelThankYouPageModuleDTO> modules;

    public Boolean getShowPurchaseConditions() {
        return showPurchaseConditions;
    }

    public void setShowPurchaseConditions(Boolean showPurchaseConditions) {
        this.showPurchaseConditions = showPurchaseConditions;
    }

    public List<ChannelWhitelabelThankYouPageModuleDTO> getModules() {
        return modules;
    }

    public void setModules(List<ChannelWhitelabelThankYouPageModuleDTO> modules) {
        this.modules = modules;
    }
}
