package es.onebox.mgmt.events.converter;

import es.onebox.mgmt.datasources.integration.dispatcher.dto.ExternalEventBase;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.ExternalEventBaseList;
import es.onebox.mgmt.events.dto.ExternalEventBaseDTO;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;

public class ExternalEventConverter {

    private ExternalEventConverter() {
    }

    public static List<ExternalEventBaseDTO> fromIntDispatcher(ExternalEventBaseList sourceList) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return Collections.emptyList();
        }

        return sourceList.stream().map(ExternalEventConverter::fromIntDispatcher).toList();
    }

    public static ExternalEventBaseDTO fromIntDispatcher(ExternalEventBase source){
        if(source == null){
            return null;
        }

        ExternalEventBaseDTO target = new ExternalEventBaseDTO();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setInventoryId(source.getInventoryId());
        target.setStandalone(source.getStandalone());

        return target;
    }

}
