package es.onebox.mgmt.venues.converter;

import es.onebox.core.serializer.dto.common.CodeNameDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.venue.dto.Coordinates;
import es.onebox.mgmt.datasources.ms.venue.dto.Venue;
import es.onebox.mgmt.datasources.ms.venue.dto.VenueContact;
import es.onebox.mgmt.datasources.ms.venue.dto.VenueItemPostRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.VenueItemPutRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.VenueItemRequest;
import es.onebox.mgmt.venues.dto.CoordinatesDTO;
import es.onebox.mgmt.venues.dto.VenueContactDTO;
import es.onebox.mgmt.venues.dto.VenueDTO;
import es.onebox.mgmt.venues.dto.VenueDetailsDTO;
import es.onebox.mgmt.venues.dto.VenueItemDTO;
import es.onebox.mgmt.venues.dto.VenueItemPostRequestDTO;
import es.onebox.mgmt.venues.dto.VenueItemPutRequestDTO;
import es.onebox.mgmt.venues.dto.VenueItemRequestDTO;
import es.onebox.mgmt.venues.enums.VenueStatus;

public class VenueConverter {

    private VenueConverter() {}

    public static VenueItemDTO fromMsDTO(Venue source) {
        if (source == null) {
            return null;
        }
        return fromMsDTO(source, new VenueItemDTO());
    }

    public static VenueItemDTO fromMsDTO(Venue source, VenueItemDTO target) {
        if (source == null) {
            return null;
        }

        target.setId(source.getId());
        target.setName(source.getName());
        target.setCity(source.getCity());
        if (source.getTimezone() != null) {
            target.setTimezone(source.getTimezone().getOlsonId());
        }
        if (source.getEntity() != null) {
            target.setEntity(new IdNameDTO(source.getEntity().getId(), source.getEntity().getName()));
        }
        target.setCapacity(source.getCapacity());
        target.setType(source.getType());
        target.setPublic(source.getPublic());
        target.setCalendar(source.getCalendar());
        target.setCountry(new CodeNameDTO(source.getCountry().getCode(), source.getCountry().getName()));
        target.setCountrySubdivision(new CodeNameDTO(source.getCountrySubdivision().getCode(), source.getCountrySubdivision().getName()));
        target.setPostalCode(source.getPostalCode());
        target.setAddress(source.getAddress());
        if(source.getCoordinates() != null){
            target.setCoordinates(fromVenueCoordinatesToVenueCoordinatesDto(source.getCoordinates()));
        }
        target.setManager(source.getManager());
        target.setOwner(source.getOwner());
        target.setWebsite(source.getWebsite());
        if(source.getContact() != null) {
            target.setContact(fromVenueContactToVenueContactDto(source.getContact()));
        }
        target.setImageLogoUrl(source.getPathLogo());
        target.setGooglePlaceId(source.getGooglePlaceId());
        target.setExternalId(source.getExternalId());

        return target;
    }

    public static VenueDetailsDTO fromMsDTO(Venue source, VenueDetailsDTO target) {
        target = (VenueDetailsDTO) fromMsDTO(source, (VenueItemDTO) target);

        if (source != null) {
            target.setSpaces(source.getSpaces());
        }
        return target;
    }

    public static VenueDTO fromMsEntityToGridDetail(Venue source){
        VenueDTO tinyDetail = new VenueDTO();
        tinyDetail.setId(source.getId());
        tinyDetail.setName(source.getName());
        if(source.getStatus() != null) {
            tinyDetail.setStatus(VenueStatus.valueOf(source.getStatus().name()));
        }
        tinyDetail.setEntity(source.getEntity());
        tinyDetail.setCity(source.getCity());
        if(source.getCountry() != null){
            tinyDetail.setCountry(source.getCountry().getCode());
        }
        if(source.getTimezone() != null){
            tinyDetail.setTimezone(source.getTimezone().getOlsonId());
        }
        tinyDetail.setCapacity(source.getCapacity());
        tinyDetail.setGooglePlaceId(source.getGooglePlaceId());
        tinyDetail.setExternalId(source.getExternalId());
        return tinyDetail;
    }

    private static VenueContactDTO fromVenueContactToVenueContactDto(VenueContact msDTO){
        VenueContactDTO apiDTO = new VenueContactDTO();
            apiDTO.setName(msDTO.getName());
            apiDTO.setSurname(msDTO.getSurname());
            apiDTO.setJob(msDTO.getJob());
            apiDTO.setEmail(msDTO.getEmail());
            apiDTO.setPhone(msDTO.getPhone());
        return apiDTO;
    }

    private static VenueContact fromVenueContactDtoToVenueContact(VenueContactDTO apiDTO){
        VenueContact msDTO = new VenueContact();
            msDTO.setName(apiDTO.getName());
            msDTO.setSurname(apiDTO.getSurname());
            msDTO.setJob(apiDTO.getJob());
            msDTO.setEmail(apiDTO.getEmail());
            msDTO.setPhone(apiDTO.getPhone());
        return msDTO;
    }

    private static CoordinatesDTO fromVenueCoordinatesToVenueCoordinatesDto(Coordinates msCoordinates){
        return new CoordinatesDTO(msCoordinates.getLatitude(), msCoordinates.getLongitude());
    }

    private static Coordinates fromVenueCoordinatesDtoToVenueCoordinatesMs(CoordinatesDTO dtoCoordinates){
        return new Coordinates(dtoCoordinates.getLatitude(), dtoCoordinates.getLongitude());
    }

    public static VenueItemPostRequest toVenueItemRequestPost(VenueItemPostRequestDTO source){
        VenueItemPostRequest venuePostRequest = toBaseItemRequest(new VenueItemPostRequest(), source);

        venuePostRequest.setEntityId(source.getEntityId());

        return venuePostRequest;
    }

    public static VenueItemPutRequest toVenueItemRequestPut(VenueItemPutRequestDTO source){
        VenueItemPutRequest venuePutRequest = toBaseItemRequest(new VenueItemPutRequest(), source);

        venuePutRequest.setId(source.getId());
        venuePutRequest.setPublic(source.getPublic());
        venuePutRequest.setLogoBinary(source.getImageLogo());

        return venuePutRequest;
    }

    private static <T extends VenueItemRequest> T toBaseItemRequest(T target, VenueItemRequestDTO source){
        target.setName(source.getName());
        target.setTimezone(source.getTimezone());
        target.setCapacity(source.getCapacity());
        target.setType(source.getType());
        target.setCalendarId(source.getCalendarId());
        target.setCountryCode(source.getCountryCode());
        target.setCountrySubdivisionCode(source.getCountrySubdivisionCode());
        target.setCity(source.getCity());
        target.setPostalCode(source.getPostalCode());
        target.setAddress(source.getAddress());
        target.setManager(source.getManager());
        target.setOwner(source.getOwner());
        target.setWebsite(source.getWebsite());
        if(source.getContact() != null) {
            target.setContact(fromVenueContactDtoToVenueContact(source.getContact()));
        }
        if(source.getCoordinates()!=null){
            target.setCoordinates(fromVenueCoordinatesDtoToVenueCoordinatesMs(source.getCoordinates()));
        }
        target.setGooglePlaceId(source.getGooglePlaceId());
        target.setExternalId(source.getExternalId());
        return target;
    }
}
