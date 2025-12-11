package es.onebox.mgmt.channels.members.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.converter.ChannelConverter;
import es.onebox.mgmt.channels.dto.DatesFilter;
import es.onebox.mgmt.channels.dto.DatesFilterDTO;
import es.onebox.mgmt.channels.dto.EmissionReasonsDTO;
import es.onebox.mgmt.channels.dto.MemberCapacitiesListDTO;
import es.onebox.mgmt.channels.dto.MemberCapacitiesRequestDTO;
import es.onebox.mgmt.channels.dto.MemberConfigsDTO;
import es.onebox.mgmt.channels.dto.MemberDatesFilterDTO;
import es.onebox.mgmt.channels.dto.MembershipPaymentInfoDTO;
import es.onebox.mgmt.channels.dto.PaymentModesDTO;
import es.onebox.mgmt.channels.dto.SubscriptionModeDTO;
import es.onebox.mgmt.channels.dto.TranslationsDTO;
import es.onebox.mgmt.channels.dto.TranslationsRequestDTO;
import es.onebox.mgmt.channels.dto.UpdateDateFilterDTO;
import es.onebox.mgmt.channels.dto.UpdateMemberConfigChargesDTO;
import es.onebox.mgmt.channels.dto.UpdateMemberConfigsDTO;
import es.onebox.mgmt.channels.members.converter.MembersConfigConverter;
import es.onebox.mgmt.channels.members.converter.MembersConverter;
import es.onebox.mgmt.channels.members.dto.MemberPeriodicityDTO;
import es.onebox.mgmt.channels.members.dto.MemberRoleDTO;
import es.onebox.mgmt.channels.utils.ChannelUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.integration.avetconfig.repository.AvetConfigRepository;
import es.onebox.mgmt.datasources.integration.dispatcher.repository.DispatcherRepository;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelStatus;
import es.onebox.mgmt.datasources.ms.entity.dto.MemberCapacity;
import es.onebox.mgmt.datasources.ms.entity.dto.MemberConfigDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.MemberOperationPeriod;
import es.onebox.mgmt.datasources.ms.entity.dto.Periodicity;
import es.onebox.mgmt.datasources.ms.entity.dto.RoleTranslation;
import es.onebox.mgmt.datasources.ms.entity.dto.SubscriptionMode;
import es.onebox.mgmt.datasources.ms.entity.dto.Translations;
import es.onebox.mgmt.datasources.ms.entity.enums.MemberPeriodType;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class MembersConfigService {

    private final ChannelsHelper channelsHelper;
    private final AvetConfigRepository avetConfigRepository;
    private final MasterdataService masterdataService;
    private final DispatcherRepository dispatcherRepository;

    @Autowired
    public MembersConfigService(ChannelsHelper channelsHelper, AvetConfigRepository avetConfigRepository,
                                MasterdataService masterdataService, DispatcherRepository dispatcherRepository) {
        this.channelsHelper = channelsHelper;
        this.avetConfigRepository = avetConfigRepository;
        this.masterdataService = masterdataService;
        this.dispatcherRepository = dispatcherRepository;
    }

    public MemberConfigsDTO getMemberConfig(Long channelId) {
        channelsHelper.getAndCheckChannel(channelId);
        MemberConfigDTO memberConfigDTO = getMemberConfigOrFail(channelId);
        String entityId = memberConfigDTO.getEntityId() != null ? memberConfigDTO.getEntityId().toString() : null;
        if (entityId == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_CONFIG_NOT_COMPLETED);
        }
        return MembersConfigConverter.fromIntAvetConnector(memberConfigDTO);
    }

    public void updateMemberConfigCharges(Long channelId, UpdateMemberConfigChargesDTO updateMemberConfigChargesDTO) {
        channelsHelper.getAndCheckChannel(channelId);
        MemberConfigDTO memberConfigDTO = getMemberConfigOrFail(channelId);
        MembersConfigConverter.toMemberConfigCharges(memberConfigDTO, updateMemberConfigChargesDTO);
        avetConfigRepository.updateMemberConfigByChannel(channelId, memberConfigDTO);
    }

    public void updateMemberConfig(Long channelId, UpdateMemberConfigsDTO updateMemberConfigsDTO) {
        ChannelResponse channelResponse = channelsHelper.getAndCheckChannel(channelId);
        MemberConfigDTO memberConfigDTO = getMemberConfigOrFail(channelId);
        if ((channelResponse.getStatus() != null && channelResponse.getStatus().equals(ChannelStatus.ACTIVE))
                && memberConfigDTO.getMemberOperationPeriods() != null) {
            ChannelUtils.channelMembersValidations(memberConfigDTO);
        }
        if (BooleanUtils.isTrue(updateMemberConfigsDTO.getCaptchaEnabled()) &&
                (updateMemberConfigsDTO.getCaptchaSecretKey() == null || updateMemberConfigsDTO.getCaptchaSiteKey() == null)) {
            throw new OneboxRestException(ApiMgmtErrorCode.CAPTCHA_KEY_SECRET_REQUIRED);
        }
        MembersConfigConverter.toMemberConfig(memberConfigDTO, updateMemberConfigsDTO);
        avetConfigRepository.updateMemberConfigByChannel(channelId, memberConfigDTO);
    }

    public List<SubscriptionModeDTO> getSubscriptionModes(Long channelId) {
        channelsHelper.getAndCheckChannel(channelId);
        MemberConfigDTO memberConfigDTO = getMemberConfigOrFail(channelId);

        return MembersConfigConverter.convert(memberConfigDTO.getSubscriptionModes());
    }

    public void createSubscriptionModes(Long channelId, SubscriptionModeDTO subscriptionModes) {
        channelsHelper.getAndCheckChannel(channelId);
        MemberConfigDTO memberConfigDTO = getMemberConfigOrFail(channelId);
        getMainCapacityOrFail(memberConfigDTO);
        validateSubscriptionModeSid(subscriptionModes);

        List<SubscriptionMode> subscriptionModesMemberConfig = memberConfigDTO.getSubscriptionModes();
        if (subscriptionModesMemberConfig == null) {
            subscriptionModesMemberConfig = new ArrayList<>();
        }

        if (subscriptionModesMemberConfig.stream().anyMatch(mode -> mode.getSid().equals(subscriptionModes.getSid()))) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_SUBSCRIPTION_MODE_SID_EXISTS);
        }
        subscriptionModes.setActive(true);
        subscriptionModesMemberConfig.add(MembersConfigConverter.toMSEntity(subscriptionModes));
        memberConfigDTO.setSubscriptionModes(subscriptionModesMemberConfig);
        avetConfigRepository.updateMemberConfigByChannel(channelId, memberConfigDTO);
    }

    public void updateSubscriptionMode(Long channelId, String sid, SubscriptionModeDTO subscriptionMode) {
        channelsHelper.getAndCheckChannel(channelId);
        MemberConfigDTO memberConfigDTO = getMemberConfigOrFail(channelId);
        getMainCapacityOrFail(memberConfigDTO);
        if (subscriptionMode.getSid() != null) {
            validateSubscriptionModeSid(subscriptionMode);
        }

        SubscriptionMode mode = memberConfigDTO.getSubscriptionModes().stream()
                .filter(sm -> sm.getSid().equals(sid))
                .findAny().orElseThrow(() -> new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_SUBSCRIPTION_MODE_NOT_FOUND));

        mode.setCapacities(subscriptionMode.getCapacities() != null ? subscriptionMode.getCapacities() : mode.getCapacities());
        mode.setAllowedRoles(subscriptionMode.getRoles() != null ? subscriptionMode.getRoles() : mode.getAllowedRoles());
        mode.setAllowedPeriodicities(subscriptionMode.getPeriodicities() != null ? subscriptionMode.getPeriodicities() : mode.getAllowedPeriodicities());
        mode.setName(subscriptionMode.getName() != null ? subscriptionMode.getName() : mode.getName());
        mode.setDefaultBuyPeriodicity(subscriptionMode.getDefaultBuyPeriodicity() != null ? subscriptionMode.getDefaultBuyPeriodicity() : mode.getDefaultBuyPeriodicity());
        mode.setDefaultBuyRoleId(subscriptionMode.getDefaultBuyRoleId() != null ? subscriptionMode.getDefaultBuyRoleId() : mode.getDefaultBuyRoleId());
        mode.setActive(subscriptionMode.getActive() != null ? subscriptionMode.getActive() : mode.getActive());
        avetConfigRepository.updateMemberConfigByChannel(channelId, memberConfigDTO);
    }

    public void deleteSubscriptionMode(Long channelId, String sid) {
        channelsHelper.getAndCheckChannel(channelId);
        MemberConfigDTO memberConfigDTO = getMemberConfigOrFail(channelId);
        getMainCapacityOrFail(memberConfigDTO);

        if (memberConfigDTO.getSubscriptionModes().stream().anyMatch(mode -> mode.getSid().equals(sid))) {
            memberConfigDTO.setSubscriptionModes(memberConfigDTO.getSubscriptionModes().stream()
                    .filter(mode -> !mode.getSid().equals(sid)).collect(Collectors.toList()));
            avetConfigRepository.updateMemberConfigByChannel(channelId, memberConfigDTO);
        } else {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_SUBSCRIPTION_MODE_NOT_FOUND);
        }
    }

    public SubscriptionModeDTO getSubscriptionMode(Long channelId, String sId) {
        channelsHelper.getAndCheckChannel(channelId);
        MemberConfigDTO memberConfigDTO = getMemberConfigOrFail(channelId);
        MemberCapacity mainCapacity = memberConfigDTO.getCapacities().stream().filter(MemberCapacity::isMain).findFirst().orElse(null);
        if (mainCapacity == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_CAPACITY_NOT_FOUND);
        }

        SubscriptionMode subscriptionMode = memberConfigDTO.getSubscriptionModes().stream()
                .filter(mode -> mode.getSid().equals(sId)).findAny().orElse(null);

        if (subscriptionMode == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_SUBSCRIPTION_MODE_NOT_FOUND);
        }
        return MembersConfigConverter.toDTO(subscriptionMode);
    }

    public Map<String, TranslationsDTO> getSubscriptionModeCommunications(Long channelId, String sId) {
        channelsHelper.getAndCheckChannel(channelId);
        MemberConfigDTO memberConfigDTO = getMemberConfigOrFail(channelId);

        MemberCapacity mainCapacity = memberConfigDTO.getCapacities().stream().filter(MemberCapacity::isMain).findFirst().orElse(null);
        if (mainCapacity == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_CAPACITY_NOT_FOUND);
        }

        Map<String, TranslationsDTO> translationsDTO = null;
        SubscriptionMode subscriptionMode = memberConfigDTO.getSubscriptionModes().stream().filter(mode -> mode.getSid().equalsIgnoreCase(sId)).findFirst().orElse(null);
        if (subscriptionMode != null) {
            translationsDTO = MembersConfigConverter.toDTO(subscriptionMode).getTranslations();
        }
        return translationsDTO;
    }

    public void updateSubscriptionModeCommunications(Long channelId, String sId, TranslationsRequestDTO translationsMap) {
        ChannelResponse channelResponse = channelsHelper.getAndCheckChannel(channelId);
        MemberConfigDTO memberConfigDTO = getMemberConfigOrFail(channelId);

        MemberCapacity mainCapacity = memberConfigDTO.getCapacities().stream().filter(MemberCapacity::isMain).findFirst().orElse(null);
        if (mainCapacity == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_CAPACITY_NOT_FOUND);
        }
        SubscriptionMode subscriptionMode = memberConfigDTO.getSubscriptionModes().stream()
                .filter(mode -> mode.getSid().equals(sId)).findAny().orElse(null);

        if (subscriptionMode == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_SUBSCRIPTION_MODE_NOT_FOUND);
        }
        Set<String> channelLanguages = ChannelConverter.fromLanguageIdToLanguageCode(channelResponse.getLanguages().getSelectedLanguages(),
                masterdataService.getLanguagesByIds());


        if (translationsMap != null) {
            Map<String, Translations> translations = new HashMap<>();
            for (Map.Entry<String, TranslationsDTO> entry : translationsMap.entrySet()) {
                if (channelLanguages.contains(entry.getKey())) {
                    translations.put(entry.getKey(), new Translations(entry.getValue().getName(), entry.getValue().getDescription(), entry.getValue().getLink(), entry.getValue().getLinkText()));
                }
            }
            subscriptionMode.setTranslations(translations);
        }

        avetConfigRepository.updateMemberConfigByChannel(channelId, memberConfigDTO);
    }

    public List<MemberPeriodicityDTO> getMemberPeriodicities(Long channelId) {
        channelsHelper.getAndCheckChannel(channelId);
        MemberConfigDTO memberConfigDTO = getMemberConfigOrFail(channelId);
        List<MemberPeriodicityDTO> result = new ArrayList<>();
        for (Periodicity periodicity : memberConfigDTO.getPeriodicityTranslations()) {
            MemberPeriodicityDTO memberPeriodicityDTO = new MemberPeriodicityDTO();
            memberPeriodicityDTO.setPeriodicityId(periodicity.getId());
            memberPeriodicityDTO.setTranslations(MembersConfigConverter.convertTranslations(periodicity.getTranslations()));
            result.add(memberPeriodicityDTO);
        }
        return result;
    }

    public TranslationsRequestDTO getMemberPeriodicityCommunication(Long channelId, Long periodicityId) {
        channelsHelper.getAndCheckChannel(channelId);
        MemberConfigDTO memberConfigDTO = avetConfigRepository.getMemberConfigByChannel(channelId);
        if (memberConfigDTO == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_CONFIG_NOT_FOUND);
        }
        return MembersConverter.toPeriodicitiesCommunication(memberConfigDTO, periodicityId);
    }

    public void updateMemberPeriodicityCommunication(Long channelId, Long periodicityId, TranslationsRequestDTO translationsRequestDTO) {
        channelsHelper.getAndCheckChannel(channelId);
        MemberConfigDTO memberConfigDTO = getMemberConfigOrFail(channelId);

        if (memberConfigDTO.getPeriodicityTranslations() != null) {
            Optional<Periodicity> periodicity = memberConfigDTO.getPeriodicityTranslations().stream().filter(pe -> pe.getId().equals(periodicityId)).findFirst();
            if (periodicity.isPresent()) {
                periodicity.get().setTranslations(MembersConfigConverter.convertTranslationsDTO(translationsRequestDTO));
            } else {
                Periodicity newPeriodicity = new Periodicity();
                newPeriodicity.setId(periodicityId);
                newPeriodicity.setTranslations(MembersConfigConverter.convertTranslationsDTO(translationsRequestDTO));
                memberConfigDTO.getPeriodicityTranslations().add(newPeriodicity);
            }
        } else {
            memberConfigDTO.setPeriodicityTranslations(new ArrayList<>());
            Periodicity newPeriodicity = new Periodicity();
            newPeriodicity.setId(periodicityId);
            newPeriodicity.setTranslations(MembersConfigConverter.convertTranslationsDTO(translationsRequestDTO));
            memberConfigDTO.getPeriodicityTranslations().add(newPeriodicity);
        }

        avetConfigRepository.updateMemberConfigByChannel(channelId, memberConfigDTO);
    }

    public TranslationsRequestDTO getMemberRoleCommunication(Long channelId, Long roleId) {
        channelsHelper.getAndCheckChannel(channelId);
        MemberConfigDTO memberConfigDTO = getMemberConfigOrFail(channelId);

        MemberCapacity mainCapacity = memberConfigDTO.getCapacities().stream().filter(MemberCapacity::isMain).findFirst().orElse(null);
        if (mainCapacity == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_CAPACITY_NOT_FOUND);
        }
        return MembersConverter.toRolesCommunication(memberConfigDTO, roleId);
    }

    public void updateMemberRoleCommunication(Long channelId, Long roleId, TranslationsRequestDTO translationsRequestDTO) {
        channelsHelper.getAndCheckChannel(channelId);
        MemberConfigDTO memberConfigDTO = getMemberConfigOrFail(channelId);

        MemberCapacity mainCapacity = memberConfigDTO.getCapacities().stream().filter(MemberCapacity::isMain).findFirst().orElse(null);
        if (mainCapacity == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_CAPACITY_NOT_FOUND);
        }
        if (memberConfigDTO.getRoleTranslations() != null) {
            Optional<RoleTranslation> role = memberConfigDTO.getRoleTranslations().stream().filter(pe -> pe.getId().equals(roleId)).findFirst();
            if (role.isPresent()) {
                role.get().setTranslations(MembersConfigConverter.convertTranslationsDTO(translationsRequestDTO));
            } else {
                RoleTranslation newRole = new RoleTranslation();
                newRole.setId(roleId);
                newRole.setTranslations(MembersConfigConverter.convertTranslationsDTO(translationsRequestDTO));
                memberConfigDTO.getRoleTranslations().add(newRole);
            }
        } else {
            memberConfigDTO.setRoleTranslations(new ArrayList<>());
            RoleTranslation newRole = new RoleTranslation();
            newRole.setId(roleId);
            newRole.setTranslations(MembersConfigConverter.convertTranslationsDTO(translationsRequestDTO));
            memberConfigDTO.getRoleTranslations().add(newRole);
        }
        avetConfigRepository.updateMemberConfigByChannel(channelId, memberConfigDTO);
    }

    public List<MemberRoleDTO> getMemberRoles(Long channelId) {
        channelsHelper.getAndCheckChannel(channelId);
        MemberConfigDTO memberConfigDTO = getMemberConfigOrFail(channelId);

        MemberCapacity mainCapacity = memberConfigDTO.getCapacities().stream().filter(MemberCapacity::isMain).findFirst().orElse(null);
        if (mainCapacity == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_CAPACITY_NOT_FOUND);
        }
        List<MemberRoleDTO> result = new ArrayList<>();
        for (RoleTranslation role : memberConfigDTO.getRoleTranslations()) {
            MemberRoleDTO memberRoleDTO = new MemberRoleDTO();
            memberRoleDTO.setRoleId(role.getId());
            memberRoleDTO.setTranslations(MembersConfigConverter.convertTranslations(role.getTranslations()));
            result.add(memberRoleDTO);
        }
        return result;
    }

    public MemberCapacitiesListDTO getMemberConfigCapacities(Long channelId) {
        channelsHelper.getAndCheckChannel(channelId);
        MemberConfigDTO memberConfigDTO = getMemberConfigOrFail(channelId);
        List<MemberCapacity> capacities = memberConfigDTO.getCapacities();
        return MembersConfigConverter.toDTO(capacities);
    }

    public void updateMemberConfigCapacities(Long channelId, MemberCapacitiesRequestDTO memberCapacitiesRequestDTO) {
        channelsHelper.getAndCheckChannel(channelId);
        if (memberCapacitiesRequestDTO.size() > 1 && memberCapacitiesRequestDTO.stream().filter(ca -> ca.getMain().equals(true)).count() > 1) {
            throw new OneboxRestException(ApiMgmtErrorCode.MEMBER_CONFIG_UNIQUE_MAIN_CAPACITY);
        }
        MemberConfigDTO memberConfigDTO = avetConfigRepository.getMemberConfigByChannel(channelId);

        if (memberConfigDTO != null) {
            memberConfigDTO.setCapacities(MembersConfigConverter.toMSEntity(memberCapacitiesRequestDTO));
            avetConfigRepository.updateMemberConfigByChannel(channelId, memberConfigDTO);
        } else {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_SUBSCRIPTION_MODE_NOT_FOUND);
        }
    }

    public void deleteMemberConfigCapacities(Long channelId, Long capacityId) {
        channelsHelper.getAndCheckChannel(channelId);
        MemberConfigDTO memberConfigDTO = getMemberConfigOrFail(channelId);
        List<MemberCapacity> capacities = memberConfigDTO.getCapacities();

        if (capacityId != null) {
            capacities = capacities.stream().filter(capacity -> !capacity.getAvetCapacityId().equals(capacityId)).collect(Collectors.toList());
            memberConfigDTO.setCapacities(capacities);
            avetConfigRepository.updateMemberConfigByChannel(channelId, memberConfigDTO);
        } else {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_SUBSCRIPTION_MODE_NOT_FOUND);
        }
    }

    public void updateDatesFilter(Long channelId, MemberPeriodType type, UpdateDateFilterDTO dateFilterDTO) {
        if (BooleanUtils.isTrue(dateFilterDTO.getDateFilterEnabled()) && dateFilterDTO.getDefaultAccess() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "default_access is mandatory", null);
        }
        MemberConfigDTO memberConfigDTO = getMemberConfigOrFail(channelId);
        String memberClubName = memberConfigDTO.getUrl();
        MemberConfigDTO updateMemberConfigDTO = MembersConfigConverter.updateDateFilterEnabled(memberConfigDTO, type, dateFilterDTO);
        DatesFilter datesFilter = MembersConfigConverter.toDatesFilter(dateFilterDTO, type);

        avetConfigRepository.updateMemberConfigByChannel(channelId, updateMemberConfigDTO);
        avetConfigRepository.updateDatesFilter(memberClubName, datesFilter);
    }

    public MemberDatesFilterDTO getDatesFilter(Long channelId, MemberPeriodType type) {
        MemberConfigDTO memberConfigDTO = getMemberConfigOrFail(channelId);
        String memberClubName = memberConfigDTO.getUrl();
        DatesFilterDTO datesFilterDTO = getValidDatesFilter(memberClubName, type);
        MemberOperationPeriod periodConfig = memberConfigDTO.getMemberOperationPeriods().getOrDefault(type, null);
        return MembersConfigConverter.toMemberDatesFilterDTO(periodConfig, datesFilterDTO);
    }
  
    public void setMembershipPaymentInfo(Long channelId, MembershipPaymentInfoDTO membershipPaymentInfo) {
        channelsHelper.getAndCheckChannel(channelId);
        MemberConfigDTO memberConfigDTO = getMemberConfigOrFail(channelId);
        memberConfigDTO.setMembershipTermId(membershipPaymentInfo.getTermId());
        memberConfigDTO.setMembershipPeriodicityId(membershipPaymentInfo.getPeriodicityId());
        avetConfigRepository.updateMemberConfigByChannel(channelId, memberConfigDTO);
    }

    public EmissionReasonsDTO getEmissionReasons(Long channelId) {
        channelsHelper.getAndCheckChannel(channelId);
        MemberConfigDTO memberConfigDTO = getMemberConfigOrFail(channelId);

        return MembersConfigConverter.toDTO(dispatcherRepository.getEmissionReasons(memberConfigDTO.getEntityId()));
    }

    public PaymentModesDTO getPaymentModes(Long channelId) {
        channelsHelper.getAndCheckChannel(channelId);
        MemberConfigDTO memberConfigDTO = getMemberConfigOrFail(channelId);

        return MembersConfigConverter.toDTO(dispatcherRepository.getPaymentModes(memberConfigDTO.getEntityId()));
    }

    private void validateSubscriptionModeSid(SubscriptionModeDTO subscriptionMode) {
        Pattern p = Pattern.compile("[^A-Za-z0-9-_#]");
        Matcher m = p.matcher(subscriptionMode.getSid());
        boolean hasSpecialChars = m.find();
        if (hasSpecialChars) {
            throw new OneboxRestException(ApiMgmtErrorCode.SUBSCRIPTION_MODES_ALLOWED_CHARACTERS);
        }
    }

    private MemberConfigDTO getMemberConfigOrFail(Long channelId) {
        MemberConfigDTO memberConfigDTO = avetConfigRepository.getMemberConfigByChannel(channelId);
        if (memberConfigDTO == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_NOT_FOUND);
        }
        return memberConfigDTO;
    }

    private DatesFilterDTO getValidDatesFilter(String memberClubName, MemberPeriodType type) {
        DatesFilterDTO datesFilter = avetConfigRepository.getDatesFilter(memberClubName, type);
        if (datesFilter == null) {
            datesFilter = new DatesFilterDTO();
        }
        return datesFilter;
    }

    private void getMainCapacityOrFail(MemberConfigDTO memberConfig) {
        memberConfig.getCapacities().stream().filter(MemberCapacity::isMain).findFirst()
                .orElseThrow(() -> new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_CAPACITY_NOT_FOUND));
    }
}
