package es.onebox.mgmt.entities.converter;

import es.onebox.mgmt.datasources.ms.entity.dto.EntityOperatorVisibility;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityVisibilities;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityVisibility;
import es.onebox.mgmt.datasources.ms.entity.enums.EntityVisibilityRelationType;
import es.onebox.mgmt.entities.dto.EntityOperatorVisibilityDTO;
import es.onebox.mgmt.entities.dto.EntityVisibilitiesDTO;
import es.onebox.mgmt.entities.dto.EntityVisibilityDTO;
import es.onebox.mgmt.entities.enums.EntityVisibilityType;
import es.onebox.mgmt.entities.enums.VisibilityRelationType;
import org.apache.commons.collections.CollectionUtils;

import java.util.stream.Collectors;

public class EntityVisibilitiesConverter {

    private EntityVisibilitiesConverter() {
    }

    public static EntityVisibilitiesDTO fromMs(EntityVisibilities in) {
        EntityVisibilitiesDTO out = new EntityVisibilitiesDTO();
        EntityVisibilityType type = in.getType() == null ? null : EntityVisibilityType.valueOf(in.getType().getPublicValue());
        out.setVisibilityType(type);
        if (CollectionUtils.isNotEmpty(in.getVisibleEntities())) {
            out.setVisibleEntities(in.getVisibleEntities().stream().map(EntityVisibilitiesConverter::fromMs).collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(in.getVisibleOperators())) {
            out.setVisibleOperators(in.getVisibleOperators().stream().map(EntityVisibilitiesConverter::fromMs).collect(Collectors.toList()));
        }
        return out;
    }

    private static EntityOperatorVisibilityDTO fromMs(EntityOperatorVisibility in) {
        EntityOperatorVisibilityDTO out = new EntityOperatorVisibilityDTO();
        VisibilityRelationType type = in.getRelationType() == null ? null : VisibilityRelationType.valueOf(in.getRelationType().name());
        out.setType(type);
        out.setName(in.getName());
        out.setId(in.getId());
        return out;
    }

    private static EntityVisibilityDTO fromMs(EntityVisibility in) {
        EntityVisibilityDTO out = new EntityVisibilityDTO();
        VisibilityRelationType type = in.getRelationType() == null ? null : VisibilityRelationType.valueOf(in.getRelationType().name());
        out.setType(type);
        out.setName(in.getName());
        out.setId(in.getId());
        out.setOperatorId(in.getOperatorId());
        return out;
    }

    public static EntityVisibilities toMs(EntityVisibilitiesDTO in) {
        EntityVisibilities out = new EntityVisibilities();
        out.setType(es.onebox.mgmt.datasources.ms.entity.enums.EntityVisibilityType.fromPublicValue(in.getVisibilityType().name()));
        if (CollectionUtils.isNotEmpty(in.getVisibleEntities())) {
            out.setVisibleEntities(in.getVisibleEntities().stream().map(EntityVisibilitiesConverter::toMs).collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(in.getVisibleOperators())) {
            out.setVisibleOperators(in.getVisibleOperators().stream().map(EntityVisibilitiesConverter::toMs).collect(Collectors.toList()));
        }
        return out;
    }

    private static EntityOperatorVisibility toMs(EntityOperatorVisibilityDTO in) {
        EntityOperatorVisibility out = new EntityOperatorVisibility();
        EntityVisibilityRelationType type = in.getType() == null ? null : EntityVisibilityRelationType.valueOf(in.getType().name());
        out.setRelationType(type);
        out.setId(in.getId());
        return out;
    }

    private static EntityVisibility toMs(EntityVisibilityDTO in) {
        EntityVisibility out = new EntityVisibility();
        EntityVisibilityRelationType type = in.getType() == null ? null : EntityVisibilityRelationType.valueOf(in.getType().name());
        out.setRelationType(type);
        out.setId(in.getId());
        out.setOperatorId(in.getOperatorId());
        return out;
    }

}