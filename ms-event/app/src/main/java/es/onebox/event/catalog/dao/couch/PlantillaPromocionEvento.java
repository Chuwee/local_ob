package es.onebox.event.catalog.dao.couch;

import java.util.ArrayList;
import java.util.List;

public class PlantillaPromocionEvento {

    private Integer idPromocionEvento;
    private List<PromocionCondicionTarifa> condicionesTarifa = new ArrayList();

    public Integer getIdPromocionEvento() {
        return idPromocionEvento;
    }

    public void setIdPromocionEvento(Integer idPromocionEvento) {
        this.idPromocionEvento = idPromocionEvento;
    }

    public List<PromocionCondicionTarifa> getCondicionesTarifa() {
        return condicionesTarifa;
    }

    public void setCondicionesTarifa(List<PromocionCondicionTarifa> condicionesTarifa) {
        this.condicionesTarifa = condicionesTarifa;
    }
}
