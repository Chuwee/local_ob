package es.onebox.mgmt.datasources.ms.channel.dto.catalog;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.validation.MaxLimit;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.List;


@MaxLimit(50)
public class ChannelSessionsMsFilter extends BaseRequestFilter {

    @Serial
    private static final long serialVersionUID = 4263010188221562432L;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private List<FilterWithOperator<ZonedDateTime>> startDate;
    private List<DayOfWeek> daysOfWeek;
    private String olsonId;
    private String q;

    public List<FilterWithOperator<ZonedDateTime>> getStartDate() {
        return startDate;
    }

    public void setStartDate(List<FilterWithOperator<ZonedDateTime>> startDate) {
        this.startDate = startDate;
    }

    public List<DayOfWeek> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(List<DayOfWeek> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public String getOlsonId() {
        return olsonId;
    }

    public void setOlsonId(String olsonId) {
        this.olsonId = olsonId;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }
}