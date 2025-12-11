package es.onebox.mgmt.venues.converter;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTagCounterDTO;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTagDTO;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTagNotNumberedZoneDTO;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTagSeatDTO;
import es.onebox.mgmt.venues.dto.BaseVenueTagDTO;
import es.onebox.mgmt.venues.dto.VenueTagBlockingReasonCounterDTO;
import es.onebox.mgmt.venues.dto.VenueTagNotNumberedZoneBulkRequestDTO;
import es.onebox.mgmt.venues.dto.VenueTagNotNumberedZoneRequestDTO;
import es.onebox.mgmt.venues.dto.VenueTagQuotaCounterDTO;
import es.onebox.mgmt.venues.dto.VenueTagSeatRequestDTO;
import es.onebox.mgmt.venues.dto.VenueTagStatusCounterDTO;
import es.onebox.mgmt.venues.enums.VenueTagStatus;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Optional;

public class VenueTagConverter {

    private VenueTagConverter() {
    }

    public static VenueTagSeatDTO[] fromVenueTagRequest(VenueTagSeatRequestDTO[] tagRequests) {
        VenueTagSeatDTO[] tagsDTO = new VenueTagSeatDTO[tagRequests.length];
        int i = 0;
        for (VenueTagSeatRequestDTO capacity : tagRequests) {
            VenueTagSeatDTO tagDTO = (VenueTagSeatDTO) fromVenueTagRequest(capacity, new VenueTagSeatDTO());
            tagDTO.setStatus(capacity.getStatus() != null ?
                    es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTagStatus.getById(capacity.getStatus().getStatus()) : null);
            tagDTO.setBlockingReason(capacity.getBlockingReason());
            tagDTO.setQuota(capacity.getQuota());
            tagsDTO[i] = tagDTO;
            i++;
        }
        return tagsDTO;
    }

    public static VenueTagNotNumberedZoneDTO[] fromVenueTagRequest(VenueTagNotNumberedZoneRequestDTO[] tagRequests) {
        VenueTagNotNumberedZoneDTO[] tagsDTO = new VenueTagNotNumberedZoneDTO[tagRequests.length];
        int i = 0;
        for (VenueTagNotNumberedZoneRequestDTO capacity : tagRequests) {
            VenueTagNotNumberedZoneDTO tagDTO = (VenueTagNotNumberedZoneDTO) fromVenueTagRequest(capacity, new VenueTagNotNumberedZoneDTO());
            if (!CommonUtils.isEmpty(capacity.getStatusCounters())) {
                tagDTO.setStatusCounters(new ArrayList<>());
                for (VenueTagStatusCounterDTO status : capacity.getStatusCounters()) {
                    tagDTO.getStatusCounters().add(fillCapacityCounter(
                            status.getStatus().getStatus(), status.getSource(), status.getCount()));
                }
            }
            if (!CommonUtils.isEmpty(capacity.getBlockingReasonCounters())) {
                tagDTO.setBlockingReasonCounters(new ArrayList<>());
                for (VenueTagBlockingReasonCounterDTO br : capacity.getBlockingReasonCounters()) {
                    tagDTO.getBlockingReasonCounters().add(fillCapacityCounter(
                            br.getBlockingReason().intValue(), br.getSource(), br.getCount()));
                }
            }
            if (!CommonUtils.isEmpty(capacity.getQuotaCounters())) {
                tagDTO.setQuotaCounters(new ArrayList<>());
                for (VenueTagQuotaCounterDTO quota : capacity.getQuotaCounters()) {
                    tagDTO.getQuotaCounters().add(fillQuotaCounter(
                            quota.getQuota().intValue(), quota.getSource(), quota.getCount()));
                }
            }
            tagsDTO[i] = tagDTO;
            i++;
        }
        return tagsDTO;
    }

    public static VenueTagNotNumberedZoneDTO[] fromVenueTagRequest(VenueTagNotNumberedZoneBulkRequestDTO[] tagRequests) {
        VenueTagNotNumberedZoneDTO[] tagsDTO = new VenueTagNotNumberedZoneDTO[tagRequests.length];
        int i = 0;
        for (VenueTagNotNumberedZoneBulkRequestDTO capacity : tagRequests) {
            VenueTagNotNumberedZoneDTO tagDTO = (VenueTagNotNumberedZoneDTO) fromVenueTagRequest(capacity, new VenueTagNotNumberedZoneDTO());
            Optional.ofNullable(capacity.getStatus())
                    .map(VenueTagStatus::getStatus)
                    .map(es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTagStatus::getById)
                    .ifPresent(tagDTO::setStatus);
            tagDTO.setBlockingReason(capacity.getBlockingReason());
            tagDTO.setQuota(capacity.getQuota());
            tagsDTO[i] = tagDTO;
            i++;
        }
        return tagsDTO;
    }

    private static VenueTagCounterDTO fillQuotaCounter(Integer target, String source, Integer count) {
        VenueTagCounterDTO counter = new VenueTagCounterDTO(target, count);
        if (source != null) {
            counter.setSourceQuota(Integer.parseInt(source));
        }
        return counter;
    }

    private static VenueTagCounterDTO fillCapacityCounter(Integer target, String source, Integer count) {
        VenueTagCounterDTO counter = new VenueTagCounterDTO(target, count);
        if (source != null) {
            if (StringUtils.isNumeric(source)) {
                counter.setSourceBlockingReason(Integer.parseInt(source));
            } else {
                counter.setSourceStatus(VenueTagStatus.valueOf(source).getStatus());
            }
        }
        return counter;
    }

    private static VenueTagDTO fromVenueTagRequest(BaseVenueTagDTO tagRequestDTO, VenueTagDTO tagDTO) {
        tagDTO.setId(tagRequestDTO.getId());
        tagDTO.setPriceType(tagRequestDTO.getPriceType());
        tagDTO.setVisibility(tagRequestDTO.getVisibility());
        tagDTO.setAccessibility(tagRequestDTO.getAccessibility());
        tagDTO.setGate(tagRequestDTO.getGate());
        tagDTO.setDynamicTag1(tagRequestDTO.getDynamicTag1());
        tagDTO.setDynamicTag2(tagRequestDTO.getDynamicTag2());
        return tagDTO;
    }

}
