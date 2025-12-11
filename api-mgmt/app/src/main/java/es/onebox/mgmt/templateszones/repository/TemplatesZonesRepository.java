package es.onebox.mgmt.templateszones.repository;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.entity.MsEntityDatasource;
import es.onebox.mgmt.datasources.ms.entity.TemplatesZonesResponse;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityTextBlock;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityTextBlockFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.TemplateZones;
import es.onebox.mgmt.datasources.ms.entity.dto.TemplatesZonesRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.TemplatesZonesRequestFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.TemplatesZonesUpdateRequest;
import es.onebox.mgmt.entities.contents.dto.UpdateEntityTextBlocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TemplatesZonesRepository {

    private final MsEntityDatasource msEntityDatasource;

    @Autowired
    public TemplatesZonesRepository(MsEntityDatasource msEntityDatasource) {
       this.msEntityDatasource = msEntityDatasource;
    }

    public TemplatesZonesResponse getTemplatesZones(Integer entityId, TemplatesZonesRequestFilter filter) {
        return msEntityDatasource.getTemplatesZones(entityId, filter);
    }

    public IdDTO createTemplateZones(Integer entityId, TemplatesZonesRequest request) {
        return msEntityDatasource.createTemplateZones(entityId, request);
    }

    public TemplateZones getTemplateZones(Integer entityId, Integer templateZonesId) {
        return msEntityDatasource.getTemplateZones(entityId, templateZonesId);
    }

    public void updateTemplateZones(Integer entityId, Integer templateZonesId, TemplatesZonesUpdateRequest updateDTO) {
        msEntityDatasource.updateTemplateZones(entityId, templateZonesId, updateDTO);
    }

    public void deleteTemplateZones(Integer entityId, Integer templateZonesId) {
        msEntityDatasource.deleteTemplateZones(entityId, templateZonesId);
    }

    public List<EntityTextBlock> getTextBlocks(Long channelId, Long templateZoneId, EntityTextBlockFilter filter) {
        return this.msEntityDatasource.getEntityTemplateZoneTextBlocks(channelId, templateZoneId, filter);
    }

    public void updateTextBlocks(Long entityId, Long templateZoneId, UpdateEntityTextBlocks body) {
        this.msEntityDatasource.updateEntityTemplateZonesTextBlocks(entityId, templateZoneId, body);
    }
}
