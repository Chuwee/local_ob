package es.onebox.mgmt.salerequests.pricesimulation.dto;

import java.io.Serializable;
import java.util.List;

public class PriceTypeDTO implements Serializable {

    private static final long serialVersionUID = -5861063670076797589L;

    private Long id;
    private String name;
    private List<PriceSimulationDTO> simulations;

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

    public List<PriceSimulationDTO> getSimulations() {
        return simulations;
    }

    public void setSimulations(List<PriceSimulationDTO> simulations) {
        this.simulations = simulations;
    }
}
