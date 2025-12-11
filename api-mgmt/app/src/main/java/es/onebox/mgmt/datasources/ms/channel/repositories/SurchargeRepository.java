package es.onebox.mgmt.datasources.ms.channel.repositories;

import es.onebox.mgmt.common.surcharges.dto.SurchargeTypeDTO;
import es.onebox.mgmt.datasources.common.dto.Surcharge;
import es.onebox.mgmt.datasources.ms.channel.MsChannelDatasource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SurchargeRepository {

    private final MsChannelDatasource msChannelDatasource;

    @Autowired
    public SurchargeRepository(MsChannelDatasource msChannelDatasource) {
        this.msChannelDatasource = msChannelDatasource;
    }

    public void setSurcharge(long channelId, List<Surcharge> requestDTO) {
        msChannelDatasource.setSurcharge(channelId, requestDTO);
    }

    public List<Surcharge> getChannelRanges(Long channelId, List<SurchargeTypeDTO> types) {
        return msChannelDatasource.getChannelRanges(channelId, types, null);
    }

}