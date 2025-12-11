package es.onebox.mgmt.events.tours;



import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.events.tours.dto.TourFilter;
import es.onebox.mgmt.events.tours.dto.TourSearchFilter;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;

public class ToursConverter {

    private ToursConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static TourFilter toMs(Long operatorId, TourSearchFilter filter) {
        if (filter == null) {
            return null;
        }

        if (operatorId == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Operator is mandatory field", null);
        }

        TourFilter tourFilter = new TourFilter();

        tourFilter.setEntityId(filter.getEntityId());
        tourFilter.setOperatorId(operatorId);

        if (filter.getStatus() != null) {
            tourFilter.setStatus(filter.getStatus().name());
        }

        tourFilter.setEntityAdminId(filter.getEntityAdminId());
        tourFilter.setLimit(filter.getLimit());
        tourFilter.setOffset(filter.getOffset());

        return tourFilter;
    }
}
