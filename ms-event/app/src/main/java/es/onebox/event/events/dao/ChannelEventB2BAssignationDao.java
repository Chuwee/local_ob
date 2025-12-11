package es.onebox.event.events.dao;

import es.onebox.event.events.dao.record.ChannelEventB2BAssignationRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalCupoB2bRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_CANAL_CUPO_B2B;
import static es.onebox.jooq.cpanel.Tables.CPANEL_CANAL_CUPO_B2B_ASIGN;
import static es.onebox.jooq.cpanel.Tables.CPANEL_CUPOS_CONFIG;

@Repository
public class ChannelEventB2BAssignationDao extends DaoImpl<CpanelCanalCupoB2bRecord, Integer> {

    protected ChannelEventB2BAssignationDao() {
        super(CPANEL_CANAL_CUPO_B2B);
    }

    public CpanelCanalCupoB2bRecord getByChannelEventAndQuota(Integer channelEventId, Integer quotaId) {
        return dsl.select()
                .from(CPANEL_CANAL_CUPO_B2B)
                .where(CPANEL_CANAL_CUPO_B2B.IDCANALEVENTO.eq(channelEventId)
                        .and(CPANEL_CANAL_CUPO_B2B.IDCUPO.eq(quotaId)))
                .fetchOneInto(CpanelCanalCupoB2bRecord.class);
    }

    public void upsertQuotaChannelEventRelation(Integer channelEventId, Integer quotaId, Byte allClients) {
        dsl.insertInto(CPANEL_CANAL_CUPO_B2B, CPANEL_CANAL_CUPO_B2B.IDCANALEVENTO, CPANEL_CANAL_CUPO_B2B.IDCUPO, CPANEL_CANAL_CUPO_B2B.ALLCLIENTS)
                .values(channelEventId, quotaId, allClients)
                .onDuplicateKeyUpdate()
                .set(CPANEL_CANAL_CUPO_B2B.ALLCLIENTS, allClients)
                .execute();
    }

    public void removeAllByQuotaAssignation(Integer idQuotaB2B) {
        dsl.deleteFrom(CPANEL_CANAL_CUPO_B2B_ASIGN)
                .where(CPANEL_CANAL_CUPO_B2B_ASIGN.IDCANALCUPOB2B.eq(idQuotaB2B))
                .execute();
    }

    public void insertQuotaAssignation(Integer idQuotaB2B, Integer clientId) {
        dsl.insertInto(CPANEL_CANAL_CUPO_B2B_ASIGN, CPANEL_CANAL_CUPO_B2B_ASIGN.IDCANALCUPOB2B, CPANEL_CANAL_CUPO_B2B_ASIGN.CLIENTENTITYID)
                .values(idQuotaB2B, clientId)
                .onDuplicateKeyIgnore()
                .execute();
    }
    public List<ChannelEventB2BAssignationRecord> fetchAll(Integer channelEventId) {
        return dsl.select()
                .from(CPANEL_CANAL_CUPO_B2B)
                .leftJoin(CPANEL_CANAL_CUPO_B2B_ASIGN).on(CPANEL_CANAL_CUPO_B2B_ASIGN.IDCANALCUPOB2B.eq(CPANEL_CANAL_CUPO_B2B.IDCANALCUPOB2B))
                .join(CPANEL_CUPOS_CONFIG).on(CPANEL_CUPOS_CONFIG.IDCUPO.eq(CPANEL_CANAL_CUPO_B2B.IDCUPO))
                .where(CPANEL_CANAL_CUPO_B2B.IDCANALEVENTO.eq(channelEventId))
                .fetch()
                .map(ChannelEventB2BAssignationDao::buildRecord);
    }

    public void deleteByChannelEventId(Integer channelEventId) {
        dsl.deleteFrom(CPANEL_CANAL_CUPO_B2B)
                .where(CPANEL_CANAL_CUPO_B2B.IDCANALEVENTO.eq(channelEventId))
                .execute();
    }

    private static ChannelEventB2BAssignationRecord buildRecord(Record record) {
        ChannelEventB2BAssignationRecord target = new ChannelEventB2BAssignationRecord();
        target.setIdcanalevento(record.get(CPANEL_CANAL_CUPO_B2B.IDCANALEVENTO));
        target.setIdcanalcupob2b(record.get(CPANEL_CANAL_CUPO_B2B.IDCANALCUPOB2B));
        target.setAllclients(record.get(CPANEL_CANAL_CUPO_B2B.ALLCLIENTS));
        target.setQuotaId(record.get(CPANEL_CANAL_CUPO_B2B.IDCUPO));
        target.setQuotaDescription(record.get(CPANEL_CUPOS_CONFIG.DESCRIPCION));
        target.setClientId(record.get(CPANEL_CANAL_CUPO_B2B_ASIGN.CLIENTENTITYID));
        return target;
    }
}
