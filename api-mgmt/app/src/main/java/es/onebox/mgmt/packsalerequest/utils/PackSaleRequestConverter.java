package es.onebox.mgmt.packsalerequest.utils;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.channel.packsalerequests.dto.request.FilterPackSalesRequests;
import es.onebox.mgmt.datasources.ms.channel.packsalerequests.dto.response.PackChannelSaleRequestStatus;
import es.onebox.mgmt.datasources.ms.channel.packsalerequests.dto.response.PackSaleRequestResponse;
import es.onebox.mgmt.datasources.ms.channel.packsalerequests.dto.response.PackSalesRequestBase;
import es.onebox.mgmt.packsalerequest.dto.request.PackSaleRequestsSearchFilterDTO;
import es.onebox.mgmt.packsalerequest.dto.response.ChannelSaleRequestDTO;
import es.onebox.mgmt.packsalerequest.dto.response.PackSaleRequestBaseResponseDTO;
import es.onebox.mgmt.packsalerequest.dto.response.PackSaleRequestDTO;
import es.onebox.mgmt.packsalerequest.dto.response.PackSaleRequestResponseDTO;
import es.onebox.mgmt.packsalerequest.enums.PackSaleRequestStatus;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PackSaleRequestConverter {

    private PackSaleRequestConverter() {throw new UnsupportedOperationException("Can not instantiate this class.");}

    public static FilterPackSalesRequests buildSearchFilter(PackSaleRequestsSearchFilterDTO filterDTO) {
        FilterPackSalesRequests filter = new FilterPackSalesRequests();
        filter.setOperatorId(SecurityUtils.getUserOperatorId());
        if (filterDTO.getEntityId() != null) {
            if (CollectionUtils.isEmpty(filterDTO.getChannelEntityId())) {
                filter.setChannelEntityId(new ArrayList<>());
            }
            filter.getChannelEntityId().add(filterDTO.getEntityId());
        }
        if (CollectionUtils.isNotEmpty(filterDTO.getDate())) {
            filter.setDate(filterDTO.getDate());
        }
        if (CollectionUtils.isNotEmpty(filterDTO.getStatus())) {
            filter.setStatus(filterDTO.getStatus().stream().map( st -> PackChannelSaleRequestStatus.getById(st.getId())).toList());
        }
        if (StringUtils.isNotBlank(filterDTO.getQ())) {
            filter.setQ(filterDTO.getQ());
        }
        return filter;
    }

    public static PackSaleRequestResponseDTO convertToPackSaleRequestResponseDTO(PackSaleRequestResponse response) {
        PackSaleRequestResponseDTO responseDTO = new PackSaleRequestResponseDTO();

        if (response.getData() != null) {
            List<PackSaleRequestBaseResponseDTO> convertedData = response.getData().stream()
                    .map(PackSaleRequestConverter::convertToPackSaleRequestBaseResponseDTO)
                    .collect(Collectors.toList());
            responseDTO.setData(convertedData);
        }

        responseDTO.setMetadata(response.getMetadata());
        return responseDTO;
    }

    public static PackSaleRequestBaseResponseDTO convertToPackSaleRequestBaseResponseDTO(PackSalesRequestBase source) {
        PackSaleRequestBaseResponseDTO target = new PackSaleRequestBaseResponseDTO();

        target.setId(source.getId().longValue());
        target.setDate(source.getCreationDate());
        target.setStatus(PackSaleRequestStatus.getById(source.getState().getId()));
        target.setPack(createPackSaleRequestDTO(source));
        target.setChannel(createChannelSaleRequestDTO(source));

        return target;
    }

    private static PackSaleRequestDTO createPackSaleRequestDTO(PackSalesRequestBase source) {
        PackSaleRequestDTO packDTO = new PackSaleRequestDTO();

        packDTO.setId(source.getPackId().longValue());
        packDTO.setName(source.getPackName());

        if (source.getPackEntityId() != null && source.getPackEntityName() != null) {
            packDTO.setEntity(getIdNameDTO(source.getPackEntityId().longValue(), source.getPackEntityName()));
        }

        return packDTO;
    }

    private static ChannelSaleRequestDTO createChannelSaleRequestDTO(PackSalesRequestBase source) {
        ChannelSaleRequestDTO channelDTO = new ChannelSaleRequestDTO();
        channelDTO.setId(source.getChannelId().longValue());
        channelDTO.setName(source.getChannelName());

        if (source.getChannelEntityId() != null && source.getChannelEntityName() != null) {
            channelDTO.setEntity(getIdNameDTO(source.getChannelEntityId().longValue(), source.getChannelEntityName()));
        }

        return channelDTO;
    }

    private static IdNameDTO getIdNameDTO(Long id, String name) {
        IdNameDTO idNameDTO = new IdNameDTO();
        idNameDTO.setId(id);
        idNameDTO.setName(name);
        return idNameDTO;
    }

}
