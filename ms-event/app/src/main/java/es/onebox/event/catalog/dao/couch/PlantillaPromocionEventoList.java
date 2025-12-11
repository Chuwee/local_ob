package es.onebox.event.catalog.dao.couch;

import java.io.Serializable;
import java.util.List;

public class PlantillaPromocionEventoList implements Serializable {

    private static final long serialVersionUID = 1L;
    List<PlantillaPromocionEvento> promotionTemplates;

    public List<PlantillaPromocionEvento> getPromotionTemplates() {
        return this.promotionTemplates;
    }

    public void setPromotionTemplates(List<PlantillaPromocionEvento> promotionTemplates) {
        this.promotionTemplates = promotionTemplates;
    }
}