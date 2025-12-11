package es.onebox.mgmt.channels.gateways;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.channels.gateways.converter.ChannelGatewaysConverter;
import es.onebox.mgmt.channels.gateways.converter.OneboxAccountingGatewayConfigurationFields;
import es.onebox.mgmt.channels.gateways.dto.ChannelGatewayDTO;
import es.onebox.mgmt.channels.gateways.dto.ChannelGatewayDetailDTO;
import es.onebox.mgmt.channels.gateways.dto.CreateChannelGateway;
import es.onebox.mgmt.channels.gateways.dto.UpdateChannelGateway;
import es.onebox.mgmt.channels.gateways.dto.UpdateChannelGateways;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.api.accounting.ApiAccountingDatasource;
import es.onebox.mgmt.datasources.ms.channel.dto.BookingCheckoutPaymentSettings;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelAccounting;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelConfig;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelFormField;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelFormsResponse;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelStatus;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelContentsRepository;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityTax;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.dto.WalletConfigDTO;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.entity.repository.OperatorsRepository;
import es.onebox.mgmt.datasources.ms.payment.ApiPaymentDatasource;
import es.onebox.mgmt.datasources.ms.payment.dto.ChannelGatewayConfig;
import es.onebox.mgmt.datasources.ms.payment.dto.GatewayConfig;
import es.onebox.mgmt.datasources.ms.payment.dto.Surcharge;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static es.onebox.core.utils.common.CommonUtils.isBlank;
import static es.onebox.core.utils.common.CommonUtils.isEmpty;

@Service
public class ChannelGatewaysService {

    public static final String CONFIG_SID_TIMESTAMP_FORMAT = "yyyyMMddHHmmss";
    private static final String CHANNEL_FORM_TYPE = "default";
    private static final String GATEWAY_CASH = "cash";
    private static final String GATEWAY_ONEBOXACCOUNTING= "oneboxaccounting";

    private static final DateFormat df = new SimpleDateFormat(CONFIG_SID_TIMESTAMP_FORMAT);

    private final ApiPaymentDatasource apiPaymentDatasource;
    private final ChannelsRepository channelsRepository;
    private final SecurityManager securityManager;
    private final EntitiesRepository entitiesRepository;
    private final ChannelContentsRepository channelContentsRepository;
    private final MasterdataService masterdataService;
    private final ApiAccountingDatasource apiAccountingDatasource;
    private final OperatorsRepository operatorsRepository;

    @Autowired
    public ChannelGatewaysService(ApiPaymentDatasource apiPaymentDatasource, ChannelsRepository channelsRepository,
                                  SecurityManager securityManager, EntitiesRepository entitiesRepository,
                                  ChannelContentsRepository channelContentsRepository, MasterdataService masterdataService, ApiAccountingDatasource apiAccountingDatasource, OperatorsRepository operatorsRepository) {
        this.apiPaymentDatasource = apiPaymentDatasource;
        this.channelsRepository = channelsRepository;
        this.securityManager = securityManager;
        this.entitiesRepository = entitiesRepository;
        this.channelContentsRepository = channelContentsRepository;
        this.masterdataService = masterdataService;
        this.apiAccountingDatasource = apiAccountingDatasource;
        this.operatorsRepository = operatorsRepository;
    }

    public List<ChannelGatewayDTO> getGateways(Long channelId) {
        validateChannel(channelId);

        List<ChannelGatewayConfig> channelGatewayConfigs = apiPaymentDatasource.getChannelGatewayConfigs(channelId);
        List<Currency> allCurrencies  = masterdataService.getCurrencies();
        Operator operator = entitiesRepository.getCachedOperator(channelsRepository.getChannel(channelId).getEntityId());

        return ChannelGatewaysConverter.fromApiPayment(channelGatewayConfigs, allCurrencies, operator);
    }

    public void updateGateways(Long channelId, UpdateChannelGateways updateGateways) {
        validateChannel(channelId);

        List<ChannelGatewayConfig> channelGatewayConfigs = apiPaymentDatasource.getChannelGatewayConfigs(channelId);

        validateChannelGatewayConfig(channelGatewayConfigs, updateGateways);

        List<ChannelGatewayConfig> configList = updateGateways
                .stream()
                .map(updateChannelGateway -> getChannelGatewayConfig(updateChannelGateway, channelGatewayConfigs))
                .toList();
        apiPaymentDatasource.createOrUpdateChannelGatewayConfigs(channelId, configList);
    }

