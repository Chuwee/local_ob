package es.onebox.mgmt.venues.converter;

import es.onebox.mgmt.datasources.ms.venue.dto.template.UpsertVenueTemplateImage;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateImage;
import es.onebox.mgmt.venues.dto.UpsertVenueTemplateImageDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateImageDTO;

import java.util.List;
import java.util.stream.Collectors;

public class VenueTemplateImageConverter {

    private VenueTemplateImageConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static VenueTemplateImageDTO fromMs(VenueTemplateImage msDto) {
        VenueTemplateImageDTO dto = new VenueTemplateImageDTO();
        dto.setId(msDto.getId());
        dto.setUrl(msDto.getUrl());
        return dto;
    }

    public static List<VenueTemplateImageDTO> fromMs(List<VenueTemplateImage> source) {
        if (source == null) {
            return null;
        }
        return source.stream().map(VenueTemplateImageConverter::fromMs).collect(Collectors.toList());
    }

    public static UpsertVenueTemplateImage toMs(UpsertVenueTemplateImageDTO dto) {
        UpsertVenueTemplateImage msDto = new UpsertVenueTemplateImage();
        msDto.setFilename(dto.getFilename());
        msDto.setImageBinary(dto.getImageBinary());
        msDto.setTemporary(dto.getTemporary());
        return msDto;
    }
}
