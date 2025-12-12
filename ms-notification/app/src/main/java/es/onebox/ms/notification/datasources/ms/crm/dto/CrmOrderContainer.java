package es.onebox.ms.notification.datasources.ms.crm.dto;

import java.util.List;


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
