package es.onebox.event.report.converter;

import static es.onebox.core.file.exporter.generator.export.TranslationUtils.searchTranslation;

import es.onebox.core.file.exporter.generator.export.Translation;
import es.onebox.event.common.amqp.eventsreport.PriceSimulationReportMessage;
import es.onebox.event.priceengine.simulation.domain.BasePromotion;
import es.onebox.event.priceengine.simulation.domain.PriceSimulation;
import es.onebox.event.priceengine.simulation.domain.Surcharge;
import es.onebox.event.priceengine.simulation.domain.VenueConfigPricesSimulation;
import es.onebox.event.priceengine.simulation.domain.enums.PriceType;
import es.onebox.event.report.enums.MsEventReportType;
import es.onebox.event.report.model.filter.PriceSimulationReportRequest;
import es.onebox.event.report.model.report.PriceSimulationReportDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;

public class PriceSimulationReportConverter {

    private PriceSimulationReportConverter() {
        throw new UnsupportedOperationException();
    }

    public static PriceSimulationReportMessage toMessage(final PriceSimulationReportRequest filter,
        final String exportId) {
        if (filter == null) {
            return null;
        }
        PriceSimulationReportMessage message = new PriceSimulationReportMessage();
        message.setSaleRequestId(filter.getSaleRequestId());
        message.setFields(filter.getFields());
        message.setFormat(filter.getFormat());
        message.setUserId(filter.getUserId());
        message.setEmail(filter.getEmail());
        message.setExportType(MsEventReportType.PRICE_SIMULATION);
        message.setExportId(exportId);
        message.setTimeZone(filter.getTimeZone());
        message.setCharset(filter.getCharset());
        message.setCsvSeparatorFormat(filter.getCsvSeparatorFormat());
        message.setCsvfractionDigitsSeparatorFormat(filter.getCsvfractionDigitsSeparatorFormat());
        message.setLanguage(filter.getLanguage());
        return message;
    }


    public static List<PriceSimulationReportDTO> toReport(
        VenueConfigPricesSimulation domain,
        Set<Translation> translations) {
        List<PriceSimulationReportDTO> priceSimulationReportDTOs = new ArrayList<>();
        setRates(translations, priceSimulationReportDTOs, domain);
        return priceSimulationReportDTOs;
    }

    private static void setRates(Set<Translation> translations,
        List<PriceSimulationReportDTO> priceSimulationReportDTOs,
        VenueConfigPricesSimulation domain) {
        long venueConfigId = domain.getVenueConfig().getId();
        String venueConfigName = domain.getVenueConfig().getName();
        PriceSimulationReportDTO dto = new PriceSimulationReportDTO();
        dto.setVenueConfigId(venueConfigId);
        dto.setVenueConfigName(venueConfigName);
        if (domain.getRates() != null) {
            domain.getRates().forEach(rate ->
            {
                PriceSimulationReportDTO dtoRate = new PriceSimulationReportDTO();
                setPriceSimulationReportDTOVenue(dtoRate, dto);
                dtoRate.setRateId(rate.getId());
                dtoRate.setRateName(rate.getName());
                setPriceType(translations, priceSimulationReportDTOs, dtoRate,
                    rate.getPriceTypes());
            });
        } else {
            priceSimulationReportDTOs.add(dto);
        }
    }

    private static void setPriceType(Set<Translation> translations,
        List<PriceSimulationReportDTO> priceSimulationReportDTOs,
        PriceSimulationReportDTO dtoRates, List<PriceType> priceTypes) {
        if (priceTypes != null) {
            priceTypes.forEach(priceType -> {
                PriceSimulationReportDTO dtoPriceType = new PriceSimulationReportDTO();
                setPriceSimulationReportDTOVenue(dtoPriceType, dtoRates);
                setPriceSimulationReportDTORate(dtoRates, dtoPriceType);
                dtoPriceType.setPriceTypeId(priceType.getId());
                dtoPriceType.setPriceTypeName(priceType.getName());
                setPriceSimulation(translations, priceSimulationReportDTOs, dtoPriceType,
                    priceType.getSimulations());
            });
        } else {
            priceSimulationReportDTOs.add(dtoRates);
        }

    }

    private static void setPriceSimulation(Set<Translation> translations,
        List<PriceSimulationReportDTO> priceSimulationReportDTOs,
        PriceSimulationReportDTO dtoPriceTypes, List<PriceSimulation> simulations) {
        if (simulations != null) {
            simulations.forEach(simulation -> {
                PriceSimulationReportDTO dtoSimulation = new PriceSimulationReportDTO();
                setPriceSimulationReportDTOVenue(dtoSimulation, dtoPriceTypes);
                setPriceSimulationReportDTORate(dtoPriceTypes, dtoSimulation);
                setPriceSimulationReportDTOPriceType(dtoPriceTypes, dtoSimulation);
                dtoSimulation.setPriceBase(simulation.getPrice().getBase());
                dtoSimulation.setPriceTotal(simulation.getPrice().getTotal());
                List<PriceSimulationReportDTO> dtoSurcharges = setSurcharge(
                    dtoSimulation, simulation.getPrice().getSurcharges());
                setBasePromotion(translations, dtoSurcharges, priceSimulationReportDTOs,
                    simulation.getBasePromotions());
            });
        } else {
            priceSimulationReportDTOs.add(dtoPriceTypes);
        }

    }

