package es.onebox.mgmt.datasources.ms.channel.dto.customresources.assets;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;

public class CreateCustomResourceAssetsMsDTO extends ArrayList<CreateCustomResourceAssetMsDTO> {

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
