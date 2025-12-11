package es.onebox.mgmt.venues.converter;

import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.LimitlessValueDTO;
import es.onebox.mgmt.datasources.ms.venue.dto.space.VenueSpace;
import es.onebox.mgmt.datasources.ms.venue.dto.space.VenueSpaces;
import es.onebox.mgmt.venues.dto.VenueSpaceDTO;
import es.onebox.mgmt.venues.dto.VenueSpacePostRequest;
import es.onebox.mgmt.venues.dto.VenueSpacePutRequest;
import es.onebox.mgmt.venues.dto.VenueSpacesResponse;

import java.util.stream.Collectors;

public class SpaceConverter {

    public static VenueSpaceDTO toSpaceDTO(VenueSpace msSpace) {
        VenueSpaceDTO spaceDTO = new VenueSpaceDTO();
        spaceDTO.setVenueId(msSpace.getVenueId());
        spaceDTO.setSpaceId(msSpace.getId());
        spaceDTO.setName(msSpace.getName());
        spaceDTO.setCapacity(new LimitlessValueDTO(msSpace.getCapacity()));
        spaceDTO.setDefault(msSpace.getDefault());
        spaceDTO.setNotes(msSpace.getNotes());
        return spaceDTO;
    }

    public static VenueSpace toSpaceDTO(Long venueId, Long spaceId, VenueSpacePostRequest sourceRequest){
        VenueSpace spaceDTO = new VenueSpace();
        if(venueId != null) {
            spaceDTO.setVenueId(venueId);
        }
        if(venueId != null) {
            spaceDTO.setId(spaceId);
        }
        spaceDTO.setName(sourceRequest.getName());
        spaceDTO.setCapacity(ConverterUtils.getIntLimitlessValue(sourceRequest.getCapacity()));
        spaceDTO.setNotes(sourceRequest.getNotes());
        return spaceDTO;
    }

    public static VenueSpace toSpaceDTO(Long venueId, Long spaceId, VenueSpacePutRequest sourceRequest){
        VenueSpace spaceDTO = new VenueSpace();
        if(venueId != null) {
            spaceDTO.setVenueId(venueId);
        }
        if(venueId != null) {
            spaceDTO.setId(spaceId);
        }
        spaceDTO.setName(sourceRequest.getName());
        spaceDTO.setCapacity(ConverterUtils.getIntLimitlessValue(sourceRequest.getCapacity()));
        spaceDTO.setDefault(sourceRequest.getDefault());
        spaceDTO.setNotes(sourceRequest.getNotes());
        return spaceDTO;
    }

    public static VenueSpacesResponse toVenueSpacesResponse(VenueSpaces venueSpaces) {
        VenueSpacesResponse spaces = new VenueSpacesResponse();
        spaces.setData(
                venueSpaces.getData()
                        .stream()
                        .map(SpaceConverter::toSpaceDTO)
                        .collect(Collectors.toList())
        );
        spaces.setMetadata(venueSpaces.getMetadata());
        return spaces;
    }
}
