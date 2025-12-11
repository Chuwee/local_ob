package es.onebox.mgmt.salerequests.delivery;

import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.SaleRequestDelivery;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.UpdateSaleRequestDelivery;
import es.onebox.mgmt.salerequests.delivery.dto.SaleRequestDeliveryDTO;
import es.onebox.mgmt.salerequests.delivery.dto.UpdateSaleRequestDeliveryDTO;

public class SaleRequestDeliveryConverter {

    private SaleRequestDeliveryConverter() {

    }

    public static SaleRequestDeliveryDTO toDTO(SaleRequestDelivery in) {
        if (in == null) {
            return null;
        }
        SaleRequestDeliveryDTO out = new SaleRequestDeliveryDTO();
        out.setTicketHandling(in.getTicketHandling());
        return out;
    }

    public static UpdateSaleRequestDelivery toEntity(UpdateSaleRequestDeliveryDTO in) {
        if (in == null) {
            return null;
        }
        UpdateSaleRequestDelivery out = new UpdateSaleRequestDelivery();
        out.setTicketHandling(in.getTicketHandling());
        return out;
    }

}
