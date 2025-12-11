package es.onebox.mgmt.datasources.ms.collective.repository;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.collective.MsCollectiveDatasource;
import es.onebox.mgmt.datasources.ms.collective.dto.EntitiesCollective;
import es.onebox.mgmt.datasources.ms.collective.dto.MsCollectiveDetailDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.MsCollectivesDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.MsExternalValidatorsDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsCollectiveCreateDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsCollectiveRequest;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsCollectiveUpdateDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsEntitiesAssignationRequest;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsUpdateCollectiveStatusRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CollectivesRepository {

    private final MsCollectiveDatasource msCollectiveDatasource;

    @Autowired
    public CollectivesRepository(MsCollectiveDatasource msCollectiveDatasource) {
        this.msCollectiveDatasource = msCollectiveDatasource;
    }

    public MsCollectivesDTO getCollectives(MsCollectiveRequest request){
        return msCollectiveDatasource.getCollectives(request);
    }

    public MsCollectiveDetailDTO getCollective(Long collectiveId, Long operatorId, Long entityId, Long entityAdminId) {
        return msCollectiveDatasource.getCollective(collectiveId, operatorId, entityId, entityAdminId);
    }

    public IdDTO createCollective(MsCollectiveCreateDTO request) {
        return msCollectiveDatasource.createCollective(request);
    }

    public void updateCollective(Long collectiveId, MsCollectiveUpdateDTO msCollectiveUpdateDTO){
        msCollectiveDatasource.updateCollective(collectiveId, msCollectiveUpdateDTO);
    }

    public void deleteCollective(Long collectiveId) {
        msCollectiveDatasource.deleteCollective(collectiveId);
    }

    public void updateCollectiveStatus(Long collectiveId, MsUpdateCollectiveStatusRequest request) {
        msCollectiveDatasource.updateCollectiveStatus(collectiveId, request);
    }

    public MsExternalValidatorsDTO getExternalValidators() {
        return msCollectiveDatasource.getExternalValidators();
    }

    public EntitiesCollective getEntitiesAssignedToCollective(Long collectiveId) {
        return msCollectiveDatasource.getEntitiesAssignedToCollective(collectiveId);
    }

    public void assignEntitiesToCollective(Long collectiveId, MsEntitiesAssignationRequest request) {
        msCollectiveDatasource.assignEntitiesToCollective(collectiveId, request);
    }
}
