package es.onebox.mgmt.channels.forms;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.forms.converter.ChannelDefaultFormConverter;
import es.onebox.mgmt.channels.forms.dto.ChannelDefaultFormDTO;
import es.onebox.mgmt.channels.forms.dto.UpdateChannelDefaultFormDTO;
import es.onebox.mgmt.channels.forms.enums.FormType;
import es.onebox.mgmt.channels.utils.ChannelUtils;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelFormsResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.UpdateDefaultChannelForms;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelContentsRepository;
import es.onebox.mgmt.datasources.ms.payment.ApiPaymentDatasource;
import es.onebox.mgmt.datasources.ms.payment.dto.ChannelGatewayConfig;
import es.onebox.mgmt.datasources.ms.payment.dto.GatewayConfig;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;

@Service
public class ChannelFormsService {

	private static final String EMAIL = "email";
	private static final Set<String> IMMUTABLE_PORTAL_PURCHASE_FIELDS = Set.of("lastName", EMAIL, "firstName");
	private static final Set<String> IMMUTABLE_BOX_OFFICE_BOOKING_FIELDS = Set.of("firstName");
	private static final Set<String> IMMUTABLE_MEMBERS_PURCHASE_FIELDS = Set.of(EMAIL);
	private static final Set<String> IMMUTABLE_MEMBERS_MEMBER_FIELDS = Set.of("name", "surname1", EMAIL);
    private static final String DEFAULT_FORM_MS_CHANNEL = "default";
    private static final String DATA_PROTECTION_FORM_MS_CHANNEL = "data-protection";

    private final ChannelsHelper channelsHelper;
    private final ChannelContentsRepository channelContentsRepository;
    private final ApiPaymentDatasource apiPaymentDatasource;

    @Autowired
    public ChannelFormsService(ChannelsHelper channelsHelper, ChannelContentsRepository channelContentsRepository,
			ApiPaymentDatasource apiPaymentDatasource) {
        this.channelsHelper = channelsHelper;
        this.channelContentsRepository = channelContentsRepository;
        this.apiPaymentDatasource = apiPaymentDatasource;
    }

    public void updateForm(final Long channelId, FormType formType, UpdateChannelDefaultFormDTO body) {
        String formTypeMsChannel = validateFormTypeAndConvert(formType);
        ChannelResponse channelResponse = this.channelsHelper.getAndCheckChannel(channelId);
        ChannelUtils.validateOBChannelOrMembers(channelResponse.getType());
        if (ChannelUtils.isBoxOffice(channelResponse.getSubtype()) && StringUtils.isBlank(channelResponse.getUrl())) {
            validateBoxOfficeInvalidParams(body);
        }
        validateNonMutableParams(body, channelResponse.getSubtype());
        validateGatewayParams(channelId, body);
        UpdateDefaultChannelForms msBody = ChannelDefaultFormConverter.toMS(body);
        this.channelContentsRepository.updateFormsByType(channelId, formTypeMsChannel, msBody);
    }

    public ChannelDefaultFormDTO getFormByFormType(final Long channelId, FormType formType) {
        String formTypeMsChannel = validateFormTypeAndConvert(formType);
        ChannelResponse channelResponse = this.channelsHelper.getAndCheckChannel(channelId);
        ChannelUtils.validateOBChannelOrMembers(channelResponse.getType());
        ChannelFormsResponse in = this.channelContentsRepository.getFormsByType(channelId, formTypeMsChannel);
        return ChannelDefaultFormConverter.toDto(in);
    }

    private String validateFormTypeAndConvert(FormType formType) {
        if (formType != null) {
            if (FormType.BUYER_DATA_FORMS.equals(formType)) {
                return DEFAULT_FORM_MS_CHANNEL;
            }
            if (FormType.DATA_PROTECTION_FORMS.equals(formType)) {
                return DATA_PROTECTION_FORM_MS_CHANNEL;
            }
        }
        throw new OneboxRestException(ApiMgmtChannelsErrorCode.NOT_FOUND);
    }

