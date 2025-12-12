package es.onebox.common.datasources.dispatcher.repositoty;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.common.datasources.dispatcher.IntDispatcherDatasource;
import es.onebox.common.datasources.dispatcher.dto.CheckStatusResponse;
import es.onebox.common.datasources.dispatcher.dto.PartnerInfoConnectorRequest;
import es.onebox.common.datasources.dispatcher.dto.PartnerInfoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class IntAvetDispatcherRepository {

    @Autowired
    private IntDispatcherDatasource intDispatcherDatasource;

    @Cached(key = "ClubConfig", expires = 30)
    public CheckStatusResponse checkStatus(@CachedArg String clubCode) {
        return intDispatcherDatasource.checkStatus(clubCode);
    }

    @Cached(key = "getPartnerInformation", expires = 1, timeUnit = TimeUnit.MINUTES)
    public PartnerInfoResponse getPartnerInformation(@CachedArg Long entityId, @CachedArg String memberId,
                                                     @CachedArg String memberPass, @CachedArg Integer capacityId) {
        PartnerInfoConnectorRequest partnerInfoConnectorRequest = new PartnerInfoConnectorRequest();
        partnerInfoConnectorRequest.setEntityId(entityId);
        partnerInfoConnectorRequest.setMemberId(memberId);
        partnerInfoConnectorRequest.setPartnerPass(memberPass);
        partnerInfoConnectorRequest.setCapacityId(capacityId);
        return intDispatcherDatasource.getPartnerInformation(partnerInfoConnectorRequest);
    }

}