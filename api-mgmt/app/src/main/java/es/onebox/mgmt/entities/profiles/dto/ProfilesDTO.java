package es.onebox.mgmt.entities.profiles.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class ProfilesDTO extends ArrayList<ProfileDTO> implements Serializable {

    private static final long serialVersionUID = 1L;

    public ProfilesDTO(Collection<? extends ProfileDTO> c) {
        super(c);
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