    private void validateChannelGatewayConfig(List<ChannelGatewayConfig> channelGatewayConfigs,
                                              UpdateChannelGateways updateChannelGateways) {
        updateChannelGateways.forEach(gc -> updateConfigs(channelGatewayConfigs, gc));

        List<ChannelGatewayConfig> defaultGatewayConfigs = channelGatewayConfigs.stream()
                .filter(ChannelGatewayConfig::isByDefault).collect(Collectors.toList());

        if (defaultGatewayConfigs.size() > 1) {
            throw new OneboxRestException(ApiMgmtErrorCode.DELIVERY_METHOD_TOO_MANY_DEFAULTS);
        }
        if (channelGatewayConfigs.stream().noneMatch(ChannelGatewayConfig::isActive)
                && !isEmpty(defaultGatewayConfigs)) {
            throw new OneboxRestException(ApiMgmtErrorCode.GATEWAY_ACTIVE_MANDATORY_IF_DEFAULT);
        }

        if (channelGatewayConfigs.stream().anyMatch(ChannelGatewayConfig::isActive) && isEmpty(defaultGatewayConfigs)) {
            throw new OneboxRestException(ApiMgmtErrorCode.GATEWAY_DEFAULT_MANDATORY);
        }

        Optional<ChannelGatewayConfig> defaultConf = defaultGatewayConfigs.stream().findFirst();
        if (defaultConf.isPresent() && !defaultConf.get().isActive()) {
            throw new OneboxRestException(ApiMgmtErrorCode.GATEWAY_ACTIVE_MANDATORY_IF_DEFAULT);
        }

    }

    private void updateConfigs(List<ChannelGatewayConfig> config, UpdateChannelGateway channelGatewaysDetailDTO) {
        for (ChannelGatewayConfig conf : config) {
            if (conf.getConfSid().equals(channelGatewaysDetailDTO.getConfigurationSid())) {
                conf.setActive(channelGatewaysDetailDTO.getActive());
                conf.setByDefault(channelGatewaysDetailDTO.getDefaultGateway());
            }
        }
    }

    private ChannelGatewayConfig getChannelGatewayConfig(UpdateChannelGateway channelGatewayDetailDTO,
                                                         List<ChannelGatewayConfig> channelGatewayConfigs) {
        Optional<ChannelGatewayConfig> optional = channelGatewayConfigs.stream()
                .filter(c -> c.getConfSid().equals(channelGatewayDetailDTO.getConfigurationSid())
                        && c.getGatewaySid().equals(channelGatewayDetailDTO.getGatewaySid())).findFirst();

        ChannelGatewayConfig channelGateway = new ChannelGatewayConfig();
        if (optional.isPresent()) {
            channelGateway = optional.get();
        }
        channelGateway.setByDefault(channelGatewayDetailDTO.getDefaultGateway());
        channelGateway.setActive(channelGatewayDetailDTO.getActive());
        return channelGateway;
    }

    public ChannelGatewayDetailDTO getGateway(Long channelId, String gatewaySid, String configSid) {

        validateChannel(channelId);
        validateIds(gatewaySid, configSid);

        ChannelGatewayConfig config = apiPaymentDatasource.getChannelGatewayConfig(channelId, gatewaySid, configSid);
        if (config == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.CONFIG_NOT_FOUND);
        }

