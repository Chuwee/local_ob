package es.onebox.event.sessions.dao;

import es.onebox.event.catalog.dao.record.SessionTaxesForCatalogRecord;
import es.onebox.event.sessions.dto.SessionTaxDTO;
import es.onebox.event.sessions.enums.SessionTaxesType;
import es.onebox.event.sessions.enums.TaxType;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelImpuesto;
import es.onebox.jooq.cpanel.tables.CpanelLocationTaxes;
import es.onebox.jooq.cpanel.tables.CpanelSesion;
import es.onebox.jooq.cpanel.tables.CpanelSessionTaxes;
import es.onebox.jooq.cpanel.tables.CpanelTaxesDetail;
import es.onebox.jooq.cpanel.tables.records.CpanelImpuestoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSessionTaxesRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static es.onebox.jooq.cpanel.Tables.CPANEL_LOCATION_TAXES;
import static es.onebox.jooq.cpanel.Tables.CPANEL_SESION;
import static es.onebox.jooq.cpanel.Tables.CPANEL_SESSION_TAXES;
import static es.onebox.jooq.cpanel.Tables.CPANEL_TAXES_DETAIL;

@Repository
public class SessionTaxesDao extends DaoImpl<CpanelSessionTaxesRecord, Integer> {

    protected SessionTaxesDao() {
        super(CPANEL_SESSION_TAXES);
    }

    protected static final CpanelSessionTaxes sessionTaxes = CPANEL_SESSION_TAXES.as("sessionTaxes");

    protected static final CpanelSesion session = CPANEL_SESION.as("session");
    protected static final CpanelImpuesto ticketTax = Tables.CPANEL_IMPUESTO.as("ticketTax");
    protected static final CpanelImpuesto chargesTax = Tables.CPANEL_IMPUESTO.as("chargesTax");
    protected static final CpanelLocationTaxes locationTaxes = CPANEL_LOCATION_TAXES.as("locationTaxes");
    protected static final CpanelTaxesDetail taxDetail  = CPANEL_TAXES_DETAIL.as("taxDetail");

    protected static final TableField<CpanelImpuestoRecord, Integer> JOIN_TICKET_TAX_ID = ticketTax.IDIMPUESTO;
    protected static final TableField<CpanelImpuestoRecord, String> JOIN_TICKET_TAX_NAME = ticketTax.NOMBRE;
    protected static final TableField<CpanelImpuestoRecord, Double> JOIN_TICKET_TAX_VALUE = ticketTax.VALOR;
    protected static final TableField<CpanelImpuestoRecord, Integer> JOIN_TICKET_TAX_LOCATION_TAX = ticketTax.LOCATION_TAX_ID;

    protected static final TableField<CpanelImpuestoRecord, Integer> JOIN_CHARGE_TAX_ID = chargesTax.IDIMPUESTO;
    protected static final TableField<CpanelImpuestoRecord, String> JOIN_CHARGE_TAX_NAME = chargesTax.NOMBRE;
    protected static final TableField<CpanelImpuestoRecord, Double> JOIN_CHARGE_TAX_VALUE = chargesTax.VALOR;
    protected static final TableField<CpanelImpuestoRecord, Integer> JOIN_CHARGE_TAX_LOCATION_TAX = chargesTax.LOCATION_TAX_ID;

    public List<CpanelSessionTaxesRecord> findFlatSessionsTaxes(Integer eventId, Integer sessionId, SessionTaxesType sessionTaxesType) {
        return dsl.select(ArrayUtils.addAll(sessionTaxes.fields()))
                .from(sessionTaxes)
                .where(buildWhereClause(eventId, sessionId, sessionTaxesType))
                .fetch().map(this::buildFlatSessionsTaxes);
    }

