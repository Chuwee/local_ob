package es.onebox.mgmt.datasources.ms.channel.dto.domainconfig;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class DomainFallbackConfigRule implements Serializable {

    @Serial
    private static final long serialVersionUID = 4528176245818588635L;

    private List<String> values;
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
