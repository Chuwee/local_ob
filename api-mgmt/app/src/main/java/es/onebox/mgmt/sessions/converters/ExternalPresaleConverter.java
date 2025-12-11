package es.onebox.mgmt.sessions.converters;

import es.onebox.mgmt.datasources.integration.dispatcher.dto.ExternalPresaleBase;
import es.onebox.mgmt.events.dto.ExternalPresaleBaseDTO;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;

public class ExternalPresaleConverter {

    private ExternalPresaleConverter() {
    }

    public static List<ExternalPresaleBaseDTO> fromIntDispatcher(List<ExternalPresaleBase> sourceList) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return Collections.emptyList();
        }

        return sourceList.stream().map(ExternalPresaleConverter::fromIntDispatcher).toList();
    }

    public static ExternalPresaleBaseDTO fromIntDispatcher(ExternalPresaleBase source){
        if(source == null){
            return null;
        }

        ExternalPresaleBaseDTO target = new ExternalPresaleBaseDTO();
        target.setId(source.getId());
        target.setName(source.getName());

        return target;
    }
}
