package es.onebox.event.events.dao;

import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelProvinciaRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import static es.onebox.jooq.cpanel.Tables.CPANEL_PROVINCIA;

@Repository
public class CountrySubdivisionDao extends DaoImpl<CpanelProvinciaRecord, Integer> {
    protected CountrySubdivisionDao() {
        super(Tables.CPANEL_PROVINCIA);
    }

    public IdNameCodeDTO getCountrySubInfo(Integer id) {
        return dsl.select(CPANEL_PROVINCIA.IDPROVINCIA, CPANEL_PROVINCIA.NOMBRE, CPANEL_PROVINCIA.CODIGO)
                .from(CPANEL_PROVINCIA)
                .where(CPANEL_PROVINCIA.IDPROVINCIA.eq(id))
                .fetchOne()
                .map(r -> new IdNameCodeDTO(
                        r.get(CPANEL_PROVINCIA.IDPROVINCIA).longValue(),
                        r.get(CPANEL_PROVINCIA.NOMBRE),
                        r.get(CPANEL_PROVINCIA.CODIGO)));
    }

}
