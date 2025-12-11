package es.onebox.event.catalog.elasticsearch.pricematrix;

import es.onebox.core.order.utils.tax.TaxUtils;
import es.onebox.core.utils.common.NumberUtils;
import es.onebox.event.catalog.elasticsearch.dto.PriceZonePrice;
import es.onebox.event.catalog.elasticsearch.dto.RateBase;
import es.onebox.event.catalog.elasticsearch.dto.RatePrice;
import es.onebox.event.catalog.elasticsearch.dto.VenueTemplatePrice;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionRate;
import es.onebox.event.datasources.ms.ticket.dto.secondarymarket.SecondaryMarketSearch;
import es.onebox.event.datasources.ms.ticket.dto.secondarymarket.Ticket;
import es.onebox.event.priceengine.surcharges.SurchargeUtils;
import es.onebox.event.priceengine.surcharges.dto.ChannelEventSurcharges;
import es.onebox.event.priceengine.taxes.domain.ChannelTaxInfo;
import es.onebox.event.priceengine.taxes.domain.SessionTaxInfo;
import es.onebox.event.promotions.dto.EventPromotion;
import es.onebox.event.promotions.dto.PromotionPriceVariationValue;
import es.onebox.event.promotions.enums.PromotionPriceVariationType;
import es.onebox.event.promotions.enums.PromotionType;
import es.onebox.event.sessions.converter.DynamicPriceConverter;
import es.onebox.event.sessions.domain.sessionconfig.DynamicPrice;
import es.onebox.event.sessions.domain.sessionconfig.DynamicPriceZone;
import es.onebox.event.sessions.domain.sessionconfig.DynamicRatesPrice;
import es.onebox.event.sessions.domain.sessionconfig.SessionDynamicPriceConfig;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class PriceMatrixCalculator {

    private static final double ZERO = 0d;

    private List<VenueTemplatePrice> prices;
    private ChannelEventSurcharges channelEventSurcharges;
    private List<Long> priceZonesBySession;
    private List<SessionRate> rates;
    private List<EventPromotion> eventPromotions;
    private List<Long> sessionPromotions;
    private List<SecondaryMarketSearch> secondaryMarket;
    private SessionDynamicPriceConfig sessionDynamicPriceConfig;
    private List<SessionTaxInfo> sessionTaxes;
    private List<SessionTaxInfo> surchargesTaxes;
    private List<ChannelTaxInfo> channelSurchargesTaxes;

    private PriceMatrixCalculator() {
        super();
    }

    public static PriceMatrixCalculator builder() {
        return new PriceMatrixCalculator();
    }

    public PriceMatrix build() {
        PriceMatrix priceMatrix = new PriceMatrix();
        priceMatrix.setPrices(new ArrayList<>());
        priceMatrix.setNetPrices(new ArrayList<>());

        if (priceZonesBySession == null || priceZonesBySession.isEmpty()) {
            return priceMatrix;
        }

        Map<Long, Boolean> sessionRates = rates.stream().collect(Collectors.toMap(SessionRate::getId, RateBase::getDefaultRate));
        if (CollectionUtils.isNotEmpty(secondaryMarket)) {
            secondaryMarket.stream()
                    .filter(Objects::nonNull)
                    .map(SecondaryMarketSearch::getTicket)
                    .filter(Objects::nonNull)
                    .filter(ticket -> {
                        Double price = ticket.getPrice();
                        Double charges = ticket.getPromoterCharges();
                        return price != null && (charges == null || charges == 0);
                    })
                    .forEach(ticket -> {
                        double price = ticket.getPrice();
                        double updatedCharge = SurchargeUtils.calculateSecondaryMarketSurcharge(
                                price,
                                channelEventSurcharges,
                                false
                        );
                        ticket.setPromoterCharges(updatedCharge);
                    });
        }


        List<PriceZonePrice> pricesByPriceZone = prices.stream()
                .map(VenueTemplatePrice::getPriceZones)
                .flatMap(Collection::stream)
                .filter(zp -> priceZonesBySession.contains(zp.getId().longValue()))
                .collect(Collectors.toList());

        Double sessionTaxDivisor = TaxUtils.calculateTaxDivisor(sessionTaxes.stream().map(SessionTaxInfo::getValue).toList());
        Double surchargeTaxDivisor = TaxUtils.calculateTaxDivisor(surchargesTaxes.stream().map(SessionTaxInfo::getValue).toList());
        Double channelSurchargeTaxDivisor = TaxUtils.calculateTaxDivisor(channelSurchargesTaxes.stream().map(ChannelTaxInfo::getValue).toList());


        pricesByPriceZone.forEach(p -> {
            PriceZonePrices priceZonePrices = new PriceZonePrices();
            priceZonePrices.setPriceZoneId(p.getId().longValue());
            priceZonePrices.setRates(new ArrayList<>());
            List<RatePrice> filteredRates = p.getRates().stream()
                    .filter(r -> sessionRates.containsKey(r.getId()))
                    .collect(Collectors.toList());
            filteredRates.forEach((RatePrice rate) -> {
                Long rateId = rate.getId();
                RatePrices ratePrices = new RatePrices();
                ratePrices.setId(rateId);
                ratePrices.setDefaultRate(sessionRates.get(rateId));

                //Base surcharges
                ratePrices.setRatePrice(new Price());
                ratePrices.getRatePrice().setSurcharge(new PriceSurcharges());
                if (sessionDynamicPriceConfig != null && BooleanUtils.isTrue(sessionDynamicPriceConfig.getActive()) && CollectionUtils.isNotEmpty(sessionDynamicPriceConfig.getDynamicPriceZone())) {
                    Optional<DynamicPriceZone> optionalAssignmentZonePrice = sessionDynamicPriceConfig.getDynamicPriceZone().stream()
                            .filter(dynamicPriceZone -> Objects.equals(dynamicPriceZone.getIdPriceZone(), priceZonePrices.getPriceZoneId()))
                            .findFirst();
                    if (optionalAssignmentZonePrice.isPresent() && optionalAssignmentZonePrice.get().getActiveZone() != null && CollectionUtils.isNotEmpty(optionalAssignmentZonePrice.get().getDynamicPrices())) {
                        int activeZone = optionalAssignmentZonePrice.get().getActiveZone().intValue();
                        List<DynamicPrice> priceZoneAssignment = optionalAssignmentZonePrice.get().getDynamicPrices();
                        if (activeZone < priceZoneAssignment.size()) {
                            Optional<DynamicRatesPrice> optionalDynamicPriceRate = priceZoneAssignment.get(activeZone).getDynamicRatesPrice().stream()
                                    .filter(dynamicRatesPrice -> Objects.equals(dynamicRatesPrice.getId(), ratePrices.getId()))
                                    .findFirst();
                            if (optionalDynamicPriceRate.isPresent()) {
                                ratePrices.getRatePrice().setValue(optionalDynamicPriceRate
                                        .map(DynamicRatesPrice::getPrice).orElse(rate.getPrice()));
                                if (CollectionUtils.isNotEmpty(priceZoneAssignment.get(activeZone).getTranslations())) {
                                    priceZonePrices.setDynamicPriceTranslations(DynamicPriceConverter.toDynamicPriceTranslationDTOList(priceZoneAssignment.get(activeZone).getTranslations()));
                                }
                            } else {
                                ratePrices.getRatePrice().setValue(rate.getPrice());
                            }
                        } else {
                            ratePrices.getRatePrice().setValue(rate.getPrice());
                        }
                    } else {
                        ratePrices.getRatePrice().setValue(rate.getPrice());
                    }
                } else {
                    ratePrices.getRatePrice().setValue(rate.getPrice());
                }

                ratePrices.getRatePrice().getSurcharge().setPromoter(SurchargeUtils.calculatePromoterSurcharge(rate.getPrice(), channelEventSurcharges, false));
                ratePrices.getRatePrice().getSurcharge().setChannel(SurchargeUtils.calculateChannelSurcharge(rate.getPrice(), channelEventSurcharges, false));

                List<EventPromotion> automaticPromotions = findAutomaticPromotions(p);
                if (CollectionUtils.isNotEmpty(automaticPromotions)) {
                    List<PromotedPrice> promotedPrices = automaticPromotions.stream()
                            .map(promoPrice -> buildRatePromotedPrice(promoPrice, rate)).collect(Collectors.toList());
                    ratePrices.setPromotedPrices(promotedPrices);
                }
                priceZonePrices.getRates().add(ratePrices);
            });
            priceMatrix.getPrices().add(priceZonePrices);
            priceMatrix.getNetPrices().add(toNetPriceZonePrices(priceZonePrices, sessionTaxDivisor, surchargeTaxDivisor, channelSurchargeTaxDivisor));
        });
        ////base and promoted from calculation WE ONLY TAKE INTO ACCOUNT THE DEFAULT RATE

        List<SecondaryMarketSearch> secondaryMarketNetPrices = secondaryMarket != null ?
                secondaryMarket.stream().map(secondaryMarketSearch -> toNetSecondaryMarket(secondaryMarketSearch, sessionTaxDivisor, surchargeTaxDivisor, channelSurchargeTaxDivisor)).toList() :
                null;

        priceMatrix.setMinBasePrice(PriceMatrixCalculatorUtils.findMinPriceByDefaultRate(priceMatrix.getPrices(), secondaryMarket, Price::getValue));
        priceMatrix.setMinPromotedPrices(PriceMatrixCalculatorUtils.findMinPromotedPricesByDefaultRate(priceMatrix.getPrices(), PromotedPrice::getValue));

        priceMatrix.setMaxBasePrice(PriceMatrixCalculatorUtils.findMaxPriceByDefaultRate(priceMatrix.getPrices(), secondaryMarket, Price::getValue));

        priceMatrix.setMinNetPrice(PriceMatrixCalculatorUtils.findMinPriceByDefaultRate(priceMatrix.getNetPrices(), secondaryMarketNetPrices, Price::getValue));
        priceMatrix.setMinNetPromotedPrices(PriceMatrixCalculatorUtils.findMinPromotedPricesByDefaultRate(priceMatrix.getNetPrices(), PromotedPrice::getValue));
        priceMatrix.setMaxNetPrice(PriceMatrixCalculatorUtils.findMaxPriceByDefaultRate(priceMatrix.getNetPrices(), secondaryMarketNetPrices, Price::getValue));

        priceMatrix.setMinFinalPrice(PriceMatrixCalculatorUtils.findMinPriceByDefaultRate(priceMatrix.getPrices(), secondaryMarket, PriceMatrixCalculatorUtils::calculatePriceTotal));
        priceMatrix.setMinFinalPromotedPrices(PriceMatrixCalculatorUtils.findMinPromotedPricesByDefaultRate(priceMatrix.getPrices(), PriceMatrixCalculatorUtils::calculatePriceTotal));
        priceMatrix.setMaxFinalPrice(PriceMatrixCalculatorUtils.findMaxPriceByDefaultRate(priceMatrix.getPrices(), secondaryMarket, PriceMatrixCalculatorUtils::calculatePriceTotal));

        return priceMatrix;
    }

    private PriceZonePrices toNetPriceZonePrices(PriceZonePrices basePriceZone, Double sessionTaxDivisor, Double surchargeTaxDivisor, Double channelSurchargeTaxDivisor) {
        PriceZonePrices netPriceZone = new PriceZonePrices();
        netPriceZone.setPriceZoneId(basePriceZone.getPriceZoneId());
        netPriceZone.setRates(basePriceZone.getRates().stream().map(r -> toNetRate(r, sessionTaxDivisor, surchargeTaxDivisor, channelSurchargeTaxDivisor)).toList());
        return netPriceZone;
    }

    private RatePrices toNetRate(RatePrices baseRate, Double sessionTaxDivisor, Double surchargeTaxDivisor, Double channelSurchargeTaxDivisor) {
        RatePrices netRate = new RatePrices();
        netRate.setId(baseRate.getId());
        netRate.setDefaultRate(baseRate.getDefaultRate());

        Price netPrice = new Price();
        if (baseRate.getRatePrice() != null) {
            netPrice.setValue(NumberUtils.roundedDivide(baseRate.getRatePrice().getValue(), sessionTaxDivisor));
            if (baseRate.getRatePrice().getSurcharge() != null) {
                PriceSurcharges baseSurcharge = baseRate.getRatePrice().getSurcharge();
                PriceSurcharges netSurcharge = new PriceSurcharges();
                netSurcharge.setPromoter(NumberUtils.roundedDivide(baseSurcharge.getPromoter(), surchargeTaxDivisor));
                netSurcharge.setChannel(NumberUtils.roundedDivide(baseSurcharge.getChannel(), channelSurchargeTaxDivisor));
                netPrice.setSurcharge(netSurcharge);
            }
        }
        netRate.setRatePrice(netPrice);

        if (CollectionUtils.isNotEmpty(baseRate.getPromotedPrices())) {
            netRate.setPromotedPrices(baseRate.getPromotedPrices().stream().map(p -> toNetPromoted(p, sessionTaxDivisor, surchargeTaxDivisor, channelSurchargeTaxDivisor)).toList());
        }

        return netRate;
    }

    private PromotedPrice toNetPromoted(PromotedPrice basePromoted, Double sessionTaxDivisor, Double surchargeTaxDivisor, Double channelSurchargeTaxDivisor) {
        PromotedPrice netPromoted = new PromotedPrice();
        netPromoted.setEventPromotionTemplateId(basePromoted.getEventPromotionTemplateId());
        netPromoted.setVariationType(basePromoted.getVariationType());
        netPromoted.setValidationPeriod(basePromoted.getValidationPeriod());
        netPromoted.setVariationValue(basePromoted.getVariationValue());

        netPromoted.setValue(NumberUtils.roundedDivide(basePromoted.getValue(), sessionTaxDivisor));
        netPromoted.setOriginalPrice(NumberUtils.roundedDivide(basePromoted.getOriginalPrice(), sessionTaxDivisor));
        netPromoted.setDiscountedValue(NumberUtils.minus(netPromoted.getOriginalPrice(), netPromoted.getValue()));

        PriceSurcharges baseSurcharge = basePromoted.getSurcharge();
        if (baseSurcharge != null) {
            PriceSurcharges netSurcharge = new PriceSurcharges();
            netSurcharge.setPromoter(NumberUtils.roundedDivide(baseSurcharge.getPromoter(), surchargeTaxDivisor));
            netSurcharge.setChannel(NumberUtils.roundedDivide(baseSurcharge.getChannel(), channelSurchargeTaxDivisor));
            netPromoted.setSurcharge(netSurcharge);
        }
        return netPromoted;
    }

    private List<EventPromotion> findAutomaticPromotions(PriceZonePrice priceZonePrice) {
        return eventPromotions.stream()
                .filter(promo
                        -> sessionPromotions.contains(promo.getEventPromotionTemplateId())
                        && PromotionType.AUTOMATIC.equals(promo.getType())
                        && (CollectionUtils.isEmpty(promo.getRestrictions().getPriceZones()) || promo.getRestrictions().getPriceZones().contains(priceZonePrice.getId().longValue()))
                ).collect(Collectors.toList());
    }

    private PromotedPrice buildRatePromotedPrice(EventPromotion automaticPromotion, RatePrice r) {
        List<PromotionPriceVariationValue> value = automaticPromotion.getPriceVariation().getValue();
        //We sort values in DESC to get range easily
        List<PromotionPriceVariationValue> sortedValues = value.stream()
                .sorted((v1, v2) -> Double.compare(v2.getFrom(), v1.getFrom()))
                .collect(Collectors.toList());
        Double basePromotedPrice = Math.max(
                ZERO,
                sortedValues.stream()
                        .filter(v -> v.getFrom().compareTo(r.getPrice()) <= 0)
                        .findFirst()
                        .map(promotedValue -> {
                            if (PromotionPriceVariationType.FIXED.equals(automaticPromotion.getPriceVariation().getType())) {
                                return NumberUtils.minus(r.getPrice(), promotedValue.getValue());
                            } else if (PromotionPriceVariationType.PERCENTAGE.equals(automaticPromotion.getPriceVariation().getType())) {
                                return NumberUtils.minus(r.getPrice(), NumberUtils.roundedPercentageOf(r.getPrice(), promotedValue.getValue()));
                            } else if (PromotionPriceVariationType.NEW_BASE_PRICE.equals(automaticPromotion.getPriceVariation().getType())) {
                                return promotedValue.getValue();
                            }
                            return ZERO;
                        })
                        .orElse(ZERO)
        );

        Double promoterSurcharge = SurchargeUtils.calculatePromoterSurcharge(basePromotedPrice, channelEventSurcharges, automaticPromotion.getApplyPromoterSpecificCharges());
        Double channelSurcharge = SurchargeUtils.calculateChannelSurcharge(basePromotedPrice, channelEventSurcharges, automaticPromotion.getApplyChannelSpecificCharges());

        PromotedPrice promotedPrice = new PromotedPrice();
        promotedPrice.setEventPromotionTemplateId(automaticPromotion.getEventPromotionTemplateId());
        promotedPrice.setValue(basePromotedPrice);
        promotedPrice.setDiscountedValue(NumberUtils.minus(r.getPrice(), basePromotedPrice));
        promotedPrice.setSurcharge(new PriceSurcharges());
        promotedPrice.getSurcharge().setPromoter(promoterSurcharge);
        promotedPrice.getSurcharge().setChannel(channelSurcharge);
        promotedPrice.setOriginalPrice(r.getPrice());
        if (automaticPromotion.getRestrictions() != null && automaticPromotion.getRestrictions().getValidationPeriod() != null) {
            promotedPrice.setValidationPeriod(automaticPromotion.getRestrictions().getValidationPeriod());
        }
        if (automaticPromotion.getPriceVariation() != null) {
            promotedPrice.setVariationType(automaticPromotion.getPriceVariation().getType());
            if ((PromotionPriceVariationType.FIXED.equals(automaticPromotion.getPriceVariation().getType())
                    || PromotionPriceVariationType.PERCENTAGE.equals(automaticPromotion.getPriceVariation().getType()))
                    && CollectionUtils.isNotEmpty(automaticPromotion.getPriceVariation().getValue())
                    && automaticPromotion.getPriceVariation().getValue().size() == 1) {
                promotedPrice.setVariationValue(automaticPromotion.getPriceVariation().getValue().get(0).getValue());
            }
        }

        return promotedPrice;
    }

    private static SecondaryMarketSearch toNetSecondaryMarket(SecondaryMarketSearch secondaryMarketSearch, Double sessionTaxDivisor, Double surchargeTaxDivisor, Double channelSurchargeTaxDivisor) {

        if (secondaryMarketSearch == null || secondaryMarketSearch.getTicket() == null) {
            return null;
        }

        Ticket ticket = secondaryMarketSearch.getTicket();

        Ticket netTicket = new Ticket();
        netTicket.setPrice(NumberUtils.roundedDivide(ticket.getPrice(), Objects.requireNonNullElse(sessionTaxDivisor, 1d)));
        netTicket.setPromoterCharges(NumberUtils.roundedDivide(ticket.getPromoterCharges(), Objects.requireNonNullElse(surchargeTaxDivisor, 1d)));
        netTicket.setChannelCharges(NumberUtils.roundedDivide(ticket.getChannelCharges(), Objects.requireNonNullElse(channelSurchargeTaxDivisor, 1d)));

        SecondaryMarketSearch secondaryMarketSearchNet = new SecondaryMarketSearch();
        secondaryMarketSearchNet.setTicket(netTicket);

        return secondaryMarketSearchNet;

    }

    public PriceMatrixCalculator promotions(final List<EventPromotion> value) {
        this.eventPromotions = value;
        return this;
    }

    public PriceMatrixCalculator prices(final List<VenueTemplatePrice> value) {
        this.prices = value;
        return this;
    }

    public PriceMatrixCalculator channelEventSurcharges(final ChannelEventSurcharges value) {
        this.channelEventSurcharges = value;
        return this;
    }

    public PriceMatrixCalculator priceZonesBySession(final List<Long> value) {
        this.priceZonesBySession = value;
        return this;
    }

    public PriceMatrixCalculator rates(final List<SessionRate> value) {
        this.rates = value;
        return this;
    }

    public PriceMatrixCalculator eventPromotions(final List<EventPromotion> value) {
        this.eventPromotions = value;
        return this;
    }

    public PriceMatrixCalculator sessionPromotions(final List<Long> value) {
        this.sessionPromotions = value;
        return this;
    }

    public PriceMatrixCalculator secondaryMarket(final List<SecondaryMarketSearch> value) {
        this.secondaryMarket = value;
        return this;
    }

    public PriceMatrixCalculator sessionDynamicPriceConfig(final SessionDynamicPriceConfig value) {
        this.sessionDynamicPriceConfig = value;
        return this;
    }

    public PriceMatrixCalculator sessionTaxes(final List<SessionTaxInfo> value) {
        this.sessionTaxes = value;
        return this;
    }

    public PriceMatrixCalculator surchargesTaxes(final List<SessionTaxInfo> value) {
        this.surchargesTaxes = value;
        return this;
    }

    public PriceMatrixCalculator channelSurchargesTaxes(final List<ChannelTaxInfo> value) {
        this.channelSurchargesTaxes = value;
        return this;
    }
}
