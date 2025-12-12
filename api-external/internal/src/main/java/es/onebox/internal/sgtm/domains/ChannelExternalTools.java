package es.onebox.internal.sgtm.domains;

import es.onebox.internal.sgtm.dto.ChannelExternalToolDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;

public class ChannelExternalTools implements Serializable {

    private static final long serialVersionUID = 1L;

    private ArrayList<ChannelExternalToolDTO> externalToolDTOs;

    public ArrayList<ChannelExternalToolDTO> getExternalToolDTOs() {
        return externalToolDTOs;
    }

    public void setExternalToolDTOs(ArrayList<ChannelExternalToolDTO> externalToolDTOs) {
        this.externalToolDTOs = externalToolDTOs;
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
