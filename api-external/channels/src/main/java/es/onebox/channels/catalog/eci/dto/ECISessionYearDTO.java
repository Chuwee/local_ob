package es.onebox.channels.catalog.eci.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ECISessionYearDTO implements Serializable, Comparable<ECISessionYearDTO> {

    private static final long serialVersionUID = 1L;

    private final String year;
    private final List<ECISessionMonthDTO> months;

    public ECISessionYearDTO(String year) {
        this.year = year;
        this.months = new ArrayList<>();
    }

    public String getYear() {
        return year;
    }

    public List<ECISessionMonthDTO> getMonths() {
        return months;
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
    public int compareTo(ECISessionYearDTO o) {
        return year.compareTo(o.year);
    }
}
