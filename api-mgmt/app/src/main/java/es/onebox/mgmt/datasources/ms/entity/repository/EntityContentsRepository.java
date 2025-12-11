package es.onebox.mgmt.datasources.ms.entity.repository;

import es.onebox.mgmt.datasources.ms.channel.dto.contents.UpdateChannelTextBlocks;
import es.onebox.mgmt.datasources.ms.entity.MsEntityDatasource;
import es.onebox.mgmt.datasources.ms.entity.contents.EntityLiterals;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityTextBlock;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityTextBlockFilter;
import es.onebox.mgmt.entities.contents.dto.UpdateEntityTextBlocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class EntityContentsRepository {

    private final MsEntityDatasource msEntityDatasource;

    @Autowired
    public EntityContentsRepository(MsEntityDatasource msEntityDatasource) {
        this.msEntityDatasource = msEntityDatasource;
    }

    public EntityLiterals getEntityLiterals(Long entityId, String languageCode) {
        return this.msEntityDatasource.getEntityLiterals(entityId, languageCode);
    }

    public void createOrUpdateEntityLiterals(Long entityId, String languageCode, EntityLiterals body) {
        this.msEntityDatasource.createOrUpdateEntityLiterals(entityId, languageCode, body);
    }

    public List<EntityTextBlock> getEntityTextBlocks(Long channelId, EntityTextBlockFilter filter) {
        return this.msEntityDatasource.getEntityTextBlocks(channelId, filter);
    }

    public void updateEntityTextBlocks(Long entityId, UpdateEntityTextBlocks body) {
        this.msEntityDatasource.updateEntityTextBlocks(entityId, body);
    }
}
