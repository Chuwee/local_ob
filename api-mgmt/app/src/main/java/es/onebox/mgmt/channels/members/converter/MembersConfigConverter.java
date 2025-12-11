package es.onebox.mgmt.channels.members.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.channels.dto.AccessUsersDatesFilter;
import es.onebox.mgmt.channels.dto.DatesFilter;
import es.onebox.mgmt.channels.dto.DatesFilterDTO;
import es.onebox.mgmt.channels.dto.EmissionReasonsDTO;
import es.onebox.mgmt.channels.dto.MemberCapacitiesListDTO;
import es.onebox.mgmt.channels.dto.MemberCapacitiesRequestDTO;
import es.onebox.mgmt.channels.dto.MemberCapacityDTO;
import es.onebox.mgmt.channels.dto.MemberConfigsDTO;
import es.onebox.mgmt.channels.dto.MemberConfigurationStructureDTO;
import es.onebox.mgmt.channels.dto.MemberDatesFilter;
import es.onebox.mgmt.channels.dto.MemberDatesFilterDTO;
import es.onebox.mgmt.channels.dto.MemberOperationPeriodDTO;
import es.onebox.mgmt.channels.dto.MembersCardImageContentDTO;
import es.onebox.mgmt.channels.dto.PaymentModesDTO;
import es.onebox.mgmt.channels.dto.SubscriptionModeDTO;
import es.onebox.mgmt.channels.dto.TranslationsDTO;
import es.onebox.mgmt.channels.dto.UpdateDateFilterDTO;
import es.onebox.mgmt.channels.dto.UpdateMemberConfigChargesDTO;
import es.onebox.mgmt.channels.dto.UpdateMemberConfigsDTO;
import es.onebox.mgmt.channels.enums.AvetPermission;
import es.onebox.mgmt.channels.enums.MemberStructureType;
import es.onebox.mgmt.channels.members.dto.AvatarDTO;
import es.onebox.mgmt.channels.members.dto.AvetEvent;
import es.onebox.mgmt.channels.members.dto.AvetEventDTO;
import es.onebox.mgmt.channels.utils.ChannelUtils;
import es.onebox.mgmt.common.restrictions.DynamicBusinessRuleFieldType;
import es.onebox.mgmt.common.restrictions.StructureContainer;
import es.onebox.mgmt.common.restrictions.StructureContainerDataOrigin;
import es.onebox.mgmt.common.restrictions.dto.ConfigurationStructureFieldDTO;
import es.onebox.mgmt.datasources.integration.avetconfig.dto.Avatar;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.MotivoEmisionSummary;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.PaymentModes;
import es.onebox.mgmt.datasources.ms.channel.dto.MembersCardImageContent;
import es.onebox.mgmt.datasources.ms.entity.dto.MemberCapacity;
import es.onebox.mgmt.datasources.ms.entity.dto.MemberConfigDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.MemberOperationPeriod;
import es.onebox.mgmt.datasources.ms.entity.dto.SubscriptionMode;
import es.onebox.mgmt.datasources.ms.entity.dto.Translations;
import es.onebox.mgmt.datasources.ms.entity.enums.MemberPeriodType;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.members.DynamicBusinessRuleConfigurable;
import es.onebox.mgmt.members.DynamicBusinessRuleFieldContainer;
import es.onebox.mgmt.members.DynamicBusinessRuleFields;
import es.onebox.mgmt.members.DynamicBusinessRuleTypes;
import es.onebox.mgmt.members.MemberOrderType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static es.onebox.mgmt.common.ConverterUtils.updateField;

public class MembersConfigConverter {
    private static final String DEFAULT = "default";

    private MembersConfigConverter() {
    }

