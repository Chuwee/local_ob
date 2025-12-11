package es.onebox.event.priceengine.builders;

import es.onebox.event.priceengine.surcharges.dto.SurchargeRange;

public final class SurchargeRangeBuilder {
    private Double from;
    private Double to;
    private Double fixedValue;
    private Double percentageValue;
    private Double maximumValue;
    private Double minimumValue;

    private SurchargeRangeBuilder(double from) {
        this.from = from;
    }

    public static SurchargeRangeBuilder aRange(double from) {
        return new SurchargeRangeBuilder(from);
    }

    public SurchargeRangeBuilder withPercentage(Double percentage) {
        this.percentageValue = percentage;
        return this;
    }

    public SurchargeRangeBuilder withTo(Double to) {
        this.to = to;
        return this;
    }

    public SurchargeRangeBuilder withMinimumValue(Double minimumValue) {
        this.minimumValue = minimumValue;
        return this;
    }

    public SurchargeRangeBuilder withMaximumValue(Double maximumValue) {
        this.maximumValue = maximumValue;
        return this;
    }

    public SurchargeRangeBuilder withFixedValue(Double fixedValue) {
        this.fixedValue = fixedValue;
        return this;
    }

    public SurchargeRange build() {
        return new SurchargeRange(from, to, fixedValue, percentageValue, minimumValue, maximumValue);
    }
}
