package es.onebox.mgmt.channels.members.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.dto.MemberConfigurationStructureDTO;
import es.onebox.mgmt.channels.members.MembersSupport;
import es.onebox.mgmt.channels.members.converter.MembersConfigConverter;
import es.onebox.mgmt.channels.members.converter.MembersConverter;
import es.onebox.mgmt.channels.members.dto.AforoInfoDTO;
import es.onebox.mgmt.channels.members.dto.AvetEventDTO;
import es.onebox.mgmt.channels.members.dto.PeriodicityDTO;
import es.onebox.mgmt.channels.members.dto.RolPartnerInfoDTO;
import es.onebox.mgmt.channels.members.dto.TermDTO;
import es.onebox.mgmt.common.restrictions.DynamicBusinessRuleFieldType;
import es.onebox.mgmt.common.restrictions.StructureContainer;
import es.onebox.mgmt.common.restrictions.StructureContainerDataOrigin;
import es.onebox.mgmt.common.restrictions.dto.ConfigurationStructureFieldDTO;
import es.onebox.mgmt.datasources.integration.avetconfig.repository.AvetConfigRepository;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.AforosList;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.RolInfoList;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.TermInfoList;
import es.onebox.mgmt.datasources.integration.dispatcher.repository.DispatcherRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.MemberCapacity;
import es.onebox.mgmt.datasources.ms.entity.dto.MemberConfigDTO;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.members.DynamicBusinessRuleFields;
import es.onebox.mgmt.members.DynamicBusinessRuleTypes;
import es.onebox.mgmt.members.MemberOrderType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MembersService {

    private final ChannelsHelper channelsHelper;
    private final AvetConfigRepository avetConfigRepository;

    private final DispatcherRepository dispatcherRepository;

    private final ObjectMapper jacksonMapper;

    @Autowired
    public MembersService(ChannelsHelper channelsHelper, AvetConfigRepository avetConfigRepository,
                          DispatcherRepository dispatcherRepository, ObjectMapper jacksonMapper) {
        this.channelsHelper = channelsHelper;
        this.avetConfigRepository = avetConfigRepository;
        this.dispatcherRepository = dispatcherRepository;
        this.jacksonMapper = jacksonMapper;
    }

    public List<PeriodicityDTO> getMemberPeriodicities(Long channelId) {
        channelsHelper.getAndCheckChannel(channelId);
        MemberConfigDTO memberConfigDTO = avetConfigRepository.getMemberConfigByChannel(channelId);

        if (memberConfigDTO == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_NOT_FOUND);
        }
        if (memberConfigDTO.getEntityId() == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_VENUE_PROPERTY_NOT_FOUND);
        }

        TermInfoList termInfos = dispatcherRepository.getTermsInfo(memberConfigDTO.getEntityId());

        return MembersConverter.toPeriodicities(termInfos);
    }

    public List<TermDTO> getMemberTerms(Long channelId) {
        channelsHelper.getAndCheckChannel(channelId);
        MemberConfigDTO memberConfigDTO = avetConfigRepository.getMemberConfigByChannel(channelId);

        if (memberConfigDTO == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_NOT_FOUND);
        }
        if (memberConfigDTO.getEntityId() == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_VENUE_PROPERTY_NOT_FOUND);
        }

        TermInfoList termInfos = dispatcherRepository.getTermsInfo(memberConfigDTO.getEntityId());

        return MembersConverter.toTerms(termInfos);
    }

    public List<RolPartnerInfoDTO> getMemberRoles(Long channelId) {
        channelsHelper.getAndCheckChannel(channelId);
        MemberConfigDTO memberConfigDTO = avetConfigRepository.getMemberConfigByChannel(channelId);

        if (memberConfigDTO == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_NOT_FOUND);
        }

        MemberCapacity mainCapacity = CollectionUtils.isEmpty(memberConfigDTO.getCapacities()) ? null :
                memberConfigDTO.getCapacities().stream().filter(MemberCapacity::isMain).findFirst().orElse(null);

        if (mainCapacity == null || memberConfigDTO.getEntityId() == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_CAPACITY_NOT_FOUND);
        }

        RolInfoList rolesInfo = dispatcherRepository.getRolesInfo(memberConfigDTO.getEntityId(), mainCapacity.getAvetCapacityId());

        return MembersConverter.toRoles(rolesInfo);
    }

    public List<AforoInfoDTO> getMemberAforos(Long channelId) {
        channelsHelper.getAndCheckChannel(channelId);
        MemberConfigDTO memberConfigDTO = avetConfigRepository.getMemberConfigByChannel(channelId);

        if (memberConfigDTO == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_NOT_FOUND);
        }

        if (memberConfigDTO.getEntityId() == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_PROPERTY_NOT_FOUND);
        }

        AforosList aforosInfo = dispatcherRepository.getAforosInfo(memberConfigDTO.getEntityId());

        return MembersConverter.toAforos(aforosInfo);
    }

    public List<MemberConfigurationStructureDTO> getMemberConfigStructure(DynamicBusinessRuleTypes type, MemberOrderType orderType) {
        List<MemberConfigurationStructureDTO> filtered;
        if (type != null) {
            DynamicBusinessRuleTypes result = DynamicBusinessRuleTypes.valueOf(type.name());
            filtered = MembersConfigConverter.toStructure(result, null);
        } else {
            DynamicBusinessRuleTypes[] dynamicBusinessRuleTypes = DynamicBusinessRuleTypes.values();
            filtered = MembersConfigConverter.toStructure(dynamicBusinessRuleTypes, null);
        }
        if (orderType != null) {
            return filtered.stream().filter(fi -> fi.getOrderType() == null || (fi.getOrderType() != null && fi.getOrderType().equals(orderType))).collect(Collectors.toList());
        } else {
            return filtered;
        }
    }

    public List<MemberConfigurationStructureDTO> getChannelMemberConfigStructure(Long channelId, DynamicBusinessRuleTypes type, MemberOrderType orderType) {
        MemberConfigDTO memberConfigDTO = avetConfigRepository.getMemberConfigByChannel(channelId);
        if (memberConfigDTO == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.MEMBER_CONFIG_NOT_FOUND);
        }
        MemberCapacity mainCapacity = memberConfigDTO.getCapacities().stream().filter(MemberCapacity::isMain).findFirst().orElse(null);
        if (mainCapacity == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_CAPACITY_NOT_FOUND);
        }
        Map<String, Object> memberConfigMap = jacksonMapper.convertValue(memberConfigDTO, Map.class);
        List<MemberConfigurationStructureDTO> filtered;
        if (type != null) {
            DynamicBusinessRuleTypes result = DynamicBusinessRuleTypes.valueOf(type.name());
            filtered = MembersConfigConverter.toStructure(result, memberConfigMap);
        } else {
            DynamicBusinessRuleTypes[] dynamicBusinessRuleTypes = DynamicBusinessRuleTypes.values();
            filtered = MembersConfigConverter.toStructure(dynamicBusinessRuleTypes, memberConfigMap);
        }
        if (orderType != null) {
            return filtered.stream().filter(fi -> fi.getOrderType() == null || fi.getOrderType().equals(orderType)).collect(Collectors.toList());
        } else {
            return filtered;
        }
    }

    public void updateChannelMemberConfigStructure(Long channelId, String operationName, MemberConfigurationStructureDTO memberConfigurationStructureDTO) {
        MemberConfigDTO memberConfigDTO = avetConfigRepository.getMemberConfigByChannel(channelId);
        if (memberConfigDTO == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.MEMBER_CONFIG_NOT_FOUND);
        }
        MemberCapacity mainCapacity = memberConfigDTO.getCapacities().stream().filter(MemberCapacity::isMain).findFirst().orElse(null);
        if (mainCapacity == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_CAPACITY_NOT_FOUND);
        }
        Map<String, Object> memberConfigMap = jacksonMapper.convertValue(memberConfigDTO, Map.class);
        validateValues(channelId, operationName, memberConfigurationStructureDTO);
        MemberConfigDTO result = MembersConfigConverter.fromStructure(memberConfigurationStructureDTO, memberConfigMap, operationName, jacksonMapper);
        avetConfigRepository.updateMemberConfigByChannel(channelId, result);
    }

    public void validateValues(Long channelId, String operationName, MemberConfigurationStructureDTO memberConfigurationStructureDTO) {
        List<DynamicBusinessRuleFields> fields = MembersConfigConverter.findFields(memberConfigurationStructureDTO.getType(), operationName);
        Map<String, List<Integer>> dataOrigins = new HashMap<>();
        // fill values
        for (DynamicBusinessRuleFields field : fields) {
            List<Integer> values = new ArrayList<>();
            if (field.getValueSource() != null) {
                StructureContainerDataOrigin sourceDataOrigin = StructureContainerDataOrigin.valueOf(field.getValueSource());
                if (!dataOrigins.containsKey(sourceDataOrigin.toString())) {
                    if (sourceDataOrigin.equals(StructureContainerDataOrigin.ROLE_ID)) {
                        getMemberRoles(channelId).stream().forEach(ro -> values.add(ro.getType()));
                    }
                    if (sourceDataOrigin.equals(StructureContainerDataOrigin.PERIODICITY_ID)) {
                        getMemberPeriodicities(channelId).stream().forEach(pe -> values.add(pe.getPeriodicityId().intValue()));
                    }
                    if (sourceDataOrigin.equals(StructureContainerDataOrigin.CAPACITY_ID)) {
                        getMemberAforos(channelId).stream().forEach(ca -> values.add(ca.getIdAforo()));
                    }
                    if (sourceDataOrigin.equals(StructureContainerDataOrigin.TERM_ID)) {
                        getMemberTerms(channelId).stream().forEach(te -> values.add(te.getTermId().intValue()));
                    }
                    dataOrigins.put(sourceDataOrigin.toString(), values);
                }
            }
            if (field.getValueTarget() != null) {
                StructureContainerDataOrigin targetDataOrigin = StructureContainerDataOrigin.valueOf(field.getValueTarget());
                if (!dataOrigins.containsKey(targetDataOrigin.toString())) {
                    if (!dataOrigins.containsKey(targetDataOrigin.toString())) {
                        if (targetDataOrigin.equals(StructureContainerDataOrigin.ROLE_ID)) {
                            getMemberRoles(channelId).stream().forEach(ro -> values.add(ro.getType()));
                        }
                        if (targetDataOrigin.equals(StructureContainerDataOrigin.PERIODICITY_ID)) {
                            getMemberPeriodicities(channelId).stream().forEach(pe -> values.add(pe.getPeriodicityId().intValue()));
                        }
                        if (targetDataOrigin.equals(StructureContainerDataOrigin.CAPACITY_ID)) {
                            getMemberAforos(channelId).stream().forEach(ca -> values.add(ca.getIdAforo()));
                        }
                        if (targetDataOrigin.equals(StructureContainerDataOrigin.TERM_ID)) {
                            getMemberTerms(channelId).stream().forEach(te -> values.add(te.getTermId().intValue()));
                        }
                        dataOrigins.put(targetDataOrigin.toString(), values);
                    }
                }
            }
        }

        // check values
        for (ConfigurationStructureFieldDTO field : memberConfigurationStructureDTO.getFields()) {
            if ((field.getValue() != null && !field.getValue().toString().trim().isEmpty()) && field.getSource() != null || field.getTarget() != null) {
                checkValue(field, dataOrigins);
            }
        }
    }

    public void checkValue(ConfigurationStructureFieldDTO field, Map<String, List<Integer>> dataOrigins) {
        List<Integer> sourceAllowedValue = null;
        List<Integer> targetAllowedValue = null;
        if (field.getSource() != null) {
            sourceAllowedValue = dataOrigins.get(field.getSource().toString());
        }
        if (field.getTarget() != null) {
            targetAllowedValue = dataOrigins.get(field.getTarget().toString());
        }
        if (field.getContainer().equals(StructureContainer.LIST)) {
            if (field.getType().equals(DynamicBusinessRuleFieldType.INTEGER)) {
                if (field.getValue() != null && !field.getValue().toString().trim().isEmpty()) {
                    String[] values = (String[]) field.getValue().toString().replace("[", "").replace("]", "").split(",");
                    if (field.getSource() != null) {
                        MembersSupport.checkValueInts(values, sourceAllowedValue, field.getId());
                    }
                    if (field.getTarget() != null) {
                        MembersSupport.checkValueInts(values, targetAllowedValue, field.getId());
                    }
                }
            } else {
                if (field.getValue() != null && !field.getValue().toString().trim().isEmpty()) {
                    String[] valuesStr = field.getValue().toString().split(",");
                    if (field.getSource() != null) {
                        MembersSupport.checkValueStrings(valuesStr, sourceAllowedValue, field.getId());
                    }
                    if (field.getTarget() != null) {
                        MembersSupport.checkValueStrings(valuesStr, targetAllowedValue, field.getId());
                    }
                }
            }
        }
        if (field.getContainer().equals(StructureContainer.MAP)) {
            Map<String, String> values = new HashMap<>();
            if (field.getValue() != null && !field.getValue().toString().trim().isEmpty()) {
                values = (Map<String, String>) field.getValue();
                if (field.getSource() != null) {
                    MembersSupport.checkValueSource(values, sourceAllowedValue, field.getId());
                }
                if (field.getTarget() != null) {
                    MembersSupport.checkValueTarget(values, targetAllowedValue, field.getId());
                }
                if (field.getSource() != null && field.getTarget() != null) {
                    MembersSupport.chechValueMap(values, sourceAllowedValue, targetAllowedValue, field.getId());
                }
            }
        }
        if (field.getContainer().equals(StructureContainer.SINGLE)) {
            if (field.getType().equals(DynamicBusinessRuleFieldType.INTEGER)) {
                if (field.getValue() != null) {
                    Integer valueInt = Integer.valueOf(field.getValue().toString().trim());
                    MembersSupport.checkValueInt(valueInt, sourceAllowedValue, field.getId());
                }
            } else {
                if (field.getValue() != null) {
                    String value = field.getValue().toString().trim();
                    MembersSupport.checkValueString(value, sourceAllowedValue, field.getId());
                }
            }
        }
    }

    public List<AvetEventDTO> getMemberEvents(Long channelId) {
        channelsHelper.getAndCheckChannel(channelId);
        MemberConfigDTO memberConfigDTO = avetConfigRepository.getMemberConfigByChannel(channelId);
        if (memberConfigDTO == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.MEMBER_CONFIG_NOT_FOUND);
        }
        if (memberConfigDTO.getEntityId() == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_PROPERTY_NOT_FOUND);
        }

        Long entityId = memberConfigDTO.getEntityId();
        return MembersConfigConverter.toAvetEventDTO(avetConfigRepository.getEventsInformation(entityId));
    }
}
