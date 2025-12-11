package es.onebox.mgmt.events.promotions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class EventPromotionPacksDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4226948198534018050L;

    @JsonProperty("allow_entity_packs")
    private Boolean useEntityPacks;
    private List<IdNameDTO> packs;

    public Boolean getUseEntityPacks() {
        return useEntityPacks;
    }

    public void setUseEntityPacks(Boolean useEntityPacks) {
        this.useEntityPacks = useEntityPacks;
    }

    public List<IdNameDTO> getPacks() {
        return packs;
    }

    public void setPacks(List<IdNameDTO> packs) {
        this.packs = packs;
    }
}
