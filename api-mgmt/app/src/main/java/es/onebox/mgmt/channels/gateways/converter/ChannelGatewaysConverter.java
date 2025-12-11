package es.onebox.mgmt.channels.gateways.converter;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.CodeNameDTO;
import es.onebox.mgmt.channels.gateways.dto.ChannelGatewayDTO;
import es.onebox.mgmt.channels.gateways.dto.ChannelGatewayDetailDTO;
import es.onebox.mgmt.channels.gateways.dto.ChannelGatewayDetailTranslationsDTO;
import es.onebox.mgmt.channels.gateways.dto.CreateChannelGateway;
import es.onebox.mgmt.channels.gateways.dto.SurchargeDTO;
import es.onebox.mgmt.channels.gateways.dto.SurchargeType;
import es.onebox.mgmt.channels.gateways.dto.TaxInfoDTO;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityTax;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.payment.dto.ChannelGatewayConfig;
import es.onebox.mgmt.datasources.ms.payment.dto.Surcharge;
import es.onebox.mgmt.datasources.ms.payment.dto.TaxInfo;
import es.onebox.mgmt.exception.ApiMgmtEntitiesErrorCode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static es.onebox.core.utils.common.CommonUtils.ifNull;
import static es.onebox.mgmt.common.ConverterUtils.updateField;

public class ChannelGatewaysConverter {

    private ChannelGatewaysConverter() {
    }

    public static ChannelGatewayDetailDTO fromApiPaymentDetail(ChannelGatewayConfig config, List<Currency> allCurrencies) {
        if (config == null) {
            return null;
        }
        ChannelGatewayDetailDTO dto = new ChannelGatewayDetailDTO();
        dto.setGatewaySid(config.getGatewaySid());
        dto.setConfigurationSid(config.getConfSid());
        dto.setName(config.getInternalName());
        dto.setTranslations(fromApiGateway(config.getName(), config.getSubtitle()));
        dto.setDescription(config.getDescription());
        dto.setAttempts(config.getAttempts());
        dto.setRefund(config.isRefund());
        dto.setShowBillingForm(config.isShowBillingForm());
        dto.setSaveCardByDefault(config.isSaveCardByDefault());
        dto.setForceRiskEvaluation(config.isForceRiskEvaluation());
        dto.setAllowBenefits(config.isAllowBenefits());
        dto.setActive(config.isActive());
        dto.setDefaultGateway(config.isByDefault());
        dto.setSendAdditionalData(config.isSendAdditionalData());
        dto.setPriceRangeEnabled(config.isPriceRangeEnabled());
        dto.setPriceRange(config.getPriceRange());
        dto.setLive(config.isLive());
        dto.setFieldValues(config.getFieldsValues());
        if (CollectionUtils.isNotEmpty(config.getCurrencies())) {
            List<CodeNameDTO> currencies = getChannelGatewayCurrencies(config, allCurrencies);
            dto.setCurrencies(currencies);
        }
        if (CollectionUtils.isNotEmpty(config.getSurcharges())) {
            List<SurchargeDTO> surchargeDTOS = config.getSurcharges().stream()
                    .map(ChannelGatewaysConverter::fromSurcharge)
                    .toList();
            dto.setSurcharges(surchargeDTOS);
        }
        if (CollectionUtils.isNotEmpty(config.getTaxes())) {
            List<TaxInfoDTO> taxes = config.getTaxes().stream()
                    .map(ChannelGatewaysConverter::toTaxInfoDTO)
                    .toList();
            dto.setTaxes(taxes);
        }
        return dto;
    }

    private static SurchargeDTO fromSurcharge(Surcharge surcharge) {
        SurchargeDTO dto = new SurchargeDTO();
        dto.setCurrency(surcharge.getCurrency());
        dto.setType(surcharge.getType());
        dto.setMaxValue(surcharge.getMaxValue());
        dto.setValue(surcharge.getValue());
        dto.setMinValue(surcharge.getMinValue());
        return dto;
    }

    @NotNull
    private static List<CodeNameDTO> getChannelGatewayCurrencies(ChannelGatewayConfig config, List<Currency> allCurrencies) {
        List<CodeNameDTO> currencies = new ArrayList<>();
        allCurrencies.forEach(c->
                config.getCurrencies().forEach(gc-> {
                    if (c.getCode().equals(gc)) {
                    CodeNameDTO codeNameDTO = new CodeNameDTO();
                    codeNameDTO.setCode(c.getCode());
                    codeNameDTO.setName(c.getDescription());
                    currencies.add(codeNameDTO);
                    }
                })
        );
        return currencies;
    }

