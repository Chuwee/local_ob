package es.onebox.common.datasources.ms.venue.dto;

import java.io.Serial;

public class MsPriceTypeDTO extends BasePriceType {

    private Long priority;
    private PriceTypeAdditionalConfigDTO additionalConfig;

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public PriceTypeAdditionalConfigDTO getAdditionalConfig() {
        return additionalConfig;
    }

    public void setAdditionalConfig(PriceTypeAdditionalConfigDTO additionalConfig) {
        this.additionalConfig = additionalConfig;
    }

    @Serial
    private static final long serialVersionUID = -6480766366767577323L;
}
