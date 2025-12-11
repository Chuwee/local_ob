package es.onebox.event.events.dynamicpricing;

import es.onebox.jooq.dao.SnowflakeDaoImpl;
import es.onebox.jooq.dp.tables.OneboxTimeSlotTierAssignments;
import es.onebox.jooq.dp.tables.records.OneboxTimeSlotTierAssignmentsRecord;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.dp.Tables.ONEBOX_TIME_SLOT_TIER_ASSIGNMENTS;

@Repository
public class DynamicPricingDao extends SnowflakeDaoImpl<OneboxTimeSlotTierAssignmentsRecord, Integer> {

    private static final String SUB = "sub";
    private static final String RN = "rn";

    protected DynamicPricingDao() {
        super(ONEBOX_TIME_SLOT_TIER_ASSIGNMENTS);
    }

    public List<OneboxTimeSlotTierAssignmentsRecord> getLastExecutionRecords() {

        Field<Integer> rn = DSL.rowNumber().over(DSL.partitionBy(ONEBOX_TIME_SLOT_TIER_ASSIGNMENTS.ID_EVENT).orderBy(ONEBOX_TIME_SLOT_TIER_ASSIGNMENTS.CREATED_AT.desc())).as(RN);

        var sub = dsl.select(ONEBOX_TIME_SLOT_TIER_ASSIGNMENTS.ID_EVENT, ONEBOX_TIME_SLOT_TIER_ASSIGNMENTS.ID_RUN, rn)
                .from(ONEBOX_TIME_SLOT_TIER_ASSIGNMENTS)
                .where(DSL.trueCondition())
                .asTable(SUB);

        return dsl.select()
                .from(ONEBOX_TIME_SLOT_TIER_ASSIGNMENTS)
                .join(sub)
                .using(ONEBOX_TIME_SLOT_TIER_ASSIGNMENTS.ID_EVENT, ONEBOX_TIME_SLOT_TIER_ASSIGNMENTS.ID_RUN)
                .where(DSL.field(DSL.name(SUB, RN)).eq(1))
                .fetchInto(OneboxTimeSlotTierAssignmentsRecord.class);
    }

    private static final OneboxTimeSlotTierAssignments dp = ONEBOX_TIME_SLOT_TIER_ASSIGNMENTS.as("dp");
}
