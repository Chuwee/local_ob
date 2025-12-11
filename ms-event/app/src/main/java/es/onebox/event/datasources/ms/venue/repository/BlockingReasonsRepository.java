package es.onebox.event.datasources.ms.venue.repository;

import es.onebox.event.datasources.ms.venue.MsVenueDatasource;
import es.onebox.event.datasources.ms.venue.dto.BlockingReason;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BlockingReasonsRepository {

    private final MsVenueDatasource msVenueDatasource;

    @Autowired
    public BlockingReasonsRepository(MsVenueDatasource msVenueDatasource) {
        this.msVenueDatasource = msVenueDatasource;
    }

    public List<BlockingReason> getBlockingReasons(Long venueTemplateId) {
        return msVenueDatasource.getBlockingReasons(venueTemplateId);
    }
}
