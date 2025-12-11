package es.onebox.mgmt.venues.converter;

import es.onebox.mgmt.datasources.ms.venue.dto.template.DynamicTag;
import es.onebox.mgmt.datasources.ms.venue.dto.template.DynamicTagGroup;
import es.onebox.mgmt.venues.dto.DynamicTagDTO;
import es.onebox.mgmt.venues.dto.DynamicTagGroupDTO;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class VenueTemplateDynamicTagsConverter {

    private VenueTemplateDynamicTagsConverter() {
    }

    public static List<DynamicTagGroupDTO> fromMsVenueGroups(List<DynamicTagGroup> tagGroups) {
        if (CollectionUtils.isEmpty(tagGroups)) {
            return List.of();
        }
        return tagGroups.stream().map(t -> {
            DynamicTagGroupDTO tag = new DynamicTagGroupDTO();
            tag.setId(t.getId());
            tag.setCode(t.getCode());
            tag.setName(t.getName());
            return tag;
        }).toList();
    }

    public static List<DynamicTagDTO> fromMsVenueTags(List<DynamicTag> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            return List.of();
        }
        return tags.stream()
                .map(tag -> {
                    DynamicTagDTO labelDTO = new DynamicTagDTO();
                    labelDTO.setId(tag.getId());
                    labelDTO.setCode(tag.getCode());
                    labelDTO.setName(tag.getName());
                    labelDTO.setColor(tag.getColor());
                    labelDTO.setDefault(tag.getDefault());
                    return labelDTO;
                })
                .collect(Collectors.toList());
    }
}
