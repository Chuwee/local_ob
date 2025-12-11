package es.onebox.event.sessions.converter;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.sessions.dao.record.ZonaPreciosConfigRecord;
import es.onebox.event.sessions.dto.PriceTypeAdditionalConfigDTO;
import es.onebox.event.sessions.dto.PriceTypeDTO;

public class PriceTypeConverter {

    private PriceTypeConverter() {
    }

    public static PriceTypeDTO toPriceTypeConfigDTO(ZonaPreciosConfigRecord zonaPreciosConfigRecord) {
        PriceTypeDTO result = new PriceTypeDTO();

        result.setId(zonaPreciosConfigRecord.getIdzona().longValue());
        result.setName(zonaPreciosConfigRecord.getDescripcion());
        result.setAdditionalConfig(new PriceTypeAdditionalConfigDTO());
        result.getAdditionalConfig().setRestrictiveAccess(CommonUtils.isTrue(zonaPreciosConfigRecord.getRestrictiveaccess()));
        result.getAdditionalConfig().setGateId(zonaPreciosConfigRecord.getGateId());

        return result;
    }

}