        List<Currency> allCurrencies  = masterdataService.getCurrencies();
        return ChannelGatewaysConverter.fromApiPaymentDetail(config, allCurrencies);
    }

    public String createChannelGatewayConfig(Long channelId, String gatewaySid,
                                             CreateChannelGateway createChannelGateway) {
        if (channelId == null || channelId <= 0) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_ID_INVALID);
        }
        if (CommonUtils.isBlank(gatewaySid)) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_GATEWAY_SID);
        }
        if (createChannelGateway == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
        }

        ChannelResponse channel = channelsRepository.getChannel(channelId);
        validateChannel(channel);

        if (Objects.equals(GATEWAY_CASH, gatewaySid.toLowerCase()) &&
                ChannelSubtype.PORTAL_B2B.equals(channel.getSubtype())){
                throw new OneboxRestException(ApiMgmtErrorCode.INVALID_GATEWAY_CONFIG_FOR_B2B_CHANNEL);
        }

        if (Objects.equals(GATEWAY_ONEBOXACCOUNTING, gatewaySid.toLowerCase())) {
            if (!ChannelSubtype.PORTAL_B2B.equals(channel.getSubtype())) {
                throw new OneboxRestException(ApiMgmtErrorCode.INVALID_GATEWAY_CONFIG_IF_NOT_B2B_CHANNEL);
            }

            if (createChannelGateway.getFieldValues() == null || createChannelGateway.getFieldValues().isEmpty()) {
                createChannelGateway.setFieldValues(seekOneboxAccountingGatewayFieldsAndValues(
                        apiPaymentDatasource.getGatewayConfigFields(gatewaySid),
                        apiAccountingDatasource.upsertProviderChannelAccounting(channel.getEntityId(), channelId)
                ));
            }
        }
        List<EntityTax> operatorTaxes = getOperatorTaxes(createChannelGateway, channel.getOperatorId());

        ChannelGatewayConfig config = ChannelGatewaysConverter.fromChannelGatewayCreate(createChannelGateway, operatorTaxes);
        config.setConfSid(df.format(new Date()));
        config.setChannelId(channelId.intValue());
        config.setGatewaySid(gatewaySid);

        validateOperatorCurrencies(createChannelGateway, channel, config);

        upsertChannelGatewayConfig(config, channel);

        return config.getConfSid();
    }

    private void validateOperatorCurrencies(CreateChannelGateway createChannelGateway, ChannelResponse channel,
                                            ChannelGatewayConfig config) {
        Operator operator = entitiesRepository.getCachedOperator(channel.getEntityId());
        if (BooleanUtils.isTrue(operator.getUseMultiCurrency())) {
            if (CollectionUtils.isEmpty(createChannelGateway.getCurrencies())){
                throw new OneboxRestException(ApiMgmtErrorCode.ERROR_GATEWAY_CURRENCIES_ARE_MANDATORY);
            }
            List<String> operatorCurrencies = operator.getCurrencies().getSelected().stream().map(Currency::getCode).toList();
            if (!operatorCurrencies.containsAll(createChannelGateway.getCurrencies())) {
                throw new OneboxRestException(ApiMgmtErrorCode.ERROR_GATEWAY_CURRENCY_NOT_IN_OPERATOR);
            }

            List<String> currencyCodes = getCurrencyCodes(channel);
            if (!currencyCodes.containsAll(createChannelGateway.getCurrencies())){
                throw new OneboxRestException(ApiMgmtErrorCode.ERROR_GATEWAY_CURRENCY_NOT_IN_CHANNEL);
            }
            config.setCurrencies(createChannelGateway.getCurrencies());
        }
    }

    public List<String> getCurrencyCodes(ChannelResponse channelResponse) {
            List<Currency> allCurrencies = masterdataService.getCurrencies();
            return allCurrencies.stream()
                    .filter(c -> channelResponse.getCurrencies().contains(c.getId())).map(Currency::getCode).distinct().toList();
    }

    public void deleteChannelGatewayConfig(Long channelId, String gatewaySid, String configSid) {

        if (channelId == null || channelId <= 0) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_ID_INVALID);
        }
        ChannelResponse channel = channelsRepository.getChannel(channelId);

        validateChannel(channel);
        validateIds(gatewaySid, configSid);

        ChannelGatewayConfig config = apiPaymentDatasource.getChannelGatewayConfig(channelId, gatewaySid, configSid);
        if (config == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.CONFIG_NOT_FOUND);
        }
        validateBookingCheckoutPayments(channelId, gatewaySid, configSid);

        Operator operator = entitiesRepository.getCachedOperator(channel.getEntityId());
        validateWalletRelationship(channelId, gatewaySid, configSid, operator);

        apiPaymentDatasource.deleteChannelGatewayConfig(channelId, gatewaySid, configSid);
    }

    private void validateWalletRelationship(Long channelId, String gatewaySid, String configSid, Operator operator) {
        List<WalletConfigDTO> walletConfigDTOList = Optional.ofNullable(operator.getWallets()).orElse(new ArrayList<>());
        Set<String> gatewayIds = walletConfigDTOList.stream()
                .map(WalletConfigDTO::getGateways)
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        Set<String> walletPayments = walletConfigDTOList.stream()
                .map(WalletConfigDTO::getWallet)
                .collect(Collectors.toSet());

        if (gatewayIds.contains(gatewaySid)) {
            List<ChannelGatewayConfig> channelGatewayConfigs = apiPaymentDatasource.getChannelGatewayConfigs(channelId);
            if (channelGatewayConfigs.stream()
                    .filter(cgc -> walletPayments.contains(cgc.getGatewaySid()))
                    .anyMatch(wp -> wp.getFieldsValues().get("GATEWAY_ASSOCIATION_CONFIG_SID").equals(configSid))) {
                throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_GATEWAY_WALLET_RELATIONSHIP_FOUND);
            }
        }
    }

    public void updateChannelGatewayConfig(Long channelId, String gatewaySid, String configSid,
                                           CreateChannelGateway createChannelGateway) {

        ChannelResponse channel = channelsRepository.getChannel(channelId);
        validateChannel(channel);
        validateIds(gatewaySid, configSid);

        if (createChannelGateway == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
        }
        if (CollectionUtils.isNotEmpty(createChannelGateway.getCurrencies())){
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
        }

        if (createChannelGateway.getTranslations() != null && createChannelGateway.getTranslations().getName() != null
                && createChannelGateway.getTranslations().getName().isEmpty()) {
            throw new OneboxRestException(ApiMgmtErrorCode.GATEWAY_NAME_TRANSLATIONS_EMPTY);
        }

        ChannelGatewayConfig currentConfig = apiPaymentDatasource.getChannelGatewayConfig(channelId, gatewaySid, configSid);
        if (currentConfig == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.CONFIG_NOT_FOUND);
        }

        List<EntityTax> operatorTaxes = getOperatorTaxes(createChannelGateway, channel.getOperatorId());

        ChannelGatewayConfig config = ChannelGatewaysConverter.fromChannelGatewayUpdate(currentConfig, createChannelGateway, operatorTaxes);
        config.setConfSid(configSid);
        config.setChannelId(channelId.intValue());
        config.setGatewaySid(gatewaySid);

        upsertChannelGatewayConfig(config, channel);
    }

    private List<EntityTax> getOperatorTaxes(CreateChannelGateway createChannelGateway, Long operatorId) {
        List<EntityTax> operatorTaxes = null;
        if (CollectionUtils.isNotEmpty(createChannelGateway.getTaxes())) {
            operatorTaxes = operatorsRepository.getOperatorTaxes(operatorId);
            Set<Integer> operatorTaxesIds = operatorTaxes.stream().map(EntityTax::getIdImpuesto).collect(Collectors.toSet());
            Set<Integer> gatewayTaxes = createChannelGateway.getTaxes().stream().map(t -> t.getId().intValue()).collect(Collectors.toSet());
            if (!operatorTaxesIds.containsAll(gatewayTaxes)) {
                throw new OneboxRestException(ApiMgmtErrorCode.INVALID_ENTITY_TAX);
            }
        }
        return operatorTaxes;
    }

    private void upsertChannelGatewayConfig(ChannelGatewayConfig config,  ChannelResponse channel) {
        validateCreationData(config, channel);
        apiPaymentDatasource.createOrUpdateChannelGatewayConfig(config);
    }

    private void validateCreationData(ChannelGatewayConfig config, ChannelResponse channel) {
        if (config.getChannelId() == null || config.getChannelId() <= 0) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_ID_INVALID);
        }
        if (isBlank(config.getGatewaySid())) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_GATEWAY_SID);
        }
        checkGatewaysOperator(config, channel);
        GatewayConfig gatewayConfig = checkGatewaysConfig(config);

        // Checking if all 'custom' fields for gateway are set
        checkGatewayFields(config);
        // Check if the channel has configured the required form field for the gateway
        checkMandatoryFormFields(config, gatewayConfig);
        // Check if surcharges and validate
        checkSurcharges(config);
        checkGatewayBenefits(config, channel, gatewayConfig);

        if (config.getName() == null || config.getName().isEmpty()) {
            throw new OneboxRestException(ApiMgmtErrorCode.ONE_TRANSLATION_REQUIRED);
        }
        if (config.getInternalName() == null) {
            config.setInternalName(config.getName().values().stream().findFirst().orElse(null));
        }
        if (gatewayConfig.isRetry() && (config.getAttempts() == null || config.getAttempts() < 1)) {
            throw new OneboxRestException(ApiMgmtErrorCode.ATTEMPTS_REQUIRED);
        }
        if (!gatewayConfig.isRetry() && config.getAttempts() != null && config.getAttempts() > 1) {
            throw new OneboxRestException(ApiMgmtErrorCode.GATEWAY_NOT_ALLOW_RETRIES);
        }


    }

    private void checkSurcharges(ChannelGatewayConfig config) {
        if (CollectionUtils.isNotEmpty(config.getSurcharges())) {
            config.getSurcharges().forEach(surcharge -> {
                if (CollectionUtils.isEmpty(config.getCurrencies()) || !config.getCurrencies().contains(surcharge.getCurrency())) {
                    throw new OneboxRestException(ApiMgmtErrorCode.ERROR_SURCHARGE_CURRENCY_NOT_IN_CONFIGURATION);
                }
                validateSurcharge(surcharge);
            });

            if ((long) config.getSurcharges().size() != config.getSurcharges().stream().map(Surcharge::getCurrency).distinct().count()) {
                throw new OneboxRestException(ApiMgmtErrorCode.ERROR_SURCHARGE_CURRENCY_DUPLICATED);
            }
        }
    }

    private void checkGatewayBenefits(ChannelGatewayConfig config, ChannelResponse channel, GatewayConfig gatewayConfig) {
        if (config.isAllowBenefits()) {
            Entity entity = entitiesRepository.getCachedEntity(channel.getEntityId());
            if (!BooleanUtils.isTrue(gatewayConfig.getAllowBenefits()) || !BooleanUtils.isTrue(entity.getAllowGatewayBenefits())) {
                throw new OneboxRestException(ApiMgmtErrorCode.GATEWAY_OR_ENTITY_NOT_ALLOW_BENEFITS);
            }
        }
    }

    private void validateSurcharge(Surcharge surcharge) {
        if (surcharge.getMinValue() != null && surcharge.getMaxValue() != null
                && surcharge.getMinValue() > surcharge.getMaxValue()) {
            // Error max value must be greater than min value
            throw new OneboxRestException(ApiMgmtErrorCode.ERROR_SURCHARGE_MAXVALUE_GREATER_THAN_MINVALUE);
        }
    }

    private GatewayConfig checkGatewaysConfig(ChannelGatewayConfig config) {
        GatewayConfig gatewayConfig = apiPaymentDatasource.getGatewayConfig(config.getGatewaySid());
        if (gatewayConfig == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.GATEWAY_NOT_FOUND);
        }
        return gatewayConfig;
    }

    private void checkGatewaysOperator(ChannelGatewayConfig config, ChannelResponse channel) {
        Operator operator = entitiesRepository.getCachedOperator(channel.getEntityId());
        if (operator == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.GATEWAY_NOT_ALLOWED_FOR_OPERATOR);
        }

        List<String> operatorGateways = new ArrayList<>(operator.getGateways());
        if (operator.getWallets() != null) {
            operatorGateways.addAll(
                    operator.getWallets().stream().map(WalletConfigDTO::getWallet).toList());
        }

        if (CollectionUtils.isEmpty(operatorGateways) || !operatorGateways.contains(config.getGatewaySid())) {
            throw new OneboxRestException(ApiMgmtErrorCode.GATEWAY_NOT_ALLOWED_FOR_OPERATOR);
        }
    }

    private void checkGatewayFields(ChannelGatewayConfig config) {
        List<String> gatewayFields = apiPaymentDatasource.getGatewayConfigFields(config.getGatewaySid());
        if (gatewayFields != null && !gatewayFields.isEmpty()
                && (config.getFieldsValues() == null || config.getFieldsValues().isEmpty()
                || !config.getFieldsValues().keySet().containsAll(gatewayFields))) {
            throw new OneboxRestException(ApiMgmtErrorCode.FIELD_VALUES_REQUIRED);
        }
    }

    private void checkMandatoryFormFields(ChannelGatewayConfig config, GatewayConfig gatewayConfig) {
        if (CollectionUtils.isNotEmpty(gatewayConfig.getMandatoryFormFields())) {
            ChannelFormsResponse channelFormsResponse = channelContentsRepository.getFormsByType(config.getChannelId().longValue(), CHANNEL_FORM_TYPE);
            Set<String> currentMandatoryPurchaseFields = channelFormsResponse.getPurchase().stream()
                    .filter(field -> BooleanUtils.isTrue(field.getMandatory()))
                    .map(ChannelFormField::getKey)
                    .collect(Collectors.toSet());
            if (!currentMandatoryPurchaseFields.containsAll(gatewayConfig.getMandatoryFormFields())) {
                throw ExceptionBuilder.build(ApiMgmtErrorCode.MANDATORY_FORM_FIELDS_REQUIRED, gatewayConfig.getMandatoryFormFields());
            }
        }
    }

    private void validateIds(String gatewaySid, String configSid) {
        if (isBlank(gatewaySid)) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_GATEWAY_SID);
        }
        if (isBlank(configSid)) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_CONFIG_SID);
        }
    }

    private void validateChannel(ChannelResponse channelResponse) {
        if (channelResponse == null || channelResponse.getStatus() == ChannelStatus.DELETED) {
            throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_NOT_FOUND);
        }
        securityManager.checkEntityAccessible(channelResponse.getEntityId());
    }

    private void validateChannel(Long channelId) {
        if (channelId == null || channelId <= 0) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_ID_INVALID);
        }
        ChannelResponse channelResponse = channelsRepository.getChannel(channelId);
        validateChannel(channelResponse);
    }

    private void validateBookingCheckoutPayments(Long channelId, String gatewaySid, String confSid) {
        ChannelConfig config = channelsRepository.getChannelConfig(channelId);
        if (config != null && config.getSharingSettings() != null && config.getSharingSettings().getBookingCheckout() != null
                && config.getSharingSettings().getBookingCheckout().getPaymentSettings() != null) {
            List<BookingCheckoutPaymentSettings> paymentSettings = config.getSharingSettings().getBookingCheckout().getPaymentSettings();
            for (BookingCheckoutPaymentSettings bookingCheckoutPaymentMethod : paymentSettings) {
                if (gatewaySid.equals(bookingCheckoutPaymentMethod.getGatewaySid())
                        && confSid.equals(bookingCheckoutPaymentMethod.getConfSid())
                        && BooleanUtils.isTrue(bookingCheckoutPaymentMethod.getActive())) {
                    throw new OneboxRestException(ApiMgmtErrorCode.CANNOT_DELETE_PAYMENT_METHOD_ACTIVE_FOR_BOOKING_CHECKOUT);
                }
            }
        }
    }

    private Map<String,String> seekOneboxAccountingGatewayFieldsAndValues(List<String> gatewayConfigFields, ChannelAccounting channelAccounting) {
        Map<String,String> accountingValues = new HashMap<>();
        gatewayConfigFields.forEach(gcf -> {
            String fieldValue = searchBoundFieldValue(gcf, channelAccounting);
            if (fieldValue != null) {
                accountingValues.put(gcf, fieldValue);
            }
        });

        return accountingValues;
    }

    private String searchBoundFieldValue(String gatewatConfigField, ChannelAccounting channelAccounting) {
        String methodName = getBoundFieldMethodName(gatewatConfigField);
        if (methodName != null) {
            Method boundFieldGetter = getBoundFieldGetter(methodName, channelAccounting);
            if (boundFieldGetter != null) {
                return getBoundFieldValue(boundFieldGetter, channelAccounting);
            }
        }
        return null;
    }

    private String getBoundFieldMethodName(String gatewayConfigField){
        try {
            OneboxAccountingGatewayConfigurationFields mappedField = OneboxAccountingGatewayConfigurationFields.valueOf(gatewayConfigField);
            return mappedField.getChannelAccountingBoundFieldGetter();
        } catch (IllegalArgumentException iae) {
            return null;
        }
    }

    private Method getBoundFieldGetter(String methodName, ChannelAccounting channelAccounting) {
        try {
            return channelAccounting.getClass().getMethod(methodName);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private String getBoundFieldValue(Method boundFieldGetter, ChannelAccounting channelAccounting){
        if (boundFieldGetter != null) {
            try {
                Object value = boundFieldGetter.invoke(channelAccounting);
                if (value != null) {
                    return value.toString();
                }
            } catch (InvocationTargetException | IllegalAccessException ignored) {}
        }
        return null;
    }
}
