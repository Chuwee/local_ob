package es.onebox.common.datasources.webhook.dto.fever.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serial;
import java.io.Serializable;

@JsonNaming(SnakeCaseStrategy.class)
public class EventChannelSurchargesFeverDTO extends SurchargesFeverDTO implements Serializable{

    @Serial
    private static final long serialVersionUID = 1L;
    private Boolean enabledRanges;

    public Boolean getEnabledRanges() {
        return enabledRanges;
    }

    public void setEnabledRanges(Boolean enabledRanges) {
        this.enabledRanges = enabledRanges;
    }
}
