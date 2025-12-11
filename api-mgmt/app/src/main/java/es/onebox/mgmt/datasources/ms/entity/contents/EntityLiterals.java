package es.onebox.mgmt.datasources.ms.entity.contents;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Collection;

public class EntityLiterals extends ArrayList<EntityLiteral> {

    private static final long serialVersionUID = 1L;

    public EntityLiterals(Collection<EntityLiteral> in) {
        super(in);
    }

    public EntityLiterals() {
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