    public static ChannelGatewayDTO fromApiPayment(ChannelGatewayConfig config, List<Currency> allCurrencies, Operator operator) {
        ChannelGatewayDTO dto = new ChannelGatewayDTO();
        if (config == null) {
            return null;
        }
        dto.setActive(config.isActive());
        dto.setConfigurationSid(config.getConfSid());
        dto.setDefaultGateway(config.isByDefault());
        dto.setGatewaySid(config.getGatewaySid());
        dto.setDescription(config.getDescription());
        dto.setName(config.getInternalName());
        dto.setAllowBenefits(config.isAllowBenefits());
        if (CollectionUtils.isEmpty(config.getCurrencies()) && Boolean.TRUE.equals(operator.getUseMultiCurrency())) {
            String operatorDefaultCurrencyCode;
            if (operator.getCurrencies() != null && operator.getCurrencies().getDefaultCurrency() != null) {
                operatorDefaultCurrencyCode = operator.getCurrencies().getDefaultCurrency();
            } else {
                throw new OneboxRestException(ApiMgmtEntitiesErrorCode.DEFAULT_CURRENCY_NOT_SET);
            } if (config.getCurrencies() == null) {
                config.setCurrencies(new ArrayList<>());
            }
            config.getCurrencies().add(operatorDefaultCurrencyCode);
        }
        if (CollectionUtils.isNotEmpty(config.getCurrencies())) {
            List<CodeNameDTO> currencies = getChannelGatewayCurrencies(config, allCurrencies);
            dto.setCurrencies(currencies);
        }
        if (CollectionUtils.isNotEmpty(config.getSurcharges())) {
            Set<SurchargeType> surchargeDTOS = config.getSurcharges().stream()
                    .map(Surcharge::getType)
                    .collect(Collectors.toSet());
            dto.setSurcharges(surchargeDTOS);
        }
        return dto;
    }

    public static List<ChannelGatewayDTO> fromApiPayment(List<ChannelGatewayConfig> configs, List<Currency> allCurrencies, Operator operator) {
        if (configs == null) {
            return new ArrayList<>();
        }
        return configs.stream()
                .map(c->fromApiPayment(c,allCurrencies, operator))
                .toList();
    }

    public static ChannelGatewayConfig fromChannelGatewayCreate(CreateChannelGateway channelGatewayDetail, List<EntityTax> operatorTaxes) {
        if (channelGatewayDetail == null) {
            return null;
        }
        ChannelGatewayConfig channelGatewayConfig = new ChannelGatewayConfig();
        channelGatewayConfig.setInternalName(channelGatewayDetail.getName());
        channelGatewayConfig.setDescription(channelGatewayDetail.getDescription());
        channelGatewayConfig.setAttempts(ifNull(channelGatewayDetail.getAttempts(), 1));
        channelGatewayConfig.setRefund(ifNull(channelGatewayDetail.getRefund(), false));
        channelGatewayConfig.setShowBillingForm(ifNull(channelGatewayDetail.getShowBillingForm(), false));
        channelGatewayConfig.setSaveCardByDefault(ifNull(channelGatewayDetail.getSaveCardByDefault(), false));
        channelGatewayConfig.setForceRiskEvaluation(ifNull(channelGatewayDetail.getForceRiskEvaluation(), false));
        channelGatewayConfig.setAllowBenefits(ifNull(channelGatewayDetail.getAllowBenefits(), false));
        channelGatewayConfig.setSendAdditionalData(ifNull(channelGatewayDetail.getSendAdditionalData(), false));
        channelGatewayConfig.setPriceRangeEnabled(ifNull(channelGatewayDetail.getPriceRangeEnabled(), false));
        channelGatewayConfig.setPriceRange(channelGatewayDetail.getPriceRange());
        channelGatewayConfig.setLive(ifNull(channelGatewayDetail.getLive(), false));
        channelGatewayConfig.setFieldsValues(channelGatewayDetail.getFieldValues());

        if (channelGatewayDetail.getTranslations() != null) {
            channelGatewayConfig.setName(fixLanguage(channelGatewayDetail.getTranslations().getName(), ConverterUtils::toLocale));
            channelGatewayConfig.setSubtitle(fixLanguage(channelGatewayDetail.getTranslations().getSubtitle(), ConverterUtils::toLocale));
        }

        if (CollectionUtils.isNotEmpty(channelGatewayDetail.getSurcharges())) {
            List<Surcharge> surcharges = new ArrayList<>();
            channelGatewayDetail.getSurcharges().forEach(surchargeDTO -> surcharges.add(toSurcharge(channelGatewayConfig, surchargeDTO)));
            channelGatewayConfig.setSurcharges(surcharges);
        }

        if (channelGatewayDetail.getTaxes() != null) {
            List<TaxInfo> taxes = getTaxesInfos(channelGatewayDetail, operatorTaxes);
            channelGatewayConfig.setTaxes(taxes);
        }

        return channelGatewayConfig;
    }

