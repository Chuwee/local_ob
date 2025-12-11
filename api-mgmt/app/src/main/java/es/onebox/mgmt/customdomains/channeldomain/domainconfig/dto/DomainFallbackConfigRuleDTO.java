package es.onebox.mgmt.customdomains.channeldomain.domainconfig.dto;

import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class DomainFallbackConfigRuleDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4528176245818588635L;

    @Size(max = 20, message = "value array must not exceed 20 items")
    private List<@Size(max = 50, message = "Value field must not exceed 50 characters") String> values;
    private AllowedChannelsRuleType type;

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public AllowedChannelsRuleType getType() {
        return type;
    }

    public void setType(AllowedChannelsRuleType type) {
        this.type = type;
    }
}
