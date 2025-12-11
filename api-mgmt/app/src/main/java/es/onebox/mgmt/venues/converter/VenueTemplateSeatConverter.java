package es.onebox.mgmt.venues.converter;

import es.onebox.mgmt.datasources.ms.venue.dto.template.AccessibilityType;
import es.onebox.mgmt.datasources.ms.venue.dto.template.CreateVenueTemplateSeat;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateSeatStatus;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateVenueTemplateSeat;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateBaseSeat;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateSeat;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VisibilityType;
import es.onebox.mgmt.venues.dto.CreateVenueTemplateSeatDTO;
import es.onebox.mgmt.venues.dto.CreateVenueTemplateSeatsDTO;
import es.onebox.mgmt.venues.dto.UpdateVenueTemplateSeatDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateBaseSeatDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateSeatDTO;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VenueTemplateSeatConverter {

    private VenueTemplateSeatConverter() {
    }

    public static CreateVenueTemplateSeat toMs(CreateVenueTemplateSeatDTO requestDTO) {
        CreateVenueTemplateSeat msDto = new CreateVenueTemplateSeat();
        msDto.setRowId(requestDTO.getRowId());
        msDto.setName(requestDTO.getName());
        msDto.setViewId(requestDTO.getViewId());
        msDto.setPositionX(requestDTO.getPositionX());
        msDto.setPositionY(requestDTO.getPositionY());
        msDto.setWeight(requestDTO.getWeight());
        msDto.setSort(requestDTO.getSort());
        msDto.setRowBlock(requestDTO.getRowBlock());
        msDto.setExternalId(requestDTO.getExternalId());
        return msDto;
    }

    public static List<CreateVenueTemplateSeat> toMs(CreateVenueTemplateSeatsDTO requestDTO) {
        if (CollectionUtils.isEmpty(requestDTO)) {
            return new ArrayList<>();
        }
        return requestDTO.stream().map(VenueTemplateSeatConverter::toMs).collect(Collectors.toList());
    }

    public static UpdateVenueTemplateSeat[] toMs(UpdateVenueTemplateSeatDTO[] seats) {
        if (seats == null || seats.length == 0) {
            return null;
        }
        UpdateVenueTemplateSeat[] msSeats = new UpdateVenueTemplateSeat[seats.length];
        for (int i = 0; i < seats.length; i++) {
            msSeats[i] = toMs(seats[i]);
        }
        return msSeats;
    }

    private static UpdateVenueTemplateSeat toMs(UpdateVenueTemplateSeatDTO dto) {
        UpdateVenueTemplateSeat seat = new UpdateVenueTemplateSeat();
        seat.setStatus(dto.getStatus() == null ? null : UpdateSeatStatus.valueOf(dto.getStatus().name()));
        seat.setBlockingReason(dto.getBlockingReason());
        seat.setQuota(dto.getQuota());
        seat.setRowId(dto.getRowId());
        seat.setName(dto.getName());
        seat.setViewId(dto.getViewId());
        seat.setPositionX(dto.getPositionX());
        seat.setPositionY(dto.getPositionY());
        seat.setWeight(dto.getWeight());
        seat.setSort(dto.getSort());
        seat.setRowBlock(dto.getRowBlock());
        seat.setExternalId(dto.getExternalId());
        seat.setId(dto.getId());
        seat.setPriceType(dto.getPriceType());
        seat.setVisibility(dto.getVisibility());
        seat.setAccessibility(dto.getAccessibility());
        seat.setGate(dto.getGate());
        seat.setDynamicTag1(dto.getDynamicTag1());
        seat.setDynamicTag2(dto.getDynamicTag2());
        return seat;
    }

    public static List<VenueTemplateBaseSeatDTO> toDto(List<VenueTemplateBaseSeat> seats) {
        if (CollectionUtils.isEmpty(seats)) {
            return new ArrayList<>();
        }
        return seats.stream().map(VenueTemplateSeatConverter::toDto).collect(Collectors.toList());
    }

    public static VenueTemplateSeatDTO toDto(VenueTemplateSeat seat) {
        VenueTemplateSeatDTO dto = new VenueTemplateSeatDTO();
        toDto(dto, seat);
        dto.setRowName(seat.getRowName());
        dto.setSectorId(seat.getSectorId());
        dto.setSectorCode(seat.getSectorCode());
        dto.setSectorName(seat.getSectorName());
        dto.setViewName(seat.getViewName());
        return dto;
    }

    public static VenueTemplateBaseSeatDTO toDto(VenueTemplateBaseSeat in) {
        return toDto(new VenueTemplateBaseSeatDTO(), in);
    }

    public static VenueTemplateBaseSeatDTO toDto(VenueTemplateBaseSeatDTO out, VenueTemplateBaseSeat in) {
        out.setId(in.getId());
        out.setName(in.getName());
        out.setStatus(in.getStatus() == null ? null : in.getStatus());
        out.setRowId(in.getRowId());
        out.setPriceZoneId(in.getPriceZoneId());
        out.setQuotaId(in.getQuotaId());
        out.setViewId(in.getViewId());
        out.setVisibility(VisibilityType.byId(in.getVisibility()));
        out.setAccessibility(AccessibilityType.byId(in.getAccessibility()));
        out.setRowBlock(in.getRowBlock());
        out.setSort(in.getSort());
        out.setWeight(in.getWeight());
        out.setExternalId(in.getExternalId());
        out.setPositionX(in.getPositionX());
        out.setPositionY(in.getPositionY());
        return out;
    }
}
