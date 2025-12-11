package es.onebox.mgmt.events.promotions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public class UpdateEventPromotionPacksDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 872546391931840105L;

    @JsonProperty("allow_entity_packs")
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
