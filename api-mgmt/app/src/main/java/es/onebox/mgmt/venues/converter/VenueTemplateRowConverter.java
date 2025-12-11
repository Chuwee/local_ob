package es.onebox.mgmt.venues.converter;

import es.onebox.mgmt.datasources.ms.venue.dto.template.CreateVenueTemplateRow;
import es.onebox.mgmt.datasources.ms.venue.dto.template.RowDetail;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateVenueTemplateRow;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateVenueTemplateRowBulk;
import es.onebox.mgmt.venues.dto.CreateVenueTemplateRowDTO;
import es.onebox.mgmt.venues.dto.CreateVenueTemplateRowsDTO;
import es.onebox.mgmt.venues.dto.UpdateVenueTemplateRowDTO;
import es.onebox.mgmt.venues.dto.UpdateVenueTemplateRowsDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateRowDTO;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class VenueTemplateRowConverter {

    private VenueTemplateRowConverter() {
    }

    public static VenueTemplateRowDTO fromMsVenue(RowDetail row) {
        if (row == null) {
            return null;
        }
        VenueTemplateRowDTO venueTemplateRowDTO = new VenueTemplateRowDTO();
        venueTemplateRowDTO.setId(row.getId());
        venueTemplateRowDTO.setName(row.getName());
        venueTemplateRowDTO.setOrder(row.getOrder());
        venueTemplateRowDTO.setSectorId(row.getSectorId());
        return venueTemplateRowDTO;
    }

    public static CreateVenueTemplateRow toMs(CreateVenueTemplateRowDTO requestDTO) {
        CreateVenueTemplateRow msDto = new CreateVenueTemplateRow();
        msDto.setSectorId(requestDTO.getSectorId());
        msDto.setName(requestDTO.getName());
        msDto.setOrder(requestDTO.getOrder());
        return msDto;
    }

    public static List<CreateVenueTemplateRow> toMs(CreateVenueTemplateRowsDTO requestDTO) {
        if (CollectionUtils.isEmpty(requestDTO)) {
            return Collections.emptyList();
        }
        return requestDTO.stream().map(rowDTO -> {
            CreateVenueTemplateRow msDto = new CreateVenueTemplateRow();
            msDto.setSectorId(rowDTO.getSectorId());
            msDto.setName(rowDTO.getName());
            msDto.setOrder(rowDTO.getOrder());
            return msDto;
        }).collect(Collectors.toList());
    }

    public static UpdateVenueTemplateRow toMs(UpdateVenueTemplateRowDTO requestDTO) {
        UpdateVenueTemplateRow msDto = new UpdateVenueTemplateRow();
        msDto.setSectorId(requestDTO.getSectorId());
        msDto.setName(requestDTO.getName());
        msDto.setOrder(requestDTO.getOrder());
        return msDto;
    }

    public static List<UpdateVenueTemplateRowBulk> toMs(UpdateVenueTemplateRowsDTO requestDTO) {
        if (CollectionUtils.isEmpty(requestDTO)) {
            return Collections.emptyList();
        }
        return requestDTO.stream().map(rowDTO -> {
            UpdateVenueTemplateRowBulk msDto = new UpdateVenueTemplateRowBulk();
            msDto.setId(rowDTO.getId());
            msDto.setSectorId(rowDTO.getSectorId());
            msDto.setName(rowDTO.getName());
            msDto.setOrder(rowDTO.getOrder());
            return msDto;
        }).collect(Collectors.toList());
    }
}
