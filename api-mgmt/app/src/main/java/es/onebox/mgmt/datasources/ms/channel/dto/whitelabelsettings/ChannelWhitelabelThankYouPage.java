package es.onebox.mgmt.datasources.ms.channel.dto.whitelabelsettings;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelWhitelabelThankYouPage implements Serializable {

    @Serial
    private static final long serialVersionUID = -8433639264999715511L;

    private Boolean showPurchaseConditions;
    private List<ChannelWhitelabelThankYouPageModule> modules;

    public Boolean getShowPurchaseConditions() {
        return showPurchaseConditions;
    }

    public void setShowPurchaseConditions(Boolean showPurchaseConditions) {
        this.showPurchaseConditions = showPurchaseConditions;
    }

    public List<ChannelWhitelabelThankYouPageModule> getModules() {
        return modules;
    }

    public void setModules(List<ChannelWhitelabelThankYouPageModule> modules) {
        this.modules = modules;
    }
}
