package es.onebox.mgmt.venues.converter;

import es.onebox.mgmt.datasources.ms.venue.dto.template.AccessibilityType;
import es.onebox.mgmt.datasources.ms.venue.dto.template.NotNumberedZone;
import es.onebox.mgmt.datasources.ms.venue.dto.template.NotNumberedZoneCapacity;
import es.onebox.mgmt.datasources.ms.venue.dto.template.QuotaCounter;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateNotNumberedZone;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VisibilityType;
import es.onebox.mgmt.venues.dto.BaseNotNumberedZoneDTO;
import es.onebox.mgmt.venues.dto.BlockingReasonCounterDTO;
import es.onebox.mgmt.venues.dto.CreateNotNumberedZoneBulkDTO;
import es.onebox.mgmt.venues.dto.CreateNotNumberedZoneDTO;
import es.onebox.mgmt.venues.dto.NotNumberedZoneDTO;
import es.onebox.mgmt.venues.dto.QuotaCounterDTO;
import es.onebox.mgmt.venues.dto.QuotaCounterNNZCreationDTO;
import es.onebox.mgmt.venues.dto.StatusCounterDTO;
import es.onebox.mgmt.venues.dto.UpdateNotNumberedZoneDTO;
import es.onebox.mgmt.venues.dto.UpdateNotNumberedZonesBulkDTO;
import es.onebox.mgmt.venues.enums.SeatStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class VenueTemplateNotNumberedZoneConverter {

    private VenueTemplateNotNumberedZoneConverter() {
    }

    public static BaseNotNumberedZoneDTO fromMsVenue(NotNumberedZone nnZone) {
        if (nnZone == null) {
            return null;
        }

        BaseNotNumberedZoneDTO nnZoneDTO = new BaseNotNumberedZoneDTO();
        nnZoneDTO.setId(nnZone.getId());
        nnZoneDTO.setName(nnZone.getName());
        nnZoneDTO.setCapacity(nnZone.getCapacity());
        nnZoneDTO.setSectorId(nnZone.getSectorId());
        nnZoneDTO.setOrder(nnZone.getOrder());
        return nnZoneDTO;
    }

    public static NotNumberedZoneDTO fromMsVenue(NotNumberedZoneCapacity nnz) {
        if (nnz == null) {
            return null;
        }
        NotNumberedZoneDTO result =  new NotNumberedZoneDTO();
        result.setId(nnz.getId());
        result.setName(nnz.getName());
        result.setSectorId(nnz.getSectorId());
        result.setViewId(nnz.getViewId());
        result.setCapacity(nnz.getCapacity());
        StatusCounterDTO statusCounterDTO = new StatusCounterDTO();
        statusCounterDTO.setStatus(SeatStatus.byId(nnz.getStatusValue()));
        statusCounterDTO.setCount(nnz.getCapacity());
        result.setStatusCounters(Collections.singletonList(statusCounterDTO));
        result.setBlockingReasonCounters(new ArrayList<>());
        if (nnz.getStatusValue().equals(SeatStatus.PROMOTOR_LOCKED.getStatus())) {
            BlockingReasonCounterDTO blockingReasonCounterDTO = new BlockingReasonCounterDTO();
            blockingReasonCounterDTO.setBlockingReason(nnz.getBlockingReason().longValue());
            blockingReasonCounterDTO.setCount(nnz.getCapacity());
            result.getBlockingReasonCounters().add(blockingReasonCounterDTO);
        }
        result.setPriceType(nnz.getPriceType());
        result.setQuota(nnz.getQuota());
        result.setVisibility(VisibilityType.byId(nnz.getVisibilityValue()));
        result.setAccessibility(AccessibilityType.byId(nnz.getAccessibilityValue()));
        result.setGate(nnz.getGate());
        result.setQuotaCounters(toDTO(nnz.getQuotaCounters()));
        return result;
    }

    public static UpdateNotNumberedZone toDTO(UpdateNotNumberedZoneDTO body) {
        UpdateNotNumberedZone out = new UpdateNotNumberedZone();
        out.setName(body.getName());
        out.setCapacity(body.getCapacity());
        out.setQuotaId(body.getQuotaId());
        out.setViewId(body.getViewId());
        out.setOrder(body.getOrder());
        return out;
    }

    public static Set<UpdateNotNumberedZone> toDTO(UpdateNotNumberedZonesBulkDTO body) {
        return body.stream().map(el -> {
            UpdateNotNumberedZone dto = new UpdateNotNumberedZone();
            dto.setCapacity(el.getCapacity());
            dto.setQuotaId(el.getQuotaId());
            dto.setName(el.getName());
            dto.setId(el.getId());
            dto.setViewId(el.getViewId());
            dto.setOrder(el.getOrder());
            return dto;
        }).collect(Collectors.toSet());
    }

    public static Set<NotNumberedZone> toMS(CreateNotNumberedZoneBulkDTO body) {
        return body.stream().map(VenueTemplateNotNumberedZoneConverter::toMS).collect(Collectors.toSet());
    }

    public static NotNumberedZone toMS(CreateNotNumberedZoneDTO in) {
        NotNumberedZone body = new NotNumberedZone();
        body.setName(in.getName());
        body.setCapacity(in.getCapacity());
        body.setSectorId(in.getSectorId());
        body.setViewId(in.getViewId());
        body.setQuotaCounters(toMS(in.getQuotaCounters()));
        return body;
    }

    private static List<QuotaCounterDTO> toDTO(List<QuotaCounter> quotaCounters) {
        if(quotaCounters == null || quotaCounters.isEmpty()) {
            return null;
        }
        return quotaCounters.stream().map(VenueTemplateNotNumberedZoneConverter::toDTO).toList();
    }

    private static QuotaCounterDTO toDTO(QuotaCounter quotaCounter) {
        if(quotaCounter == null) {
            return null;
        }
        QuotaCounterDTO counterDTO = new QuotaCounterDTO();
        counterDTO.setQuota(quotaCounter.getQuotaId());
        counterDTO.setAvailable(quotaCounter.getAvailable());
        counterDTO.setCount(quotaCounter.getCount());
        return counterDTO;
    }

    private static List<QuotaCounter> toMS(List<QuotaCounterNNZCreationDTO> quotaCounters) {
        if(quotaCounters == null) {
            return null;
        }
        return quotaCounters.stream().map(VenueTemplateNotNumberedZoneConverter::toMS).collect(Collectors.toList());
    }

    private static QuotaCounter toMS(QuotaCounterNNZCreationDTO quotaCounterNNZCreationDTO) {
        if(quotaCounterNNZCreationDTO == null) {
            return null;
        }
        QuotaCounter quotaCounter = new QuotaCounter();
        quotaCounter.setQuotaId(quotaCounterNNZCreationDTO.getQuota());
        quotaCounter.setCount(quotaCounterNNZCreationDTO.getCount().longValue());
        return quotaCounter;
    }
}
