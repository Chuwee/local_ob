package es.onebox.mgmt.common.surcharges.dto;

import java.util.ArrayList;
import java.util.List;

public class EntitySurchargesDTO extends ArrayList<SurchargeDTO> {

    public EntitySurchargesDTO(){}
    public EntitySurchargesDTO(List<SurchargeDTO> surcharges) {
        this.addAll(surcharges);
    }
}
