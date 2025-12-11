package es.onebox.event.catalog.converter;

import es.onebox.event.catalog.dto.CatalogLocationDTO;
import es.onebox.event.catalog.dto.CatalogVenueDTO;
import es.onebox.event.catalog.elasticsearch.dto.Venue;
import es.onebox.event.common.services.S3URLResolver;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CatalogVenueConverter {

    private CatalogVenueConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static List<CatalogVenueDTO> convert(List<Long> ids, List<Venue> venues, Integer operatorId, String s3Repository) {
        if (ids == null || venues == null) {
            return Collections.emptyList();
        }
        return venues.stream()
                .filter(venue -> ids.contains(venue.getId()))
                .map(venue -> CatalogVenueConverter.convert(venue, operatorId, s3Repository))
                .collect(Collectors.toList());
    }

    public static CatalogVenueDTO convert(Long venueId, List<Venue> venues, Integer operatorId, String s3Repository) {
        if (venueId == null || venues == null) {
            return null;
        }
        return venues.stream().filter(item -> item.getId().longValue() == venueId).findFirst()
                .map(item -> CatalogVenueConverter.convert(item, operatorId, s3Repository)).orElse(null);
    }

    private static CatalogVenueDTO convert(Venue venue, Integer operatorId, String s3Repository) {
        if (venue == null) {
            return null;
        }
        CatalogVenueDTO catalogVenue = new CatalogVenueDTO();
        catalogVenue.setId(venue.getId());
        catalogVenue.setName(venue.getName());
        catalogVenue.setEntityId(venue.getEntityId());
        catalogVenue.setPostalCode(venue.getPostalCode());
        catalogVenue.setTimeZone(venue.getTimeZone());
        catalogVenue.setCountry(CatalogLocationDTO.builder().withCode(venue.getCountryCode()).withName(venue.getCountry()).build());
        catalogVenue.setCountrySubdivision(CatalogLocationDTO.builder().withCode(venue.getProvinceCode()).withName(venue.getProvince()).build());
        catalogVenue.setCity(venue.getMunicipality());
        catalogVenue.setAddress(venue.getAddress());
        catalogVenue.setGooglePlaceId(venue.getGooglePlaceId());

        if (StringUtils.isNotBlank(venue.getImage())) {
            catalogVenue.setImage(buildImageUrl(operatorId, venue.getEntityId(), venue.getId(), venue.getImage(), s3Repository));
        }
        return catalogVenue;
    }

    public static String buildImageUrl(Integer operatorId, Long entityId, Long venueId, String fileName, String s3Repository){
        return S3URLResolver.builder()
                .withUrl(s3Repository)
                .withType(S3URLResolver.S3ImageType.VENUE_IMAGE)
                .withVenueId(venueId)
                .withEntityId(entityId)
                .withOperatorId(operatorId)
                .build()
                .buildPath(fileName);
    }
}
