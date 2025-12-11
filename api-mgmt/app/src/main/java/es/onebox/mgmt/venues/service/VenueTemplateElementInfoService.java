package es.onebox.mgmt.venues.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.SessionTemplateInfoBulkUpdateRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.SessionTemplateInfoListResponse;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.SessionTemplateInfoRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.SessionTemplateInfoResponse;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.SessionTemplateInfoStatusUpdateRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.SessionUpdateTemplateInfo;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.TemplateInfoBaseResponse;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.TemplateInfoBulkUpdateRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.TemplateInfoCreateRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.TemplateInfoImagesDeleteFilterDTO;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.TemplateInfoListResponse;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.UpdateTemplateInfoDefault;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.enums.ElementInfoImageType;
import es.onebox.mgmt.datasources.ms.venue.repository.VenueTemplateElementsInfoRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.sessions.dto.templateelementsinfo.SessionVenueTemplateElementInfoSearchResponseDTO;
import es.onebox.mgmt.templateszones.dto.TemplateZonesDTO;
import es.onebox.mgmt.templateszones.dto.TemplatesZonesRequestFilterDTO;
import es.onebox.mgmt.templateszones.dto.TemplatesZonesResponseDTO;
import es.onebox.mgmt.templateszones.enums.TemplatesZonesStatus;
import es.onebox.mgmt.templateszones.service.TemplatesZonesService;
import es.onebox.mgmt.validation.ValidationService;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementAggregatedInfoDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementDefaultInfoCreateDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementDefaultInfoUpdateDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfoBulkRequestDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfoBulkUpdateRequestDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfoDefaultResponseDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfoSearchDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfoSearchResponseDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfoSessionResponseDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfoSessionUpdateDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateSessionElementDefaultInfoCreateDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateSessionElementInfoBulkUpdateRequestDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateSessionElementInfoSearchDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateSessionElementInfoStatusRequestDTO;
import es.onebox.mgmt.venues.enums.ElementType;
import es.onebox.mgmt.venues.utils.VenueTemplateElementInfoImagesValidationUtils;
import es.onebox.mgmt.venues.utils.VenueTemplateElementInfoUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VenueTemplateElementInfoService {

    private final ValidationService validationService;
    private final VenueTemplateElementsInfoRepository venueTemplateElementsInfoRepository;
    private final TemplatesZonesService templatesZonesService;

    @Autowired
    public VenueTemplateElementInfoService(ValidationService validationService,
                                           VenueTemplateElementsInfoRepository venueTemplateElementsInfoRepository,
                                           TemplatesZonesService templatesZonesService) {
        this.validationService = validationService;
        this.venueTemplateElementsInfoRepository = venueTemplateElementsInfoRepository;
        this.templatesZonesService = templatesZonesService;
    }

    public VenueTemplateElementInfoSearchResponseDTO searchVenueTemplateElementsInfo(Long venueTemplateId,
                                                                                     VenueTemplateElementInfoSearchDTO searchRequest) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);
        TemplateInfoListResponse templateElementInfoListResponse
                = venueTemplateElementsInfoRepository.searchVenueTemplateElementsInfo(venueTemplateId, searchRequest);
        return VenueTemplateElementInfoUtils.convertToResponseSearchDTO(templateElementInfoListResponse);
    }

    public VenueTemplateElementInfoDefaultResponseDTO getVenueTemplateElementsInfo(Long venueTemplateId, ElementType elementType, Long elementId) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);
        TemplateInfoBaseResponse templateElementInfoListResponse
                = venueTemplateElementsInfoRepository.getVenueTemplateElementsInfo(venueTemplateId, elementType, elementId);
        return VenueTemplateElementInfoUtils.convertToElementInfoDefaultResponseDTO(templateElementInfoListResponse);
    }

    public void createTemplateElementInfo(Long venueTemplateId, VenueTemplateElementDefaultInfoCreateDTO requestDTO) {
        VenueTemplate venueTemplate = validationService.getAndCheckVenueTemplate(venueTemplateId);
        if (requestDTO.getCopyInfo() != null && requestDTO.getCopyInfo().source() != null) {
            validationService.getAndCheckVenueTemplate(requestDTO.getCopyInfo().source());
        }
        VenueTemplateElementInfoImagesValidationUtils.validateImages(requestDTO.getDefaultInfo());
        validateTemplateZonesIds(venueTemplate.getEntityId().intValue(), requestDTO.getDefaultInfo());

        TemplateInfoCreateRequest request = VenueTemplateElementInfoUtils.convertToTemplateInfoCreateRequest(requestDTO, venueTemplateId);
        venueTemplateElementsInfoRepository.createTemplateElementInfo(venueTemplateId, request);
    }

    public void bulkUpdateTemplateElementInfo(Long venueTemplateId, VenueTemplateElementInfoBulkUpdateRequestDTO bulkUpdateRequestDTO,
                                              VenueTemplateElementInfoSearchDTO requestSearch) {
        VenueTemplate venueTemplate = validationService.getAndCheckVenueTemplate(venueTemplateId);
        if (bulkUpdateRequestDTO.getElementInfo() != null) {
            validateTemplateZonesIds(venueTemplate.getEntityId().intValue(), bulkUpdateRequestDTO.getElementInfo().getDefaultInfo());
        }

        VenueTemplateElementInfoImagesValidationUtils.validateImages(bulkUpdateRequestDTO.getElementInfo());
        TemplateInfoBulkUpdateRequest bulkUpdateRequest = VenueTemplateElementInfoUtils.convertToTemplateInfoBulkRequest(bulkUpdateRequestDTO);
        venueTemplateElementsInfoRepository.bulkUpdateTemplateElementInfo(venueTemplateId, bulkUpdateRequest, requestSearch);
    }

    public void bulkDeleteVenueTemplateElementInfo(Long venueTemplateId, VenueTemplateElementInfoBulkRequestDTO filters) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);
        venueTemplateElementsInfoRepository.bulkDeleteVenueTemplateElementInfo(venueTemplateId, filters);
    }

    public void updateTemplateElementInfo(Long venueTemplateId, ElementType elementType, Long elementId,
                                          VenueTemplateElementDefaultInfoUpdateDTO elementDefaultInfoUpdateDTO) {
        VenueTemplate venueTemplate = validationService.getAndCheckVenueTemplate(venueTemplateId);
        validateTemplateZonesIds(venueTemplate.getEntityId().intValue(), elementDefaultInfoUpdateDTO.getDefaultInfo());

        VenueTemplateElementInfoImagesValidationUtils.validateImages(elementDefaultInfoUpdateDTO.getDefaultInfo());
        UpdateTemplateInfoDefault updateTemplateInfoDefault = VenueTemplateElementInfoUtils.convertToUpdateTemplateInfoDefault(elementDefaultInfoUpdateDTO);
        venueTemplateElementsInfoRepository.updateTemplateElementInfo(venueTemplateId, elementType, elementId, updateTemplateInfoDefault);
    }

    public void deleteTemplateElementInfo(Long venueTemplateId, ElementType elementType, Long elementId) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);
        venueTemplateElementsInfoRepository.deleteTemplateElementInfo(venueTemplateId, elementType, elementId);
    }

    public void deleteTemplateElementInfoImages(Long venueTemplateId, ElementType elementType, Long elementId,
                                                ElementInfoImageType imageType, String language,
                                                TemplateInfoImagesDeleteFilterDTO filter) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);
        language = ConverterUtils.toLocale(language);
        venueTemplateElementsInfoRepository
                .deleteTemplateElementInfoImages(venueTemplateId, elementType, elementId, imageType, language, filter);
    }

    public SessionVenueTemplateElementInfoSearchResponseDTO searchSessionVenueTemplateElementsInfo(Long sessionId,
                                                                                                   VenueTemplateSessionElementInfoSearchDTO searchRequest) {
        Session session = validationService.getAndCheckVisibilitySession(sessionId);
        SessionTemplateInfoListResponse response
                = venueTemplateElementsInfoRepository.searchSessionsVenueTemplateElementsInfo(session.getVenueConfigId(), session.getId(), searchRequest);
        return  VenueTemplateElementInfoUtils.convertToResponseSearchSessionsDTO(response);
    }

    public void createSessionTemplateElementInfo(Long sessionId, VenueTemplateSessionElementDefaultInfoCreateDTO requestDto) {
        Session session = validationService.getAndCheckVisibilitySession(sessionId);
        validateTemplateZonesIds(session.getEntityId().intValue(), requestDto.getDefaultInfo());

        VenueTemplateElementInfoImagesValidationUtils.validateImages(requestDto.getDefaultInfo());
        SessionTemplateInfoRequest request = VenueTemplateElementInfoUtils.convertToSessionTemplateInfoRequest(requestDto, session.getVenueConfigId());
        venueTemplateElementsInfoRepository.createSessionTemplateElementInfo(session.getVenueConfigId(), session.getId(), request);
    }

    public VenueTemplateElementInfoSessionResponseDTO getSessionVenueTemplateElementInfo(Long sessionId, ElementType elementType, Long elementId) {
        Session session = validationService.getAndCheckVisibilitySession(sessionId);
        SessionTemplateInfoResponse response =
                venueTemplateElementsInfoRepository.getSessionVenueTemplateElementsInfo(session.getVenueConfigId(), elementType, elementId, session.getId());
        return VenueTemplateElementInfoUtils.convertToElementInfoSessionResponseDTO(response);
    }

    public void updateSessionVenueTemplateElementInfo(Long sessionId, ElementType elementType, Long elementId, VenueTemplateElementInfoSessionUpdateDTO requestDto) {
        Session session = validationService.getAndCheckVisibilitySession(sessionId);
        validateTemplateZonesIds(session.getEntityId().intValue(), requestDto.getDefaultInfo());

        VenueTemplateElementInfoImagesValidationUtils.validateImages(requestDto.getDefaultInfo());
        SessionUpdateTemplateInfo request = VenueTemplateElementInfoUtils.convertToSessionVenueTemplateElementsInfo(requestDto);
        venueTemplateElementsInfoRepository.updateSessionVenueTemplateElementInfo(session.getVenueConfigId(), elementType, elementId, session.getId(), request);
    }

    public void deleteSessionVenueTemplateElementInfo(Long sessionId, ElementType elementType, Long elementId) {
        Session session = validationService.getAndCheckVisibilitySession(sessionId);
        venueTemplateElementsInfoRepository.deleteSessionVenueTemplateElementInfo(session.getVenueConfigId(), elementType, elementId, session.getId());
    }


    public void bulkUpdateSessionVenueTemplateElementInfo(Long sessionId, VenueTemplateSessionElementInfoBulkUpdateRequestDTO requestDTO,
                                                          VenueTemplateElementInfoSearchDTO requestSearch) {
        Session session = validationService.getAndCheckVisibilitySession(sessionId);
        if (requestDTO.getSessionElementInfo() != null) {
            validateTemplateZonesIds(session.getEntityId().intValue(), requestDTO.getSessionElementInfo().getDefaultInfo());
        }

        SessionTemplateInfoBulkUpdateRequest request = VenueTemplateElementInfoUtils.convertToSessionTemplateInfoBulkRequest(requestDTO);
        VenueTemplateElementInfoImagesValidationUtils.validateImages(requestDTO.getSessionElementInfo());
        venueTemplateElementsInfoRepository.bulkUpdateSessionTemplateElementInfo(session.getVenueConfigId(), session.getId(), request, requestSearch);
    }

    public void updateStatusSessionVenueTemplateElementInfo(Long sessionId, ElementType elementType, Long elementId,
                                                            VenueTemplateSessionElementInfoStatusRequestDTO requestDTO) {
        Session session = validationService.getAndCheckVisibilitySession(sessionId);
        SessionTemplateInfoStatusUpdateRequest request
                = VenueTemplateElementInfoUtils.convertToSessionTemplateInfoStatusUpdateRequest(requestDTO);
        venueTemplateElementsInfoRepository.updateStatusSessionVenueTemplateElementInfo(session.getVenueConfigId(), elementType, elementId, session.getId(), request);
    }

    public void deleteSessionTemplateElementInfoImages(Long sessionId, ElementType elementType, Long elementId,
                                                       ElementInfoImageType imageType, String language,
                                                       TemplateInfoImagesDeleteFilterDTO filter) {
        Session session = validationService.getAndCheckVisibilitySession(sessionId);
        language = ConverterUtils.toLocale(language);
        venueTemplateElementsInfoRepository.deleteSessionTemplateElementInfoImages(
                session.getVenueConfigId(), elementType, elementId, sessionId, imageType, language, filter);
    }

    private void validateTemplateZonesIds(Integer entityId, VenueTemplateElementAggregatedInfoDTO defaultInfo) {
        if (defaultInfo != null && CollectionUtils.isNotEmpty(defaultInfo.getTemplateZonesIds())) {
            TemplatesZonesRequestFilterDTO filter = new TemplatesZonesRequestFilterDTO();
            filter.setStatus(TemplatesZonesStatus.ENABLED);
            TemplatesZonesResponseDTO templatesZones = templatesZonesService.getTemplatesZones(entityId, filter);
            Set<Integer> templatesZonesIds = new HashSet<>();
            if (CollectionUtils.isNotEmpty(templatesZones.getData())) {
                templatesZonesIds = templatesZones.getData().stream().map(TemplateZonesDTO::getId).collect(Collectors.toSet());
            }
            if (CollectionUtils.isEmpty(templatesZonesIds) || !new HashSet<>(templatesZonesIds).containsAll(defaultInfo.getTemplateZonesIds())) {
                throw new OneboxRestException(ApiMgmtErrorCode.TEMPLATE_ZONES_NOT_FOUND);
            }
        }
    }
}
