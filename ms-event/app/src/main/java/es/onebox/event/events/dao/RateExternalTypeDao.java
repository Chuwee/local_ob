/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.onebox.event.events.dao;

import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelExternalRateTypeRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Field;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_EXTERNAL_RATE_TYPE;

@Repository
public class RateExternalTypeDao extends DaoImpl<CpanelExternalRateTypeRecord, Integer> {

    private static final Field[] FIELDS_RATES_EXTERNAL_TYPES = new Field[]{
            CPANEL_EXTERNAL_RATE_TYPE.ID,
            CPANEL_EXTERNAL_RATE_TYPE.CODE,
            CPANEL_EXTERNAL_RATE_TYPE.NAME
    };

    protected RateExternalTypeDao() {
        super(Tables.CPANEL_EXTERNAL_RATE_TYPE);
    }

    public List<CpanelExternalRateTypeRecord> getEventRateExternalTypes(String provider) {
        return dsl.select().from(CPANEL_EXTERNAL_RATE_TYPE).
                where(CPANEL_EXTERNAL_RATE_TYPE.PROVIDER.eq(provider)).
                fetch().into(CpanelExternalRateTypeRecord.class);
    }

}