    public List<SessionTaxesForCatalogRecord> findSessionsTicketTaxes(Integer eventId) {
        List<SessionTaxesForCatalogRecord> result = new ArrayList<>();

        // new ticket taxes
        List<Field> newTaxesFields = new ArrayList<>();
        newTaxesFields.addAll(Arrays.stream(sessionTaxes.fields()).toList());
        newTaxesFields.addAll(Arrays.stream(taxDetail.fields()).toList());
        newTaxesFields.add(ticketTax.IDIMPUESTO);
        newTaxesFields.add(ticketTax.NOMBRE);
        List<SessionTaxesForCatalogRecord> newTicketTaxes = dsl.select(newTaxesFields)
                .from(sessionTaxes)
                .join(session).on(sessionTaxes.SESSION_ID.eq(session.IDSESION)).and(session.IDEVENTO.eq(eventId))
                .join(taxDetail).on(taxDetail.TAX_DETAIL_ID.eq(sessionTaxes.TAX_ID))
                .join(ticketTax).on(ticketTax.IDIMPUESTO.eq(session.IDIMPUESTO))
                 .where(sessionTaxes.TIPO.in(TaxType.TICKET.getType(), TaxType.TICKET_INVITATION.getType()))
                .fetch().map(this::buildLocationTaxes);
        result.addAll(newTicketTaxes);

        List<Integer> ticketTaxesSessionIds = new ArrayList<>();
        if (newTicketTaxes != null && !newTicketTaxes.isEmpty()) {
            ticketTaxesSessionIds.addAll(newTicketTaxes.stream().map(SessionTaxesForCatalogRecord::getSessionId).collect(Collectors.toList()));
        }

        // new charges taxes
        newTaxesFields = new ArrayList<>();
        newTaxesFields.addAll(Arrays.stream(sessionTaxes.fields()).toList());
        newTaxesFields.addAll(Arrays.stream(taxDetail.fields()).toList());
        newTaxesFields.add(chargesTax.IDIMPUESTO);
        newTaxesFields.add(chargesTax.NOMBRE);
        List<SessionTaxesForCatalogRecord> newChargesTaxes = dsl.select(newTaxesFields)
                .from(sessionTaxes)
                .join(session).on(sessionTaxes.SESSION_ID.eq(session.IDSESION)).and(session.IDEVENTO.eq(eventId))
                .join(taxDetail).on(taxDetail.TAX_DETAIL_ID.eq(sessionTaxes.TAX_ID))
                .join(chargesTax).on(chargesTax.IDIMPUESTO.eq(session.IDIMPUESTORECARGO))
                .where(sessionTaxes.TIPO.in(TaxType.CHARGES.getType()))
                .fetch().map(this::buildLocationTaxes);
        result.addAll(newChargesTaxes);

        List<Integer> chargesTaxesSessionIds = new ArrayList<>();
        if (newChargesTaxes != null && !newChargesTaxes.isEmpty()) {
            chargesTaxesSessionIds.addAll(newChargesTaxes.stream().map(SessionTaxesForCatalogRecord::getSessionId).collect(Collectors.toList()));
        }

        // Old ticket taxes
        List<Field> oldTaxesFields = new ArrayList<>();
        oldTaxesFields.add(session.IDSESION);
        oldTaxesFields.add(session.IDIMPUESTO);
        oldTaxesFields.add(JOIN_TICKET_TAX_ID);
        oldTaxesFields.add(JOIN_TICKET_TAX_NAME);
        oldTaxesFields.add(JOIN_TICKET_TAX_VALUE);
        oldTaxesFields.add(JOIN_TICKET_TAX_LOCATION_TAX);

        Condition conditions = DSL.trueCondition();
        if (!ticketTaxesSessionIds.isEmpty()) {
            conditions = conditions.and(session.IDSESION.notIn(ticketTaxesSessionIds));
        }
        List<SessionTaxesForCatalogRecord> oldTicketTaxes = dsl.selectDistinct(oldTaxesFields)
                .from(session)
                .join(ticketTax).on(ticketTax.IDIMPUESTO.eq(session.IDIMPUESTO))
                .where(buildWhereClauseUnrelatedTaxes(eventId, conditions))
                .fetch().map(this::buildTicketSessionsTaxes);
        result.addAll(oldTicketTaxes);

        // Old charges  taxes
        oldTaxesFields = new ArrayList<>();
        oldTaxesFields.add(session.IDSESION);
        oldTaxesFields.add(session.IDIMPUESTO);
        oldTaxesFields.add(JOIN_CHARGE_TAX_ID);
        oldTaxesFields.add(JOIN_CHARGE_TAX_NAME);
        oldTaxesFields.add(JOIN_CHARGE_TAX_VALUE);
        oldTaxesFields.add(JOIN_CHARGE_TAX_LOCATION_TAX);

        conditions = DSL.trueCondition();
        if (!chargesTaxesSessionIds.isEmpty()) {
            conditions = conditions.and(session.IDSESION.notIn(chargesTaxesSessionIds));
        }
        List<SessionTaxesForCatalogRecord> oldChargesTaxes = dsl.selectDistinct(oldTaxesFields)
                .from(session)
                .join(chargesTax).on(chargesTax.IDIMPUESTO.eq(session.IDIMPUESTORECARGO))
                .where(buildWhereClauseUnrelatedTaxes(eventId, conditions))
                .fetch().map(this::buildChargeSessionsTaxes);
        result.addAll(oldChargesTaxes);

        return result;
    }

    private Condition buildWhereClauseUnrelatedTaxes(Integer eventId, Condition conditions) {
         conditions = conditions.and(session.IDEVENTO.eq(eventId));
        return conditions;
    }

