package es.onebox.mgmt.datasources.ms.channel.repositories;

import es.onebox.mgmt.datasources.ms.channel.MsChannelDatasource;
import es.onebox.mgmt.datasources.ms.channel.dto.taxes.ChannelSurchargesTaxes;
import es.onebox.mgmt.datasources.ms.channel.dto.taxes.ChannelSurchargesTaxesUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ChannelsTaxesRepository {

    private final MsChannelDatasource msChannelDatasource;

    @Autowired
    public ChannelsTaxesRepository(MsChannelDatasource msChannelDatasource) {
        this.msChannelDatasource = msChannelDatasource;
    }

    public ChannelSurchargesTaxes getChannelSurchargesTaxes(Long channelId) {
        return msChannelDatasource.getChannelSurchargesTaxes(channelId);
    }

    public void updateChannelSurchargesTaxes(Long channelId, ChannelSurchargesTaxesUpdate update) {
        msChannelDatasource.updateChannelSurchargesTaxes(channelId, update);
    }

}
