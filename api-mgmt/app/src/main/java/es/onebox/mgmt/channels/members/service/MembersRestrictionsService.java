package es.onebox.mgmt.channels.members.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.dto.MemberRestrictionCreateRequestDTO;
import es.onebox.mgmt.channels.dto.MemberRestrictionDTO;
import es.onebox.mgmt.channels.dto.MemberRestrictionDetailDTO;
import es.onebox.mgmt.channels.dto.MemberRestrictionRequestDTO;
import es.onebox.mgmt.channels.enums.RestrictionType;
import es.onebox.mgmt.channels.members.converter.MembersConverter;
import es.onebox.mgmt.common.restrictions.DynamicBusinessRuleFieldType;
import es.onebox.mgmt.datasources.integration.avetconfig.repository.AvetConfigRepository;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.RolInfoList;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.RolPartnerInfo;
import es.onebox.mgmt.datasources.integration.dispatcher.repository.DispatcherRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.MemberCapacity;
import es.onebox.mgmt.datasources.ms.entity.dto.MemberConfigDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.MemberRestriction;
import es.onebox.mgmt.datasources.ms.venue.dto.template.Sector;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.members.DynamicBusinessRuleFieldContainer;
import es.onebox.mgmt.members.MembersRestrictionTypesFields;
import es.onebox.mgmt.members.RestrictionTypes;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class MembersRestrictionsService {
    private final ChannelsHelper channelsHelper;
    private final AvetConfigRepository avetConfigRepository;
    private final DispatcherRepository dispatcherRepository;
    private final VenuesRepository venuesRepository;

    @Autowired
    public MembersRestrictionsService(ChannelsHelper channelsHelper, AvetConfigRepository avetConfigRepository, DispatcherRepository dispatcherRepository,
                                      VenuesRepository venuesRepository) {
        this.channelsHelper = channelsHelper;
        this.avetConfigRepository = avetConfigRepository;
        this.dispatcherRepository = dispatcherRepository;
        this.venuesRepository = venuesRepository;
    }

    public MemberRestrictionDetailDTO getMemberRestriction(Long channelId, String restrictionSid) {
        channelsHelper.getAndCheckChannel(channelId);

        MemberConfigDTO memberConfigDTO = avetConfigRepository.getMemberConfigByChannel(channelId);

        if (memberConfigDTO == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_NOT_FOUND);
        }
        if (memberConfigDTO.getMemberRestrictions() == null || memberConfigDTO.getMemberRestrictions().isEmpty()) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_RESTRICTION_NOT_FOUND);
        }

        Optional<MemberRestriction> memberRestrictionOpt = memberConfigDTO.getMemberRestrictions().stream().filter(re -> re.getSid().equals(restrictionSid)).findFirst();
        if(memberRestrictionOpt.isPresent()) {
            return MembersConverter.toRestrictionDetailDTO(memberRestrictionOpt.get());
        } else {
            throw new OneboxRestException(ApiMgmtErrorCode.MEMBER_CONFIG_RESTRICTION_NOT_FOUND);
        }
    }

    public List<MemberRestrictionDTO> getMemberRestrictions(Long channelId) {
        channelsHelper.getAndCheckChannel(channelId);

        MemberConfigDTO memberConfigDTO = avetConfigRepository.getMemberConfigByChannel(channelId);

        if (memberConfigDTO == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_NOT_FOUND);
        }
        if (memberConfigDTO.getMemberRestrictions() == null || memberConfigDTO.getMemberRestrictions().isEmpty()) {
            return new ArrayList<>();
        }
        return MembersConverter.toRestrictionsDTO(memberConfigDTO.getMemberRestrictions());
    }

    public void createMemberRestriction(Long channelId, MemberRestrictionCreateRequestDTO memberRestrictionCreateRequestDTO) {
        if (memberRestrictionCreateRequestDTO == null) {
            return;
        }
        if(memberRestrictionCreateRequestDTO.getRestrictionName() == null || memberRestrictionCreateRequestDTO.getRestrictionName().isEmpty() || memberRestrictionCreateRequestDTO.getRestrictionName().trim().length() == 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.RESTRICTIONS_NAME_IS_REQUIRED);
        }

        channelsHelper.getAndCheckChannel(channelId);

        MemberConfigDTO memberConfigDTO = avetConfigRepository.getMemberConfigByChannel(channelId);

        if(memberConfigDTO.getMemberRestrictions() == null) {
            memberConfigDTO.setMemberRestrictions(new ArrayList<>());
        }

        if(memberConfigDTO.getMemberRestrictions().stream().filter(re -> re.getSid().equals(memberRestrictionCreateRequestDTO.getSid())).count() > 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.MEMBER_CONFIG_UNIQUE_RESTRICTION_SID);
        }

        if(memberConfigDTO.getMemberRestrictions().stream().filter(re -> re.getName().equals(memberRestrictionCreateRequestDTO.getRestrictionName())).count() > 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.MEMBER_CONFIG_UNIQUE_RESTRICTION_NAME);
        }

        MemberRestriction memberRestriction = MembersConverter.toNewRestriction(memberRestrictionCreateRequestDTO);
        validateRestrictionSid(memberRestriction);
        validateMainCapacity(memberConfigDTO);
        memberConfigDTO.getMemberRestrictions().add(memberRestriction);

        avetConfigRepository.updateMemberConfigByChannel(channelId, memberConfigDTO);
    }

    public void updateMemberRestriction(Long channelId, String restrictionSid, MemberRestrictionRequestDTO memberRestrictionRequestDTO) {
        if (memberRestrictionRequestDTO == null) {
            return;
        }

        if(memberRestrictionRequestDTO.getRestrictionName() != null && (memberRestrictionRequestDTO.getRestrictionName().isEmpty() || memberRestrictionRequestDTO.getRestrictionName().trim().length() == 0)) {
            throw new OneboxRestException(ApiMgmtErrorCode.RESTRICTIONS_NAME_IS_REQUIRED);
        }

        channelsHelper.getAndCheckChannel(channelId);

        MemberConfigDTO memberConfigDTO = avetConfigRepository.getMemberConfigByChannel(channelId);

        if(memberConfigDTO.getMemberRestrictions() == null || memberConfigDTO.getMemberRestrictions().isEmpty()) {
            throw new OneboxRestException(ApiMgmtErrorCode.MEMBER_CONFIG_RESTRICTION_NOT_FOUND);
        }

        if(memberConfigDTO.getMemberRestrictions().stream().noneMatch(re -> re.getSid().equals(restrictionSid))) {
            throw new OneboxRestException(ApiMgmtErrorCode.MEMBER_CONFIG_RESTRICTION_NOT_FOUND);
        }

        List<MemberRestriction> currentRestrictionsByName = memberConfigDTO.getMemberRestrictions().stream().filter(mr -> mr.getName().equals(memberRestrictionRequestDTO.getRestrictionName())).collect(Collectors.toList());
        if(!currentRestrictionsByName.isEmpty() && currentRestrictionsByName.size() > 1) {
            throw new OneboxRestException(ApiMgmtErrorCode.MEMBER_CONFIG_UNIQUE_RESTRICTION_NAME);
        } else if(!currentRestrictionsByName.isEmpty() && currentRestrictionsByName.size() == 1 && !currentRestrictionsByName.get(0).getSid().equals(restrictionSid)) {
            // different sid, same name
            throw new OneboxRestException(ApiMgmtErrorCode.MEMBER_CONFIG_UNIQUE_RESTRICTION_NAME);
        }

        Optional<MemberRestriction> currentRestriction = memberConfigDTO.getMemberRestrictions().stream().filter(mr -> mr.getSid().equals(restrictionSid)).findFirst();
        if(currentRestriction.isEmpty()) {
            throw new OneboxRestException(ApiMgmtErrorCode.MEMBER_CONFIG_RESTRICTION_NOT_FOUND);
        }

        MemberRestriction memberRestriction = MembersConverter.toRestriction(memberRestrictionRequestDTO, currentRestriction.get());
        memberRestriction.setSid(restrictionSid);
        memberRestriction.setRestrictionType(currentRestriction.get().getRestrictionType());

        if(BooleanUtils.isTrue(memberRestrictionRequestDTO.getActivated())
        || memberRestrictionRequestDTO.getActivated() == null && BooleanUtils.isTrue(memberRestriction.getActivated())) {
            checkMemberRestrictionStatus(memberRestriction);
        }

        validateRestrictionSid(memberRestriction);
        validateMemberRestriction(memberRestriction, memberConfigDTO, currentRestriction.get().getRestrictionType());


        List<MemberRestriction> currentRestrictions = memberConfigDTO.getMemberRestrictions().stream().filter(mr -> !mr.getSid().equals(restrictionSid)).collect(Collectors.toList());
        currentRestrictions.add(memberRestriction);
        memberConfigDTO.setMemberRestrictions(currentRestrictions);

        avetConfigRepository.updateMemberConfigByChannel(channelId, memberConfigDTO);
    }

    public void deleteMemberRestriction(Long channelId, String restrictionSid) {
        channelsHelper.getAndCheckChannel(channelId);

        MemberConfigDTO memberConfigDTO = avetConfigRepository.getMemberConfigByChannel(channelId);

        if (memberConfigDTO == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_NOT_FOUND);
        }
        if (memberConfigDTO.getMemberRestrictions() == null || memberConfigDTO.getMemberRestrictions().isEmpty()) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_RESTRICTION_NOT_FOUND);
        }

        if(memberConfigDTO.getMemberRestrictions().stream().noneMatch(re -> re.getSid().equals(restrictionSid))) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_RESTRICTION_NOT_FOUND);
        }

        List<MemberRestriction> memberRestrictions = memberConfigDTO.getMemberRestrictions().stream().filter(mr -> !mr.getSid().equals(restrictionSid)).collect(Collectors.toList());
        memberConfigDTO.setMemberRestrictions(memberRestrictions);

        avetConfigRepository.updateMemberConfigByChannel(channelId, memberConfigDTO);
    }

    private void checkMemberRestrictionStatus(MemberRestriction memberRestriction) {
        if(memberRestriction.getMemberPeriods() == null || memberRestriction.getMemberPeriods().isEmpty()
                || memberRestriction.getName() == null || memberRestriction.getRestrictionType() == null
                || memberRestriction.getTranslations() == null || memberRestriction.getTranslations().isEmpty()
        || memberRestriction.getVenueTemplateSectors() == null || memberRestriction.getVenueTemplateSectors().isEmpty()) {
            throw new OneboxRestException(ApiMgmtErrorCode.RESTRICTION_CANNOT_BE_ACTIVATED);
        } else {
            if(memberRestriction.getFields() == null || memberRestriction.getFields().isEmpty()) {
                throw new OneboxRestException(ApiMgmtErrorCode.RESTRICTION_CANNOT_BE_ACTIVATED);
            }
            RestrictionTypes restrictionTypes = RestrictionTypes.valueOf(memberRestriction.getRestrictionType().name());
            for(MembersRestrictionTypesFields restrictionTypesField : restrictionTypes.getFields()) {
                Map<String, Object> requestFields = (Map<String, Object>) memberRestriction.getFields();
                if(!requestFields.containsKey(restrictionTypesField.getFieldName())) {
                    throw new OneboxRestException(ApiMgmtErrorCode.RESTRICTION_CANNOT_BE_ACTIVATED);
                } else {
                    if(restrictionTypesField.getFieldContainer().equals(DynamicBusinessRuleFieldContainer.SINGLE) && requestFields.get(restrictionTypesField.getFieldName()) == null) {
                        throw new OneboxRestException(ApiMgmtErrorCode.RESTRICTION_CANNOT_BE_ACTIVATED);
                    }
                    if(restrictionTypesField.getFieldContainer().equals(DynamicBusinessRuleFieldContainer.LIST) && ((List) requestFields.get(restrictionTypesField.getFieldName())).isEmpty()) {
                        throw new OneboxRestException(ApiMgmtErrorCode.RESTRICTION_CANNOT_BE_ACTIVATED);
                    }
                    if(restrictionTypesField.getFieldContainer().equals(DynamicBusinessRuleFieldContainer.MAP) && ((Map) requestFields.get(restrictionTypesField.getFieldName())).isEmpty()) {
                        throw new OneboxRestException(ApiMgmtErrorCode.RESTRICTION_CANNOT_BE_ACTIVATED);
                    }
                }
            }
        }
    }

    private void validateRestrictionSid(MemberRestriction memberRestriction) {
        Pattern p = Pattern.compile("[^A-Za-z0-9-_#]");
        Matcher m = p.matcher(memberRestriction.getSid());
        boolean hasSpecialChars = m.find();
        if (hasSpecialChars) {
            throw new OneboxRestException(ApiMgmtErrorCode.RESTRICTIONS_ALLOWED_CHARACTERS);
        }
    }

    private void validateMainCapacity(MemberConfigDTO memberConfigDTO) {
        Optional<MemberCapacity> mainCapacity = memberConfigDTO.getCapacities().stream().filter(ca -> BooleanUtils.isTrue(ca.getMain())).findFirst();
        if(mainCapacity.isEmpty()) {
            throw new OneboxRestException(ApiMgmtErrorCode.MEMBER_CONFIG_HAS_NOT_MAIN_CAPACITY);
        }
    }
    private void validateMemberRestriction(MemberRestriction memberRestriction, MemberConfigDTO memberConfigDTO, RestrictionType restrictionType) {
        Optional<MemberCapacity> mainCapacity = memberConfigDTO.getCapacities().stream().filter(ca -> BooleanUtils.isTrue(ca.getMain())).findFirst();
        if(mainCapacity.isEmpty()) {
            throw new OneboxRestException(ApiMgmtErrorCode.MEMBER_CONFIG_HAS_NOT_MAIN_CAPACITY);
        }
        RolInfoList rolInfoList = dispatcherRepository.getRolesInfo(memberConfigDTO.getEntityId(), mainCapacity.get().getAvetCapacityId());
        List<Integer> roleInfoListIds = rolInfoList.stream().map(RolPartnerInfo::getIdTipo).distinct().collect(Collectors.toList());

        List<Integer> memberRestrictionRoles = new ArrayList<>();

        if(memberRestriction.getFields() != null && !memberRestriction.getFields().isEmpty()) {
            RestrictionTypes restrictionTypes = RestrictionTypes.valueOf(restrictionType.name());
            for (MembersRestrictionTypesFields restrictionTypesField : restrictionTypes.getFields()) {
                Map<String, Object> field = (Map<String, Object>) memberRestriction.getFields();
                if(restrictionTypesField.getFieldContainer().equals(DynamicBusinessRuleFieldContainer.SINGLE) && restrictionTypesField.getFieldType().equals(DynamicBusinessRuleFieldType.INTEGER)) {
                    if (field.containsKey(restrictionTypesField.getFieldName())) {
                        Integer object = (Integer) field.get(restrictionTypesField.getFieldName());
                        if(object != null && object < 0) {
                            throw new OneboxRestException(ApiMgmtErrorCode.RESTRICTIONS_VALUES_MUST_BE_A_POSITIVE_NUMBER);
                        }
                    }
                }
                if (restrictionTypesField.getValueSource() != null && restrictionTypesField.getValueSource().equals("ROLE_ID")) {
                    if (field.containsKey(restrictionTypesField.getFieldName())) {
                        Object object = (Object) field.get(restrictionTypesField.getFieldName());
                        if(object != null) {
                            List<Integer> roles = (List<Integer>) object;
                            memberRestrictionRoles.addAll(roles);
                        }
                    }
                }
            }

            if (!roleInfoListIds.containsAll(memberRestrictionRoles)) {
                throw new OneboxRestException(ApiMgmtErrorCode.INVALID_ROLE);
            }

            List<Sector> sectors = venuesRepository.getSectors(mainCapacity.get().getVenueTemplateId());
            List<Long> obSectorIds = sectors.stream().map(Sector::getId).distinct().collect(Collectors.toList());
            if (memberRestriction.getVenueTemplateSectors() != null && !obSectorIds.containsAll(memberRestriction.getVenueTemplateSectors())) {
                throw new OneboxRestException(ApiMgmtErrorCode.INVALID_OB_SECTOR);
            }
        }

        memberRestriction.setVenueTemplateId(mainCapacity.get().getVenueTemplateId());
    }

}
