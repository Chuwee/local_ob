package es.onebox.event.priceengine.simulation.service;

import es.onebox.event.priceengine.simulation.domain.BasePromotion;
import es.onebox.event.priceengine.simulation.domain.Price;
import es.onebox.event.priceengine.simulation.domain.PriceSimulation;
import es.onebox.event.priceengine.simulation.domain.Rate;
import es.onebox.event.priceengine.simulation.domain.Surcharge;
import es.onebox.event.priceengine.simulation.domain.VenueConfigBase;
import es.onebox.event.priceengine.simulation.domain.VenueConfigPricesSimulation;
import es.onebox.event.priceengine.simulation.domain.enums.PriceType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class PriceEngineSimulationAbstractTest {

    void validateListOfVenueConfigPricesSimulation(List<VenueConfigPricesSimulation> actual,
                                                   List<VenueConfigPricesSimulation> expected) {
        assertEquals(actual.size(), expected.size());
        for (int i = 0; i < actual.size(); i++) {
            validateVenueConfigPricesSimulation(actual.get(i), expected.get(i));
        }
    }

    private void validateVenueConfigPricesSimulation(VenueConfigPricesSimulation actual, VenueConfigPricesSimulation expected) {
        validateVenueConfig(actual.getVenueConfig(), expected.getVenueConfig());
        validateRates(actual.getRates(), expected.getRates());
    }

    private void validateVenueConfig(VenueConfigBase actual, VenueConfigBase expected) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }

    private void validateRates(List<Rate> actual, List<Rate> expected) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < actual.size(); i++) {
            validateRate(actual.get(i), expected.get(i));
        }
    }

    private void validateRate(Rate actual, Rate expected) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        validatePriceTypes(actual.getPriceTypes(), expected.getPriceTypes());
    }

    private void validatePriceTypes(List<PriceType> actual, List<PriceType> expected) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < actual.size(); i++) {
            validatePriceType(actual.get(i), expected.get(i));
        }
    }

    private void validatePriceType(PriceType actual, PriceType expected) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        validateSimulations(actual.getSimulations(), expected.getSimulations());
    }

    private void validateSimulations(List<PriceSimulation> actual, List<PriceSimulation> expected) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < actual.size(); i++) {
            validateSimulation(actual.get(i), expected.get(i));
        }
    }

    private void validateSimulation(PriceSimulation actual, PriceSimulation expected) {
        validatePrice(actual.getPrice(), expected.getPrice());
        if (CollectionUtils.isNotEmpty(expected.getBasePromotions())) {
            validatePromotions(actual.getBasePromotions(), expected.getBasePromotions());
        }
    }

    private void validatePromotions(List<BasePromotion> actual, List<BasePromotion> expected) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < actual.size(); i++) {
            validatePromotion(actual.get(i), expected.get(i));
        }
    }

    private void validatePromotion(BasePromotion actual, BasePromotion expected) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getType().name(), actual.getType().name());
    }

    private void validatePrice(Price actual, Price expected) {
        assertEquals(expected.getBase(), actual.getBase());
        assertEquals(expected.getTotal(), actual.getTotal());
        if (CollectionUtils.isNotEmpty(expected.getSurcharges())) {
            validateSurcharges(actual.getSurcharges(), expected.getSurcharges());
        }
    }

    private void validateSurcharges(List<Surcharge> actual, List<Surcharge> expected) {
        assertEquals(actual.size(), expected.size());
        for (int i = 0; i < actual.size(); i++) {
            validateSurcharge(actual.get(i), expected.get(i));
        }
    }

    private void validateSurcharge(Surcharge actual, Surcharge expected) {
        assertEquals(expected.getValue(), actual.getValue());
        assertEquals(expected.getType().name(), actual.getType().name());
    }

}
