package es.onebox.mgmt.oneboxinvoicing.converters;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.CreateOneboxInvoiceEntityRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityInvoiceConfigurationSearchFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.OneboxInvoiceEntities;
import es.onebox.mgmt.datasources.ms.entity.dto.OneboxInvoiceEntitiesFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.OneboxInvoiceEntity;
import es.onebox.mgmt.datasources.ms.entity.dto.OneboxInvoiceEntityFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.UpdateOneboxInvoiceEntityRequest;
import es.onebox.mgmt.oneboxinvoicing.dto.CreateOneboxInvoiceEntityRequestDTO;
import es.onebox.mgmt.oneboxinvoicing.dto.EntityInvoiceConfigurationSearchFilterDTO;
import es.onebox.mgmt.oneboxinvoicing.dto.OneboxInvoiceEntitiesDTO;
import es.onebox.mgmt.oneboxinvoicing.dto.OneboxInvoiceEntitiesFilterDTO;
import es.onebox.mgmt.oneboxinvoicing.dto.OneboxInvoiceEntityDTO;
import es.onebox.mgmt.oneboxinvoicing.dto.OneboxInvoiceEntityFilterDTO;
import es.onebox.mgmt.oneboxinvoicing.dto.UpdateOneboxInvoiceEntityRequestDTO;
import es.onebox.mgmt.oneboxinvoicing.enums.OneboxInvoiceType;

import java.util.List;
import java.util.stream.Collectors;

public class OneboxInvoiceEntitiesConverter {

    private OneboxInvoiceEntitiesConverter() {
    }

    public static OneboxInvoiceEntitiesFilterDTO toDTO(OneboxInvoiceEntitiesFilter in) {
        return new OneboxInvoiceEntitiesFilterDTO(in.stream().map(OneboxInvoiceEntitiesConverter::toDTO).collect(Collectors.toList()));

    }

    private static OneboxInvoiceEntityFilterDTO toDTO(OneboxInvoiceEntityFilter in) {
        OneboxInvoiceEntityFilterDTO out = new OneboxInvoiceEntityFilterDTO();
        out.setEntityIds(in.getEntityIds());
        out.setCode(in.getCode());
        out.setName(in.getName());
        return out;
    }

    public static OneboxInvoiceEntitiesDTO toDTO(OneboxInvoiceEntities in) {
        return new OneboxInvoiceEntitiesDTO(in.stream().map(OneboxInvoiceEntitiesConverter::toDTO).collect(Collectors.toList()));
    }

    private static OneboxInvoiceEntityDTO toDTO(OneboxInvoiceEntity in) {
        OneboxInvoiceEntityDTO out = new OneboxInvoiceEntityDTO();
        out.setEntity(new IdNameDTO(in.getEntityId(), in.getEntityName()));
        out.setFixed(in.getFixed());
        out.setVariable(in.getVariable());
        out.setInvitation(in.getInvitation());
        out.setMax(in.getMax());
        out.setMin(in.getMin());
        out.setRefund(in.getRefund());
        out.setType(toDTO(in.getType()));
        return out;
    }

    public static CreateOneboxInvoiceEntityRequest toMs(CreateOneboxInvoiceEntityRequestDTO in) {
        CreateOneboxInvoiceEntityRequest out = new CreateOneboxInvoiceEntityRequest();
        out.setFixed(in.fixed());
        out.setInvitation(in.invitation());
        out.setMax(in.max());
        out.setMin(in.min());
        out.setVariable(in.variable());
        out.setRefund(in.refund());
        out.setType(toMs(in.type()));
        return out;
    }

    public static UpdateOneboxInvoiceEntityRequest toMs(UpdateOneboxInvoiceEntityRequestDTO in) {
        UpdateOneboxInvoiceEntityRequest out = new UpdateOneboxInvoiceEntityRequest();
        out.setFixed(in.fixed());
        out.setInvitation(in.invitation());
        out.setMax(in.max());
        out.setMin(in.min());
        out.setVariable(in.variable());
        out.setRefund(in.refund());
        return out;
    }


    public static EntityInvoiceConfigurationSearchFilter toMs(EntityInvoiceConfigurationSearchFilterDTO filterDTO) {
        EntityInvoiceConfigurationSearchFilter ms = new EntityInvoiceConfigurationSearchFilter();
        ms.setSort(filterDTO.getSort());
        ms.setFreeSearch(filterDTO.getFreeSearch());
        ms.setType(toMs(filterDTO.getType()));
        return ms;
    }

    private static List<es.onebox.mgmt.datasources.ms.entity.dto.OneboxInvoiceType> toMs(List<OneboxInvoiceType> types) {
        if (types == null || types.isEmpty()) {
            return null;
        } else {
            return types.stream().map(OneboxInvoiceEntitiesConverter::toMs).collect(Collectors.toList());
        }
    }

    public static es.onebox.mgmt.datasources.ms.entity.dto.OneboxInvoiceType toMs(OneboxInvoiceType type) {
        if (type == null) {
            return null;
        } else {
            return switch (type) {
                case UNDEFINED -> es.onebox.mgmt.datasources.ms.entity.dto.OneboxInvoiceType.UNDEFINED;
                case EVENT -> es.onebox.mgmt.datasources.ms.entity.dto.OneboxInvoiceType.EVENT;
                case CHANNEL -> es.onebox.mgmt.datasources.ms.entity.dto.OneboxInvoiceType.CHANNEL;
            };
        }
    }

    private static OneboxInvoiceType toDTO(es.onebox.mgmt.datasources.ms.entity.dto.OneboxInvoiceType type) {
        if (type == null) {
            return null;
        } else {
            return switch (type) {
                case UNDEFINED -> OneboxInvoiceType.UNDEFINED;
                case EVENT -> OneboxInvoiceType.EVENT;
                case CHANNEL -> OneboxInvoiceType.CHANNEL;
            };
        }
    }
}
