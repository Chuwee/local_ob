package es.onebox.mgmt.datasources.ms.entity.dto.customertypes;

import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.enums.AssignationTrigger;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.enums.AssignationType;

import java.util.List;

public record CustomerTypeCreateRequest(String name, String code, AssignationType assignationType, List<AssignationTrigger> triggers) {

}