    private Condition buildWhereClause(Integer eventId, Integer sessionId, SessionTaxesType sessionTaxesType) {
        Condition conditions = DSL.trueCondition();
        if (eventId != null) {
            conditions = conditions.and(session.IDEVENTO.eq(eventId));
        }
        if (sessionId != null) {
            conditions = conditions.and(sessionTaxes.SESSION_ID.eq(sessionId));
        }
        if (sessionTaxesType != null) {
            conditions = conditions.and(sessionTaxes.TIPO.eq(sessionTaxesType.getType()));
        }
        return conditions;
    }

    private CpanelSessionTaxesRecord buildFlatSessionsTaxes(Record r) {
        CpanelSessionTaxesRecord out = r.into(CpanelSessionTaxesRecord.class);
        return out;
    }

    private SessionTaxesForCatalogRecord buildTicketSessionsTaxes(Record r) {
        return buildSessionsTaxes(r, TaxType.TICKET);
    }

    private SessionTaxesForCatalogRecord buildChargeSessionsTaxes(Record r) {
        return buildSessionsTaxes(r, TaxType.CHARGES);
    }

    private SessionTaxesForCatalogRecord buildSessionsTaxes(Record r, TaxType taxType) {
        SessionTaxesForCatalogRecord out = r.into(SessionTaxesForCatalogRecord.class);
        out.setTipo(taxType.getType());
        out.setId(r.get(session.IDIMPUESTO, Integer.class));
        out.setSessionId(r.get(session.IDSESION, Integer.class));
        if (taxType.equals(TaxType.TICKET)) {
            out.setTaxId(r.get(JOIN_TICKET_TAX_ID, Integer.class));
            out.setTaxName(r.get(JOIN_TICKET_TAX_NAME, String.class));
            out.setTaxValue(r.get(JOIN_TICKET_TAX_VALUE, Double.class));
            out.setLocationTaxId(r.get(JOIN_TICKET_TAX_LOCATION_TAX, Long.class));
        } else if(taxType.equals(TaxType.CHARGES)) {
            out.setTaxId(r.get(JOIN_CHARGE_TAX_ID, Integer.class));
            out.setTaxName(r.get(JOIN_CHARGE_TAX_NAME, String.class));
            out.setTaxValue(r.get(JOIN_CHARGE_TAX_VALUE, Double.class));
            out.setLocationTaxId(r.get(JOIN_CHARGE_TAX_LOCATION_TAX, Long.class));
        }
        return out;
    }

    private SessionTaxesForCatalogRecord buildLocationTaxes(Record r) {
        SessionTaxesForCatalogRecord out = new SessionTaxesForCatalogRecord();
        if (r.get(ticketTax.IDIMPUESTO, Long.class) != null) {
            out.setId(r.get(ticketTax.IDIMPUESTO, Integer.class));
            out.setTaxName(r.get(ticketTax.NOMBRE, String.class));
        } else {
            out.setId(r.get(chargesTax.IDIMPUESTO, Integer.class));
            out.setTaxName(r.get(chargesTax.NOMBRE, String.class));
        }
        out.setLocationTaxId(r.get(taxDetail.TAX_DETAIL_ID, Long.class));
        out.setTaxCounter(r.get(taxDetail.TAX_DETAIL_ITEM_ID, Long.class));
        out.setTipo(r.get(sessionTaxes.TIPO, Integer.class));
        out.setTaxId(r.get(sessionTaxes.TAX_ID, Integer.class));
        out.setMinRange(r.get(taxDetail.PRICE_MIN_RANGE, Double.class));
        out.setMaxRange(r.get(taxDetail.PRICE_MAX_RANGE, Double.class));
        out.setTaxValue(r.get(taxDetail.TAX_VALUE, Double.class));
        out.setProgressive(r.get(taxDetail.PROGRESSIVE, Boolean.class));
        out.setMinProgressive(r.get(taxDetail.PROGRESSIVE_MIN, Double.class));
        out.setMaxProgressive(r.get(taxDetail.PROGRESSIVE_MAX, Double.class));
        out.setStartDate(r.get(taxDetail.START_DATE_RANGE, Timestamp.class));
        out.setEndDate(r.get(taxDetail.END_DATE_RANGE, Timestamp.class));
        out.setCapacityType(r.get(taxDetail.CAPACITY_TYPE_RANGE, Integer.class));
        out.setCapacityMin(r.get(taxDetail.CAPACITY_MIN_RANGE, Integer.class));
        out.setCapacityMax(r.get(taxDetail.CAPACITY_MAX_RANGE, Integer.class));
        out.setSessionId(r.get(sessionTaxes.SESSION_ID, Integer.class));
        return out;
    }
}
