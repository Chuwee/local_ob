package es.onebox.mgmt.channels.utils;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.enums.ChannelSubtype;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.Language;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelType;
import es.onebox.mgmt.datasources.ms.entity.dto.MemberConfigDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.MemberOperationPeriod;
import es.onebox.mgmt.datasources.ms.entity.enums.MemberPeriodType;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.apache.commons.collections.CollectionUtils;

import java.util.Map;

public class ChannelUtils {

    private ChannelUtils() {
    }

    public static boolean isObPortal(ChannelSubtype type) {
        return type == ChannelSubtype.WEB || type == ChannelSubtype.WEB_B2B || type == ChannelSubtype.WEB_BOX_OFFICE
                || type == ChannelSubtype.WEB_SUBSCRIBERS;
    }

    public static boolean isBoxOffice(es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype type) {
        return es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype.BOX_OFFICE_ONEBOX.equals(type);
    }

    public static void validateOBChannel(ChannelType type) {
        if (Boolean.FALSE.equals(ChannelUtils.isObChannel(type))) {
            throw ExceptionBuilder.build(ApiMgmtChannelsErrorCode.CHANNEL_UNSUPPORTED_OPERATION);
        }
    }

    public static void validateOBChannelOrMembers(ChannelType type) {
        if (Boolean.FALSE.equals(ChannelUtils.isObChannel(type)) && Boolean.FALSE.equals(ChannelType.MEMBER.equals(type))) {
            throw ExceptionBuilder.build(ApiMgmtChannelsErrorCode.CHANNEL_UNSUPPORTED_OPERATION);
        }
    }

    public static void validateOBBoxOffice(ChannelType type) {
        if (Boolean.FALSE.equals(ChannelType.OB_BOX_OFFICE.equals(type))) {
            throw ExceptionBuilder.build(ApiMgmtChannelsErrorCode.CHANNEL_UNSUPPORTED_OPERATION);
        }
    }

    public static void validateMember(ChannelType type) {
        if (Boolean.FALSE.equals(ChannelType.MEMBER.equals(type))) {
            throw ExceptionBuilder.build(ApiMgmtChannelsErrorCode.CHANNEL_UNSUPPORTED_OPERATION);
        }
    }

    public static void validateOBPortal(ChannelType type) {
        if (Boolean.FALSE.equals(ChannelType.OB_PORTAL.equals(type))) {
            throw ExceptionBuilder.build(ApiMgmtChannelsErrorCode.CHANNEL_UNSUPPORTED_OPERATION);
        }
    }

    public static void validateOBPortalOrMembers(ChannelType type) {
        if (Boolean.FALSE.equals(ChannelType.OB_PORTAL.equals(type)) && Boolean.FALSE.equals(ChannelType.MEMBER.equals(type))) {
            throw ExceptionBuilder.build(ApiMgmtChannelsErrorCode.CHANNEL_UNSUPPORTED_OPERATION);
        }
    }

    public static void validateNotExternal(ChannelType type) {
        if (Boolean.TRUE.equals(ChannelType.EXTERNAL.equals(type))) {
            throw ExceptionBuilder.build(ApiMgmtChannelsErrorCode.CHANNEL_UNSUPPORTED_OPERATION);
        }
    }

    public static void validateChannelLanguages(ChannelResponse channel) {
        Language languages = channel.getLanguages();
        if (languages == null || CollectionUtils.isEmpty(languages.getSelectedLanguages())) {
            throw ExceptionBuilder.build(ApiMgmtChannelsErrorCode.CHANNEL_ACTIVE_LANGUAGE_MANDATORY);
        }
    }

    private static boolean isObChannel(ChannelType type) {
        return ChannelType.OB_BOX_OFFICE.equals(type) || ChannelType.OB_PORTAL.equals(type);
    }

    public static boolean isMembers(ChannelSubtype type) {
        return ChannelSubtype.MEMBERS.equals(type);
    }

    public static void channelMembersValidations(MemberConfigDTO memberConfigDTO) {
        if (memberConfigDTO.getMemberOperationPeriods().get(MemberPeriodType.CHANGE_SEAT) != null
                && memberConfigDTO.getMemberOperationPeriods().get(MemberPeriodType.CHANGE_SEAT).getActive()) {
            if (memberConfigDTO.getPreviousPriceCalculatorData() == null || memberConfigDTO.getPreviousPriceCalculatorData().getClassName() == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.MEMBER_DYNAMIC_CONFIG_IS_NOT_COMPLETED);
            }
            if (memberConfigDTO.getChangeSeatValidatorData() == null || memberConfigDTO.getChangeSeatValidatorData().getClassName() == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.MEMBER_DYNAMIC_CONFIG_IS_NOT_COMPLETED);
            }
        }
        if (memberConfigDTO.getMemberOperationPeriods().get(MemberPeriodType.BUY_SEAT) != null
                && memberConfigDTO.getMemberOperationPeriods().get(MemberPeriodType.BUY_SEAT).getActive()) {
            if (memberConfigDTO.getBuySeatValidatorData() == null || memberConfigDTO.getBuySeatValidatorData().getClassName() == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.MEMBER_DYNAMIC_CONFIG_IS_NOT_COMPLETED);
            }
        }
        if (memberConfigDTO.getMemberOperationPeriods().get(MemberPeriodType.RENEWAL) != null
                && memberConfigDTO.getMemberOperationPeriods().get(MemberPeriodType.RENEWAL).getActive()) {
            if (memberConfigDTO.getRenewalValidatorData() == null || memberConfigDTO.getRenewalValidatorData().getClassName() == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.MEMBER_DYNAMIC_CONFIG_IS_NOT_COMPLETED);
            }
        }
    }

    public static boolean hasActivePeriod(Map<MemberPeriodType, MemberOperationPeriod> memberOperationPeriodMap) {
        return memberOperationPeriodMap.keySet().stream().anyMatch(memberPeriodType ->
                memberOperationPeriodMap.get(memberPeriodType).getActive() != null && memberOperationPeriodMap.get(memberPeriodType).getActive());
    }

    public static boolean isB2bPortal(ChannelResponse channel) {
        return channel.getSubtype().getIdSubtipo() == ChannelSubtype.WEB_B2B.getId();
    }
}
