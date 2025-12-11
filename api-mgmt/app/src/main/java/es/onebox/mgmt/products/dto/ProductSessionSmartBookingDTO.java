package es.onebox.mgmt.products.dto;

import es.onebox.mgmt.sessions.enums.SessionSmartBookingType;

public class ProductSessionSmartBookingDTO {

    private SessionSmartBookingType type;

    public SessionSmartBookingType getType() {
        return type;
    }

    public void setType(SessionSmartBookingType type) {
        this.type = type;
    }
}
