package es.onebox.common.datasources.ms.event.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductSessionBase implements Serializable {

    @Serial
    private static final long serialVersionUID = -8061105847538611953L;

    private Long id;
    private String name;
    private SessionDateDTO dates;

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

    public SessionDateDTO getDates() {
        return dates;
    }

    public void setDates(SessionDateDTO dates) {
        this.dates = dates;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
