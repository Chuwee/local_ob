package es.onebox.event.catalog.elasticsearch.dto;

import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class RateCustomerTypesRestriction implements Serializable {

    @Serial
    private static final long serialVersionUID = 253609915189981338L;

    private List<IdNameCodeDTO> allowedCustomerTypes;

    public List<IdNameCodeDTO> getAllowedCustomerTypes() {
        return allowedCustomerTypes;
    }

    public void setAllowedCustomerTypes(List<IdNameCodeDTO> allowedCustomerTypes) {
        this.allowedCustomerTypes = allowedCustomerTypes;
    }
}
