package es.onebox.event.catalog.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class CatalogLocationDTO implements Serializable {

    private static final long serialVersionUID = -6078369563458421045L;

    private String code;
    private String name;

    public CatalogLocationDTO() {

    }

    private CatalogLocationDTO(Builder builder) {
        this.code = builder.code;
        this.name = builder.name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @JsonIgnore
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder to build {@link CatalogLocationDTO}.
     */
    public static final class Builder {
        private String code;
        private String name;

        private Builder() {
        }

        public Builder withCode(String code) {
            this.code = code;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public CatalogLocationDTO build() {
            return new CatalogLocationDTO(this);
        }
    }
}
