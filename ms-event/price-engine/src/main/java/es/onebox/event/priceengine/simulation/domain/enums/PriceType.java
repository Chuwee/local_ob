package es.onebox.event.priceengine.simulation.domain.enums;

import es.onebox.event.priceengine.simulation.domain.PriceSimulation;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PriceType implements Serializable {

    @Serial
    private static final long serialVersionUID = -3725515646908112192L;

    private Long id;
    private String name;
    private List<PriceSimulation> simulations;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PriceSimulation> getSimulations() {
        return simulations;
    }

    public void setSimulations(List<PriceSimulation> simulations) {
        this.simulations = simulations;
    }
}
