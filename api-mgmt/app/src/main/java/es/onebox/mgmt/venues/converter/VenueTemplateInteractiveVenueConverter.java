package es.onebox.mgmt.venues.converter;

import es.onebox.mgmt.datasources.ms.venue.dto.template.ExternalPlugin;
import es.onebox.mgmt.datasources.ms.venue.dto.template.InteractiveVenue;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateInteractiveVenue;
import es.onebox.mgmt.venues.dto.ExternalPluginDTO;
import es.onebox.mgmt.venues.dto.InteractiveVenueDTO;
import es.onebox.mgmt.venues.dto.InteractiveVenueRequestDTO;
import es.onebox.mgmt.venues.enums.ExternalPluginType;

import java.util.List;
import java.util.stream.Collectors;

public class VenueTemplateInteractiveVenueConverter {

    private VenueTemplateInteractiveVenueConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static InteractiveVenueDTO fromMsDTO(InteractiveVenue source) {
        if (source == null) {
            return null;
        }
        InteractiveVenueDTO target = new InteractiveVenueDTO();
        target.setEnabled(source.getEnabled());
        target.setMultimediaContentCode(source.getMultimediaContentCode());
        target.setExternalMinimapId(source.getExternalMinimapId());
        target.setExternalPlugins(fromMsDTO(source.getExternalPlugins()));

        return target;
    }

    private static List<ExternalPluginDTO> fromMsDTO(List<ExternalPlugin> source) {
        if (source == null) {
            return null;
        }
        return source.stream()
                .map(VenueTemplateInteractiveVenueConverter::fromMsDTO)
                .collect(Collectors.toList());
    }

    private static ExternalPluginDTO fromMsDTO(ExternalPlugin source) {
        ExternalPluginDTO target = new ExternalPluginDTO();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setType(ExternalPluginType.valueOf(source.getType().name()));
        target.setEnabled(source.getEnabled());
        return target;
    }

    public static UpdateInteractiveVenue toMsDTO(InteractiveVenueRequestDTO source) {
        UpdateInteractiveVenue target = new UpdateInteractiveVenue();
        target.setEnabled(source.getEnabled());
        target.setMultimediaContentCode(source.getMultimediaContentCode());
        target.setExternalMinimapId(source.getExternalMinimapId());
        target.setExternalPluginIds(source.getExternalPluginIds());
        return target;
    }
}
