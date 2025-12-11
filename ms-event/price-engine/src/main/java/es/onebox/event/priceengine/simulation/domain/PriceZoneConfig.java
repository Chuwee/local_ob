package es.onebox.event.priceengine.simulation.domain;

public class PriceZoneConfig {

    public PriceZoneConfig() {
    }

    public PriceZoneConfig(String code, String description) {
        this.code = code;
        this.description = description;
    }

    private String code;
    private String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
