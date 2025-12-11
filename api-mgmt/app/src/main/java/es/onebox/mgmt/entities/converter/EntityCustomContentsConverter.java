package es.onebox.mgmt.entities.converter;

import es.onebox.mgmt.datasources.ms.entity.dto.EntityCustomContents;
import es.onebox.mgmt.datasources.ms.entity.dto.UpdateEntityCustomContents;
import es.onebox.mgmt.entities.dto.EntityCustomContentsDTO;
import es.onebox.mgmt.entities.dto.UpdateEntityCustomContentsListDTO;
import es.onebox.mgmt.entities.enums.EntityCustomContentsType;

import java.util.List;
import java.util.stream.Collectors;

public class EntityCustomContentsConverter {

    public static List<EntityCustomContentsDTO> toDTO (List<EntityCustomContents> customContentsList) {
        return customContentsList.stream()
                .map(customContents -> {
                    EntityCustomContentsDTO dto = new EntityCustomContentsDTO();
                    dto.setTag(EntityCustomContentsType.valueOf(customContents.getTag()));
                    dto.setValue(customContents.getValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public static List<UpdateEntityCustomContents> toMs(UpdateEntityCustomContentsListDTO request) {
        return request.stream()
                .map(customContents -> {
                    UpdateEntityCustomContents customContentsDTO = new UpdateEntityCustomContents();
                    customContentsDTO.setExtension(customContents.getExtension().name());
                    customContentsDTO.setTag(customContents.getTag().name());
                    customContentsDTO.setValue(customContents.getValue());
                    return customContentsDTO;
                })
                .collect(Collectors.toList());
    }
}