	private void validateNonMutableParams(UpdateChannelDefaultFormDTO body, ChannelSubtype subtype) {
        switch (subtype) {
            case BOX_OFFICE_ONEBOX -> {
                if (CollectionUtils.isNotEmpty(body.getBooking())
                        && body.getBooking().stream().anyMatch(el -> IMMUTABLE_BOX_OFFICE_BOOKING_FIELDS.contains(el.getKey()))) {
                    throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "booking request body has immutable fields", null);
                }
            }
			case AVET -> validateAvetRequest(body);
            default -> {
                if (CollectionUtils.isNotEmpty(body.getPurchase())
                        && body.getPurchase().stream().anyMatch(el -> IMMUTABLE_PORTAL_PURCHASE_FIELDS.contains(el.getKey()))) {
                    throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "purchase request body has immutable fields", null);
                }
            }
        }
    }

	private void validateAvetRequest(UpdateChannelDefaultFormDTO body) {
		if (CollectionUtils.isNotEmpty(body.getPurchase())
				&& body.getPurchase().stream()
						.anyMatch(el -> IMMUTABLE_MEMBERS_PURCHASE_FIELDS.contains(el.getKey()))) {
			throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER,
					"purchase request body has immutable members fields", null);
		}
		if (CollectionUtils.isNotEmpty(body.getNewMember())
				&& body.getNewMember().stream().anyMatch(el -> IMMUTABLE_MEMBERS_MEMBER_FIELDS.contains(el.getKey()))) {
			throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER,
					"new member request body has immutable members fields", null);
		}
		if (CollectionUtils.isNotEmpty(body.getTutor())
				&& body.getTutor().stream().anyMatch(el -> IMMUTABLE_MEMBERS_MEMBER_FIELDS.contains(el.getKey()))) {
			throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER,
					"tutor request body has immutable members fields", null);
		}
	}

    private void validateGatewayParams(Long channelId, UpdateChannelDefaultFormDTO body) {
        List<GatewayConfig> gateways = apiPaymentDatasource.getGateways();
        List<String> channelGatewaysSid = apiPaymentDatasource.getChannelGatewayConfigs(channelId).stream()
                .map(ChannelGatewayConfig::getGatewaySid).toList();
        Set<String> mandatoryFormFields = gateways.stream()
                .filter(gateway -> channelGatewaysSid.contains(gateway.getSid()) && CollectionUtils.isNotEmpty(gateway.getMandatoryFormFields()))
                .flatMap(gateway -> gateway.getMandatoryFormFields().stream())
                .collect(Collectors.toSet());

        if (body.getPurchase().stream()
                .filter(form -> mandatoryFormFields.contains(form.getKey()))
                .anyMatch(form -> BooleanUtils.isFalse(form.getMandatory()) || BooleanUtils.isFalse(form.getVisible()))) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.INVALID_FORM_FIELDS, mandatoryFormFields);
        }
    }

	private void validateBoxOfficeInvalidParams(UpdateChannelDefaultFormDTO body) {
        boolean purchaseInvalidParams = false;
        boolean bookingInvalidParams = false;
        boolean issueInvalidParams = false;
        if (CollectionUtils.isNotEmpty(body.getPurchase())) {
            purchaseInvalidParams = body.getPurchase().stream().anyMatch(el -> el.getVisible() != null);
        }
        if (CollectionUtils.isNotEmpty(body.getBooking())) {
            bookingInvalidParams = body.getBooking().stream().anyMatch(el -> el.getVisible() != null);
        }
        if (CollectionUtils.isNotEmpty(body.getIssue())) {
            issueInvalidParams = body.getIssue().stream().anyMatch(el -> el.getVisible() != null);
        }
        boolean[] invalidParams = {purchaseInvalidParams, bookingInvalidParams, issueInvalidParams};
        if (BooleanUtils.or(invalidParams)) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "visible cant not be setted for this kind of channel",
                    null);
        }
    }
}
