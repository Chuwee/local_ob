package es.onebox.event.report.enums;

import es.onebox.core.file.exporter.generator.model.FileCode;

public enum PriceSimulationField implements FileCode {

    VENUE_CONFIG_ID("venue_config_id"),
    VENUE_CONFIG_NAME("venue_config_name"),
    RATE_ID("rate_id"),
    RATE_NAME("rate_name"),
    RATE_PRICE_TYPE_ID("rate_price_type_id"),
    RATE_PRICE_TYPE_NAME("rate_price_type_name"),
    RATE_PRICE_TYPE_SIMULATION_PRICE_BASE("rate_price_type_simulation_price_base"),
    RATE_PRICE_TYPE_SIMULATION_PRICE_TOTAL("rate_price_type_simulation_price_total"),
    RATE_PRICE_TYPE_SIMULATION_PRICE_SURCHARGE_VALUE("rate_price_type_simulation_price_surcharge_value"),
    RATE_PRICE_TYPE_SIMULATION_PRICE_SURCHARGE_TYPE("rate_price_type_simulation_price_surcharge_type"),
    RATE_PRICE_TYPE_SIMULATION_PRICE_PROMOTION_ID("rate_price_type_simulation_price_promotion_id"),
    RATE_PRICE_TYPE_SIMULATION_PRICE_PROMOTION_NAME("rate_price_type_simulation_price_promotion_name"),
    RATE_PRICE_TYPE_SIMULATION_PRICE_PROMOTION_TYPE("rate_price_type_simulation_price_promotion_type");

    private final String code;

    PriceSimulationField(final String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}
