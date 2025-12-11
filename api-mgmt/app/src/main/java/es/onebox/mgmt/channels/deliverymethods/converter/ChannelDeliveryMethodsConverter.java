package es.onebox.mgmt.channels.deliverymethods.converter;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.channels.deliverymethods.dto.B2bExternalDownloadURLDTO;
import es.onebox.mgmt.channels.deliverymethods.dto.B2bExternalDownloadURLUpdateDTO;
import es.onebox.mgmt.channels.deliverymethods.dto.B2bExternalTargetChannelDTO;
import es.onebox.mgmt.channels.deliverymethods.dto.ChannelDeliveryMethodCurrenciesDTO;
import es.onebox.mgmt.channels.deliverymethods.dto.ChannelDeliveryMethodDTO;
import es.onebox.mgmt.channels.deliverymethods.dto.ChannelDeliveryMethodStatusDTO;
import es.onebox.mgmt.channels.deliverymethods.dto.ChannelDeliveryMethodsDTO;
import es.onebox.mgmt.channels.deliverymethods.dto.ChannelDeliveryMethodsUpdateDTO;
import es.onebox.mgmt.channels.deliverymethods.dto.CheckoutTicketDisplayDTO;
import es.onebox.mgmt.channels.deliverymethods.dto.DeliveryMethodDTO;
import es.onebox.mgmt.channels.deliverymethods.dto.EmailModeDTO;
import es.onebox.mgmt.channels.deliverymethods.dto.ReceiptTicketDisplayDTO;
import es.onebox.mgmt.channels.deliverymethods.dto.TaxInfoDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.deliverymethod.B2bExternalDownloadURL;
import es.onebox.mgmt.datasources.ms.channel.dto.deliverymethod.ChannelDeliveryMethod;
import es.onebox.mgmt.datasources.ms.channel.dto.deliverymethod.ChannelDeliveryMethodCurrencies;
import es.onebox.mgmt.datasources.ms.channel.dto.deliverymethod.ChannelDeliveryMethods;
import es.onebox.mgmt.datasources.ms.channel.dto.deliverymethod.CheckoutTicketDisplay;
import es.onebox.mgmt.datasources.ms.channel.dto.deliverymethod.DeliveryMethod;
import es.onebox.mgmt.datasources.ms.channel.dto.deliverymethod.DeliveryMethodStatus;
import es.onebox.mgmt.datasources.ms.channel.dto.deliverymethod.EmailMode;
import es.onebox.mgmt.datasources.ms.channel.dto.deliverymethod.ReceiptTicketDisplay;
import es.onebox.mgmt.datasources.ms.channel.dto.taxes.TaxInfo;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChannelDeliveryMethodsConverter {

    private ChannelDeliveryMethodsConverter() {
    }

    public static ChannelDeliveryMethodsDTO fromMs(ChannelDeliveryMethods source, ChannelResponse channel, List<Currency> currencies, String defaultCurrency) {
        ChannelDeliveryMethodsDTO target = new ChannelDeliveryMethodsDTO();

        List<ChannelDeliveryMethodDTO> deliveryMethods = source.getDeliveryMethods().stream()
                .map(dm -> fromMs(dm, currencies, defaultCurrency)).toList();

        target.setDeliveryMethods(deliveryMethods);
        target.setUseNFC(source.getUseNFC());
        target.setAllowSessionPackMultiTicket(source.getForceMultiTicket());
        EmailModeDTO emailMode = source.getEmailMode() == null ?
                null : EmailModeDTO.valueOf(source.getEmailMode().name());
        target.setEmailMode(emailMode);
        target.setB2bExternalDownloadURL(fromMs(source.getB2bExternalDownloadURL(), channel));
        target.setReceiptTicketDisplayDTO(fromMs(source.getReceiptTicketDisplay()));
        target.setCheckoutTicketDisplayDTO(fromMs(source.getCheckoutTicketDisplay()));
        return target;
    }


    public static ChannelDeliveryMethodDTO fromMs(ChannelDeliveryMethod source, List<Currency> currencies, String defaultCurrency) {
        ChannelDeliveryMethodDTO target = new ChannelDeliveryMethodDTO();

        List<ChannelDeliveryMethodCurrenciesDTO> channelDeliveryMethodCurrenciesDTO = new ArrayList<>();
        if (CollectionUtils.isEmpty(source.getCurrencies())) {
            channelDeliveryMethodCurrenciesDTO.add(fromMs(source.getCost(), defaultCurrency));
        } else {
            source.getCurrencies().forEach(currency -> currencies.stream()
                    .filter(curr -> curr.getId().equals(currency.getCurrencyId()))
                    .map(Currency::getCode)
                    .findFirst()
                    .ifPresent(currencyCode -> channelDeliveryMethodCurrenciesDTO.add(fromMs(currency.getCost(), currencyCode))));

        }
        target.setType(DeliveryMethodDTO.fromMs(source.getType()));
        target.setDefaultMethod(source.getDefaultMethod());
        ChannelDeliveryMethodStatusDTO deliveryMethod = source.getStatus() == null ?
                null : ChannelDeliveryMethodStatusDTO.valueOf(source.getStatus().name());
        target.setStatus(deliveryMethod);
        target.setCurrencies(channelDeliveryMethodCurrenciesDTO);
        target.setTaxes(fromMs(source.getTaxes()));

        return target;
    }

    public static ChannelDeliveryMethodCurrenciesDTO fromMs(Double coste, String currencyCode) {
        ChannelDeliveryMethodCurrenciesDTO deliveryMethodCurrenciesDTO = new ChannelDeliveryMethodCurrenciesDTO();
        deliveryMethodCurrenciesDTO.setCurrencyCode(currencyCode);
        deliveryMethodCurrenciesDTO.setCost(coste);
        return deliveryMethodCurrenciesDTO;
    }

    public static B2bExternalDownloadURLDTO fromMs(B2bExternalDownloadURL source, ChannelResponse channel) {
        if (source != null) {
            B2bExternalDownloadURLDTO target = new B2bExternalDownloadURLDTO();
            B2bExternalTargetChannelDTO targetChannel = new B2bExternalTargetChannelDTO();
            targetChannel.setId(source.getTargetChannelId());
            if (channel != null) {
                targetChannel.setName(channel.getName());
                targetChannel.setUrl(channel.getUrl());
            }
            target.setTargetChannel(targetChannel);
            target.setEnabled(CommonUtils.isTrue(source.getEnabled()));
            return target;
        }
        return null;
    }

    public static ChannelDeliveryMethods toMs(ChannelDeliveryMethodsUpdateDTO source, List<Currency> currencies) {
        ChannelDeliveryMethods target = new ChannelDeliveryMethods();

        if (CollectionUtils.isNotEmpty(source.getDeliveryMethods())) {
            List<ChannelDeliveryMethod> deliveryMethods =
                    source.getDeliveryMethods().stream()
                            .map(dm -> toMs(dm, currencies)).toList();
            target.setDeliveryMethods(deliveryMethods);
        }
        target.setUseNFC(source.getUseNFC());
        EmailMode emailMode = source.getEmailMode() == null ?
                null : EmailMode.valueOf(source.getEmailMode().name());
        target.setEmailMode(emailMode);
        if (source.getB2bExternalDownloadUrlUpdate() != null) {
            target.setB2bExternalDownloadURL(toMs(source.getB2bExternalDownloadUrlUpdate()));
        }
        target.setCheckoutTicketDisplay(toMs(source.getCheckoutTicketDisplay()));
        target.setReceiptTicketDisplay(toMs(source.getReceiptTicketDisplay()));
        return target;
    }

    public static ChannelDeliveryMethod toMs(ChannelDeliveryMethodDTO source, List<Currency> currencies) {
        ChannelDeliveryMethod target = new ChannelDeliveryMethod();

        target.setType(DeliveryMethod.valueOf(source.getType().name()));
        target.setDefaultMethod(source.getDefaultMethod());
        DeliveryMethodStatus deliveryMethod = source.getStatus() == null ?
                null : DeliveryMethodStatus.valueOf(source.getStatus().name());
        target.setStatus(deliveryMethod);
        List<ChannelDeliveryMethodCurrencies> channelDeliveryMethodCurrenciesList = source.getCurrencies().stream()
                .map(dto -> {
                    Currency currency = currencies.stream()
                            .filter(curr -> curr.getCode().equals(dto.getCurrencyCode()))
                            .findFirst()
                            .orElseThrow(() -> new OneboxRestException(ApiMgmtErrorCode.CURRENCY_NOT_FOUND));
                    return new ChannelDeliveryMethodCurrencies(dto.getCost(), currency.getId());
                }).toList();
        if (channelDeliveryMethodCurrenciesList.size() == 1) {
            target.setCost(channelDeliveryMethodCurrenciesList.get(0).getCost());
        }
        target.setCurrencies(channelDeliveryMethodCurrenciesList);
        target.setTaxes(toMs(source.getTaxes()));
        return target;
    }

    public static B2bExternalDownloadURL toMs(B2bExternalDownloadURLUpdateDTO source) {
        B2bExternalDownloadURL target = new B2bExternalDownloadURL();
        target.setEnabled(source.getEnabled());
        target.setTargetChannelId(source.getTargetChannelId());
        return target;
    }

    private static ReceiptTicketDisplayDTO fromMs(ReceiptTicketDisplay source) {
        if (source != null) {
            ReceiptTicketDisplayDTO target = new ReceiptTicketDisplayDTO();
            target.setPdf(source.getPdf());
            target.setPassbook(source.getPassbook());
            target.setQr(source.getQr());
            return target;
        } else {
            return null;
        }
    }

    private static CheckoutTicketDisplayDTO fromMs(CheckoutTicketDisplay source) {
        if (source != null) {
            CheckoutTicketDisplayDTO target = new CheckoutTicketDisplayDTO();
            target.setPdf(source.getPdf());
            target.setPassbook(source.getPassbook());
            return target;
        } else {
            return null;
        }
    }

    private static ReceiptTicketDisplay toMs(ReceiptTicketDisplayDTO source) {
        if (source != null) {
            ReceiptTicketDisplay target = new ReceiptTicketDisplay();
            target.setPdf(source.getPdf());
            target.setPassbook(source.getPassbook());
            target.setQr(source.getQr());
            return target;
        } else {
            return null;
        }
    }

    private static CheckoutTicketDisplay toMs(CheckoutTicketDisplayDTO source) {
        if (source != null) {
            CheckoutTicketDisplay target = new CheckoutTicketDisplay();
            target.setPdf(source.getPdf());
            target.setPassbook(source.getPassbook());
            return target;
        } else {
            return null;
        }
    }

    private static List<TaxInfoDTO> fromMs(List<TaxInfo> taxes) {
        if (CollectionUtils.isEmpty(taxes)) {
            return Collections.emptyList();
        }
        return taxes.stream().map(tax -> {
            TaxInfoDTO taxInfoDTO = new TaxInfoDTO();
            taxInfoDTO.setId(tax.getId());
            taxInfoDTO.setName(tax.getName());
            return taxInfoDTO;
        }).toList();
    }

    private static List<TaxInfo> toMs(List<TaxInfoDTO> taxes) {
        if (taxes == null) {
            return null;
        }
        return taxes.stream().map(taxDTO -> {
            TaxInfo taxInfo = new TaxInfo();
            taxInfo.setId(taxDTO.getId());
            return taxInfo;
        }).toList();
    }
}
