package es.onebox.event.events.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelCanalCurrencyRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_CANAL_CURRENCY;

@Repository
public class ChannelCurrenciesDao extends DaoImpl<CpanelCanalCurrencyRecord, Integer> {

    protected ChannelCurrenciesDao() {
        super(CPANEL_CANAL_CURRENCY);
    }

    public List<Long> getCurrenciesByChannelId(Long channelId) {
        return dsl.select(CPANEL_CANAL_CURRENCY.IDCURRENCY).from(CPANEL_CANAL_CURRENCY)
                .where(CPANEL_CANAL_CURRENCY.IDCANAL.eq(channelId.intValue()))
                .fetchInto(Long.class);
    }

    public List<CpanelCanalCurrencyRecord> getCurrencies(List<Long> channelIds) {
        return dsl.select(CPANEL_CANAL_CURRENCY.IDCANAL, CPANEL_CANAL_CURRENCY.IDCURRENCY).from(CPANEL_CANAL_CURRENCY)
                .where(CPANEL_CANAL_CURRENCY.IDCANAL.in(channelIds))
                .fetchInto(CpanelCanalCurrencyRecord.class);
    }
}