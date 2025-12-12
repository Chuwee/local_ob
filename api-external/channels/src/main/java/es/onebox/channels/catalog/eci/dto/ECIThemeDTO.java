package es.onebox.channels.catalog.eci.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ECIThemeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private ECICategoryThemeDTO category;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ECICategoryThemeDTO getCategory() {
        return category;
    }

    public void setCategory(ECICategoryThemeDTO category) {
        this.category = category;
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
