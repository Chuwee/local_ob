package es.onebox.mgmt.datasources.ms.channel.salerequests.repositories;


import es.onebox.mgmt.datasources.ms.channel.dto.taxes.SaleRequestSurchargesTaxes;
import es.onebox.mgmt.datasources.ms.channel.dto.taxes.SaleRequestSurchargesTaxesUpdate;
import es.onebox.mgmt.datasources.ms.channel.salerequests.MsSaleRequestsDatasource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SaleRequestsTaxesRepository {

    private final MsSaleRequestsDatasource msSaleRequestsDatasource;

    @Autowired
    public SaleRequestsTaxesRepository(MsSaleRequestsDatasource msSaleRequestsDatasource) {
        this.msSaleRequestsDatasource = msSaleRequestsDatasource;
    }

    public SaleRequestSurchargesTaxes getSaleRequestSurchargesTaxes(Long saleRequestId) {
        return msSaleRequestsDatasource.getSaleRequestSurchargesTaxes(saleRequestId);
    }

    public void updateSaleRequestSurchargesTaxes(Long saleRequestId, SaleRequestSurchargesTaxesUpdate update) {
        msSaleRequestsDatasource.updateSaleRequestSurchargesTaxes(saleRequestId, update);
    }

}
