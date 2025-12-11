package es.onebox.mgmt.venues.converter;

import es.onebox.mgmt.datasources.ms.venue.dto.template.CreateVenueTemplateViewLink;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateView;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateViewLink;
import es.onebox.mgmt.venues.dto.CreateViewLinkDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateViewDTO;
import es.onebox.mgmt.venues.dto.ViewLinkDTO;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;

public class VenueTemplateViewLinkConverter {

    private VenueTemplateViewLinkConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static void fromMs(VenueTemplateView venueTemplateView, VenueTemplateViewDTO viewDTO) {
        if (CollectionUtils.isNotEmpty(venueTemplateView.getLinks())) {
            viewDTO.setLinks(new ArrayList<>());
            for (VenueTemplateViewLink link : venueTemplateView.getLinks()) {
                ViewLinkDTO viewLinkDTO = new ViewLinkDTO();
                viewLinkDTO.setId(link.getId());
                viewLinkDTO.setViewId(link.getViewId());
                viewLinkDTO.setRefId(link.getRefId());
                viewDTO.getLinks().add(viewLinkDTO);
            }
        }
    }

    public static CreateVenueTemplateViewLink toMs(CreateViewLinkDTO body) {
        CreateVenueTemplateViewLink createVenueTemplateViewLink = new CreateVenueTemplateViewLink();
        createVenueTemplateViewLink.setViewId(body.getViewId());
        return createVenueTemplateViewLink;
    }
}
