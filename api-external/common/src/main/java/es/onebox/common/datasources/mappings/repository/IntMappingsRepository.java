package es.onebox.common.datasources.mappings.repository;

import es.onebox.common.datasources.mappings.IntMappingsDatasource;
import es.onebox.common.datasources.mappings.dto.MappingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class IntMappingsRepository {

    @Autowired
    private IntMappingsDatasource intMappingsDatasource;

    public MappingResponse getOBSeatId(Long entityId, Integer capacityId, Integer matchId, Long externalSeatId) {
        return intMappingsDatasource.getOBSeatId(entityId, capacityId, matchId, externalSeatId);
    }

}