    private static void setBasePromotion(Set<Translation> translations,
        List<PriceSimulationReportDTO> dtoSurcharges,
        List<PriceSimulationReportDTO> priceSimulationReportDTOs,
        List<BasePromotion> basePromotions) {
        if (CollectionUtils.isNotEmpty(dtoSurcharges)) {
            dtoSurcharges.forEach(dto ->
                {
                    PriceSimulationReportDTO dtoPromotion = new PriceSimulationReportDTO();
                    setPriceSimulationReportDTOVenue(dtoPromotion, dto);
                    setPriceSimulationReportDTORate(dto, dtoPromotion);
                    setPriceSimulationReportDTOPriceType(dto, dtoPromotion);
                    setPriceSimulationReportDTOSimulation(dto, dtoPromotion);
                    setPriceSimulationReportDTOSurcharge(dto, dtoPromotion);
                    setPromotion(translations,
                                 dtoPromotion,
                                 priceSimulationReportDTOs,
                                 basePromotions);
                }
            );
        }
    }

    private static List<PriceSimulationReportDTO> setSurcharge(
        PriceSimulationReportDTO dto, List<Surcharge> surcharges) {
        List<PriceSimulationReportDTO> dtos = new ArrayList<>();

        if (surcharges != null) {
            surcharges.forEach(surcharge -> {
                PriceSimulationReportDTO dtoSurcharge = new PriceSimulationReportDTO();
                setPriceSimulationReportDTOVenue(dtoSurcharge, dto);
                setPriceSimulationReportDTORate(dto, dtoSurcharge);
                setPriceSimulationReportDTOPriceType(dto, dtoSurcharge);
                setPriceSimulationReportDTOSimulation(dto, dtoSurcharge);
                dtoSurcharge.setSurchargeValue(surcharge.getValue());
                dtoSurcharge.setSurchargeType(surcharge.getType().name());
                dtos.add(dtoSurcharge);
            });
        } else {
            dtos.add(dto);
        }

        return dtos;
    }

    private static void setPromotion(Set<Translation> translations,
        PriceSimulationReportDTO dtoSurcharges,
        List<PriceSimulationReportDTO> priceSimulationReportDTOs,
        List<BasePromotion> basePromotions) {
        if (basePromotions != null) {
            basePromotions.forEach(basePromotion -> {
                PriceSimulationReportDTO dtoBasePromotion = new PriceSimulationReportDTO();
                setPriceSimulationReportDTOVenue(dtoBasePromotion, dtoSurcharges);
                setPriceSimulationReportDTORate(dtoSurcharges, dtoBasePromotion);
                setPriceSimulationReportDTOPriceType(dtoSurcharges, dtoBasePromotion);
                setPriceSimulationReportDTOSurcharge(dtoSurcharges, dtoBasePromotion);
                dtoBasePromotion.setPriceBase(dtoSurcharges.getPriceBase());
                dtoBasePromotion.setPriceTotal(dtoSurcharges.getPriceTotal());
                setPriceSimulationReportDTOPromotion(basePromotion, dtoBasePromotion);
                dtoBasePromotion.setPromotionType(searchTranslation(
                    translations,
                    Optional.ofNullable(basePromotion.getType())
                        .map(Enum::name)
                        .orElse(null)
                ));
                priceSimulationReportDTOs.add(dtoBasePromotion);
            });
        } else {
            priceSimulationReportDTOs.add(dtoSurcharges);
        }
    }

    private static void setPriceSimulationReportDTOVenue(PriceSimulationReportDTO dtoRate,
        PriceSimulationReportDTO dto) {
        dtoRate.setVenueConfigId(dto.getVenueConfigId());
        dtoRate.setVenueConfigName(dto.getVenueConfigName());
    }


    private static void setPriceSimulationReportDTORate(PriceSimulationReportDTO dtoRates,
        PriceSimulationReportDTO dtoPriceType) {
        dtoPriceType.setRateId(dtoRates.getRateId());
        dtoPriceType.setRateName(dtoRates.getRateName());
    }

    private static void setPriceSimulationReportDTOPriceType(PriceSimulationReportDTO dtoPriceTypes,
        PriceSimulationReportDTO dtoSimulation) {
        dtoSimulation.setPriceTypeId(dtoPriceTypes.getPriceTypeId());
        dtoSimulation.setPriceTypeName(dtoPriceTypes.getPriceTypeName());
    }

    private static void setPriceSimulationReportDTOSimulation(PriceSimulationReportDTO dto,
        PriceSimulationReportDTO dtoPromotion) {
        dtoPromotion.setPriceTotal(dto.getPriceTotal());
        dtoPromotion.setPriceBase(dto.getPriceBase());
    }


    private static void setPriceSimulationReportDTOSurcharge(PriceSimulationReportDTO dto,
        PriceSimulationReportDTO dtoPromotion) {
        dtoPromotion.setSurchargeType(dto.getSurchargeType());
        dtoPromotion.setSurchargeValue(dto.getSurchargeValue());
    }

    private static void setPriceSimulationReportDTOPromotion(BasePromotion basePromotion,
        PriceSimulationReportDTO dtoBasePromotion) {
        dtoBasePromotion.setPromotionId(basePromotion.getId());
        dtoBasePromotion.setPromotionName(basePromotion.getName());
    }


}
