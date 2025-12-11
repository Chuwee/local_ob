package es.onebox.mgmt.datasources.ms.venue.dto.template;

public class PriceType extends BaseCodeTag {

    private static final long serialVersionUID = 1L;

    private Long priority;

    private AdditionalConfigPriceType additionalConfig;

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public PriceType() {
    }

    public PriceType(Long id) {
        super(id);
    }

    public AdditionalConfigPriceType getAdditionalConfig() {
        return additionalConfig;
    }

    public void setAdditionalConfig(AdditionalConfigPriceType additionalConfig) {
        this.additionalConfig = additionalConfig;
    }
}
