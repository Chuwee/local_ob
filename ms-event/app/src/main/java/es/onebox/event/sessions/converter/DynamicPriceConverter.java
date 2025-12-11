package es.onebox.event.sessions.converter;

import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.dto.event.EventRate;
import es.onebox.event.events.dto.EventTemplatePriceDTO;
import es.onebox.event.sessions.domain.sessionconfig.DynamicPrice;
import es.onebox.event.sessions.domain.sessionconfig.DynamicPriceTranslation;
import es.onebox.event.sessions.domain.sessionconfig.DynamicPriceZone;
import es.onebox.event.sessions.domain.sessionconfig.DynamicRatesPrice;
import es.onebox.event.sessions.domain.sessionconfig.SessionDynamicPriceConfig;
import es.onebox.event.sessions.dto.DynamicPriceDTO;
import es.onebox.event.sessions.dto.DynamicPriceTranslationDTO;
import es.onebox.event.sessions.dto.DynamicPriceZoneDTO;
import es.onebox.event.sessions.dto.DynamicRatesPriceDTO;
import es.onebox.event.sessions.dto.RateDTO;
import es.onebox.event.sessions.dto.SessionDTO;
import es.onebox.event.sessions.dto.SessionDynamicPriceConfigDTO;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DynamicPriceConverter {

    public static SessionDynamicPriceConfig initDynamicPricesConfig(SessionDynamicPriceConfig config, List<IdNameCodeDTO> priceTypes) {
        if (config == null) {
            config = new SessionDynamicPriceConfig();
        }

        if (CollectionUtils.isEmpty(config.getDynamicPriceZone())) {
            List<DynamicPriceZone> records = priceTypes.stream().map(pt -> {
                DynamicPriceZone record = new DynamicPriceZone();
                record.setIdPriceZone(pt.getId());
                record.setDynamicPrices(new ArrayList<>());
                record.setActiveZone(null);
                return record;
            }).collect(Collectors.toList());
            config.setDynamicPriceZone(records);
        } else {
            List<Long> idZones = config.getDynamicPriceZone().stream().map(DynamicPriceZone::getIdPriceZone).toList();
            priceTypes = priceTypes.stream().filter(pt -> !idZones.contains(pt.getId())).collect(Collectors.toList());
            List<DynamicPriceZone> records = priceTypes.stream().map(pt -> {
                DynamicPriceZone record = new DynamicPriceZone();
                record.setIdPriceZone(pt.getId());
                record.setDynamicPrices(new ArrayList<>());
                record.setActiveZone(null);
                return record;
            }).toList();
            config.getDynamicPriceZone().addAll(records);
        }

        return config;
    }

    public static SessionDynamicPriceConfigDTO toDTO(SessionDynamicPriceConfig config, SessionDTO sessionDTO) {
        SessionDynamicPriceConfigDTO dto = new SessionDynamicPriceConfigDTO();

        if (config != null && config.getDynamicPriceZone() != null) {
            Map<Long, String> rateMap = createRateMap(sessionDTO.getRates());
            dto.setDynamicPriceZoneDTO(convertZones(config.getDynamicPriceZone(), rateMap));
        }
        return dto;
    }

    private static List<DynamicPriceZoneDTO> convertZones(List<DynamicPriceZone> zones, Map<Long, String> rateMap) {
        return zones.stream()
                .map(zone -> convertZone(zone, rateMap))
                .toList();
    }

    private static DynamicPriceZoneDTO convertZone(DynamicPriceZone zone, Map<Long, String> rateMap) {
        DynamicPriceZoneDTO dto = new DynamicPriceZoneDTO();
        dto.setIdPriceZone(zone.getIdPriceZone());
        dto.setActiveZone(zone.getActiveZone());

        if (zone.getDynamicPrices() != null) {
            dto.setDynamicPricesDTO(convertPrices(zone.getDynamicPrices(), rateMap));
        }

        return dto;
    }

    public static DynamicPriceZoneDTO convertZone(DynamicPriceZone zone, Event catalogEvent, Boolean isActive) {
        DynamicPriceZoneDTO dto = new DynamicPriceZoneDTO();
        dto.setIdPriceZone(zone.getIdPriceZone());
        dto.setActiveZone(zone.getActiveZone());
        dto.setActive(isActive);
        Map<Long, String> rateMap = null;
        if (catalogEvent != null) {
           rateMap = createEventRateMap(catalogEvent.getRates());
        }
        if (zone.getDynamicPrices() != null) {
            dto.setDynamicPricesDTO(convertPrices(zone.getDynamicPrices(), rateMap));
        }

        if (zone.getDefaultPrice() != null) {
            dto.setDefaultPriceDTO(convertPrices(Collections.singletonList(zone.getDefaultPrice()), rateMap).get(0));
        }

        return dto;
    }


    private static List<DynamicPriceDTO> convertPrices(List<DynamicPrice> prices, Map<Long, String> rateMap) {
        return prices.stream()
                .map(price -> convertDynamicPrice(price, rateMap))
                .toList();
    }

    private static DynamicPriceDTO convertDynamicPrice(DynamicPrice price, Map<Long, String> rateMap) {
        DynamicPriceDTO dto = new DynamicPriceDTO();
        copyCommonFields(price, dto);

        if (price.getDynamicRatesPrice() != null) {
            dto.setDynamicRatesPriceDTO(toDynamicRatesPriceDTOList(price.getDynamicRatesPrice(), rateMap));
        }
        if (price.getTranslations() != null) {
            dto.setTranslationsDTO(toDynamicPriceTranslationDTOList(price.getTranslations()));
        }

        return dto;
    }

    public static DynamicPrice toEntity(DynamicPriceDTO dto) {
        DynamicPrice entity = new DynamicPrice();
        copyCommonFields(dto, entity);

        if (dto.getDynamicRatesPriceDTO() != null) {
            entity.setDynamicRatesPrice(toDynamicRatesPriceList(dto.getDynamicRatesPriceDTO()));
        }
        if (dto.getTranslationsDTO() != null) {
            entity.setTranslations(toDynamicPriceTranslationList(dto.getTranslationsDTO()));
        }
        return entity;
    }

    public static DynamicRatesPrice toEntity(DynamicRatesPriceDTO dto) {
        DynamicRatesPrice entity = new DynamicRatesPrice();
        entity.setId(dto.getId());
        entity.setPrice(dto.getPrice());
        return entity;
    }

    public static DynamicPriceDTO toDTO(DynamicPrice entity, List<RateDTO> rates) {
        DynamicPriceDTO dto = new DynamicPriceDTO();
        copyCommonFields(entity, dto);

        if (entity.getDynamicRatesPrice() != null) {
            dto.setDynamicRatesPriceDTO(toDynamicRatesPriceDTOList(entity.getDynamicRatesPrice(), rates));
        }
        if (entity.getTranslations() != null) {
            dto.setTranslationsDTO(toDynamicPriceTranslationDTOList(entity.getTranslations()));
        }

        return dto;
    }

    private static void copyCommonFields(DynamicPrice source, DynamicPriceDTO target) {
        target.setName(source.getName());
        target.setCapacity(source.getCapacity());
        target.setValidDate(source.getValidDate());
        if(source.getConditionTypes() == null) {
            target.setConditionTypes(new HashSet<>());
        }
        target.setConditionTypes(source.getConditionTypes());
        target.setOrder(source.getOrder());
    }

    private static void copyCommonFields(DynamicPriceDTO source, DynamicPrice target) {
        target.setName(source.getName());
        target.setCapacity(source.getCapacity());
        target.setValidDate(source.getValidDate());
        if(source.getConditionTypes() == null) {
            target.setConditionTypes(new HashSet<>());
        }
        target.setConditionTypes(source.getConditionTypes());
        target.setOrder(source.getOrder());
    }

    public static List<DynamicRatesPriceDTO> toDynamicRatesPriceDTOList(List<DynamicRatesPrice> entities, List<RateDTO> rates) {
        return toDynamicRatesPriceDTOList(entities, createRateMap(rates));
    }

    public static List<DynamicRatesPriceDTO> toDynamicRatesPriceDTOList(List<DynamicRatesPrice> entities, Map<Long, String> rateMap) {
        return entities.stream()
                .map(rate -> toDTO(rate, rateMap))
                .toList();
    }

    public static DynamicPrice toDynamicPriceDTOList(List<EventTemplatePriceDTO> eventTemplatePriceDTOS, Long idPriceZone) {
        List<DynamicRatesPrice> dynamicRatesPriceList = new ArrayList<>();
        DynamicPrice dynamicPrice = new DynamicPrice();
        dynamicPrice.setName("Default");
        eventTemplatePriceDTOS.stream()
                .filter(eventTemplatePriceDTO -> eventTemplatePriceDTO.getPriceTypeId().equals(idPriceZone))
                .forEach(eventTemplatePriceDTO -> {
                    DynamicRatesPrice dynamicRatesPrice = new DynamicRatesPrice();
                    dynamicRatesPrice.setPrice(eventTemplatePriceDTO.getPrice());
                    dynamicRatesPrice.setId(eventTemplatePriceDTO.getRateId().longValue());
                    dynamicRatesPriceList.add(dynamicRatesPrice);
                });
        dynamicPrice.setDynamicRatesPrice(dynamicRatesPriceList);
        return dynamicPrice;
    }

    public static List<DynamicPriceTranslationDTO> toDynamicPriceTranslationDTOList(List<DynamicPriceTranslation> translations) {
        return translations.stream()
                .map(DynamicPriceConverter::toDTO)
                .toList();
    }

    public static DynamicPriceTranslationDTO toDTO(DynamicPriceTranslation entity) {
        DynamicPriceTranslationDTO dto = new DynamicPriceTranslationDTO();
        dto.setLanguage(entity.getLanguage());
        dto.setValue(entity.getValue());
        return dto;
    }

    public static DynamicRatesPriceDTO toDTO(DynamicRatesPrice entity, Map<Long, String> rateMap) {
        DynamicRatesPriceDTO dto = new DynamicRatesPriceDTO();
        dto.setId(entity.getId());
        dto.setPrice(entity.getPrice());
        if (rateMap != null) {
            dto.setName(rateMap.get(entity.getId()));
        }
        return dto;
    }

    public static List<DynamicRatesPrice> toDynamicRatesPriceList(List<DynamicRatesPriceDTO> dtos) {
        return dtos.stream()
                .map(DynamicPriceConverter::toEntity)
                .toList();
    }

    public static List<DynamicPriceTranslation> toDynamicPriceTranslationList(List<DynamicPriceTranslationDTO> dtos) {
        return dtos.stream()
                .map(DynamicPriceConverter::toEntity)
                .toList();
    }

    public static DynamicPriceTranslation toEntity(DynamicPriceTranslationDTO dto) {
        DynamicPriceTranslation entity = new DynamicPriceTranslation();
        entity.setLanguage(dto.getLanguage());
        entity.setValue(dto.getValue());
        return entity;
    }

    public static Map<Long, String> createRateMap(List<RateDTO> rates) {
        return rates.stream()
                .collect(Collectors.toMap(RateDTO::getId, RateDTO::getName));
    }

    public static Map<Long, String> createEventRateMap(List<EventRate> rates) {
        return rates.stream()
                .collect(Collectors.toMap(EventRate::getId, EventRate::getName));
    }
}