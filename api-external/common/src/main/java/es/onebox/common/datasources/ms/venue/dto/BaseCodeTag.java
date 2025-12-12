package es.onebox.common.datasources.ms.venue.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class BaseCodeTag extends BaseTag {

    @Serial
    private static final long serialVersionUID = 2068174153585121926L;

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BaseCodeTag() {
    }

    public BaseCodeTag(Long id) {
        super(id);
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
