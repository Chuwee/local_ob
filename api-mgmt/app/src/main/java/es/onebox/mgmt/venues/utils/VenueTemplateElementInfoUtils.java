package es.onebox.mgmt.venues.utils;

import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.AggregatedInfo;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.Badge;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.ElementInfoImage;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.Feature;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.ImageSettings;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.SessionTemplateElementInfo;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.SessionTemplateInfoBulkUpdateRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.SessionTemplateInfoListResponse;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.SessionTemplateInfoRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.SessionTemplateInfoResponse;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.SessionTemplateInfoStatusUpdateRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.SessionUpdateTemplateInfo;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.TemplateElementInfo;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.TemplateElementInfoBase;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.TemplateInfo3DConfig;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.TemplateInfoBaseResponse;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.TemplateInfoBulkUpdateBaseRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.TemplateInfoBulkUpdateRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.TemplateInfoCopyInfo;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.TemplateInfoCreateRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.TemplateInfoDefault;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.TemplateInfoListResponse;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.UpdateTemplateInfoDefault;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.enums.ElementInfoImageType;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.enums.FeatureAction;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.enums.FeatureType;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.enums.TemplateInfoCopyMatchType;
import es.onebox.mgmt.sessions.dto.templateelementsinfo.SessionVenueTemplateElementInfoSearchBaseDTO;
import es.onebox.mgmt.sessions.dto.templateelementsinfo.SessionVenueTemplateElementInfoSearchResponseDTO;
import es.onebox.mgmt.sessions.dto.templateelementsinfo.SessionVenueTemplateItemElementInfoDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.ElementInfoFeatureDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementAggregatedInfoDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementBadgeDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementCopyInfo;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementDefaultInfoBaseCreateDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementDefaultInfoCreateDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementDefaultInfoUpdateDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementImageDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementImageSettingsDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfo3DConfigDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfoBaseRequestDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfoBulkUpdateBaseRequestDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfoBulkUpdateRequestDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfoDefaultResponseDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfoSearchBaseDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfoSearchResponseDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfoSessionResponseDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfoSessionUpdateDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementRestrictionLanguageDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateItemElementInfoBaseDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateItemElementInfoDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateSessionElementDefaultInfoCreateDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateSessionElementInfoBulkUpdateRequestDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateSessionElementInfoStatusRequestDTO;
import es.onebox.mgmt.venues.enums.VenueTemplateElementFeatureAction;
import es.onebox.mgmt.venues.enums.VenueTemplateElementFeatureType;
import es.onebox.mgmt.venues.enums.VenueTemplateElementImageTypeDTO;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class VenueTemplateElementInfoUtils {

    private VenueTemplateElementInfoUtils() {
        throw new UnsupportedOperationException("Can not instantiate utility class");
    }


    public static VenueTemplateElementInfoSearchResponseDTO convertToResponseSearchDTO(TemplateInfoListResponse templateElementInfoListResponse) {
        VenueTemplateElementInfoSearchResponseDTO responseDTO = new VenueTemplateElementInfoSearchResponseDTO();
        responseDTO.setMetadata(templateElementInfoListResponse.getMetadata());
        if (CollectionUtils.isEmpty(templateElementInfoListResponse.getData())) {
            responseDTO.setData(new ArrayList<>());
        } else {
            fillElementInfoSearchResponse(templateElementInfoListResponse, responseDTO);
        }
        return responseDTO;
    }

    private static void fillElementInfoSearchResponse(TemplateInfoListResponse templateElementInfoListResponse,
                                                      VenueTemplateElementInfoSearchResponseDTO responseDTO) {
        List<VenueTemplateItemElementInfoDTO> response = templateElementInfoListResponse.getData()
                .stream()
                .map(VenueTemplateElementInfoUtils::convertToItemElementInfoDTO)
                .toList();
        responseDTO.setData(response);
    }

    private static VenueTemplateItemElementInfoDTO convertToItemElementInfoDTO(TemplateElementInfo templateElementInfo) {
        VenueTemplateItemElementInfoDTO itemElementInfoDTO = new VenueTemplateItemElementInfoDTO();
        fillVenueTemplateItemElementInfoBaseDTO(templateElementInfo, itemElementInfoDTO);
        itemElementInfoDTO.setElement(convertToElementInfoBaseDTO(templateElementInfo.getTemplateInfo()));
        return itemElementInfoDTO;
    }

    private static void fillVenueTemplateItemElementInfoBaseDTO(@NotNull TemplateElementInfoBase templateElementInfoBase,
                                                                @NotNull VenueTemplateItemElementInfoBaseDTO itemElementInfoDTO) {
        itemElementInfoDTO.setId(templateElementInfoBase.getId());
        itemElementInfoDTO.setName(templateElementInfoBase.getName());
        itemElementInfoDTO.setType(templateElementInfoBase.getType());
        itemElementInfoDTO.setCode(templateElementInfoBase.getCode());
    }

    private static VenueTemplateElementInfoSearchBaseDTO convertToElementInfoBaseDTO(TemplateInfoBaseResponse templateInfo) {
        if (templateInfo != null) {
            VenueTemplateElementInfoSearchBaseDTO elementInfoBaseDTO = new VenueTemplateElementInfoSearchBaseDTO();
            elementInfoBaseDTO.setId(templateInfo.getId());
            elementInfoBaseDTO.setTags(templateInfo.getTags());
            if (templateInfo.getDefaultInfo() != null && CollectionUtils.isNotEmpty(templateInfo.getDefaultInfo().getTemplatesZones())) {
                elementInfoBaseDTO.setTemplatesZones(templateInfo.getDefaultInfo().getTemplatesZones());
            }
            return elementInfoBaseDTO;
        }
        return null;
    }

    public static UpdateTemplateInfoDefault convertToUpdateTemplateInfoDefault(VenueTemplateElementDefaultInfoUpdateDTO elementDefaultInfoUpdateDTO) {
        UpdateTemplateInfoDefault updateTemplateInfoDefault = null;
        if (elementDefaultInfoUpdateDTO != null) {
            updateTemplateInfoDefault = new UpdateTemplateInfoDefault();
            fillTemplateInfoDefault(elementDefaultInfoUpdateDTO, updateTemplateInfoDefault);
        }
        return updateTemplateInfoDefault;
    }

    private static void fillTemplateInfoDefault(VenueTemplateElementDefaultInfoUpdateDTO elementDefaultInfoUpdateDTO, UpdateTemplateInfoDefault updateTemplateInfoDefault) {
        if (elementDefaultInfoUpdateDTO != null) {
            updateTemplateInfoDefault.setTags(elementDefaultInfoUpdateDTO.getTags());
            updateTemplateInfoDefault.setDefaultInfo(convertToAggregatedInfo(elementDefaultInfoUpdateDTO.getDefaultInfo()));
        }
    }

    public static TemplateInfoCreateRequest convertToTemplateInfoCreateRequest(VenueTemplateElementDefaultInfoCreateDTO requestDTO, Long venueTemplateId) {
        if (requestDTO == null) {
            return null;
        }
        TemplateInfoCreateRequest request = new TemplateInfoCreateRequest();
        request.setCopyInfo(toMs(requestDTO.getCopyInfo()));
        fillTemplateInfoDefault(requestDTO, request, venueTemplateId);
        return request;
    }

    private static TemplateInfoCopyInfo toMs(VenueTemplateElementCopyInfo in) {
        if (in == null) {
            return null;
        }
        TemplateInfoCopyMatchType matchType = null;
        if (in.matchType() != null) {
            matchType = TemplateInfoCopyMatchType.valueOf(in.matchType().name());
        }
        return new TemplateInfoCopyInfo(in.source(), matchType);
    }

    private static void fillTemplateInfoDefault(VenueTemplateElementDefaultInfoBaseCreateDTO requestDTO, TemplateInfoDefault templateInfoDefault, Long venueTemplateId) {
        if (requestDTO != null) {
            fillVenueTemplateElementInfoBaseDTO(requestDTO, templateInfoDefault, venueTemplateId);
            templateInfoDefault.setDefaultInfo(convertToAggregatedInfo(requestDTO.getDefaultInfo()));
        }
    }

    private static AggregatedInfo convertToAggregatedInfo(VenueTemplateElementAggregatedInfoDTO defaultInfo) {
        AggregatedInfo aggregatedInfo = null;
        if (defaultInfo != null) {
            aggregatedInfo = new AggregatedInfo();
            aggregatedInfo.setName(changeMapLangKeyToUnderScore(defaultInfo.getName()));
            aggregatedInfo.setDescription(changeMapLangKeyToUnderScore(defaultInfo.getDescription()));

            if (defaultInfo.getRestriction() != null) {
                Map<String, VenueTemplateElementRestrictionLanguageDTO> translatedRestrictions =
                        defaultInfo.getRestriction().getTexts();

                if (translatedRestrictions != null) {
                    Map<String, VenueTemplateElementRestrictionLanguageDTO> texts =
                            changeMapLangKeyToUnderScore(translatedRestrictions);

                    defaultInfo.getRestriction().setTexts(texts);

                    aggregatedInfo.setRestriction(defaultInfo.getRestriction());
                }
            }

            if (MapUtils.isNotEmpty(defaultInfo.getFeatureList())) {
                Map<String, List<Feature>> features = defaultInfo.getFeatureList().entrySet().stream()
                        .collect(Collectors.toMap(e -> langKeyToUnderscore(e.getKey()), e -> convertToFeatureList(e.getValue())));
                aggregatedInfo.setFeatureList(features);
            }
            aggregatedInfo.setConfig3D(convertToConfig3D(defaultInfo.getConfig3D()));

            if (MapUtils.isNotEmpty(defaultInfo.getImageSettings())) {
                aggregatedInfo.setImageSettings(ConverterUtils.transformMapKeysAndValues(
                        defaultInfo.getImageSettings(),
                        VenueTemplateElementInfoUtils::toMs,
                        VenueTemplateElementInfoUtils::toMs
                ));
            }
            aggregatedInfo.setBadge(toMs(defaultInfo.getBadge()));
            aggregatedInfo.setTemplatesZonesIds(defaultInfo.getTemplateZonesIds());
        }
        return aggregatedInfo;
    }

    private static ElementInfoImageType toMs(VenueTemplateElementImageTypeDTO in) {
        return ElementInfoImageType.valueOf(in.name());
    }

    private static ImageSettings toMs(VenueTemplateElementImageSettingsDTO in) {
        if (in == null) {
            return null;
        }
        ImageSettings out = new ImageSettings();
        out.setEnabled(in.getEnabled());
        out.setImages(ConverterUtils.transformMapKeysAndValues(
                in.getImages(),
                VenueTemplateElementInfoUtils::langKeyToUnderscore,
                VenueTemplateElementInfoUtils::toMs
        ));
        return out;
    }

    private static List<ElementInfoImage> toMs(List<VenueTemplateElementImageDTO> in) {
        if (in == null) {
            return null;
        }
        return in.stream().map(VenueTemplateElementInfoUtils::toMs).toList();
    }


    private static ElementInfoImage toMs(VenueTemplateElementImageDTO in) {
        if (in == null) {
            return null;
        }
        ElementInfoImage out = new ElementInfoImage();
        out.setImage(in.getImage());
        out.setThumbnail(in.getThumbnail());
        out.setPosition(in.getPosition());
        out.setAltText(in.getAltText());
        return out;
    }

    private static Badge toMs(VenueTemplateElementBadgeDTO in) {
        if (in == null) {
            return null;
        }
        Badge out = new Badge();
        out.setBackgroundColor(in.getBackgroundColor());
        out.setTextColor(in.getTextColor());
        out.setText(changeMapLangKeyToUnderScore(in.getText()));
        return out;
    }

    private static List<Feature> convertToFeatureList(List<ElementInfoFeatureDTO> in) {
        if (in == null) return null;
        return in.stream()
                .map(f -> new Feature(FeatureType.getValue(f.getType()), f.getText(), f.getUrl(), FeatureAction.getValue(f.getAction())))
                .collect(Collectors.toList());
    }

    private static <T> Map<String, T> changeMapLangKeyToUnderScore(Map<String, T> in) {
        if (MapUtils.isNotEmpty(in)) {
            Map<String, T> newNames = new HashMap<>();
            in.forEach((k, v) -> newNames.put(langKeyToUnderscore(k), v));
            return newNames;
        }
        return null;
    }

    private static String langKeyToUnderscore(String k) {
        return k.replace('-', '_');
    }

    private static TemplateInfo3DConfig convertToConfig3D(VenueTemplateElementInfo3DConfigDTO config3DDTO) {
        TemplateInfo3DConfig config3D = null;
        if (config3DDTO != null) {
            config3D = new TemplateInfo3DConfig();
            config3D.setCodes(config3DDTO.getCodes());
            config3D.setEnabled(config3DDTO.getEnabled());
        }
        return config3D;
    }

    private static void fillVenueTemplateElementInfoBaseDTO(VenueTemplateElementInfoBaseRequestDTO elementInfoBaseDTO, TemplateInfoDefault templateInfoDefault, Long venueTemplateId) {
        templateInfoDefault.setId(elementInfoBaseDTO.getId());
        templateInfoDefault.setTemplateId(venueTemplateId);
        templateInfoDefault.setType(elementInfoBaseDTO.getType());
        templateInfoDefault.setTags(elementInfoBaseDTO.getTags());
    }

    public static VenueTemplateElementInfoDefaultResponseDTO convertToElementInfoDefaultResponseDTO(TemplateInfoBaseResponse templateInfoDefaultResponse) {
        VenueTemplateElementInfoDefaultResponseDTO elementInfoDefaultResponseDTO = null;
        if (templateInfoDefaultResponse != null) {
            elementInfoDefaultResponseDTO = new VenueTemplateElementInfoDefaultResponseDTO();
            fillElementInfoDefaultResponseDTO(templateInfoDefaultResponse, elementInfoDefaultResponseDTO);
        }
        return elementInfoDefaultResponseDTO;
    }

    private static void fillElementInfoDefaultResponseDTO(TemplateInfoBaseResponse templateInfoDefaultResponse, VenueTemplateElementInfoDefaultResponseDTO elementInfoDefaultResponseDTO) {
        elementInfoDefaultResponseDTO.setTags(templateInfoDefaultResponse.getTags());
        elementInfoDefaultResponseDTO.setId(templateInfoDefaultResponse.getId());
        if (templateInfoDefaultResponse.getDefaultInfo() != null) {
            elementInfoDefaultResponseDTO.setDefaultInfo(convertToAggregatedInfoDTO(templateInfoDefaultResponse.getDefaultInfo()));
        }
    }

    private static VenueTemplateElementAggregatedInfoDTO convertToAggregatedInfoDTO(AggregatedInfo defaultInfo) {
        VenueTemplateElementAggregatedInfoDTO elementAggregationInfoDTO = null;
        if (defaultInfo != null) {
            elementAggregationInfoDTO = new VenueTemplateElementAggregatedInfoDTO();
            elementAggregationInfoDTO.setName(changeTextLangKeyToKevapCase(defaultInfo.getName()));
            elementAggregationInfoDTO.setDescription(changeTextLangKeyToKevapCase(defaultInfo.getDescription()));
            elementAggregationInfoDTO.setBadge(toDTO(defaultInfo.getBadge()));

            if (defaultInfo.getRestriction() != null) {
                Map<String, VenueTemplateElementRestrictionLanguageDTO> translatedRestrictions =
                        defaultInfo.getRestriction().getTexts();

                if (translatedRestrictions != null) {
                    Map<String, VenueTemplateElementRestrictionLanguageDTO> texts =
                            changeTextLangKeyToKevapCase(translatedRestrictions);

                    defaultInfo.getRestriction().setTexts(texts);

                    elementAggregationInfoDTO.setRestriction(defaultInfo.getRestriction());
                }
            }

            if (MapUtils.isNotEmpty(defaultInfo.getFeatureList())) {
                Map<String, List<ElementInfoFeatureDTO>> features = defaultInfo.getFeatureList().entrySet().stream()
                        .collect(Collectors.toMap(e -> langKeyToKevapCase(e.getKey()), e -> fromMs(e.getValue())));

                elementAggregationInfoDTO.setFeatureList(features);
            }

            elementAggregationInfoDTO.setConfig3D(convertToConfig3DDTO(defaultInfo.getConfig3D()));
            if (MapUtils.isNotEmpty(defaultInfo.getImageSettings())) {
                elementAggregationInfoDTO.setImageSettings(ConverterUtils.transformMapKeysAndValues(
                        defaultInfo.getImageSettings(),
                        VenueTemplateElementInfoUtils::toDTO,
                        VenueTemplateElementInfoUtils::toDTO
                ));
            }
            if (defaultInfo.getTemplatesZones() != null) {
                elementAggregationInfoDTO.setTemplateZones(defaultInfo.getTemplatesZones());
            }
            if (CollectionUtils.isNotEmpty(defaultInfo.getTemplatesZones())) {
                elementAggregationInfoDTO.setTemplateZones(defaultInfo.getTemplatesZones());
            }

        }
        return elementAggregationInfoDTO;
    }

    private static VenueTemplateElementImageTypeDTO toDTO(ElementInfoImageType in) {
        return VenueTemplateElementImageTypeDTO.valueOf(in.name());
    }

    private static VenueTemplateElementImageSettingsDTO toDTO(ImageSettings in) {
        if (in == null) {
            return null;
        }
        VenueTemplateElementImageSettingsDTO out = new VenueTemplateElementImageSettingsDTO();
        out.setEnabled(in.getEnabled());
        out.setImages(ConverterUtils.transformMapKeysAndValues(
                in.getImages(),
                VenueTemplateElementInfoUtils::langKeyToKevapCase,
                VenueTemplateElementInfoUtils::toDTO
        ));
        return out;
    }

    private static List<VenueTemplateElementImageDTO> toDTO(List<ElementInfoImage> in) {
        if (in == null) {
            return null;
        }
        return in.stream().map(VenueTemplateElementInfoUtils::toDTO).toList();
    }

    private static VenueTemplateElementImageDTO toDTO(ElementInfoImage in) {
        if (in == null) {
            return null;
        }
        VenueTemplateElementImageDTO out = new VenueTemplateElementImageDTO();
        out.setImage(in.getImage());
        out.setThumbnail(in.getThumbnail());
        out.setPosition(in.getPosition());
        out.setAltText(in.getAltText());
        return out;
    }

    private static VenueTemplateElementBadgeDTO toDTO(Badge in) {
        if (in == null) {
            return null;
        }
        VenueTemplateElementBadgeDTO out = new VenueTemplateElementBadgeDTO();
        out.setTextColor(in.getTextColor());
        out.setBackgroundColor(in.getBackgroundColor());
        out.setText(changeTextLangKeyToKevapCase(in.getText()));
        return out;
    }

    private static List<ElementInfoFeatureDTO> fromMs(List<Feature> in) {
        return in.stream()
                .map(f -> new ElementInfoFeatureDTO(VenueTemplateElementFeatureType.getValue(f.getType()), f.getText(), f.getUrl(),
                        VenueTemplateElementFeatureAction.getValue(f.getAction())))
                .collect(Collectors.toList());
    }

    private static <T> Map<String, T> changeTextLangKeyToKevapCase(Map<String, T> texts) {
        if (MapUtils.isNotEmpty(texts)) {
            Map<String, T> newTexts = new HashMap<>();
            texts.forEach((k, v) -> newTexts.put(langKeyToKevapCase(k), v));
            return newTexts;
        }
        return null;
    }

    private static VenueTemplateElementInfo3DConfigDTO convertToConfig3DDTO(TemplateInfo3DConfig config3D) {
        VenueTemplateElementInfo3DConfigDTO config3DDTO = null;
        if (config3D != null) {
            config3DDTO = new VenueTemplateElementInfo3DConfigDTO();
            config3DDTO.setCodes(config3D.getCodes());
            config3DDTO.setEnabled(config3D.getEnabled());
        }
        return config3DDTO;
    }

    private static String langKeyToKevapCase(String k) {
        return k.replace('_', '-');
    }


    public static SessionVenueTemplateElementInfoSearchResponseDTO convertToResponseSearchSessionsDTO(SessionTemplateInfoListResponse response) {
        SessionVenueTemplateElementInfoSearchResponseDTO responseDTO = new SessionVenueTemplateElementInfoSearchResponseDTO();
        responseDTO.setMetadata(response.getMetadata());
        if (CollectionUtils.isNotEmpty(response.getData())) {
            responseDTO.setData(convertToSessionVenueTemplateItemElementInfoDTOList(response.getData()));
        } else {
            responseDTO.setData(new ArrayList<>());
        }
        return responseDTO;
    }

    private static List<SessionVenueTemplateItemElementInfoDTO> convertToSessionVenueTemplateItemElementInfoDTOList(List<SessionTemplateElementInfo> data) {
        return data.stream()
                .map(VenueTemplateElementInfoUtils::convertToSessionTemplateItemElementInfoDTO)
                .filter(Objects::nonNull)
                .toList();
    }

    private static SessionVenueTemplateItemElementInfoDTO convertToSessionTemplateItemElementInfoDTO(SessionTemplateElementInfo sessionTemplateElementInfo) {
        SessionVenueTemplateItemElementInfoDTO dto = null;
        if (sessionTemplateElementInfo != null) {
            dto = new SessionVenueTemplateItemElementInfoDTO();
            fillVenueTemplateItemElementInfoBaseDTO(sessionTemplateElementInfo, dto);
            dto.setElement(convertToSessionVenueTemplateElementInfoSearBaseDTO(sessionTemplateElementInfo.getTemplateInfo()));
        }
        return dto;
    }

    private static SessionVenueTemplateElementInfoSearchBaseDTO convertToSessionVenueTemplateElementInfoSearBaseDTO(
            SessionTemplateInfoResponse sessionTemplateInfoResponse) {
        SessionVenueTemplateElementInfoSearchBaseDTO dto = null;
        if (sessionTemplateInfoResponse != null) {
            dto = new SessionVenueTemplateElementInfoSearchBaseDTO();
            dto.setId(sessionTemplateInfoResponse.getId());
            dto.setTags(sessionTemplateInfoResponse.getTags());
            dto.setStatus(sessionTemplateInfoResponse.getStatus());
            if (sessionTemplateInfoResponse.getDefaultInfo() != null &&
                    CollectionUtils.isNotEmpty(sessionTemplateInfoResponse.getDefaultInfo().getTemplatesZones())) {
                dto.setTemplatesZones(sessionTemplateInfoResponse.getDefaultInfo().getTemplatesZones());
            }

        }
        return dto;
    }

    public static SessionTemplateInfoRequest convertToSessionTemplateInfoRequest(VenueTemplateSessionElementDefaultInfoCreateDTO requestDto, Long venueTemplateId) {
        SessionTemplateInfoRequest request = null;
        if (requestDto != null) {
            request = new SessionTemplateInfoRequest();
            request.setStatus(requestDto.getStatus());
            fillTemplateInfoDefault(requestDto, request, venueTemplateId);
        }
        return request;
    }

    public static VenueTemplateElementInfoSessionResponseDTO convertToElementInfoSessionResponseDTO(SessionTemplateInfoResponse response) {
        VenueTemplateElementInfoSessionResponseDTO responseDTO = null;
        if (response != null) {
            responseDTO = new VenueTemplateElementInfoSessionResponseDTO();
            responseDTO.setStatus(response.getStatus());
            fillElementInfoDefaultResponseDTO(response, responseDTO);
        }
        return responseDTO;
    }

    public static SessionUpdateTemplateInfo convertToSessionVenueTemplateElementsInfo(VenueTemplateElementInfoSessionUpdateDTO requestDto) {
        SessionUpdateTemplateInfo request = null;
        if (requestDto != null) {
            request = new SessionUpdateTemplateInfo();
            if (requestDto.getStatus() != null) {
                request.setStatus(requestDto.getStatus());
            }
            fillTemplateInfoDefault(requestDto, request);
        }
        return request;
    }

    public static TemplateInfoBulkUpdateRequest convertToTemplateInfoBulkRequest(VenueTemplateElementInfoBulkUpdateRequestDTO bulkUpdateRequestDTO) {
        TemplateInfoBulkUpdateRequest request = null;
        if (bulkUpdateRequestDTO.getElementInfo() != null) {
            request = new TemplateInfoBulkUpdateRequest();
            fillTemplateInfoBulkUpdateBaseRequest(bulkUpdateRequestDTO, request);
            request.setTemplateInfo(convertToUpdateTemplateInfoDefault(bulkUpdateRequestDTO.getElementInfo()));
        }
        return request;
    }

    private static void fillTemplateInfoBulkUpdateBaseRequest(VenueTemplateElementInfoBulkUpdateBaseRequestDTO bulkUpdateBaseRequestDTO,
                                                              TemplateInfoBulkUpdateBaseRequest bulkUpdateBaseRequest) {
        if (bulkUpdateBaseRequestDTO != null) {
            bulkUpdateBaseRequest.setUpdateAllTemplateInfo(bulkUpdateBaseRequestDTO.getUpdateAllElementsInfo());
            bulkUpdateBaseRequest.setTemplateInfoTypeWithIdsMap(removeEmptyValues(bulkUpdateBaseRequestDTO.getElementsTypeRelatedIdMap()));
        }
    }

    private static Map<String, List<Long>> removeEmptyValues(Map<String, List<Long>> map) {
        if (MapUtils.isNotEmpty(map)) {
            map.entrySet().removeIf(entry -> CollectionUtils.isEmpty(entry.getValue()));
        }
        return map;
    }

    public static SessionTemplateInfoBulkUpdateRequest convertToSessionTemplateInfoBulkRequest(VenueTemplateSessionElementInfoBulkUpdateRequestDTO requestDTO) {
        SessionTemplateInfoBulkUpdateRequest request = null;
        if (requestDTO.getSessionElementInfo() != null) {
            request = new SessionTemplateInfoBulkUpdateRequest();
            fillTemplateInfoBulkUpdateBaseRequest(requestDTO, request);
            request.setSessionTemplateInfo(convertToSessionVenueTemplateElementsInfo(requestDTO.getSessionElementInfo()));
        }
        return request;
    }

    public static SessionTemplateInfoStatusUpdateRequest convertToSessionTemplateInfoStatusUpdateRequest(VenueTemplateSessionElementInfoStatusRequestDTO requestDTO) {
        SessionTemplateInfoStatusUpdateRequest request = new SessionTemplateInfoStatusUpdateRequest();
        request.setStatus(requestDTO.getStatus());
        return request;
    }
}
