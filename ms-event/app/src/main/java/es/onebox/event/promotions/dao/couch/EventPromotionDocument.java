package es.onebox.event.promotions.dao.couch;

import es.onebox.couchbase.annotations.Id;
import es.onebox.event.promotions.dto.EventPromotion;

/**
 * @author ignasi
 */
public class EventPromotionDocument extends EventPromotion {

    private static final long serialVersionUID = -2581626070260784633L;

    @Id
    private Long eventPromotionTemplateId;
}