    public static ChannelGatewayConfig fromChannelGatewayUpdate(ChannelGatewayConfig currentConfig, CreateChannelGateway channelGatewayDetail, List<EntityTax> operatorTaxes) {
        if (channelGatewayDetail == null) {
            return currentConfig;
        }

        updateField(currentConfig::setInternalName, channelGatewayDetail.getName());
        updateField(currentConfig::setDescription, channelGatewayDetail.getDescription());
        updateField(currentConfig::setAttempts, channelGatewayDetail.getAttempts());
        updateField(currentConfig::setRefund, channelGatewayDetail.getRefund());
        updateField(currentConfig::setShowBillingForm, channelGatewayDetail.getShowBillingForm());
        updateField(currentConfig::setSaveCardByDefault, channelGatewayDetail.getSaveCardByDefault());
        updateField(currentConfig::setForceRiskEvaluation, channelGatewayDetail.getForceRiskEvaluation());
        updateField(currentConfig::setAllowBenefits, channelGatewayDetail.getAllowBenefits());
        updateField(currentConfig::setSendAdditionalData, channelGatewayDetail.getSendAdditionalData());
        updateField(currentConfig::setPriceRangeEnabled, channelGatewayDetail.getPriceRangeEnabled());
        updateField(currentConfig::setPriceRange, channelGatewayDetail.getPriceRange());
        updateField(currentConfig::setLive, channelGatewayDetail.getLive());
        updateField(currentConfig::setFieldsValues, channelGatewayDetail.getFieldValues());

        if (channelGatewayDetail.getTranslations() != null) {
            updateField(currentConfig::setName, fixLanguage(channelGatewayDetail.getTranslations().getName(), ConverterUtils::toLocale));
            updateField(currentConfig::setSubtitle, fixLanguage(channelGatewayDetail.getTranslations().getSubtitle(), ConverterUtils::toLocale));
        }

        if (channelGatewayDetail.getSurcharges() != null) {
            if (CollectionUtils.isNotEmpty(channelGatewayDetail.getSurcharges())) {
                List<Surcharge> surcharges = new ArrayList<>();
                channelGatewayDetail.getSurcharges().forEach(surchargeDTO -> surcharges.add(toSurcharge(currentConfig, surchargeDTO)));
                currentConfig.setSurcharges(surcharges);
            } else if (CollectionUtils.isEmpty(channelGatewayDetail.getSurcharges())) {
                currentConfig.setSurcharges(new ArrayList<>());
            }
        }

        if (channelGatewayDetail.getTaxes() != null) {
            List<TaxInfo> taxes = getTaxesInfos(channelGatewayDetail, operatorTaxes);
            currentConfig.setTaxes(taxes);
        }

        return currentConfig;
    }

    private static Surcharge toSurcharge(ChannelGatewayConfig currentConfig, SurchargeDTO surchargeDTO) {
        if (currentConfig.getSurcharges() == null) {
            currentConfig.setSurcharges(new ArrayList<>());
        }
        Surcharge surcharge = currentConfig.getSurcharges()
                .stream()
                .filter(e -> e.getCurrency().equals(surchargeDTO.getCurrency()))
                .findFirst()
                .orElse(new Surcharge());
        updateField(surcharge::setCurrency, surchargeDTO.getCurrency());
        updateField(surcharge::setType, surchargeDTO.getType());
        updateField(surcharge::setValue, surchargeDTO.getValue());
        if (SurchargeType.PERCENT.equals(surcharge.getType())) {
            surcharge.setMaxValue(surchargeDTO.getMaxValue());
            surcharge.setMinValue(surchargeDTO.getMinValue());
        } else {
            surcharge.setMaxValue(null);
            surcharge.setMinValue(null);
        }
        return surcharge;
    }

    private static ChannelGatewayDetailTranslationsDTO fromApiGateway(Map<String, String> name, Map<String, String>  subtitle) {
        ChannelGatewayDetailTranslationsDTO out = new ChannelGatewayDetailTranslationsDTO();
        out.setName(fixLanguage(name, ConverterUtils::toLanguageTag));
        out.setSubtitle(fixLanguage(subtitle, ConverterUtils::toLanguageTag));
        return out;
    }

    private static Map<String, String> fixLanguage(Map<String, String> data, Function<String, String> function) {
        return (data != null && !MapUtils.isEmpty(data)) ? data.entrySet()
                .stream()
                .filter(e -> StringUtils.isNotBlank(e.getValue()))
                .collect(Collectors.toMap(
                        entry -> function.apply(entry.getKey()),
                        Map.Entry::getValue
                )) : null;
    }

    private static TaxInfo toTaxInfo(EntityTax in) {
        TaxInfo taxInfo = new TaxInfo();
        taxInfo.setId(in.getIdImpuesto().longValue());
        taxInfo.setName(in.getNombre());
        taxInfo.setValue(in.getValor());
        return taxInfo;
    }

    private static TaxInfoDTO toTaxInfoDTO(TaxInfo taxInfo) {
        TaxInfoDTO dto = new TaxInfoDTO();
        dto.setId(taxInfo.getId());
        dto.setName(taxInfo.getName());
        return dto;
    }

    private static List<TaxInfo> getTaxesInfos(CreateChannelGateway channelGatewayDetail, List<EntityTax> operatorTaxes) {
        return channelGatewayDetail.getTaxes().stream()
                .map(t -> toTaxInfo(operatorTaxes.stream()
                        .filter(ot -> ot.getIdImpuesto().equals(t.getId().intValue())).findFirst().get()))
                .toList();
    }
}