    public static MemberConfigsDTO fromIntAvetConnector(MemberConfigDTO memberConfigDTO) {
        MemberConfigsDTO memberConfigsDTO = new MemberConfigsDTO();
        memberConfigsDTO.setMemberEnabled(memberConfigDTO.getMemberEnabled());
        memberConfigsDTO.setFreeSeat(memberConfigDTO.getFreeSeat() != null ? Boolean.valueOf(memberConfigDTO.getFreeSeat().toString()) : null);
        if (memberConfigDTO.getAdminOptions() != null) {
            memberConfigsDTO.setMaxAdditionalMembers(memberConfigDTO.getAdminOptions().getMaxAdditionalMembers());
            memberConfigsDTO.setCaptchaSecretKey(memberConfigDTO.getAdminOptions().getCaptchaSecretKey());
        }
        memberConfigsDTO.setSubscriptionModes(memberConfigDTO.getSubscriptionModes() != null ? convert(memberConfigDTO.getSubscriptionModes()) : null);
        memberConfigsDTO.setBlockedMatches(memberConfigDTO.getAdminOptions() != null ? memberConfigDTO.getAdminOptions().getBlockedMatches() : null);
        memberConfigsDTO.setAllowFreeSeatTill(memberConfigDTO.getAdminOptions() != null ? memberConfigDTO.getAdminOptions().getAllowFreeSeatTill() : null);
        memberConfigsDTO.setAllowRecoverSeatTill(memberConfigDTO.getAdminOptions() != null ? memberConfigDTO.getAdminOptions().getAllowRecoverSeatTill() : null);
        memberConfigsDTO.setChangePin(memberConfigDTO.getChangePin());
        memberConfigsDTO.setRememberPin(memberConfigDTO.getRememberPin());
        memberConfigsDTO.setUserArea(memberConfigDTO.getUserArea());
        memberConfigsDTO.setMemberOperationPeriods(convertPeriods(memberConfigDTO.getMemberOperationPeriods()));
        memberConfigsDTO.setPricesBatchEnabled(memberConfigDTO.getAdminOptions() != null ? memberConfigDTO.getAdminOptions().getPricesBatchEnabled() : null);
        memberConfigsDTO.setShowPreviousSeat(memberConfigDTO.getShowPreviousSeat());
        memberConfigsDTO.setShowRole(memberConfigDTO.getShowRole());
        memberConfigsDTO.setShowSubscriptionMode(memberConfigDTO.getShowSubscriptionMode());
        memberConfigsDTO.setForceRegeneratePassbook(memberConfigDTO.getForceRegeneratePassbook());
        memberConfigsDTO.setExpirationDatePassbook(memberConfigDTO.getExpirationDatePassbook());
        memberConfigsDTO.setOpenAdditionalMembers(memberConfigDTO.getOpenAdditionalMembers());
        memberConfigsDTO.setSignUpEmail(memberConfigDTO.getSignUpEmail());
        memberConfigsDTO.setBuyUrl(memberConfigDTO.getBuyUrl());
        memberConfigsDTO.setCaptchaEnabled(memberConfigDTO.getCaptchaEnabled());
        memberConfigsDTO.setCaptchaSiteKey(memberConfigDTO.getCaptchaSiteKey());
        memberConfigsDTO.setPublicAvailabilityEnabled(memberConfigDTO.getPublicAvailabilityEnabled());
        memberConfigsDTO.setLandingButtonUrl(memberConfigDTO.getLandingButtonUrl());
        memberConfigsDTO.setTransferSeat(memberConfigDTO.getTransferSeat());
        memberConfigsDTO.setDownloadPassbookPermissions(toDownloadPassbookPermissions(memberConfigDTO.getDownloadPassbookPermissions()));
        memberConfigsDTO.setBuySeatPermission(AvetPermission.fromValue(memberConfigDTO.getBuySeatPermission()));
        memberConfigsDTO.setNewMemberPermission(AvetPermission.fromValue(memberConfigDTO.getNewMemberPermission()));
        memberConfigsDTO.setMembershipTermId(memberConfigDTO.getMembershipTermId());
        memberConfigsDTO.setMembershipPeriodicityId(memberConfigDTO.getMembershipPeriodicityId());
        memberConfigsDTO.setAllowCrossPurchases(memberConfigDTO.getAllowCrossPurchases());
        memberConfigsDTO.setAllowTutorForm(memberConfigDTO.getAllowTutorForm());
        memberConfigsDTO.setTuteeMaxAge(memberConfigDTO.getTuteeMaxAge());
        if (memberConfigDTO.getMembersCardImageContent() != null) {
            memberConfigsDTO.setMembersCardImageContent(toMembersCardImageContentDTO(memberConfigDTO.getMembersCardImageContent()));
        }
        return memberConfigsDTO;
    }

    public static MembersCardImageContentDTO toMembersCardImageContentDTO(MembersCardImageContent membersCardImageContent) {
        MembersCardImageContentDTO membersCardImageContentDTO = new MembersCardImageContentDTO();
        membersCardImageContentDTO.setImageUrl(membersCardImageContent.getImageUrl());
        return membersCardImageContentDTO;
    }

    private static List<AvetPermission> toDownloadPassbookPermissions(List<String> permissions) {
        if (CollectionUtils.isEmpty(permissions)) {
            return List.of();
        }
        return permissions.stream().map(AvetPermission::fromValue).toList();
    }

    public static void toMemberConfigCharges(MemberConfigDTO memberConfigDTO, UpdateMemberConfigChargesDTO updateMemberConfigChargesDTO) {
        updateMemberConfigChargesDTO.getCharges().forEach((key, value) ->
                memberConfigDTO.getMemberOperationPeriods().get(MemberPeriodType.valueOf(key)).setCharge(value));
    }

    public static void toMemberConfig(MemberConfigDTO memberConfigDTO, UpdateMemberConfigsDTO updateMemberConfigsDTO) {
        memberConfigDTO.setFreeSeat(updateMemberConfigsDTO.getFreeSeat());
        memberConfigDTO.setMemberEnabled(updateMemberConfigsDTO.getMemberEnabled());
        memberConfigDTO.setMemberOperationPeriods(updatePeriods(updateMemberConfigsDTO.getMemberOperationPeriods(), memberConfigDTO.getMemberOperationPeriods()));
        memberConfigDTO.getAdminOptions().setMaxAdditionalMembers(updateMemberConfigsDTO.getMaxAdditionalMembers());
        memberConfigDTO.getAdminOptions().setBlockedMatches(updateMemberConfigsDTO.getBlockedMatches());
        memberConfigDTO.getAdminOptions().setAllowFreeSeatTill(updateMemberConfigsDTO.getAllowFreeSeatTill());
        memberConfigDTO.getAdminOptions().setAllowRecoverSeatTill(updateMemberConfigsDTO.getAllowRecoverSeatTill());
        memberConfigDTO.getAdminOptions().setPricesBatchEnabled(CommonUtils.isTrue(memberConfigDTO.getFreeSeat())
                || ChannelUtils.hasActivePeriod(memberConfigDTO.getMemberOperationPeriods()));
        memberConfigDTO.setChangePin(updateMemberConfigsDTO.getChangePin());
        memberConfigDTO.setRememberPin(updateMemberConfigsDTO.getRememberPin());
        memberConfigDTO.setUserArea(updateMemberConfigsDTO.getUserArea());
        memberConfigDTO.setShowRole(updateMemberConfigsDTO.getShowRole());
        memberConfigDTO.setShowPreviousSeat(updateMemberConfigsDTO.getShowPreviousSeat());
        memberConfigDTO.setShowSubscriptionMode(updateMemberConfigsDTO.getShowSubscriptionMode());
        memberConfigDTO.setForceRegeneratePassbook(updateMemberConfigsDTO.getForceRegeneratePassbook());
        memberConfigDTO.setExpirationDatePassbook(updateMemberConfigsDTO.getExpirationDatePassbook());
        memberConfigDTO.setOpenAdditionalMembers(updateMemberConfigsDTO.getOpenAdditionalMembers());
        memberConfigDTO.setSignUpEmail(updateMemberConfigsDTO.getSignUpEmail());
        memberConfigDTO.setBuyUrl(updateMemberConfigsDTO.getBuyUrl());
        memberConfigDTO.getAdminOptions().setCaptchaSecretKey(updateMemberConfigsDTO.getCaptchaSecretKey());
        memberConfigDTO.setCaptchaEnabled(updateMemberConfigsDTO.getCaptchaEnabled());
        memberConfigDTO.setCaptchaSiteKey(updateMemberConfigsDTO.getCaptchaSiteKey());
        memberConfigDTO.setPublicAvailabilityEnabled(updateMemberConfigsDTO.getPublicAvailabilityEnabled());
        memberConfigDTO.setLandingButtonUrl(updateMemberConfigsDTO.getLandingButtonUrl());
        memberConfigDTO.setTransferSeat(updateMemberConfigsDTO.getTransferSeat());
        if (CollectionUtils.isNotEmpty(updateMemberConfigsDTO.getDownloadPassbookPermissions())) {
            memberConfigDTO.setDownloadPassbookPermissions(
                    updateMemberConfigsDTO.getDownloadPassbookPermissions().stream().map(AvetPermission::getValue).toList()
            );
        }
        if (updateMemberConfigsDTO.getNewMemberPermission() != null) {
            memberConfigDTO.setNewMemberPermission(updateMemberConfigsDTO.getNewMemberPermission().getValue());
        }
        if (updateMemberConfigsDTO.getBuySeatPermission() != null) {
            memberConfigDTO.setBuySeatPermission(updateMemberConfigsDTO.getBuySeatPermission().getValue());
        }
        if (updateMemberConfigsDTO.getAllowCrossPurchases() != null) {
            memberConfigDTO.setAllowCrossPurchases(updateMemberConfigsDTO.getAllowCrossPurchases());
        }
        if (updateMemberConfigsDTO.getAllowTutorForm() != null) {
            memberConfigDTO.setAllowTutorForm(updateMemberConfigsDTO.getAllowTutorForm());
        }
        if (updateMemberConfigsDTO.getTuteeMaxAge() != null) {
            memberConfigDTO.setTuteeMaxAge(updateMemberConfigsDTO.getTuteeMaxAge());
        }
        if (updateMemberConfigsDTO.getMembersCardImageContent() != null) {
            memberConfigDTO.setMembersCardImageContent(toMembersCardImageContent(updateMemberConfigsDTO.getMembersCardImageContent()));
        }
    }

