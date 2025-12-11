package es.onebox.mgmt.channels;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.Roles;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.channels.converter.ChannelConverter;
import es.onebox.mgmt.channels.converter.ChannelFilterConverter;
import es.onebox.mgmt.channels.converter.WhitelabelSettingsConverter;
import es.onebox.mgmt.channels.dto.BookingSettingsDTO;
import es.onebox.mgmt.channels.dto.ChannelCancellationServicesDTO;
import es.onebox.mgmt.channels.dto.ChannelDetailDTO;
import es.onebox.mgmt.channels.dto.ChannelEventsSaleRestrictionsDTO;
import es.onebox.mgmt.channels.dto.ChannelLimitsDTO;
import es.onebox.mgmt.channels.dto.ChannelLimitsTicketsDTO;
import es.onebox.mgmt.channels.dto.ChannelSettingsUpdateDTO;
import es.onebox.mgmt.channels.dto.ChannelVouchersDTO;
import es.onebox.mgmt.channels.dto.ChannelWhitelabelSettingsDTO;
import es.onebox.mgmt.channels.dto.ChannelsFilter;
import es.onebox.mgmt.channels.dto.ChannelsResponseDTO;
import es.onebox.mgmt.channels.dto.NewMemberConfigDTO;
import es.onebox.mgmt.channels.dto.UpdateChannelRequestDTO;
import es.onebox.mgmt.channels.dto.UpdateChannelVouchersRequestDTO;
import es.onebox.mgmt.channels.enums.ChannelStatus;
import es.onebox.mgmt.channels.enums.ChannelSubtype;
import es.onebox.mgmt.channels.enums.WhitelabelType;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionsDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionsFilter;
import es.onebox.mgmt.channels.promotions.service.ChannelPromotionsService;
import es.onebox.mgmt.channels.utils.ChannelUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.auth.converter.AuthConverter;
import es.onebox.mgmt.common.auth.dto.AuthConfigDTO;
import es.onebox.mgmt.common.auth.validator.AuthValidator;
import es.onebox.mgmt.common.promotions.enums.PromotionStatus;
import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.common.dto.AuthConfig;
import es.onebox.mgmt.datasources.integration.avetconfig.dto.LiteralMapDTO;
import es.onebox.mgmt.datasources.integration.avetconfig.repository.AvetConfigRepository;
import es.onebox.mgmt.datasources.ms.channel.dto.BookingSettings;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelUpdateRequest;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelsResponse;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.client.dto.PhoneValidatorChannelConfig;
import es.onebox.mgmt.datasources.ms.client.repositories.PhoneValidatorEntityRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.MemberConfigDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.insurance.dto.ChannelCancellationServices;
import es.onebox.mgmt.datasources.ms.insurance.repository.InsurancePoliciesRepository;
import es.onebox.mgmt.datasources.ms.payment.ApiPaymentDatasource;
import es.onebox.mgmt.datasources.ms.payment.dto.ChannelGatewayConfig;
import es.onebox.mgmt.events.dto.CreateChannelDTO;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.loyaltypoints.channels.converter.LoyaltyPointsConverter;
import es.onebox.mgmt.loyaltypoints.channels.dto.UpdateChannelLoyaltyPointsDTO;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class ChannelsService {

    private static final String URL_PATTERN = "[\\.a-zA-Z0-9_-]*";
    private static final String URL_REGEX =
            "^((((https?|ftps?|gopher|telnet|nntp)://)|(mailto:|news:))" +
                    "(%[0-9A-Fa-f]{2}|[-()_.!~*';/?:@&=+$,A-Za-z0-9])+)" +
                    "([).!';/?:,][[:blank:]])?$";

    private static final Pattern URL_VALIDATOR_PATTERN = Pattern.compile(URL_REGEX);

    private final SecurityManager securityManager;
    private final ChannelsRepository channelsRepository;
    private final MasterdataService masterdataService;
    private final ChannelPromotionsService channelPromotionsService;
    private final InsurancePoliciesRepository insurancePoliciesRepository;
    private final ChannelsHelper channelsHelper;
    private final AvetConfigRepository avetConfigRepository;
    private final EntitiesRepository entitiesRepository;
    private final ApiPaymentDatasource apiPaymentDatasource;
    private final PhoneValidatorEntityRepository phoneValidatorEntityRepository;

    @Autowired
    public ChannelsService(SecurityManager securityManager, ChannelsRepository channelsRepository,
                           MasterdataService masterdataService, ChannelPromotionsService channelPromotionsService,
                           InsurancePoliciesRepository insurancePoliciesRepository,
                           AvetConfigRepository avetConfigRepository, EntitiesRepository entitiesRepository,
                           ChannelsHelper channelsHelper, ApiPaymentDatasource apiPaymentDatasource,
                           PhoneValidatorEntityRepository phoneValidatorEntityRepository) {
        this.securityManager = securityManager;
        this.channelsRepository = channelsRepository;
        this.masterdataService = masterdataService;
        this.channelPromotionsService = channelPromotionsService;
        this.insurancePoliciesRepository = insurancePoliciesRepository;
        this.avetConfigRepository = avetConfigRepository;
        this.entitiesRepository = entitiesRepository;
        this.channelsHelper = channelsHelper;
        this.apiPaymentDatasource = apiPaymentDatasource;
        this.phoneValidatorEntityRepository = phoneValidatorEntityRepository;
    }

    public ChannelsResponseDTO getChannels(ChannelsFilter filter) {
        securityManager.checkEntityAccessible(filter);

        if (SecurityUtils.hasAnyRole(Roles.ROLE_SYS_ANS, Roles.ROLE_SYS_MGR)) {
            return fetchChannels(filter.getOperatorId(), null, filter);
        }

        List<Long> visibleEntities = null;
        if (CommonUtils.isTrue(filter.getIncludeThirdPartyChannels())) {
            Long entityId = (SecurityUtils.isOperatorEntity() || SecurityUtils.hasAnyRole(Roles.ROLE_ENT_ADMIN)) && filter.getEntityId() != null
                    ? filter.getEntityId()
                    : SecurityUtils.getUserEntityId();

            visibleEntities = securityManager.getVisibleEntities(entityId);
        }

        return fetchChannels(SecurityUtils.getUserOperatorId(), visibleEntities, filter);
    }

    public IdDTO createChannel(CreateChannelDTO createChannel) {
        validateCreateChannelDTO(createChannel);
        securityManager.checkEntityAccessible(createChannel.getEntityId());

        if (createChannel.getType().equals(ChannelSubtype.MEMBERS)) {
            return createMemberChannel(createChannel);
        }

        return channelsRepository.create(ChannelConverter.fromCreateChannelDTO(createChannel));
    }

    private IdDTO createMemberChannel(CreateChannelDTO createChannel) {
        Entity entity = entitiesRepository.getEntity(createChannel.getEntityId());
        if (entity == null || entity.getClubCode() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_CONFIG_ERROR);
        }

        MemberConfigDTO existingConfig = avetConfigRepository.getMemberConfig(createChannel.getUrl());
        if (existingConfig != null) {
            throw new OneboxRestException(ApiMgmtErrorCode.MEMBER_CONFIG_ALREADY_EXIST);
        }

        IdDTO idDTO = channelsRepository.create(ChannelConverter.fromCreateChannelDTO(createChannel));
        saveMemberConfig(entity, createChannel, idDTO);

        return idDTO;
    }

    private void saveMemberConfig(Entity entity, CreateChannelDTO createChannel, IdDTO idDTO) {
        NewMemberConfigDTO memberConfig = new NewMemberConfigDTO();
        memberConfig.setClubCode(entity.getClubCode());
        memberConfig.setEntityId(createChannel.getEntityId());
        memberConfig.setUrl(createChannel.getUrl());
        memberConfig.setChannelId(idDTO.getId());
        avetConfigRepository.createMemberConfig(memberConfig);
    }

    public void updateChannel(Long channelId, UpdateChannelRequestDTO request) {
        ChannelDetailDTO channelDetailDTO = validateUpdateChannel(channelId, request);
        List<Long> channelCurrencies = getAndValidateChannelCurrencies(request, channelId);

        if (WhitelabelType.INTERNAL.equals(channelDetailDTO.getWhitelabelType()) && BooleanUtils.isTrue(request.getForceSquarePictures())) {
            throw new OneboxRestException(ApiMgmtErrorCode.FORCE_SQUARE_PICTURES_NOT_ALLOWED_FOR_INTERNAL_WHITELABEL);
        }

        ChannelUpdateRequest requestDTO = ChannelConverter.fromUpdateChannelRequestDTO(request,
                masterdataService.getLanguagesByIdAndCode(), channelCurrencies);

        if (channelDetailDTO.getType().equals(ChannelSubtype.MEMBERS)) {
            handleMemberUpdate(channelId, request, channelDetailDTO);
        }
        channelsRepository.updateChannel(channelId, requestDTO);
    }

    private void handleMemberUpdate(Long channelId, UpdateChannelRequestDTO request,
                                    ChannelDetailDTO channelDetailDTO) {
        MemberConfigDTO memberConfigDTO = avetConfigRepository.getMemberConfigByChannel(channelId);
        if (memberConfigDTO == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.MEMBER_CONFIG_NOT_FOUND);
        }
        if ((request.getStatus() != null && request.getStatus().equals(ChannelStatus.ACTIVE))
                || (request.getStatus() == null && channelDetailDTO.getStatus() != null
                && channelDetailDTO.getStatus().equals(ChannelStatus.ACTIVE))
                && memberConfigDTO.getMemberOperationPeriods() != null) {
            ChannelUtils.channelMembersValidations(memberConfigDTO);
        }
        if (request.getContact() != null) {
            memberConfigDTO.setPhone(request.getContact().getContactPhone());
            memberConfigDTO.getAdminOptions().setEmail(request.getContact().getContactEmail());
            avetConfigRepository.updateMemberConfigByChannel(channelId, memberConfigDTO);
        }

        validateLanguages(request);
    }

    private void validateLanguages(UpdateChannelRequestDTO request) {
        if (request.getLanguages() != null && (request.getLanguages().getSelectedLanguageCode() != null && !request
                .getLanguages().getSelectedLanguageCode().isEmpty())) {
            for (String language : request.getLanguages().getSelectedLanguageCode()) {
                String convertedLanguage = language.replace("-", "_");
                LiteralMapDTO baseLiteralMapDTO = avetConfigRepository.getBaseLiteral(convertedLanguage);
                if (baseLiteralMapDTO == null || baseLiteralMapDTO.getListLiteralMap().isEmpty()) {
                    throw new OneboxRestException(ApiMgmtErrorCode.LANGUAGE_BASE_LITERALS_NOT_FOUND);
                }
            }
        }
    }

    public void deleteChannel(Long channelId) {
        ChannelDetailDTO channelToBeDeleted = channelsHelper.getChannel(channelId, null);
        if (channelToBeDeleted != null) {
            if (channelToBeDeleted.getType().equals(ChannelSubtype.MEMBERS)) {
                MemberConfigDTO memberConfigDTO = avetConfigRepository.getMemberConfigByChannel(channelId);
                if (memberConfigDTO != null) {
                    avetConfigRepository.deleteMemberConfigByChannel(channelId);
                }
            }
            channelsRepository.deleteChannel(channelId);
        }
    }

    public void updateBookingsSettings(Long channelId, BookingSettingsDTO bookingSettings) {
        if (bookingSettings == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
        }
        ChannelDetailDTO channel = channelsHelper.getChannel(channelId, null);
        if (!ChannelSubtype.WEB_BOX_OFFICE.equals(channel.getType())) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_WEB_BOXOFFICE_ONLY);
        }
        if (BooleanUtils.isTrue(bookingSettings.getAllowBookingCheckout())) {
            if (StringUtils.isEmpty(bookingSettings.getBookingCheckoutDomain())) {
                throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_BOOKING_CHECKOUT_DOMAIN_MANDATORY);
            }
            if (!URL_VALIDATOR_PATTERN.matcher(bookingSettings.getBookingCheckoutDomain()).matches()) {
                throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_BOOKING_CHECKOUT_DOMAIN_MALFORMED);
            }
        } else {
            bookingSettings.setBookingCheckoutDomain(null);
            bookingSettings.setBookingCheckoutPaymentMethods(null);
        }
        channelsRepository.updateChannelBookingSettings(channelId, ChannelConverter.fromDTO(bookingSettings));
    }

    public BookingSettingsDTO getBookingsSettings(Long channelId) {
        ChannelDetailDTO channel = channelsHelper.getChannel(channelId, null);
        if (!ChannelSubtype.WEB_BOX_OFFICE.equals(channel.getType())) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_WEB_BOXOFFICE_ONLY);
        }
        BookingSettings bs = channelsRepository.getChannelBookingSettings(channelId);
        if (bs == null) {
            BookingSettings res = new BookingSettings();
            res.setAllowBooking(false);
            res.setAllowBookingCheckout(false);
            res.setAllowCustomerAssignation(false);
            channelsRepository.updateChannelBookingSettings(channelId, res);
            bs = channelsRepository.getChannelBookingSettings(channelId);
        }
        return ChannelConverter.toDTO(bs);
    }

    public void updateCancellationServices(Long channelId, ChannelCancellationServicesDTO cancellationServices) {
        Long operatorId = SecurityUtils.getUserOperatorId();
        insurancePoliciesRepository.updateChannelCancellationServices(channelId, ChannelConverter.fromDTO(cancellationServices, operatorId));
    }

    public ChannelCancellationServicesDTO getCancellationServices(Long channelId) {
        channelsHelper.getAndCheckChannel(channelId);
        Long operatorId = SecurityUtils.getUserOperatorId();
        ChannelCancellationServices bs = insurancePoliciesRepository.getChannelCancellationServices(channelId, operatorId);
        return ChannelConverter.toDTO(bs);
    }

    public ChannelVouchersDTO getChannelVouchersConfig(Long channelId) {
        channelsHelper.getAndCheckChannel(channelId);
        return ChannelConverter.toDTO(channelsRepository.getChannelVouchersConfig(channelId), masterdataService.getCurrencies());
    }

    public void updateChannelVouchersConfig(Long channelId, UpdateChannelVouchersRequestDTO updateChannelRequestDTO) {
        validateOperatorOnlyFields(updateChannelRequestDTO);
        ChannelResponse channelResponse = channelsHelper.getAndCheckChannel(channelId);
        Entity entity = entitiesRepository.getEntity(channelResponse.getEntityId());

        if (updateChannelRequestDTO.getChannelVoucherGiftCardDTO() != null
                && Boolean.FALSE.equals(updateChannelRequestDTO.getChannelVoucherGiftCardDTO().getAllowPurchaseGiftCard())
                && updateChannelRequestDTO.getChannelVoucherGiftCardDTO().getGiftCardId() != null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.GIFT_CARD_MUST_BE_NULL_IF_NOT_ENABLED);
        }

        if (BooleanUtils.isTrue(entity.getAllowLoyaltyPoints()) && BooleanUtils.isTrue(updateChannelRequestDTO.getAllowRedeemVouchers())) {
            UpdateChannelLoyaltyPointsDTO loyaltyPointsDTO = new UpdateChannelLoyaltyPointsDTO();
            loyaltyPointsDTO.setAllowLoyaltyPoints(false);
            channelsRepository.updateChannelLoyaltyPoints(channelId, LoyaltyPointsConverter.fromDTO(loyaltyPointsDTO));
        }

        Operator operator = entitiesRepository.getCachedOperator(SecurityUtils.getUserOperatorId());

        if (BooleanUtils.isTrue(operator.getUseMultiCurrency())) {

            handleMultiCurrency(updateChannelRequestDTO, operator, channelResponse);
        }
        channelsRepository.updateChannelVouchersConfig(channelId,
                ChannelConverter.fromDTO(updateChannelRequestDTO, masterdataService.getCurrencies()));
    }

    private void handleMultiCurrency(UpdateChannelVouchersRequestDTO updateChannelRequestDTO, Operator operator,
                                     ChannelResponse channelResponse) {
        if (updateChannelRequestDTO.getChannelCurrencyVoucherGiftCardDTO() != null
                && Boolean.FALSE.equals(
                updateChannelRequestDTO.getChannelCurrencyVoucherGiftCardDTO().getAllowPurchaseGiftCard())
                && CollectionUtils.isNotEmpty(
                updateChannelRequestDTO.getChannelCurrencyVoucherGiftCardDTO().getGiftCardsIds())) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.GIFT_CARD_MUST_BE_NULL_IF_NOT_ENABLED);
        }

        if (updateChannelRequestDTO.getChannelCurrencyVoucherGiftCardDTO() != null
                && BooleanUtils.isTrue(
                updateChannelRequestDTO.getChannelCurrencyVoucherGiftCardDTO().getAllowPurchaseGiftCard())) {
            List<Long> updateCurrencies = updateChannelRequestDTO.getChannelCurrencyVoucherGiftCardDTO()
                    .getGiftCardsIds()
                    .stream()
                    .map(c -> CurrenciesUtils.getCurrencyId(
                            operator.getCurrencies().getSelected(), c.getCurrencyCode()))
                    .toList();

            if (updateCurrencies.stream().anyMatch(c -> Collections.frequency(updateCurrencies, c) > 1)) {
                throw new OneboxRestException(ApiMgmtChannelsErrorCode.VOUCHER_GROUP_CURRENCIES_MUST_BE_CHANNEL);
            }

            if (BooleanUtils.isFalse(new HashSet<>(channelResponse.getCurrencies()).containsAll(updateCurrencies))) {
                throw new OneboxRestException(ApiMgmtChannelsErrorCode.VOUCHER_GROUP_CURRENCIES_MUST_BE_CHANNEL);
            }
        }
    }

    public ChannelEventsSaleRestrictionsDTO getEventSaleRestrictions(Long channelId) {
        channelsHelper.getAndCheckChannel(channelId);
        return ChannelConverter.toChannelEventsSaleRestrictionsDTO(channelsRepository.getEventSaleRestrictions(channelId));
    }

    public void updateEventSaleRestrictions(Long channelId, ChannelEventsSaleRestrictionsDTO request) {
        channelsHelper.getAndCheckChannel(channelId);
        channelsRepository.updateEventSaleRestrictions(channelId, ChannelConverter.toChannelEventsSaleRestrictions(request));
    }

    public ChannelDetailDTO getChannel(Long channelId) {
        ChannelPromotionsDTO promotions = channelPromotionsService.getChannelPromotions(channelId, new ChannelPromotionsFilter());
        boolean hasActivePromotion = false;
        if (promotions != null) {
            hasActivePromotion = promotions.getData().stream().anyMatch(p -> p.getStatus() == PromotionStatus.ACTIVE);
        }
        return channelsHelper.getChannel(channelId, hasActivePromotion);
    }

    public ChannelWhitelabelSettingsDTO getChannelWhitelabelSettings(Long channelId) {
        return WhitelabelSettingsConverter.fromMs(channelsRepository.getChannelWhitelabelSettings(channelId));
    }

    public void updateChannelWhitelabelSettings(Long channelId, ChannelWhitelabelSettingsDTO request) {
        if (request.getPromotions() != null && request.getPromotions().getLocations() != null && request.getPromotions().getLocations().isEmpty()) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.PROMOTION_LOCATION_REQUIRED);
        }
        if (request.getVenueMap() != null && request.getVenueMap().getPreselectedItems() != null) {
            ChannelDetailDTO channelDetailDTO = getChannel(channelId);

            ChannelLimitsDTO limits = channelDetailDTO.getLimits();
            ChannelLimitsTicketsDTO tickets = (limits != null) ? limits.getTickets() : null;
            Integer purchaseMax = (tickets != null) ? tickets.getPurchaseMax() : null;
            Integer preselectedItems = request.getVenueMap().getPreselectedItems();

            if (purchaseMax != null && purchaseMax < preselectedItems) {
                throw new OneboxRestException(ApiMgmtChannelsErrorCode.PRESELECTED_ITEMS_EXCEEDS_CHANNEL_LIMIT);
            }
        }

        channelsRepository.updateChannelWhitelabelSettings(channelId, WhitelabelSettingsConverter.toMs(request));
    }


    private void validateOperatorOnlyFields(UpdateChannelVouchersRequestDTO request) {
        if (voucherRequestContainsOperatorFields(request) && !SecurityUtils.hasAnyRole(Roles.ROLE_OPR_MGR)) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.OPERATION_NOT_ALLOWED_FOR_USER);
        }
    }

    private ChannelsResponseDTO fetchChannels(Long operatorId, List<Long> visibleEntities, ChannelsFilter filter) {
        ChannelFilter channelFilter = ChannelFilterConverter.convert(filter, visibleEntities);
        ChannelsResponse channels = channelsRepository.getChannels(operatorId, channelFilter);
        return ChannelConverter.fromMsChannelsResponse(channels, channelsHelper);
    }

    private boolean voucherRequestContainsOperatorFields(UpdateChannelVouchersRequestDTO request) {
        return request.getAllowRedeemVouchers() != null || request.getAllowRefundToVouchers() != null;
    }

    private void validateCreateChannelDTO(CreateChannelDTO createChannel) {
        if (ChannelUtils.isObPortal(createChannel.getType()) && createChannel.getUrl() == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_URL_MANDATORY_FOR_TYPE_PORTAL);
        }
        if (ChannelUtils.isObPortal(createChannel.getType()) && !isValidUrl(createChannel)) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_URL_INVALID);
        }
        validateCreateChannelByRole(createChannel);
    }

    private boolean isValidUrl(CreateChannelDTO createChannel) {
        return createChannel.getUrl().matches(URL_PATTERN);
    }

    /**
     * If user has ROLE_OPR_MGR entityId is mandatory and that entityId should be valid
     * If user hasn't ROLE_OPR_MGR createChannel entityId should be set to user entityId.
     *
     * @param createChannel new channel dto
     */
    private void validateCreateChannelByRole(CreateChannelDTO createChannel) {
        if (SecurityUtils.hasAnyRole(Roles.ROLE_OPR_MGR)) {
            if (createChannel.getEntityId() == null) {
                throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_ENTITY_ID_MANDATORY);
            }
            if (createChannel.getEntityId() <= 0 || createChannel.getEntityId() == SecurityUtils.getUserOperatorId()) {
                throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_ENTITY_ID_INVALID);
            }
        } else {
            createChannel.setEntityId(SecurityUtils.getUserEntityId());
        }
    }

    private ChannelDetailDTO validateUpdateChannel(Long channelId, UpdateChannelRequestDTO request) {
        ChannelDetailDTO channel = channelsHelper.getChannel(channelId, null);

        if (request.getName() != null && StringUtils.isBlank(request.getName())) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_NAME_INVALID);
        }

        validateWhitelabel(request, channel);

        validateChannelLanguages(request, channel);

        if (request.getBuild() != null) {
            if (!SecurityUtils.hasAnyRole(Roles.ROLE_OPR_MGR)) {
                throw new OneboxRestException(ApiMgmtErrorCode.FORBIDDEN, "Channel build can only be changed by an operator manager", null);
            }
            if (!ChannelUtils.isObPortal(channel.getType())) {
                throw ExceptionBuilder.build(ApiMgmtChannelsErrorCode.CHANNEL_UNSUPPORTED_BUILD);
            }
        }

        validateSettings(request);

        ChannelSettingsUpdateDTO settings = request.getSettings();
        if (settings != null && Boolean.TRUE.equals(settings.getUseCurrencyExchange())) {
            String currencyDefaultExchange = settings.getCurrencyDefaultExchange();
            if (StringUtils.isEmpty(currencyDefaultExchange)) {
                throw new OneboxRestException(ApiMgmtErrorCode.CURRENCY_DEFAULT_EXCHANGE_REQUIRED);
            }
            if (masterdataService.getCurrencies().stream().noneMatch(c -> c.getCode().equals(currencyDefaultExchange))) {
                throw new OneboxRestException(ApiMgmtErrorCode.ERROR_CURRENCY_DOESNT_EXIST);
            }
        }
        return channel;
    }

    private void validateWhitelabel(UpdateChannelRequestDTO request, ChannelDetailDTO channel) {
        if (Objects.nonNull(channel.getWhitelabelType())
                && WhitelabelType.INTERNAL.equals(channel.getWhitelabelType())) {
            if (Objects.nonNull(request.getDomain())) {
                throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_DOMAIN_INTERNAL);
            }
            if (Objects.nonNull(request.getWhitelabelPath())) {
                throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_WHITELABEL_PATH_INTERNAL);
            }
        }
    }

    private void validateChannelLanguages(UpdateChannelRequestDTO request, ChannelDetailDTO channel) {
        if (request.getLanguages() != null) {
            if (
                    (ChannelUtils.isObPortal(channel.getType()) || ChannelUtils.isMembers(channel.getType())) &&
                            (request.getLanguages().getSelectedLanguageCode() == null ||
                                    request.getLanguages().getSelectedLanguageCode().isEmpty() ||
                                    StringUtils.isEmpty(request.getLanguages().getDefaultLanguageCode()))) {
                throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER,
                        "Default and selected languages are mandatory", null);
            } else if (request.getLanguages().getSelectedLanguageCode() == null ||
                    request.getLanguages().getSelectedLanguageCode().isEmpty()) {
                throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER,
                        "selected languages are mandatory", null);
            }
            if (request.getLanguages().getSelectedLanguageCode().stream().anyMatch(StringUtils::isEmpty)) {
                throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER,
                        "There is an empty selected language.", null);
            }
        }
    }

    private List<Long> getAndValidateChannelCurrencies(UpdateChannelRequestDTO request, Long channelId) {
        if (CollectionUtils.isNotEmpty(request.getCurrencies())) {
            Operator operator = entitiesRepository.getCachedOperator(SecurityUtils.getUserOperatorId());
            validateMulticurrencyOperator(operator);

            List<Currency> allCurrencies = masterdataService.getCurrencies();
            List<Long> currencyIds = allCurrencies.stream()
                    .filter(c -> request.getCurrencies().contains(c.getCode())).map(Currency::getId).distinct().toList();
            if (currencyIds.size() != request.getCurrencies().size()) {
                throw new OneboxRestException(ApiMgmtErrorCode.ERROR_CURRENCY_DOESNT_EXIST);
            }
            validateOperatorCurrencies(currencyIds, operator);

            List<ChannelGatewayConfig> channelGatewayConfigs = apiPaymentDatasource.getChannelGatewayConfigs(channelId);
            channelGatewayConfigs.forEach(c -> {
                if (CollectionUtils.isNotEmpty(c.getCurrencies()) &&
                        c.getCurrencies().stream().noneMatch(code -> request.getCurrencies().contains(code))) {
                    throw new OneboxRestException(ApiMgmtErrorCode.ERROR_CURRENCY_WITH_GATEWAY_ACTIVE);
                }
            });

            ChannelPromotionsDTO promotions = channelPromotionsService.getChannelPromotions(channelId, new ChannelPromotionsFilter());
            if (promotions != null) {
                boolean hasActivePromotion = promotions.getData().stream().anyMatch(p -> p.getStatus() == PromotionStatus.ACTIVE);
                if (hasActivePromotion) {
                    throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_CANNOT_ADD_CURRENCY_WHEN_PROMOTION_ACTIVE);
                }
            }

            return currencyIds;
        }
        return Collections.emptyList();
    }

    private void validateOperatorCurrencies(List<Long> currencyIds, Operator operator) {
        List<Long> operatorCurrencies = operator.getCurrencies().getSelected().stream().map(Currency::getId).toList();
        if (CollectionUtils.isNotEmpty(operator.getCurrencies().getSelected())
                && currencyIds.stream().anyMatch(c -> !operatorCurrencies.contains(c))) {
            throw new OneboxRestException(ApiMgmtErrorCode.CURRENCY_NOT_ALLOWED);
        }
    }

    private void validateMulticurrencyOperator(Operator operator) {
        if (BooleanUtils.isNotTrue(operator.getUseMultiCurrency())) {
            throw new OneboxRestException(ApiMgmtErrorCode.ERROR_OPERATOR_WITHOUT_MULTICURRENCY);
        }
    }

    private void validateSettings(UpdateChannelRequestDTO request) {
        ChannelSettingsUpdateDTO settings = request.getSettings();
        if (settings != null) {
            if (settings.getSupportEmailDTO() != null
                    && BooleanUtils.isTrue(settings.getSupportEmailDTO().getEnabled())
                    && settings.getSupportEmailDTO().getAddress() == null) {
                throw new OneboxRestException(ApiMgmtChannelsErrorCode.INVALID_SUPPORT_EMAIL);
            }

            if (!SecurityUtils.hasAnyRole(Roles.ROLE_OPR_MGR)) {
                if (settings.getUseMultiEvent() != null) {
                    throw new OneboxRestException(ApiMgmtErrorCode.FORBIDDEN, "Multi Event can only be changed by an operator manager", null);
                }
                if (settings.getAutomaticSeatSelection() != null) {
                    throw new OneboxRestException(ApiMgmtErrorCode.FORBIDDEN, "The automatic seat selection setting may only be changed by an operator manager", null);
                }
            }
        }
    }

    public AuthConfigDTO getAuthConfig(Long channelId) {
        channelsHelper.getAndCheckChannel(channelId);
        AuthConfig authConfig = channelsRepository.getAuthConfig(channelId);
        PhoneValidatorChannelConfig phoneValidatorChannelConfig = phoneValidatorEntityRepository.getPhoneValidatorChannelConfiguration(channelId);
        return AuthConverter.toAuthConfigDTO(authConfig, phoneValidatorChannelConfig);
    }

    public void updateAuthConfig(Long channelId, AuthConfigDTO authConfigDTO) {
        channelsHelper.getAndCheckChannel(channelId);
        AuthValidator.validateAuthConfig(authConfigDTO);
        AuthConfig updatedAuthConfig = AuthConverter.toAuthConfig(authConfigDTO);
        channelsRepository.updateAuthConfig(channelId, updatedAuthConfig);
        if (authConfigDTO.getSettings() != null && authConfigDTO.getSettings().getPhoneValidator() != null) {
            PhoneValidatorChannelConfig phoneValidatorChannelConfig = new PhoneValidatorChannelConfig();
            phoneValidatorChannelConfig.setChannelId(channelId.intValue());
            phoneValidatorChannelConfig.setEnabled(authConfigDTO.getSettings().getPhoneValidator().getEnabled());
            phoneValidatorChannelConfig.setValidatorId(authConfigDTO.getSettings().getPhoneValidator().getValidatorId());
            phoneValidatorEntityRepository.updatePhoneValidatorChannelConfiguration(channelId, phoneValidatorChannelConfig);
        }
    }

}
