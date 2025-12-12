package es.onebox.ms.notification.datasources.ms.crm.dto;


import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CrmOrderResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 2514811642163018565L;

    private List<CrmOrderContainer> purchases;

    public List<CrmOrderContainer> getPurchases() {
        return purchases;
    }

    public void setPurchases(List<CrmOrderContainer> purchases) {
        this.purchases = purchases;
    }


}
