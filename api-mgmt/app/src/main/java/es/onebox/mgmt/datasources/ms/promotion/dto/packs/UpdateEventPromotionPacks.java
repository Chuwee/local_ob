package es.onebox.mgmt.datasources.ms.promotion.dto.packs;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public class UpdateEventPromotionPacks implements Serializable {

    @Serial
    private static final long serialVersionUID = -2166617096178722052L;

    private Boolean useEntityPacks;
    private Set<Integer> packs;

    public Boolean getUseEntityPacks() {
        return useEntityPacks;
    }

    public void setUseEntityPacks(Boolean useEntityPacks) {
        this.useEntityPacks = useEntityPacks;
    }

    public Set<Integer> getPacks() {
        return packs;
    }

    public void setPacks(Set<Integer> packs) {
        this.packs = packs;
    }
}
