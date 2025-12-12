package es.onebox.fcb.datasources.salesforce.repository;

import es.onebox.fcb.datasources.salesforce.AuthSalesforceDatasource;
import es.onebox.fcb.datasources.salesforce.SalesforceDatasource;
import es.onebox.fcb.datasources.salesforce.dto.RequestAbandonedOrderDTO;
import es.onebox.fcb.datasources.salesforce.dto.ResponseTokenDTO;
import org.springframework.stereotype.Repository;

@Repository
public class SalesforceRepository {
    private AuthSalesforceDatasource authSalesforceDatasource;
    private SalesforceDatasource salesforceDatasource;

    public SalesforceRepository(AuthSalesforceDatasource authSalesforceDatasource, SalesforceDatasource salesforceDatasource) {
        this.authSalesforceDatasource = authSalesforceDatasource;
        this.salesforceDatasource = salesforceDatasource;
    }

    public String login() {
        ResponseTokenDTO responseTokenDTO = authSalesforceDatasource.getToken();
        return responseTokenDTO.getAccessToken();
    }

    public void storeAbandonedOrder(String transactionId, RequestAbandonedOrderDTO requestAbandonedOrder) {
        salesforceDatasource.storeAbandonedOrder(transactionId, requestAbandonedOrder, login());
    }

}
