package es.onebox.common.datasources.avetconfig.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class ObSeatsIdsDTO extends ArrayList<Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = 6886746882003207993L;

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
