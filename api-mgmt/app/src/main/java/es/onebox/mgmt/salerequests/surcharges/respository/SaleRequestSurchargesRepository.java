package es.onebox.mgmt.salerequests.surcharges.respository;

import es.onebox.mgmt.datasources.common.dto.Surcharge;
import es.onebox.mgmt.datasources.common.enums.SurchargeType;
import es.onebox.mgmt.datasources.ms.channel.salerequests.MsSaleRequestsDatasource;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestSurchargesExtendedDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SaleRequestSurchargesRepository {

    private final MsSaleRequestsDatasource msSaleRequestsDatasource;

    @Autowired
    public SaleRequestSurchargesRepository(MsSaleRequestsDatasource msSaleRequestsDatasource) {
        this.msSaleRequestsDatasource = msSaleRequestsDatasource;
    }

    public MsSaleRequestSurchargesExtendedDTO saleRequestSurcharges(Long saleRequestId, List<SurchargeType> types) {
        return msSaleRequestsDatasource.saleRequestSurcharges(saleRequestId, types);
    }

    public void updateSaleRequestSurcharges(Long saleRequestId, List<Surcharge> surcharges) {
        msSaleRequestsDatasource.updateSaleRequestSurcharges(saleRequestId, surcharges);
    }

}
