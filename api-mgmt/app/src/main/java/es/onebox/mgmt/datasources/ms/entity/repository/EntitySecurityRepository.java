package es.onebox.mgmt.datasources.ms.entity.repository;

import es.onebox.mgmt.datasources.ms.entity.MsEntityDatasource;
import es.onebox.mgmt.datasources.ms.entity.dto.SecurityConfigDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.UpdateSecurityConfigRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class EntitySecurityRepository {
    private MsEntityDatasource msEntityDatasource;
    
    @Autowired
    public EntitySecurityRepository(MsEntityDatasource msEntityDatasource) {
        this.msEntityDatasource = msEntityDatasource;
    }

    public SecurityConfigDTO getEntitySecurityConfig(Long entityId) {
        return msEntityDatasource.getEntitySecurityConfig(entityId);
    }

    public void updateEntitySecurityConfig(Long entityId, UpdateSecurityConfigRequestDTO updateSecurityConfigRequestDTO) {
        msEntityDatasource.updateEntitySecurityConfig(entityId, updateSecurityConfigRequestDTO);
    }
}
