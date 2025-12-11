package es.onebox.event.venues.dao;

import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelProviderVenueTemplatesRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_PROVIDER_VENUE_TEMPLATES;

@Repository
public class ProviderVenueTemplateDao extends DaoImpl<CpanelProviderVenueTemplatesRecord, Integer> {

    protected ProviderVenueTemplateDao() {
        super(CPANEL_PROVIDER_VENUE_TEMPLATES);
    }

    public List<IdNameCodeDTO> getProviderVenueTemplates(String provider) {
        return dsl.select(CPANEL_PROVIDER_VENUE_TEMPLATES.fields())
                .from(CPANEL_PROVIDER_VENUE_TEMPLATES)
                .where(CPANEL_PROVIDER_VENUE_TEMPLATES.PROVIDER.eq(provider))
                .fetch(this::mapToVenueTemplateRecord);
    }

    private IdNameCodeDTO mapToVenueTemplateRecord(Record record) {
        IdNameCodeDTO idNameCodeDTO = new IdNameCodeDTO();
        idNameCodeDTO.setId(record.get(CPANEL_PROVIDER_VENUE_TEMPLATES.ID).longValue());
        idNameCodeDTO.setCode(record.get(CPANEL_PROVIDER_VENUE_TEMPLATES.EXTERNAL_VENUE_TEMPLATE_ID));
        idNameCodeDTO.setName(record.get(CPANEL_PROVIDER_VENUE_TEMPLATES.NAME));
        return idNameCodeDTO;
    }

}
