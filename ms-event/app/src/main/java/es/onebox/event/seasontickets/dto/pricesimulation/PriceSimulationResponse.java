package es.onebox.event.seasontickets.dto.pricesimulation;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.event.priceengine.simulation.domain.VenueConfigPricesSimulation;
import java.io.Serial;

public class PriceSimulationResponse extends BaseResponseCollection<VenueConfigPricesSimulation, Metadata> {

    @Serial
    private static final long serialVersionUID = 1L;


}
