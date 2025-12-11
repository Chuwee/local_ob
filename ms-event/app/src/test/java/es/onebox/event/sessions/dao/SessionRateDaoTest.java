package es.onebox.event.sessions.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelSesionTarifaRecord;
import es.onebox.jooq.dao.test.DaoImplTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;

import static es.onebox.jooq.cpanel.tables.CpanelSesionTarifa.CPANEL_SESION_TARIFA;

public class SessionRateDaoTest extends DaoImplTest {

    @InjectMocks
    SessionRateDao sessionRateDao;

    @Override
    protected String getDatabaseFile() {
        return "dao/SessionRateDao.sql";
    }

    @Test
    public void createSessionRateRelationship() {

        Integer expectedInsertedRows = 1;
        Integer sessionId = 100;
        Integer rateId = 200;

        sessionRateDao.createSessionRateRelationship(sessionId, rateId);
        List<CpanelSesionTarifaRecord> record = dsl.select(CPANEL_SESION_TARIFA.fields())
                .from(CPANEL_SESION_TARIFA)
                .where(CPANEL_SESION_TARIFA.IDTARIFA.eq(rateId))
                .and(CPANEL_SESION_TARIFA.IDSESION.eq(sessionId))
                .fetchInto(CpanelSesionTarifaRecord.class);

        Assertions.assertEquals(record.size(), expectedInsertedRows);
        Assertions.assertEquals(record.get(0).getIdsesion(), sessionId);
        Assertions.assertEquals(record.get(0).getIdtarifa(), rateId);
        Assertions.assertTrue(record.get(0).getDefecto());
    }


}
