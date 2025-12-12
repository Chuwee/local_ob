package es.onebox.common.datasources.ms.crm.dto;

import java.util.List;


/**
 * User: cgalindo
 * Date: 16/10/15
 */
public class CrmOrderContainer {

    private CrmOrderDocResponse order;

    private List<CrmProductDocResponse> products;

    public CrmOrderDocResponse getOrder() {
        return order;
    }

    public void setOrder(CrmOrderDocResponse order) {
        this.order = order;
    }

    public List<CrmProductDocResponse> getProducts() {
        return products;
    }

    public void setProducts(List<CrmProductDocResponse> products) {
        this.products = products;
    }
}
