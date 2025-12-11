package es.onebox.event.venues.dao;

import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelProviderVenuesRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_PROVIDER_VENUES;

@Repository
public class ProviderVenueDao extends DaoImpl<CpanelProviderVenuesRecord, Integer> {

    protected ProviderVenueDao() {
        super(CPANEL_PROVIDER_VENUES);
    }

    public List<IdNameCodeDTO> getProviderVenues(String provider) {
        return dsl.select(CPANEL_PROVIDER_VENUES.fields())
                .from(CPANEL_PROVIDER_VENUES)
                .where(CPANEL_PROVIDER_VENUES.PROVIDER.eq(provider))
                .fetch(this::mapToVenueRecord);
    }

    private IdNameCodeDTO mapToVenueRecord(Record record) {
        IdNameCodeDTO idNameCodeDTO = new IdNameCodeDTO();
        idNameCodeDTO.setId(record.get(CPANEL_PROVIDER_VENUES.ID).longValue());
        idNameCodeDTO.setCode(record.get(CPANEL_PROVIDER_VENUES.EXTERNAL_VENUE_ID));
        idNameCodeDTO.setName(record.get(CPANEL_PROVIDER_VENUES.NAME));
        return idNameCodeDTO;
    }

}
