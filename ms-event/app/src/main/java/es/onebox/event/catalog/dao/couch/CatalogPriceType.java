package es.onebox.event.catalog.dao.couch;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CatalogPriceType implements Serializable {

    @Serial
    private static final long serialVersionUID = -3725515646908112192L;

    private Long id;
    private String name;
    private CatalogPrice catalogPrice;
    private List<CatalogPriceSimulation> simulations;

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

    public List<CatalogPriceSimulation> getSimulations() {
        return simulations;
    }

    public void setSimulations(List<CatalogPriceSimulation> simulations) {
        this.simulations = simulations;
    }

    public CatalogPrice getPrice() {
        return catalogPrice;
    }

    public void setPrice(CatalogPrice catalogPrice) {
        this.catalogPrice = catalogPrice;
    }
}
