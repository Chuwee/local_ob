package es.onebox.mgmt.salerequests.converter;

import es.onebox.mgmt.common.promotions.enums.PromotionStatus;
import es.onebox.mgmt.common.promotions.enums.PromotionType;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestPromotionsDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestPromotionsResponseDTO;
import es.onebox.mgmt.salerequests.dto.BaseSessionSaleRequestDTO;
import es.onebox.mgmt.salerequests.dto.BaseVenueSaleRequestDTO;
import es.onebox.mgmt.salerequests.dto.CollectiveDTO;
import es.onebox.mgmt.salerequests.dto.PriceTypeDTO;
import es.onebox.mgmt.salerequests.dto.PriceVariationDTO;
import es.onebox.mgmt.salerequests.dto.RangeDTO;
import es.onebox.mgmt.salerequests.dto.RateDTO;
import es.onebox.mgmt.salerequests.dto.SaleRequestPromotionDTO;
import es.onebox.mgmt.salerequests.dto.SaleRequestPromotionResponseDTO;
import es.onebox.mgmt.salerequests.dto.SessionDate;
import es.onebox.mgmt.salerequests.dto.SessionSaleRequestDTO;
import es.onebox.mgmt.salerequests.dto.ValidityPeriodDTO;
import es.onebox.mgmt.salerequests.enums.PriceVariationType;
import es.onebox.mgmt.salerequests.enums.ValidityPeriodType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SaleRequestPromotionConverter {

    public static SaleRequestPromotionResponseDTO fromMsChannelResponse(MsSaleRequestPromotionsResponseDTO msResponse){
        SaleRequestPromotionResponseDTO dto = new SaleRequestPromotionResponseDTO();

        dto.setMetadata(msResponse.getMetadata());
        dto.setData(msResponse.getData().stream()
                .map(SaleRequestPromotionConverter::fromMsChannelObject)
                .collect(Collectors.toList()));
        return dto;
    }

    public static SaleRequestPromotionDTO fromMsChannelObject(MsSaleRequestPromotionsDTO msSaleRequestPromotions){
        SaleRequestPromotionDTO result = new SaleRequestPromotionDTO();

        result.setId(msSaleRequestPromotions.getId());
        result.setName(msSaleRequestPromotions.getName());
        result.setType(PromotionType.valueOf(msSaleRequestPromotions.getType().name()));
        result.setStatus(PromotionStatus.valueOf(msSaleRequestPromotions.getStatus().name()));
        result.setRestrictiveAccess(msSaleRequestPromotions.getRestrictiveAccess());

        Optional.ofNullable(msSaleRequestPromotions.getPriceVariation()).ifPresent(msPriceVariation -> {
            PriceVariationDTO priceVariation = new PriceVariationDTO();
            priceVariation.setType(PriceVariationType.valueOf(msPriceVariation.getType().name()));
            priceVariation.setValue(msPriceVariation.getValue());
            Optional.ofNullable(msPriceVariation.getRanges()).ifPresent(msRanges ->{
                List<RangeDTO> ranges = new ArrayList<>();
                msRanges.stream().forEach(r ->{
                    RangeDTO range = new RangeDTO();
                    range.setFrom(r.getFrom());
                    range.setTo(r.getTo());
                    range.setValue(r.getValue());
                    ranges.add(range);
                });
                priceVariation.setRanges(ranges);
            });
             result.setPriceVariation(priceVariation);
        });

        Optional.ofNullable(msSaleRequestPromotions.getCollective()).ifPresent(msCollective -> {
            CollectiveDTO collective = new CollectiveDTO();
            collective.setId(msCollective.getId());
            collective.setName(msCollective.getName());
            result.setCollective(collective);
        } );

        Optional.ofNullable(msSaleRequestPromotions.getValidityPeriod()).ifPresent(msValidityPeriod -> {
            ValidityPeriodDTO validityPeriod = new ValidityPeriodDTO();
            validityPeriod.setType(ValidityPeriodType.valueOf(msValidityPeriod.getType().name()));
            validityPeriod.setFrom(msValidityPeriod.getFrom());
            validityPeriod.setTo(msValidityPeriod.getTo());
            result.setValidityPeriod(validityPeriod);
        });

        Optional.ofNullable(msSaleRequestPromotions.getSessions()).ifPresent(msSessions -> {
            List<BaseSessionSaleRequestDTO> sessions = new ArrayList<>();
            msSessions.stream().forEach(msSession -> {
                SessionSaleRequestDTO session = new SessionSaleRequestDTO();
                session.setId(msSession.getId());
                session.setName(msSession.getName());

                SessionDate sessionDate = new SessionDate();
                sessionDate.setStart(msSession.getDate().getStart());
                session.setDate(sessionDate);

                sessions.add(session);
            });
            result.setSessions(sessions);
        });

        Optional.ofNullable(msSaleRequestPromotions.getRates()).ifPresent(msRates -> {
            List<RateDTO> rates = new ArrayList<>();
            msRates.stream().forEach(msRate ->{
                RateDTO rate = new RateDTO();
                rate.setId(msRate.getId());
                rate.setName(msRate.getName());
                rates.add(rate);
            });
            result.setRates(rates);
        });

        Optional.ofNullable(msSaleRequestPromotions.getPriceTypes()).ifPresent(msPriceTypes-> {
            List<PriceTypeDTO> priceTypes = new ArrayList<>();
            msPriceTypes.stream().forEach(msPriceType -> {
                PriceTypeDTO priceType = new PriceTypeDTO();
                priceType.setId(msPriceType.getId());
                priceType.setName(msPriceType.getName());

                BaseVenueSaleRequestDTO venueConfig = new BaseVenueSaleRequestDTO();
                venueConfig.setId(msPriceType.getVenueConfig().getId());
                venueConfig.setName(msPriceType.getVenueConfig().getName());
                priceType.setVenueConfig(venueConfig);

                priceTypes.add(priceType);
            });
            result.setPriceTypes(priceTypes);
        });

        return result;
    }
}
