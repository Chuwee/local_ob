package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class CalendarDayType implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String color;
    private List<ZonedDateTime> days;

    public CalendarDayType() {
    }

    public CalendarDayType(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<ZonedDateTime> getDays() {
        return days;
    }

    public void setDays(List<ZonedDateTime> days) {
        this.days = days;
    }
}
