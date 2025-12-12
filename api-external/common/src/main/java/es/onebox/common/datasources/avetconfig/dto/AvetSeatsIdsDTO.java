package es.onebox.common.datasources.avetconfig.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class AvetSeatsIdsDTO extends ArrayList<Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = -6165146526419407888L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
