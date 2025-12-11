package es.onebox.event.events.utils;

import es.onebox.event.events.dto.TierCondition;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;

public class TierEvaluator {

    public static List<EvaluableTierWrapper> getActiveEventTiers(List<EvaluableTierWrapper> tiers) {

        Map<Integer, List<EvaluableTierWrapper>> tiersByPriceZone = tiers.stream()
                .collect(groupingBy(EvaluableTierWrapper::getPriceTypeId));

        Instant now = Instant.now();
        List<EvaluableTierWrapper> activeTiers = new ArrayList<>();

        for (List<EvaluableTierWrapper> tiersOfPriceZone : tiersByPriceZone.values()) {
            EvaluableTierWrapper tier = getActivePriceTypeTierAt(tiersOfPriceZone, now);
            if (tier != null) {
                activeTiers.add(tier);
            }
        }

        return activeTiers;
    }

    public static EvaluableTierWrapper getActivePriceTypeTierAt(List<EvaluableTierWrapper> tiersOfPriceZone, Instant date) {

        tiersOfPriceZone.sort(Comparator.comparing(EvaluableTierWrapper::getStartDate));

        EvaluableTierWrapper activeTier = null;
        for (EvaluableTierWrapper currentTier : tiersOfPriceZone) {
            // If the tier date is before the evaluating date this is the temporary active tier and we continue
            boolean datePrevious = currentTier.getStartDate().isBefore(date) || currentTier.getStartDate().equals(date);
            // Otherwise the only option that the current tier is the active one is that the
            // temporary active tier is 'stock or date' and that its stock is 0, we continue in case the
            // current tier happens to also meet this condition, which will be evaluated in next iteration
            boolean previousTierIsStockOrDateAndHasNoStock = nonNull(activeTier)
                    && nonNull(activeTier.getCondition())
                    && activeTier.getCondition().equals(TierCondition.STOCK_OR_DATE)
                    && nonNull(activeTier.getStock())
                    && activeTier.getStock().equals(0L);

            if (datePrevious || previousTierIsStockOrDateAndHasNoStock) {
                activeTier = currentTier;
                continue;
            }
            // Otherwise temporary active tier is the one
            break;
        }

        if (nonNull(activeTier)) {
            activeTier.setActive(true);
        }

        // So we can return it
        return activeTier;
    }

    public static EvaluableTierWrapper getActivePriceTypeTier(List<EvaluableTierWrapper> tiersOfPriceZone) {
        return getActivePriceTypeTierAt(tiersOfPriceZone, Instant.now());
    }

}
