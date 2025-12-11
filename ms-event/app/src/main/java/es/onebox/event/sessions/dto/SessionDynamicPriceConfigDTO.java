package es.onebox.event.sessions.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SessionDynamicPriceConfigDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -5829475837458392847L;

    private Boolean active;
    private List<DynamicPriceZoneDTO> dynamicPriceZoneDTO;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<DynamicPriceZoneDTO> getDynamicPriceZoneDTO() {
        return dynamicPriceZoneDTO;
    }

    public void setDynamicPriceZoneDTO(List<DynamicPriceZoneDTO> dynamicPriceZoneDTO) {
        this.dynamicPriceZoneDTO = dynamicPriceZoneDTO;
    }
}
