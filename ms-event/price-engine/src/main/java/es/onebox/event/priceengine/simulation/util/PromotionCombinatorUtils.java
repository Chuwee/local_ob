package es.onebox.event.priceengine.simulation.util;

import es.onebox.event.priceengine.simulation.converter.PromotionsEventConvert;
import es.onebox.event.priceengine.simulation.domain.Promotion;
import es.onebox.event.priceengine.simulation.domain.PromotionsCombines;
import es.onebox.event.priceengine.simulation.domain.PromotionsEvent;
import es.onebox.event.priceengine.simulation.record.EventPromotionRecord;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PromotionCombinatorUtils {

    private PromotionCombinatorUtils() {throw new UnsupportedOperationException("Cannot instantiate utilities class");}

    public static PromotionsCombines combinations(List<EventPromotionRecord> records) {
        PromotionsEvent promotionsEvent = PromotionsEventConvert.convertToPromotionsEvent(records);
        PromotionsCombines combines = new PromotionsCombines();
        fillNotCumulative(combines, promotionsEvent);
        fillIndividualPromo(combines, promotionsEvent);
        fillCumulative(combines, promotionsEvent);
        return combines;
    }

    private static void fillIndividualPromo(PromotionsCombines combines, PromotionsEvent promotionsEvent) {
        fillUnique(combines, promotionsEvent.getAutomatics());
        fillUnique(combines, promotionsEvent.getDiscounts());
        fillUnique(combines, promotionsEvent.getPromotions());
    }

    private static void fillNotCumulative(PromotionsCombines combines, PromotionsEvent promotionsEvent) {
        fillUnique(combines, promotionsEvent.getNotCumulative());
    }

    private static void fillUnique(PromotionsCombines combines, List<Promotion> listPromotion) {
        if (CollectionUtils.isNotEmpty(listPromotion)) {
            listPromotion.stream().map(Collections::singletonList).forEach(combines::add);
        }
    }

    private static void fillCumulative(PromotionsCombines combines, PromotionsEvent promotionsEvent) {
        boolean automaticsNotEmpty = CollectionUtils.isNotEmpty(promotionsEvent.getAutomatics());
        boolean discountsNotEmpty = CollectionUtils.isNotEmpty(promotionsEvent.getDiscounts());
        boolean promotionsNotEmpty = CollectionUtils.isNotEmpty(promotionsEvent.getPromotions());
        if (automaticsNotEmpty && discountsNotEmpty && promotionsNotEmpty) {
            combines.addAll(combinesPairOfList(promotionsEvent.getAutomatics(), promotionsEvent.getDiscounts()));
            combines.addAll(combinesPairOfList(promotionsEvent.getAutomatics(), promotionsEvent.getPromotions()));
            combines.addAll(combinesPairOfList(promotionsEvent.getDiscounts(), promotionsEvent.getPromotions()));
            combines.addAll(combineAllPromotionsByTypes(promotionsEvent.getAutomatics(), promotionsEvent.getDiscounts(), promotionsEvent.getPromotions()));
        } else if (automaticsNotEmpty && discountsNotEmpty) {
            combines.addAll(combinesPairOfList(promotionsEvent.getAutomatics(), promotionsEvent.getDiscounts()));
        } else if (automaticsNotEmpty && promotionsNotEmpty) {
            combines.addAll(combinesPairOfList(promotionsEvent.getAutomatics(), promotionsEvent.getPromotions()));
        }else if (promotionsNotEmpty && discountsNotEmpty) {
            combines.addAll(combinesPairOfList(promotionsEvent.getDiscounts(), promotionsEvent.getPromotions()));
        }
    }

    private static PromotionsCombines combineAllPromotionsByTypes(List<Promotion> automatics, List<Promotion> discounts, List<Promotion> promotions) {
        PromotionsCombines combines = new PromotionsCombines();
        PromotionsCombines pairCombines = combinesPairOfList(automatics, discounts);

        List<Promotion> listPromotion;
        for (Promotion promotion : promotions) {
            for(List<Promotion> promo : pairCombines) {
                listPromotion = new ArrayList<>(promo);
                listPromotion.add(promotion);
                combines.add(listPromotion);
            }
        }
        return combines;
    }

    private static PromotionsCombines combinesPairOfList(List<Promotion> listA, List<Promotion> listB) {
        PromotionsCombines combines = new PromotionsCombines();
        listA.stream().map(promo -> combinesItemWithList(promo, listB)).forEach(combines::addAll);
        return combines;
    }

    private static PromotionsCombines combinesItemWithList(Promotion promo, List<Promotion> list) {
        PromotionsCombines combines = new PromotionsCombines();
        List<Promotion> listCombines;
        for (Promotion promotion : list) {
            listCombines = new ArrayList<>();
            listCombines.add(promo);
            listCombines.add(promotion);
            combines.add(listCombines);
        }
        return combines;
    }

}
