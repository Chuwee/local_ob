package es.onebox.event.sessions.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelPreventaLoyaltyProgramRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import static es.onebox.jooq.cpanel.tables.CpanelPreventaLoyaltyProgram.CPANEL_PREVENTA_LOYALTY_PROGRAM;

@Repository
public class PresaleLoyaltyProgramDao extends DaoImpl<CpanelPreventaLoyaltyProgramRecord, Integer> {

    protected PresaleLoyaltyProgramDao() {
        super(CPANEL_PREVENTA_LOYALTY_PROGRAM);
    }

    public CpanelPreventaLoyaltyProgramRecord getByPresaleId(Long presaleId) {
        return dsl.select()
                .from(CPANEL_PREVENTA_LOYALTY_PROGRAM)
                .where(CPANEL_PREVENTA_LOYALTY_PROGRAM.IDPREVENTA.eq(presaleId.intValue()))
                .fetchOneInto(CpanelPreventaLoyaltyProgramRecord.class);
    }

    public void deleteByPresaleId(Integer presaleId) {
        dsl.delete(CPANEL_PREVENTA_LOYALTY_PROGRAM)
                .where(CPANEL_PREVENTA_LOYALTY_PROGRAM.IDPREVENTA.eq(presaleId))
                .execute();
    }
}
