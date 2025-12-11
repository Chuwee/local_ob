package es.onebox.mgmt.events.converter;

import es.onebox.mgmt.datasources.integration.dispatcher.dto.ExternalSessionBase;
import es.onebox.mgmt.events.dto.ExternalSessionBaseDTO;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;

public class ExternalSessionConverter {

    private ExternalSessionConverter() {
    }

    public static List<ExternalSessionBaseDTO> fromIntDispatcher(List<ExternalSessionBase> sourceList) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return Collections.emptyList();
        }

        return sourceList.stream().map(ExternalSessionConverter::fromIntDispatcher).toList();
    }

    public static ExternalSessionBaseDTO fromIntDispatcher(ExternalSessionBase source){
        if(source == null){
            return null;
        }
        ExternalSessionBaseDTO target = new ExternalSessionBaseDTO();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setDate(source.getDate());
        target.setStatus(source.getStatus());
        target.setStandalone(source.getStandalone());
        target.setExternalProperties(source.getExternalProperties());

        return target;
    }

}
