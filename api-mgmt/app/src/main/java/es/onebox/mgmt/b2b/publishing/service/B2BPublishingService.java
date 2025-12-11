package es.onebox.mgmt.b2b.publishing.service;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.security.Roles;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.b2b.publishing.converter.B2BPublishingConverter;
import es.onebox.mgmt.b2b.publishing.converter.B2BPublishingFilterConverter;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingDTO;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingFilterTypeDTO;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingFiltersRequest;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingFiltersResponseDTO;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingsResponseDTO;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingsSearchRequest;
import es.onebox.mgmt.b2b.publishing.enums.SeatPublishingFilterType;
import es.onebox.mgmt.b2b.utils.SeatPublishingsFilterHelper;
import es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.SeatPublishing;
import es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.SeatPublishingsFilter;
import es.onebox.mgmt.datasources.ms.client.repositories.B2BPublishingRepository;
import es.onebox.mgmt.datasources.ms.client.repositories.ClientsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class B2BPublishingService {

    private final B2BPublishingRepository b2BPublishingRepository;
    private final SecurityManager securityManager;
    private final ClientsRepository clientsRepository;
    private final SeatPublishingsFilterHelper filterHelper;

    public B2BPublishingService(B2BPublishingRepository b2BPublishingRepository, SecurityManager securityManager, ClientsRepository clientsRepository, SeatPublishingsFilterHelper filterHelper) {
        this.b2BPublishingRepository = b2BPublishingRepository;
        this.securityManager = securityManager;
        this.clientsRepository = clientsRepository;
        this.filterHelper = filterHelper;
    }

    public SeatPublishingsResponseDTO searchSeatPublishings(SeatPublishingsSearchRequest searchRequest) {
        SeatPublishingsFilter msFilter = B2BPublishingConverter.toMsFilter(searchRequest);
        filterHelper.addEntityConstraints(msFilter);
        filterHelper.checkEntityFilterConstraints(searchRequest, msFilter);
        if(!CommonUtils.isEmpty(searchRequest.getEntityIds()) && CommonUtils.isEmpty(msFilter.getEntityIds())){
            return new SeatPublishingsResponseDTO();
        }

        return B2BPublishingConverter.toDtoList(b2BPublishingRepository.searchB2bSeatPublishings(msFilter));
    }

    public SeatPublishingDTO getById(Long id) {
        Long operatorId = SecurityUtils.hasAnyRole(Roles.ROLE_OPR_MGR, Roles.ROLE_OPR_ANS) ? SecurityUtils.getUserOperatorId() : null;
        SeatPublishing seatPublishing =  b2BPublishingRepository.getB2bSeatPublishingById(
                id,
                securityManager.getVisibleEntities(SecurityUtils.getUserEntityId()), operatorId
        );
        return B2BPublishingConverter.toDto(seatPublishing, clientsRepository::getClient, clientsRepository::getClientUser);
    }

    public SeatPublishingFiltersResponseDTO getSeatsFilterOptions(String filterName, SeatPublishingFiltersRequest filterRequest) {
        SeatPublishingFilterType.validateFilter(filterName);
        SeatPublishingFilterType filterType = Stream.of(SeatPublishingFilterType.values())
                .filter(it -> it.getKey().equals(filterName))
                .findFirst()
                .orElseThrow(() -> ExceptionBuilder.build(ApiMgmtErrorCode.FILTER_NOT_FOUND, filterName));

        SeatPublishingsFilter msFilter = B2BPublishingFilterConverter.toMsFilter(filterRequest);
        filterHelper.addEntityConstraints(msFilter);
        filterHelper.checkEntityFilterConstraints(filterRequest, msFilter);

        if(!CommonUtils.isEmpty(filterRequest.getEntityIds()) && CommonUtils.isEmpty(msFilter.getEntityIds())){
            return new SeatPublishingFiltersResponseDTO();
        }

        SeatPublishingFilterTypeDTO seatPublishingFilterTypeDTO = B2BPublishingFilterConverter.convertToDTO(filterType);
        return B2BPublishingFilterConverter.buildResponse(b2BPublishingRepository.getSeatsFilterOptions(seatPublishingFilterTypeDTO, msFilter));
    }

    public SeatPublishingFiltersResponseDTO getSeatsFilterSessions(SeatPublishingFiltersRequest filterRequest) {
        SeatPublishingsFilter msFilter = B2BPublishingFilterConverter.toMsFilter(filterRequest);
        filterHelper.addEntityConstraints(msFilter);
        filterHelper.checkEntityFilterConstraints(filterRequest, msFilter);

        if(!CommonUtils.isEmpty(filterRequest.getEntityIds()) && CommonUtils.isEmpty(msFilter.getEntityIds())){
            return new SeatPublishingFiltersResponseDTO();
        }
        return B2BPublishingFilterConverter.buildResponse(b2BPublishingRepository.getSeatsFilterOptions(SeatPublishingFilterTypeDTO.SESSION, msFilter));
    }
}