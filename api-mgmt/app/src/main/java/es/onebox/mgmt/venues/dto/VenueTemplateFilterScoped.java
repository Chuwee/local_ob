package es.onebox.mgmt.venues.dto;

import es.onebox.mgmt.venues.enums.VenueTemplateScopeDTO;

import java.util.List;

public interface VenueTemplateFilterScoped {

    Long getEventId();

    List<VenueTemplateScopeDTO> getScope();

    void setScope(List<VenueTemplateScopeDTO> scope);
}
