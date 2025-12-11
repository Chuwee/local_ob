package es.onebox.event.sessions.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelConfigSesionGruposRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import static es.onebox.jooq.cpanel.tables.CpanelConfigSesionGrupos.CPANEL_CONFIG_SESION_GRUPOS;

@Repository
public class SessionGroupDao extends DaoImpl<CpanelConfigSesionGruposRecord, Integer> {

    protected SessionGroupDao() {
        super(CPANEL_CONFIG_SESION_GRUPOS);
    }
}
