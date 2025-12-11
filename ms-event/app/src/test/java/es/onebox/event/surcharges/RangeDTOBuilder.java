package es.onebox.event.surcharges;

import es.onebox.event.surcharges.dto.RangeDTO;

public final class RangeDTOBuilder {
    private double from;
    private double percentage;
    private double min;
    private double max;
    private double fix;

    private RangeDTOBuilder(double from) {
        this.from = from;
    }

    public static RangeDTOBuilder aRange(double from) {
        return new RangeDTOBuilder(from);
    }

    public RangeDTOBuilder withPercentage(Double percentage) {
        this.percentage = percentage;
        return this;
    }

    public RangeDTOBuilder withMin(Double min) {
        this.min = min;
        return this;
    }

    public RangeDTOBuilder withMax(Double max) {
        this.max = max;
        return this;
    }

    public RangeDTOBuilder withFix(Double fix) {
        this.fix = fix;
        return this;
    }

    public RangeDTO build() {
        return new RangeDTO(from, fix, percentage, min, max);
    }
}
