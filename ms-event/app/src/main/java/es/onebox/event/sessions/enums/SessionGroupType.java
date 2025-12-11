package es.onebox.event.sessions.enums;

import org.jooq.DatePart;

import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.List;

public enum SessionGroupType {
    DAY(Arrays.asList(DatePart.DAY, DatePart.MONTH, DatePart.YEAR), ChronoField.HOUR_OF_DAY),
    WEEK(Arrays.asList(DatePart.WEEK, DatePart.YEAR), ChronoField.DAY_OF_WEEK),
    MONTH(Arrays.asList(DatePart.MONTH, DatePart.YEAR), ChronoField.DAY_OF_MONTH);

    private final List<DatePart> groupBy;
    private final ChronoField chronoField;

    SessionGroupType(List<DatePart> groupBy, ChronoField chronoField) {
        this.groupBy = groupBy;
        this.chronoField = chronoField;
    }

    public List<DatePart> getGroupBy() {
        return groupBy;
    }

    public ChronoField getChronoField() {
        return chronoField;
    }
}
