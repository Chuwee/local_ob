package es.onebox.mgmt.datasources.ms.promotion.dto.packs;

import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class EventPromotionPacks implements Serializable {

    @Serial
    private static final long serialVersionUID = 4855720621885873463L;

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
