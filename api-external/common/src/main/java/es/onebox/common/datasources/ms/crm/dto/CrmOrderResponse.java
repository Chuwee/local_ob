package es.onebox.common.datasources.ms.crm.dto;

import java.util.List;

public class CrmOrderResponse extends CrmResponse {

    private CrmOrderParams parameters;
    private List<CrmOrderContainer> purchases;

    public CrmOrderParams getParameters() {
        return parameters;
    }

    public void setParameters(CrmOrderParams parameters) {
        this.parameters = parameters;
    }

    public List<CrmOrderContainer> getPurchases() {
        return purchases;
    }

    public void setPurchases(List<CrmOrderContainer> purchases) {
        this.purchases = purchases;
    }


}
