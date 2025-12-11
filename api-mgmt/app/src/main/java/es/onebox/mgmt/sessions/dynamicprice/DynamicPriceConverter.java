package es.onebox.mgmt.sessions.dynamicprice;

import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.DynamicPrice;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.DynamicPriceConfig;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.DynamicPriceStatusRequest;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.DynamicPriceTranslation;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.DynamicPriceZone;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.DynamicRatesPrice;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.StatusDynamicPrice;
import es.onebox.mgmt.sessions.dynamicprice.dto.DynamicPriceConfigDTO;
import es.onebox.mgmt.sessions.dynamicprice.dto.DynamicPriceDTO;
import es.onebox.mgmt.sessions.dynamicprice.dto.DynamicPriceStatusRequestDTO;
import es.onebox.mgmt.sessions.dynamicprice.dto.DynamicPriceTranslationDTO;
import es.onebox.mgmt.sessions.dynamicprice.dto.DynamicPriceZoneDTO;
import es.onebox.mgmt.sessions.dynamicprice.dto.DynamicRatesPriceDTO;
import es.onebox.mgmt.sessions.dynamicprice.dto.RequestDynamicPriceDTO;

import java.util.ArrayList;
import java.util.List;

public class DynamicPriceConverter {

    public static DynamicPrice fromDynamicPriceDTO(RequestDynamicPriceDTO dynamicPriceDTO) {
        DynamicPrice dynamicPrice = new DynamicPrice();
        if (dynamicPriceDTO.getDynamicRatesPriceDTO() != null) {
            List<DynamicRatesPrice> dynamicRates = new ArrayList<>();
            dynamicPriceDTO.getDynamicRatesPriceDTO()
                    .forEach(dynamicRatesPriceDTO -> dynamicRates.add(fillDynamicRatePrice(dynamicRatesPriceDTO)));
            dynamicPrice.setDynamicRatesPriceDTO(dynamicRates);
        }
        if(dynamicPriceDTO.getTranslationsDTO()!=null){
            List<DynamicPriceTranslation> translations = new ArrayList<>();
            dynamicPriceDTO.getTranslationsDTO()
                    .forEach(translationDTO -> translations.add(fillDynamicPriceTranslation(translationDTO)));
            dynamicPrice.setTranslationsDTO(translations);
        }

        dynamicPrice.setCapacity(dynamicPriceDTO.getCapacity());
        dynamicPrice.setValidDate(dynamicPriceDTO.getValidDate());
        dynamicPrice.setName(dynamicPriceDTO.getName());
        dynamicPrice.setConditionTypes(dynamicPriceDTO.getConditionTypes());
        dynamicPrice.setOrder(dynamicPriceDTO.getOrder());
        return dynamicPrice;
    }

    public static DynamicPriceStatusRequest fromDynamicPriceStatusRequestDTO(DynamicPriceStatusRequestDTO dto) {
        DynamicPriceStatusRequest request = new DynamicPriceStatusRequest();
        request.setStatus(dto.getStatus());
        return request;
    }

    private static DynamicRatesPrice fillDynamicRatePrice(DynamicRatesPriceDTO dynamicRatesPriceDTO) {
        DynamicRatesPrice dynamicRatesPrice = new DynamicRatesPrice();
        dynamicRatesPrice.setPrice(dynamicRatesPriceDTO.getPrice());
        dynamicRatesPrice.setName(dynamicRatesPriceDTO.getName());
        dynamicRatesPrice.setId(dynamicRatesPriceDTO.getId());
        return dynamicRatesPrice;
    }

    private static DynamicPriceTranslation fillDynamicPriceTranslation(DynamicPriceTranslationDTO dynamicPriceTranslationDTO) {
        DynamicPriceTranslation dynamicPriceTranslation = new DynamicPriceTranslation();
        dynamicPriceTranslation.setLanguage(ConverterUtils.toLocale(dynamicPriceTranslationDTO.getLanguage()));
        dynamicPriceTranslation.setValue(dynamicPriceTranslationDTO.getValue());
        return dynamicPriceTranslation;
    }

    public static DynamicPriceConfigDTO toDynamicPriceConfigDTO(DynamicPriceConfig source) {
        DynamicPriceConfigDTO target = new DynamicPriceConfigDTO();
        if (source != null && source.getDynamicPriceZoneDTO() != null) {
            fillDynamicPriceZoneListDTO(target, source);
        }
        return target;
    }

    private static void fillDynamicPriceZoneListDTO(DynamicPriceConfigDTO target, DynamicPriceConfig source) {
        List<DynamicPriceZoneDTO> dynamicPriceZonesDTO = new ArrayList<>();
        source.getDynamicPriceZoneDTO().forEach(
                dynamicPriceZone -> dynamicPriceZonesDTO.add(toDynamicPriceZoneDTO(dynamicPriceZone))
        );
        target.setDynamicPriceZone(dynamicPriceZonesDTO);
    }