    public static MembersCardImageContent toMembersCardImageContent(MembersCardImageContentDTO membersCardImageContentDTO) {
        MembersCardImageContent membersCardImageContent = new MembersCardImageContent();
        membersCardImageContent.setImageBinary(membersCardImageContentDTO.getImageBinary());
        return membersCardImageContent;
    }

    public static List<SubscriptionModeDTO> convert(List<SubscriptionMode> subscriptionModes) {
        List<SubscriptionModeDTO> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(subscriptionModes)) {
            subscriptionModes.forEach(sm -> result.add(convert(sm)));
        }
        return result;
    }

    public static Map<MemberPeriodType, MemberOperationPeriodDTO> convertPeriods
            (Map<MemberPeriodType, MemberOperationPeriod> memberOperationPeriods) {
        if (memberOperationPeriods == null) {
            return null;
        }
        HashMap<MemberPeriodType, MemberOperationPeriodDTO> result = new HashMap<>();
        memberOperationPeriods.forEach((key, val) -> result.put(key, convert(val)));
        return result;
    }

    private static MemberOperationPeriodDTO convert(MemberOperationPeriod memberOperationPeriod) {
        MemberOperationPeriodDTO periodDTO = new MemberOperationPeriodDTO();

        periodDTO.setActive(memberOperationPeriod.getActive());
        periodDTO.setIgnoredSteps(memberOperationPeriod.getIgnoredSteps());
        periodDTO.setCharge(memberOperationPeriod.getCharge());
        periodDTO.setOrphanSeatsEnabled(memberOperationPeriod.getOrphanSeatsEnabled());
        periodDTO.setShowUpdatePartnerUser(memberOperationPeriod.getShowUpdatePartnerUser());
        periodDTO.setSkipPeriodicityModule(memberOperationPeriod.getSkipPeriodicityModule());
        periodDTO.setShowConditions(memberOperationPeriod.getShowConditions());
        periodDTO.setPaymentMode(memberOperationPeriod.getPaymentMode());
        periodDTO.setEmissionReason(memberOperationPeriod.getEmissionReason());
        periodDTO.setPayPeriod(memberOperationPeriod.getPayPeriod());
        periodDTO.setSetComment(memberOperationPeriod.getSetComment());
        periodDTO.setUpdateStatus(memberOperationPeriod.getUpdateStatus());
        periodDTO.setTargetStatus(memberOperationPeriod.getTargetStatus());
        periodDTO.setDatesFilterEnabled(memberOperationPeriod.getDatesFilterEnabled());

        periodDTO.setBuySeatFlow(memberOperationPeriod.getBuySeatFlow());

        periodDTO.setEnableMaxChangeSeat(memberOperationPeriod.getEnableMaxChangeSeat());
        periodDTO.setMaxChangeSeat(memberOperationPeriod.getMaxChangeSeat());
        periodDTO.setShowChangeSeatCounter(memberOperationPeriod.getShowChangeSeatCounter());

        periodDTO.setNewMemberId(memberOperationPeriod.getNewMemberId());
        periodDTO.setMemberPinStrategy(memberOperationPeriod.getMemberPinStrategy());
        periodDTO.setNewMemberFlow(memberOperationPeriod.getNewMemberFlow());
        periodDTO.setAvatar(convert(memberOperationPeriod.getAvatar()));

        return periodDTO;
    }

    private static AvatarDTO convert(Avatar avatar) {
        AvatarDTO avatarDTO = new AvatarDTO();

        if (avatar == null) {
            avatarDTO.setEnabled(false);
            return avatarDTO;
        }

        if (avatar.getEnabled() != null) {
            avatarDTO.setEnabled(avatar.getEnabled());
        }
        if (avatar.getMandatory() != null && Boolean.TRUE.equals(avatar.getEnabled())) {
            avatarDTO.setMandatory(avatar.getMandatory());
        }
        return avatarDTO;
    }

    private static Map<MemberPeriodType, MemberOperationPeriod> updatePeriods
            (Map<MemberPeriodType, MemberOperationPeriodDTO> updateOperationPeriods,
             Map<MemberPeriodType, MemberOperationPeriod> memberOperationPeriods) {
        if (updateOperationPeriods == null) {
            return memberOperationPeriods;
        }
        updateOperationPeriods.forEach((key, val) -> memberOperationPeriods.put(key, updatePeriod(val, memberOperationPeriods.get(key))));
        return memberOperationPeriods;
    }

    public static MemberOperationPeriod updatePeriod(MemberOperationPeriodDTO updateOperationPeriodDTO, MemberOperationPeriod
            memberOperationPeriod) {
        if (memberOperationPeriod == null) {
            memberOperationPeriod = new MemberOperationPeriod();
        }

        updateField(memberOperationPeriod::setActive, updateOperationPeriodDTO.getActive());
        updateField(memberOperationPeriod::setIgnoredSteps, updateOperationPeriodDTO.getIgnoredSteps());
        updateField(memberOperationPeriod::setCharge, updateOperationPeriodDTO.getCharge());
        updateField(memberOperationPeriod::setOrphanSeatsEnabled, updateOperationPeriodDTO.getOrphanSeatsEnabled());
        updateField(memberOperationPeriod::setShowUpdatePartnerUser, updateOperationPeriodDTO.getShowUpdatePartnerUser());
        updateField(memberOperationPeriod::setSkipPeriodicityModule, updateOperationPeriodDTO.getSkipPeriodicityModule());
        updateField(memberOperationPeriod::setShowConditions, updateOperationPeriodDTO.getShowConditions());
        updateField(memberOperationPeriod::setPaymentMode, updateOperationPeriodDTO.getPaymentMode());
        updateField(memberOperationPeriod::setEmissionReason, updateOperationPeriodDTO.getEmissionReason());
        updateField(memberOperationPeriod::setPayPeriod, updateOperationPeriodDTO.getPayPeriod());
        updateField(memberOperationPeriod::setSetComment, updateOperationPeriodDTO.getSetComment());
        updateField(memberOperationPeriod::setUpdateStatus, updateOperationPeriodDTO.getUpdateStatus());
        updateField(memberOperationPeriod::setTargetStatus, updateOperationPeriodDTO.getTargetStatus());

        updateField(memberOperationPeriod::setBuySeatFlow, updateOperationPeriodDTO.getBuySeatFlow());

        updateField(memberOperationPeriod::setEnableMaxChangeSeat, updateOperationPeriodDTO.getEnableMaxChangeSeat());
        updateField(memberOperationPeriod::setMaxChangeSeat, updateOperationPeriodDTO.getMaxChangeSeat());
        updateField(memberOperationPeriod::setShowChangeSeatCounter, updateOperationPeriodDTO.getShowChangeSeatCounter());

        updateField(memberOperationPeriod::setNewMemberId, updateOperationPeriodDTO.getNewMemberId());
        updateField(memberOperationPeriod::setMemberPinStrategy, updateOperationPeriodDTO.getMemberPinStrategy());
        updateField(memberOperationPeriod::setNewMemberFlow, updateOperationPeriodDTO.getNewMemberFlow());
        updateField(memberOperationPeriod::setAvatar, convert(updateOperationPeriodDTO.getAvatar()));

        return memberOperationPeriod;
    }

    private static Avatar convert(AvatarDTO avatarDTO) {
        Avatar avatar = new Avatar();

        if (avatarDTO == null) {
            return null;
        }

        if (avatarDTO.getEnabled() != null) {
            avatar.setEnabled(avatarDTO.getEnabled());
        }
        if (avatarDTO.getMandatory() != null) {
            avatar.setMandatory(avatarDTO.getMandatory());
        }
        return avatar;
    }

    public static SubscriptionModeDTO toDTO(SubscriptionMode subscriptionMode) {
        return convert(subscriptionMode);
    }

    public static SubscriptionMode toMSEntity(SubscriptionModeDTO subscriptionModeDTO) {
        return convert(subscriptionModeDTO);
    }

    private static SubscriptionMode convert(SubscriptionModeDTO subscriptionModeDTO) {
        SubscriptionMode subscriptionMode = new SubscriptionMode();
        subscriptionMode.setSid(subscriptionModeDTO.getSid());
        subscriptionMode.setName(subscriptionModeDTO.getName());
        subscriptionMode.setCapacities(subscriptionModeDTO.getCapacities());
        subscriptionMode.setAllowedRoles(subscriptionModeDTO.getRoles());
        subscriptionMode.setAllowedPeriodicities(subscriptionModeDTO.getPeriodicities());
        subscriptionMode.setDefaultBuyPeriodicity(subscriptionModeDTO.getDefaultBuyPeriodicity());
        subscriptionMode.setDefaultBuyRoleId(subscriptionModeDTO.getDefaultBuyRoleId());
        subscriptionMode.setActive(subscriptionModeDTO.getActive());
        if (subscriptionModeDTO.getTranslations() != null) {
            Map<String, Translations> translations = new HashMap<>();
            for (Map.Entry<String, TranslationsDTO> entry : subscriptionModeDTO.getTranslations().entrySet()) {
                translations.put(entry.getKey(), new Translations(entry.getValue().getName(), entry.getValue().getDescription(), entry.getValue().getLink(), entry.getValue().getLinkText()));
            }
            subscriptionMode.setTranslations(translations);
        }
        return subscriptionMode;
    }

    private static SubscriptionModeDTO convert(SubscriptionMode subscriptionMode) {
        SubscriptionModeDTO subscriptionModeDTO = new SubscriptionModeDTO();
        subscriptionModeDTO.setSid(subscriptionMode.getSid());
        subscriptionModeDTO.setName(subscriptionMode.getName());
        subscriptionModeDTO.setCapacities(subscriptionMode.getCapacities());
        subscriptionModeDTO.setRoles(subscriptionMode.getAllowedRoles());
        subscriptionModeDTO.setPeriodicities(subscriptionMode.getAllowedPeriodicities());
        subscriptionModeDTO.setDefaultBuyPeriodicity(subscriptionMode.getDefaultBuyPeriodicity());
        subscriptionModeDTO.setDefaultBuyRoleId(subscriptionMode.getDefaultBuyRoleId());
        subscriptionModeDTO.setActive(subscriptionMode.getActive());
        if (subscriptionMode.getTranslations() != null) {
            Map<String, TranslationsDTO> translationsDTO = new HashMap<>();
            for (Map.Entry<String, Translations> entry : subscriptionMode.getTranslations().entrySet()) {
                translationsDTO.put(entry.getKey(), new TranslationsDTO(entry.getValue().getName(), entry.getValue().getDescription(), entry.getValue().getLink(), entry.getValue().getLinkText()));
            }
            subscriptionModeDTO.setTranslations(translationsDTO);
        }
        return subscriptionModeDTO;
    }

    public static Map<String, TranslationsDTO> convertTranslations(Map<String, Translations> translations) {
        Map<String, TranslationsDTO> result = new HashMap<>();
        for (Map.Entry<String, Translations> entry : translations.entrySet()) {
            TranslationsDTO translationsDTO = new TranslationsDTO(entry.getValue().getName(), entry.getValue().getDescription(), entry.getValue().getLink(), entry.getValue().getLinkText());
            result.put(entry.getKey(), translationsDTO);
        }
        return result;
    }

    public static Map<String, Translations> convertTranslationsDTO(Map<String, TranslationsDTO> translationsDTO) {
        Map<String, Translations> result = new HashMap<>();
        for (Map.Entry<String, TranslationsDTO> entry : translationsDTO.entrySet()) {
            Translations translations = new Translations(entry.getValue().getName(), entry.getValue().getDescription(), entry.getValue().getLink(), entry.getValue().getLinkText());
            result.put(entry.getKey(), translations);
        }
        return result;
    }

    public static List<MemberConfigurationStructureDTO> toStructure(DynamicBusinessRuleTypes dynamicBusinessRuleTypes,
                                                                    Map<String, Object> memberConfigMap) {
        List<MemberConfigurationStructureDTO> list = new ArrayList<>();
        for (Enum element : dynamicBusinessRuleTypes.getAvailableRules()) {
            MemberConfigurationStructureDTO memberConfigurationStructureDTO = new MemberConfigurationStructureDTO();
            List<DynamicBusinessRuleFields> enumFields = new ArrayList<>();

            DynamicBusinessRuleConfigurable dynamicBusinessRuleConfigurable = (DynamicBusinessRuleConfigurable) element;

            if (memberConfigMap != null) {
                if (memberConfigMap.containsKey(dynamicBusinessRuleConfigurable.getId())) {
                    Map<String, Object> map = (Map<String, Object>) memberConfigMap.get(dynamicBusinessRuleConfigurable.getId());
                    if (map.get("className").toString().equals(dynamicBusinessRuleConfigurable.getJavaClass())) {
                        memberConfigurationStructureDTO.setOperationName(dynamicBusinessRuleConfigurable.getOperationName());
                        memberConfigurationStructureDTO.setImplementation(dynamicBusinessRuleConfigurable.getJavaClass());
                        memberConfigurationStructureDTO.setOrderType(dynamicBusinessRuleConfigurable.getOrderType() != null ? MemberOrderType.valueOf(dynamicBusinessRuleConfigurable.getOrderType().toString()) : null);
                        memberConfigurationStructureDTO.setType(MemberStructureType.valueOf(dynamicBusinessRuleTypes.name()));
                        if (Arrays.stream(dynamicBusinessRuleConfigurable.getFields()).filter(fi -> fi.getFieldName().equals("MODALITY_ID")).count() > 0) {
                            for (DynamicBusinessRuleFields dynamicBusinessRuleFields : dynamicBusinessRuleConfigurable.getFields()) {
                                enumFields.add(dynamicBusinessRuleFields);
                            }
                        } else {
                            enumFields.addAll(Arrays.stream(dynamicBusinessRuleConfigurable.getFields()).toList());
                        }
                    }
                }
            } else {
                memberConfigurationStructureDTO.setOperationName(dynamicBusinessRuleConfigurable.getOperationName());
                memberConfigurationStructureDTO.setImplementation(dynamicBusinessRuleConfigurable.getJavaClass());
                memberConfigurationStructureDTO.setOrderType(dynamicBusinessRuleConfigurable.getOrderType() != null ? MemberOrderType.valueOf(dynamicBusinessRuleConfigurable.getOrderType().toString()) : null);
                memberConfigurationStructureDTO.setType(MemberStructureType.valueOf(dynamicBusinessRuleTypes.name()));
                enumFields.addAll(Arrays.stream(dynamicBusinessRuleConfigurable.getFields()).toList());
            }

            if (!enumFields.isEmpty()) {
                List<ConfigurationStructureFieldDTO> fields = new ArrayList<>();
                for (DynamicBusinessRuleFields dynamicBusinessRuleFields : enumFields) {
                    ConfigurationStructureFieldDTO configurationStructureFieldDTO = new ConfigurationStructureFieldDTO();
                    configurationStructureFieldDTO.setId(dynamicBusinessRuleFields.name());
                    configurationStructureFieldDTO.setType(DynamicBusinessRuleFieldType.valueOf(dynamicBusinessRuleFields.getFieldType().toString()));
                    configurationStructureFieldDTO.setContainer(StructureContainer.valueOf(dynamicBusinessRuleFields.getFieldContainer().toString()));
                    if (dynamicBusinessRuleFields.getValueSource() != null) {
                        configurationStructureFieldDTO.setSource(StructureContainerDataOrigin.valueOf(dynamicBusinessRuleFields.getValueSource()));
                    }
                    if (dynamicBusinessRuleFields.getValueTarget() != null) {
                        configurationStructureFieldDTO.setTarget(StructureContainerDataOrigin.valueOf(dynamicBusinessRuleFields.getValueTarget()));
                    }
                    if (memberConfigMap != null) {
                        String values = mapValues(dynamicBusinessRuleConfigurable.getId(), dynamicBusinessRuleFields, memberConfigMap);
                        if (values != null && !values.replace("{", "").replace("}", "").trim().isEmpty()) {
                            if (dynamicBusinessRuleFields.getFieldName().equals("MODALITY_ID")) {
                                ConfigurationStructureFieldDTO copy = null;
                                String[] parts = values.split("],");
                                for (String part : parts) {

                                    copy = new ConfigurationStructureFieldDTO();
                                    copy.setSource(configurationStructureFieldDTO.getSource());
                                    copy.setTarget(configurationStructureFieldDTO.getTarget());
                                    copy.setType(configurationStructureFieldDTO.getType());
                                    copy.setContainer(configurationStructureFieldDTO.getContainer());

                                    String key = part.split("=")[0].replace("{", "").replace("}", "").trim();
                                    String[] elements = part.split("=")[1].replace("[", "").replace("]", "").replace("{", "").replace("}", "").split(",");
                                    if (dynamicBusinessRuleFields.getFieldType().equals(DynamicBusinessRuleFieldType.INTEGER)) {
                                        List<Integer> intElements = new ArrayList<>();
                                        for (String elem : elements) {
                                            if (!elem.trim().isEmpty()) {
                                                intElements.add(Integer.valueOf(elem.trim()));
                                            }
                                        }
                                        copy.setValue(intElements);
                                    }
                                    if (dynamicBusinessRuleFields.getFieldType().equals(DynamicBusinessRuleFieldType.STRING)) {
                                        List<String> strElements = new ArrayList<>();
                                        for (String elem : elements) {
                                            if (!elem.trim().isEmpty()) {
                                                strElements.add(elem.trim());
                                            }
                                        }
                                        copy.setValue(strElements);
                                    }
                                    copy.setId(key);
                                    fields.add(copy);
                                }
                            } else {

                                if (dynamicBusinessRuleFields.getFieldContainer().equals(DynamicBusinessRuleFieldContainer.MAP)) {
                                    Map<String, String> map = new HashMap<>();
                                    values = values.replace("{", "");
                                    values = values.replace("}", "");
                                    String[] elements = values.split(",");
                                    for (String elem : elements) {
                                        map.put(elem.split("=")[0].trim(), elem.split("=")[1].trim());
                                    }
                                    configurationStructureFieldDTO.setValue(map);
                                }
                                if (dynamicBusinessRuleFields.getFieldContainer().equals(DynamicBusinessRuleFieldContainer.LIST)) {
                                    String[] elements = values.replace("[", "").replace("]", "").split(",");
                                    if (dynamicBusinessRuleFields.getFieldType().equals(DynamicBusinessRuleFieldType.INTEGER)) {
                                        List<Integer> intElements = new ArrayList<>();
                                        for (String elem : elements) {
                                            if (!elem.trim().isEmpty()) {
                                                intElements.add(Integer.valueOf(elem.trim()));
                                            }
                                        }
                                        configurationStructureFieldDTO.setValue(intElements);
                                    }
                                    if (dynamicBusinessRuleFields.getFieldType().equals(DynamicBusinessRuleFieldType.STRING)) {
                                        List<String> strElements = new ArrayList<>();
                                        for (String elem : elements) {
                                            if (!elem.trim().isEmpty()) {
                                                strElements.add(elem.trim());
                                            }
                                        }
                                        configurationStructureFieldDTO.setValue(strElements);
                                    }
                                }
                                if (dynamicBusinessRuleFields.getFieldContainer().equals(DynamicBusinessRuleFieldContainer.SINGLE)) {
                                    configurationStructureFieldDTO.setValue(values);
                                }
                            }
                        }
                    }
                    if (!dynamicBusinessRuleFields.getFieldName().equals("MODALITY_ID")
                            || memberConfigMap == null) {

                        fields.add(configurationStructureFieldDTO);
                    }
                }
                memberConfigurationStructureDTO.setFields(fields);
                list.add(memberConfigurationStructureDTO);
            }
        }
        return list;
    }

    public static String mapValues(String confId, DynamicBusinessRuleFields dynamicBusinessRuleFields,
                                   Map<String, Object> memberConfigMap) {
        try {
            if (dynamicBusinessRuleFields.getFieldName().equals("MODALITY_ID")) {
                List<Map<String, Object>> list1 = (List<Map<String, Object>>) memberConfigMap.get("subscriptionModes");
                Map<String, Object> result = new HashMap<>();
                Map<String, Object> map2 = (Map<String, Object>) memberConfigMap.get(confId);
                Map<String, Object> map3 = (Map<String, Object>) map2.get("data");

                for (Map<String, Object> en : list1) {
                    List<Integer> values = (List<Integer>) map3.get(en.get("sid").toString());
                    result.put(en.get("sid").toString(), values != null ? values : new ArrayList<>());
                }
                return result.toString();
            } else {
                Map<String, Object> map2 = (Map<String, Object>) memberConfigMap.get(confId);
                Map<String, Object> map3 = (Map<String, Object>) map2.get("data");
                return map3.get(dynamicBusinessRuleFields.getFieldName()) != null ? map3.get(dynamicBusinessRuleFields.getFieldName()).toString() : null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static List<MemberConfigurationStructureDTO> toStructure(DynamicBusinessRuleTypes[] dynamicBusinessRuleTypes,
                                                                    Map<String, Object> memberConfigMap) {
        List<MemberConfigurationStructureDTO> result = new ArrayList<>();
        for (DynamicBusinessRuleTypes dynamicBusinessRuleType : dynamicBusinessRuleTypes) {
            List<MemberConfigurationStructureDTO> list = toStructure(dynamicBusinessRuleType, memberConfigMap);
            result.addAll(list);
        }
        return result;
    }

    public static MemberConfigDTO fromStructure(MemberConfigurationStructureDTO memberConfigurationStructureDTO,
                                                Map<String, Object> memberConfigMap, String operationName, ObjectMapper jacksonMapper) {

        DynamicBusinessRuleTypes currentElement = findType(memberConfigurationStructureDTO.getType());
        if (currentElement == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.MEMBER_CONFIG_ELEMENT_NOT_FOUND);
        }
        String operationIdentifier = findOperationId(currentElement, operationName);
        if (operationIdentifier == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.MEMBER_CONFIG_ELEMENT_NOT_FOUND);
        }

        Map<String, Object> secondMap = (Map<String, Object>) memberConfigMap.get(operationIdentifier);
        List<DynamicBusinessRuleFields> fields = findFields(memberConfigurationStructureDTO.getType(), operationName);

        if (secondMap == null) {
            // Add new attributes
            Map<String, Object> newMap = new HashMap<>();
            Map<String, Object> dataMap = new HashMap<>();
            putValues(memberConfigurationStructureDTO, dataMap, fields);
            newMap.put("data", dataMap);
            newMap.put("className", memberConfigurationStructureDTO.getImplementation());
            memberConfigMap.put(operationIdentifier, newMap);
        } else {
            // Change current object
            Map<String, Object> dataMap = (Map<String, Object>) secondMap.get("data");
            putValues(memberConfigurationStructureDTO, dataMap, fields);
            secondMap.put("data", dataMap);
        }
        return jacksonMapper.convertValue(memberConfigMap, MemberConfigDTO.class);
    }

    public static void putValues(MemberConfigurationStructureDTO
                                         memberConfigurationStructureDTO, Map<String, Object> dataMap, List<DynamicBusinessRuleFields> fields) {
        for (ConfigurationStructureFieldDTO field : memberConfigurationStructureDTO.getFields()) {
            if (memberConfigurationStructureDTO.getOperationName().equals("SUBSCRIPTION_MODE_INFERER")) {
                if (field.getType().equals(DynamicBusinessRuleFieldType.INTEGER)) {
                    if (field.getValue() != null) {
                        String[] values = field.getValue().toString().replace("[", "").replace("]", "").split(",");
                        List<Integer> list = new ArrayList<>();
                        for (String elem : values) {
                            if (!elem.trim().isEmpty()) {
                                list.add(Integer.valueOf(elem.trim()));
                            }
                        }
                        dataMap.put(field.getId(), list);
                    } else {
                        dataMap.put(field.getId(), new ArrayList<>());
                    }
                } else {
                    if (field.getValue() != null) {
                        String[] values = field.getValue().toString().replace("[", "").replace("]", "").split(",");
                        List<String> list = new ArrayList<>();
                        for (String elem : values) {
                            if (!elem.trim().isEmpty()) {
                                list.add(elem.trim());
                            }
                        }
                        dataMap.put(field.getId(), values);
                    } else {
                        dataMap.put(field.getId(), new ArrayList<>());
                    }
                }
            } else {
                Optional<DynamicBusinessRuleFields> currentField = fields.stream().filter(fi -> fi.name().equals(field.getId())).findFirst();
                if (currentField.isPresent()) {
                    if (field.getContainer().equals(StructureContainer.LIST)) {
                        if (field.getType().equals(DynamicBusinessRuleFieldType.INTEGER)) {
                            if (field.getValue() != null) {
                                String[] values = field.getValue().toString().replace("[", "").replace("]", "").split(",");
                                List<Integer> list = new ArrayList<>();
                                for (String elem : values) {
                                    if (!elem.trim().isEmpty()) {
                                        list.add(Integer.valueOf(elem.trim()));
                                    }
                                }
                                dataMap.put(currentField.get().getFieldName(), list);
                            } else {
                                dataMap.put(currentField.get().getFieldName(), new ArrayList<>());
                            }
                        } else {
                            if (field.getValue() != null) {
                                String[] values = field.getValue().toString().replace("[", "").replace("]", "").split(",");
                                List<String> list = new ArrayList<>();
                                for (String elem : values) {
                                    if (!elem.trim().isEmpty()) {
                                        list.add(elem.trim());
                                    }
                                }
                                dataMap.put(currentField.get().getFieldName(), values);
                            } else {
                                dataMap.put(currentField.get().getFieldName(), new ArrayList<>());
                            }
                        }
                    }
                    if (field.getContainer().equals(StructureContainer.MAP)) {
                        if (field.getValue() != null && !field.getValue().toString().trim().isEmpty()) {
                            Map<String, String> values = (Map<String, String>) field.getValue();
                            dataMap.put(currentField.get().getFieldName(), values);
                        } else {
                            dataMap.put(currentField.get().getFieldName(), "{}");
                        }
                    }
                    if (field.getContainer().equals(StructureContainer.SINGLE)) {
                        if (field.getType().equals(DynamicBusinessRuleFieldType.INTEGER)) {
                            if (field.getValue() != null && !field.getValue().toString().trim().isEmpty()) {
                                Integer valueInt = Integer.valueOf(field.getValue().toString().trim());
                                dataMap.put(currentField.get().getFieldName(), valueInt);
                            } else {
                                dataMap.put(currentField.get().getFieldName(), StringUtils.EMPTY);
                            }
                        } else {
                            if (field.getValue() != null && !field.getValue().toString().trim().isEmpty()) {
                                dataMap.put(currentField.get().getFieldName(), field.getValue().toString());
                            } else {
                                dataMap.put(currentField.get().getFieldName(), StringUtils.EMPTY);
                            }
                        }
                    }
                }
            }
        }
    }

    public static List<DynamicBusinessRuleFields> findFields(MemberStructureType type, String operationName) {
        DynamicBusinessRuleTypes dynamicBusinessRuleTypes = findType(type);
        for (Enum element : dynamicBusinessRuleTypes.getAvailableRules()) {
            DynamicBusinessRuleConfigurable dynamicBusinessRuleConfigurable = (DynamicBusinessRuleConfigurable) element;
            if (dynamicBusinessRuleConfigurable.getOperationName().equals(operationName)) {
                return Arrays.asList(dynamicBusinessRuleConfigurable.getFields());
            }
        }
        return null;
    }

    public static DynamicBusinessRuleTypes findType(MemberStructureType memberStructureType) {
        DynamicBusinessRuleTypes[] dynamicBusinessRuleTypes = DynamicBusinessRuleTypes.values();
        for (DynamicBusinessRuleTypes dynamicBusinessRuleTypes1 : dynamicBusinessRuleTypes) {
            if (dynamicBusinessRuleTypes1.name().equals(memberStructureType.name())) {
                return dynamicBusinessRuleTypes1;
            }
        }
        return null;
    }

    public static String findOperationId(DynamicBusinessRuleTypes dynamicBusinessRuleTypes, String operationName) {
        for (Enum element : dynamicBusinessRuleTypes.getAvailableRules()) {
            DynamicBusinessRuleConfigurable dynamicBusinessRuleConfigurable = (DynamicBusinessRuleConfigurable) element;
            if (dynamicBusinessRuleConfigurable.getOperationName().equals(operationName)) {
                return dynamicBusinessRuleConfigurable.getId();
            }
        }
        return null;
    }

    public static MemberCapacitiesListDTO toDTO(List<MemberCapacity> capacities) {
        List<MemberCapacityDTO> memberCapacitiesListDTO = CollectionUtils.isEmpty(capacities) ? new ArrayList<>() :
                capacities.stream().map(capacity -> new MemberCapacityDTO(capacity.getName(), capacity.getAvetCapacityId(), capacity.getVirtualZoneId(), capacity.getVenueTemplateId(), capacity.getMain())).collect(Collectors.toList());
        return new MemberCapacitiesListDTO(memberCapacitiesListDTO);
    }

    public static List<MemberCapacity> toMSEntity(MemberCapacitiesRequestDTO memberCapacitiesRequestDTO) {
        return memberCapacitiesRequestDTO.stream().map(capacity -> new MemberCapacity(capacity.getName(), capacity.getId(), capacity.getMain(), capacity.getVirtualZoneId(), capacity.getVenueTemplateId())).toList();
    }

    public static List<AvetEventDTO> toAvetEventDTO(List<AvetEvent> eventsInformation) {
        return eventsInformation.stream().map(event -> new AvetEventDTO(event.getEventId(), event.getName(), event.getCompetition(), event.getSeasonId())).toList();
    }

    public static EmissionReasonsDTO toDTO(MotivoEmisionSummary emissionReason) {
        EmissionReasonsDTO emissionReasonsDTO = new EmissionReasonsDTO();
        if (CollectionUtils.isNotEmpty(emissionReason.getMotivos())) {
            emissionReasonsDTO.setEmissionReasons(emissionReason.getMotivos().stream()
                    .filter(emission -> emission.getIdMotivo() != null && emission.getDescripcion() != null)
                    .map(emission -> new IdNameDTO(emission.getIdMotivo(), emission.getDescripcion().trim()))
                    .toList());
        }
        return emissionReasonsDTO;
    }

    public static PaymentModesDTO toDTO(PaymentModes paymentModes) {
        PaymentModesDTO paymentModesDTO = new PaymentModesDTO();
        if (CollectionUtils.isNotEmpty(paymentModes.getPayments())) {
            paymentModesDTO.setPaymentModes(paymentModes.getPayments().stream()
                    .filter(payment -> payment.getIdModoCobro() != null && payment.getModoCobro() != null)
                    .map(payment -> new IdNameDTO(payment.getIdModoCobro(), payment.getModoCobro().trim()))
                    .toList());
        }
        return paymentModesDTO;
    }

    public static MemberConfigDTO updateDateFilterEnabled(MemberConfigDTO memberConfigDTO, MemberPeriodType type,
                                                          UpdateDateFilterDTO dateFilterDTO) {
        Map<MemberPeriodType, MemberOperationPeriod> memberOperationPeriods = memberConfigDTO.getMemberOperationPeriods();
        if (memberOperationPeriods.containsKey(type)) {
            memberOperationPeriods.get(type).setDatesFilterEnabled(dateFilterDTO.getDateFilterEnabled());
        }
        memberConfigDTO.setMemberOperationPeriods(memberOperationPeriods);
        return memberConfigDTO;
    }

    public static DatesFilter toDatesFilter(UpdateDateFilterDTO datesFilterDTO, MemberPeriodType type) {
        DatesFilter datesFilter = new DatesFilter();
        MemberDatesFilter memberDatesFilter = new MemberDatesFilter();

        List<AccessUsersDatesFilter> accessUsersDatesFilter = datesFilterDTO.getAccess()
                .stream().map(access -> new AccessUsersDatesFilter(access.getUser(), access.getDate()))
                .toList();

        memberDatesFilter.setAccess(accessUsersDatesFilter);
        memberDatesFilter.setDefaultAccess(datesFilterDTO.getDefaultAccess());

        datesFilter.setType(type.toString());
        datesFilter.setMemberDatesFilter(memberDatesFilter);

        return datesFilter;
    }

    public static MemberDatesFilterDTO toMemberDatesFilterDTO(MemberOperationPeriod period, DatesFilterDTO datesFilterDTO) {
        List<AccessUsersDatesFilter> accessUsersDatesFilter = datesFilterDTO.entrySet()
                .stream().filter(access -> !DEFAULT.equals(access.getKey()))
                .map(entry -> new AccessUsersDatesFilter(entry.getKey(), entry.getValue()))
                .toList();

        MemberDatesFilterDTO memberDatesFilter = new MemberDatesFilterDTO();
        memberDatesFilter.setDefaultAccess(datesFilterDTO.get(DEFAULT));
        memberDatesFilter.setAccess(accessUsersDatesFilter);

        if (period != null) {
            memberDatesFilter.setDateFilterEnabled(period.getDatesFilterEnabled());
        }

        return memberDatesFilter;
    }
}
