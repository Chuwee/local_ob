package es.onebox.channels.catalog.eci.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ECISessionMonthDTO implements Serializable, Comparable<ECISessionMonthDTO> {

    private static final long serialVersionUID = 1L;

    private final String month;
    private final List<String> days;

    public ECISessionMonthDTO(String month) {
        this.month = month;
        this.days = new ArrayList<>();
    }

    public String getMonth() {
        return month;
    }

    public List<String> getDays() {
        return days;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public int compareTo(ECISessionMonthDTO o) {
        return month.compareTo(o.month);
    }
}
