package es.onebox.mgmt.datasources.ms.channel.commission.repositories;

import es.onebox.mgmt.channels.commissions.dto.CommissionTypeDTO;
import es.onebox.mgmt.datasources.ms.channel.commission.ms.MsCommissionDatasource;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelCommission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommissionRepository {

    private final MsCommissionDatasource msCommissionDatasource;

    @Autowired
    public CommissionRepository(MsCommissionDatasource msCommissionDatasource) {
        this.msCommissionDatasource = msCommissionDatasource;
    }

    public void setCommission(long channelId, List<ChannelCommission> requestDTO) {
        msCommissionDatasource.setCommission(channelId, requestDTO);
    }

    public List<ChannelCommission> getChannelCommissions(Long channelId, List<CommissionTypeDTO> types) {
        return msCommissionDatasource.getChannelCommissions(channelId, types, null);
    }
}
