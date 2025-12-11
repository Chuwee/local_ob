package es.onebox.mgmt.venues.converter;

import es.onebox.mgmt.datasources.ms.accesscontrol.dto.SkidataVenueConfig;
import es.onebox.mgmt.venues.dto.SkidataVenueConfigDTO;

public class SkidataVenueConfigConverter {

    private SkidataVenueConfigConverter() {
    }

    public static SkidataVenueConfigDTO fromMsEvent(SkidataVenueConfig entity) {
        if (entity == null) {
            return null;
        }
        SkidataVenueConfigDTO dto = new SkidataVenueConfigDTO();
        dto.setHost(entity.getHost());
        dto.setPort(entity.getPort());
        dto.setIssuer(entity.getIssuer());
        dto.setReceiver(entity.getReceiver());
        dto.setAuthorizationKey(entity.getAuthorizationKey());
        return dto;
    }

    public static SkidataVenueConfig toMsEvent(SkidataVenueConfigDTO dto) {
        if (dto == null) {
            return null;
        }
        SkidataVenueConfig entity = new SkidataVenueConfig();
        entity.setHost(dto.getHost());
        entity.setPort(dto.getPort());
        entity.setIssuer(dto.getIssuer());
        entity.setReceiver(dto.getReceiver());
        entity.setAuthorizationKey(dto.getAuthorizationKey());
        return entity;
    }
}
