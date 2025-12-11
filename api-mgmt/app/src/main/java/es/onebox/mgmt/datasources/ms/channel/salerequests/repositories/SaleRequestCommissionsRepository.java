package es.onebox.mgmt.datasources.ms.channel.salerequests.repositories;

import es.onebox.mgmt.channels.commissions.dto.CommissionTypeDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelCommission;
import es.onebox.mgmt.datasources.ms.channel.salerequests.MsSaleRequestsDatasource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SaleRequestCommissionsRepository {

    private MsSaleRequestsDatasource msSaleRequestsDatasource;

    @Autowired
    public SaleRequestCommissionsRepository(MsSaleRequestsDatasource msSaleRequestsDatasource) {
        this.msSaleRequestsDatasource = msSaleRequestsDatasource;
    }

    public List<ChannelCommission> getSaleRequestCommissions(Long saleRequestId, List<CommissionTypeDTO> types) {
        return msSaleRequestsDatasource.getSaleRequestCommissions(saleRequestId, types);
    }

    public void updateSaleRequestCommissions(Long saleRequestId, List<ChannelCommission> commissionListDto) {
        msSaleRequestsDatasource.updateSaleRequestCommissions(saleRequestId, commissionListDto);
    }
}
