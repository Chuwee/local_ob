package es.onebox.mgmt.b2b.publishing.converter;

import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingFilterDTO;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingFilterSessionsRequest;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingFilterTypeDTO;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingFiltersRequest;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingFiltersResponseDTO;
import es.onebox.mgmt.b2b.publishing.enums.SeatPublishingFilterType;
import es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.SeatPublishingFilter;
import es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.SeatPublishingFilterResponse;
import es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.SeatPublishingsFilter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class B2BPublishingFilterConverter {

    private B2BPublishingFilterConverter() {
        throw new UnsupportedOperationException();
    }

    public static SeatPublishingsFilter toMsFilter(SeatPublishingFiltersRequest filterRequest) {
        SeatPublishingsFilter seatPublishingsFilter = new SeatPublishingsFilter();
        commonFilterSetup(seatPublishingsFilter, filterRequest);
        return seatPublishingsFilter;
    }

    public static SeatPublishingFiltersResponseDTO buildResponse(SeatPublishingFilterResponse response) {
        SeatPublishingFiltersResponseDTO seatPublishingFiltersResponse = new SeatPublishingFiltersResponseDTO();
        seatPublishingFiltersResponse.setData(convert(response.getData()));
        seatPublishingFiltersResponse.setMetadata(response.getMetadata());
        return seatPublishingFiltersResponse;
    }

    public static SeatPublishingFilterTypeDTO convertToDTO(SeatPublishingFilterType filterType) {
        return SeatPublishingFilterTypeDTO.valueOf(filterType.name());
    }

    private static void commonFilterSetup(SeatPublishingsFilter filter, Object filterRequest) {
        if (filterRequest instanceof SeatPublishingFilterSessionsRequest) {
            SeatPublishingFilterSessionsRequest request = (SeatPublishingFilterSessionsRequest) filterRequest;
            setupCommonFilter(filter, request.getTerm(), request.getLimit(), request.getOffset(), request.getFrom(), request.getTo());
            filter.setEventIds(request.getEventIds());
        } else if (filterRequest instanceof SeatPublishingFiltersRequest) {
            SeatPublishingFiltersRequest request = (SeatPublishingFiltersRequest) filterRequest;
            setupCommonFilter(filter, request.getTerm(), request.getLimit(), request.getOffset(), request.getFrom(), request.getTo());
        }
    }

    private static void setupCommonFilter(SeatPublishingsFilter filter, String term, Long limit, Long offset, ZonedDateTime from, ZonedDateTime to) {
        filter.setQ(term);
        filter.setLimit(limit);
        filter.setOffset(offset);
        filter.setDateFrom(from);
        filter.setDateTo(to);
    }

    private static List<SeatPublishingFilterDTO> convert(List<SeatPublishingFilter> seatPublishingFilter) {
        List<SeatPublishingFilterDTO> seatPublishingFilters = new ArrayList<>();
        for (SeatPublishingFilter dto : seatPublishingFilter) {
            SeatPublishingFilterDTO seatPublishingFilterDTO = new SeatPublishingFilterDTO();
            seatPublishingFilterDTO.setDate(dto.getDate());
            seatPublishingFilterDTO.setId(dto.getId());
            seatPublishingFilterDTO.setName(dto.getName());
            seatPublishingFilters.add(seatPublishingFilterDTO);
        }
        return seatPublishingFilters;
    }
}
