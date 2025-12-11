package es.onebox.event.events.dao;

import es.onebox.event.datasources.ms.entity.dto.EntityState;
import es.onebox.jooq.cpanel.tables.records.CpanelEntidadRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static es.onebox.jooq.cpanel.Tables.CPANEL_CANAL;
import static es.onebox.jooq.cpanel.Tables.CPANEL_ENTIDAD;

@Repository
public class EntityDao extends DaoImpl<CpanelEntidadRecord, Integer> {

    protected EntityDao() {
        super(CPANEL_ENTIDAD);
    }

    public CpanelEntidadRecord getEntityByChannelId(Long channelId) {
        return dsl.select(CPANEL_ENTIDAD.fields())
                .from(CPANEL_ENTIDAD)
                .innerJoin(CPANEL_CANAL).on(CPANEL_CANAL.IDENTIDAD.eq(CPANEL_ENTIDAD.IDENTIDAD).and(CPANEL_CANAL.IDCANAL.eq(channelId.intValue())))
                .fetchOne()
                .into(CpanelEntidadRecord.class);
    }

    public EntityInfo getEntityInfo(Integer entityId) {
        return dsl.select(CPANEL_ENTIDAD.IDENTIDAD, CPANEL_ENTIDAD.IDOPERADORA, CPANEL_ENTIDAD.PROVINCIA)
                .from(CPANEL_ENTIDAD)
                .where(CPANEL_ENTIDAD.IDENTIDAD.eq(entityId))
                .fetchOne()
                .map(r -> new EntityInfo(
                        r.get(CPANEL_ENTIDAD.IDENTIDAD),
                        r.get(CPANEL_ENTIDAD.IDOPERADORA),
                        r.get(CPANEL_ENTIDAD.PROVINCIA)));
    }

	public boolean exists(Integer entityId) {

		return dsl.fetchExists(dsl.selectOne()
                .from(CPANEL_ENTIDAD)
                .where(CPANEL_ENTIDAD.IDENTIDAD.eq(entityId))
				.and(CPANEL_ENTIDAD.ESTADO.eq(
						EntityState.ACTIVE.getState())));
	}

    public record EntityInfo(Integer id,
                             Integer operatorId,
                             Integer countrySubId) {
    }

    public Map<Integer, Boolean> getAllowSecondaryMarketList(List<Integer> entityIds) {
        Map<Integer, Boolean> result = new HashMap<>();
        List<Record> records = dsl.select(CPANEL_ENTIDAD.fields())
                .from(CPANEL_ENTIDAD)
                .where(CPANEL_ENTIDAD.IDENTIDAD.in(entityIds))
                .fetch();

        for (Record recordValue : records) {
            Byte b = recordValue.get(CPANEL_ENTIDAD.ALLOWSECMKT);
            result.put(recordValue.get(CPANEL_ENTIDAD.IDENTIDAD), b != null ? b != 0 : Boolean.FALSE);
        }
        return result;
    }


    public Boolean getAllowSecondaryMarket(Integer entityId) {
        return dsl.select(CPANEL_ENTIDAD.ALLOWSECMKT)
                .from(CPANEL_ENTIDAD)
                .where(CPANEL_ENTIDAD.IDENTIDAD.eq(entityId))
                .fetchOne(r -> {
                    Byte b = r.get(CPANEL_ENTIDAD.ALLOWSECMKT);
                    return b != null ? b != 0 : Boolean.FALSE;
                });
    }

}