    public static DynamicPriceZoneDTO toDynamicPriceZoneDTO(DynamicPriceZone dynamicPriceZone) {
        DynamicPriceZoneDTO dynamicPriceZoneDTO = new DynamicPriceZoneDTO();
        dynamicPriceZoneDTO.setIdPriceZone(dynamicPriceZone.getIdPriceZone());
        dynamicPriceZoneDTO.setPriceZoneName(dynamicPriceZone.getPriceZoneName());
        dynamicPriceZoneDTO.setActiveZone(dynamicPriceZone.getActiveZone());
        if (dynamicPriceZone.getDynamicPricesDTO() != null) {
            List<DynamicPriceDTO> dynamicPrices = new ArrayList<>();
            Long activeZone = dynamicPriceZone.getActiveZone();
            dynamicPriceZone.getDynamicPricesDTO()
                    .forEach(dynamicPrice -> dynamicPrices.add(fillDynamicPriceDTO(dynamicPrice, activeZone)));
            dynamicPriceZoneDTO.setDynamicPricesDTO(dynamicPrices);
        }
        dynamicPriceZoneDTO.setEditable(dynamicPriceZone.getEditable());
        return dynamicPriceZoneDTO;
    }
    private static DynamicPriceDTO fillDynamicPriceDTO(DynamicPrice dynamicPrice, Long activeZone) {
        DynamicPriceDTO dynamicPriceDTO = new DynamicPriceDTO();
        if (dynamicPrice.getDynamicRatesPriceDTO() != null) {
            List<DynamicRatesPriceDTO> dynamicRates = new ArrayList<>();
            dynamicPrice.getDynamicRatesPriceDTO()
                    .forEach(dynamicRatesPrice -> dynamicRates.add(toDynamicRatePriceDTO(dynamicRatesPrice)));
            dynamicPriceDTO.setDynamicRatesPriceDTO(dynamicRates);
        }
        if(dynamicPrice.getTranslationsDTO() != null) {
            List<DynamicPriceTranslationDTO> translations = new ArrayList<>();
            dynamicPrice.getTranslationsDTO()
                    .forEach(translation -> translations.add(toDynamicPriceTranslationDTO(translation)));
            dynamicPriceDTO.setTranslationsDTO(translations);
        }
        if (dynamicPrice.getOrder() != null && activeZone != null) {
            int order = dynamicPrice.getOrder();
            long activeOrder = activeZone;
            if (order == activeOrder) {
                dynamicPriceDTO.setStatusDynamicPrice(StatusDynamicPrice.ACTIVE);
            } else if (order < activeOrder) {
                dynamicPriceDTO.setStatusDynamicPrice(StatusDynamicPrice.BLOCKED);
            } else if (order > activeOrder) {
                dynamicPriceDTO.setStatusDynamicPrice(StatusDynamicPrice.PENDING);
            }
        } else {
            dynamicPriceDTO.setStatusDynamicPrice(StatusDynamicPrice.PENDING);
        }
        dynamicPriceDTO.setCapacity(dynamicPrice.getCapacity());
        dynamicPriceDTO.setValidDate(dynamicPrice.getValidDate());
        dynamicPriceDTO.setName(dynamicPrice.getName());
        dynamicPriceDTO.setConditionTypes(dynamicPrice.getConditionTypes());
        dynamicPriceDTO.setOrder(dynamicPrice.getOrder());
        return dynamicPriceDTO;
    }

    public static DynamicRatesPriceDTO toDynamicRatePriceDTO(DynamicRatesPrice dynamicRatesPrice) {
        DynamicRatesPriceDTO dynamicRatesPriceDTO = new DynamicRatesPriceDTO();
        dynamicRatesPriceDTO.setPrice(dynamicRatesPrice.getPrice());
        dynamicRatesPriceDTO.setId(dynamicRatesPrice.getId());
        dynamicRatesPriceDTO.setName(dynamicRatesPrice.getName());
        return dynamicRatesPriceDTO;
    }

    private static DynamicPriceTranslationDTO toDynamicPriceTranslationDTO(DynamicPriceTranslation dynamicPriceTranslation) {
        DynamicPriceTranslationDTO dynamicPriceTranslationDTO = new DynamicPriceTranslationDTO();
        dynamicPriceTranslationDTO.setLanguage(ConverterUtils.toLanguageTag(dynamicPriceTranslation.getLanguage()));
        dynamicPriceTranslationDTO.setValue(dynamicPriceTranslation.getValue());
        return dynamicPriceTranslationDTO;
    }
}
