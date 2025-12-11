package es.onebox.mgmt.datasources.ms.client.repositories;

import es.onebox.mgmt.datasources.ms.channel.MsChannelDatasource;
import es.onebox.mgmt.datasources.ms.client.dto.PhoneValidatorChannelConfig;
import es.onebox.mgmt.datasources.ms.client.dto.PhoneValidatorEntityConfig;
import es.onebox.mgmt.datasources.ms.entity.MsEntityDatasource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PhoneValidatorEntityRepository {

    private final MsEntityDatasource msEntityDatasource;
    private final MsChannelDatasource msChannelDatasource;

    @Autowired
    public PhoneValidatorEntityRepository(MsEntityDatasource msEntityDatasource, MsChannelDatasource msChannelDatasource) {
        this.msEntityDatasource = msEntityDatasource;
        this.msChannelDatasource = msChannelDatasource;
    }

    public PhoneValidatorEntityConfig getPhoneValidatorEntityConfiguration(Long entityId) {
        return msEntityDatasource.getPhoneValidatorEntityConfiguration(entityId);
    }

    public void updatePhoneValidatorEntityConfiguration(Long entityId, PhoneValidatorEntityConfig phoneValidatorEntityConfig) {
        msEntityDatasource.updatePhoneValidatorEntityConfiguration(entityId, phoneValidatorEntityConfig);
    }

    public PhoneValidatorChannelConfig getPhoneValidatorChannelConfiguration(Long channelId) {
        return msChannelDatasource.getPhoneValidatorChannelConfiguration(channelId);
    }

    public void updatePhoneValidatorChannelConfiguration(Long channelId, PhoneValidatorChannelConfig phoneValidatorChannelConfig) {
        msChannelDatasource.updatePhoneValidatorChannelConfiguration(channelId, phoneValidatorChannelConfig);
    }

}
