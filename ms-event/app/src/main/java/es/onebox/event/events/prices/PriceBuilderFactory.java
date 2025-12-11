package es.onebox.event.events.prices;

import es.onebox.event.events.prices.enums.PriceBuilderType;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PriceBuilderFactory {

    private final Map<PriceBuilderType, PriceBuilder> priceBuilders;

    public PriceBuilderFactory(List<PriceBuilder> priceBuilderList) {
        this.priceBuilders = priceBuilderList.stream()
                .filter(priceBuilder -> priceBuilder.getType() != null)
                .collect(Collectors.toMap(PriceBuilder::getType, Function.identity()));
    }

    public PriceBuilder getPriceBuilder(PriceBuilderType type) {
        if (type == null) {
            return priceBuilders.get(PriceBuilderType.DEFAULT);
        }
        return priceBuilders.getOrDefault(type, priceBuilders.get(PriceBuilderType.DEFAULT));
    }
}
