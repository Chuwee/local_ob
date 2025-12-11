package es.onebox.event.communicationelements.dao;

import es.onebox.event.communicationelements.enums.EmailCommunicationElementTagType;
import es.onebox.event.events.dao.record.EmailCommElementRecord;
import es.onebox.event.events.request.EmailCommunicationElementFilter;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelCanal;
import es.onebox.jooq.cpanel.tables.CpanelElementosComEmail;
import es.onebox.jooq.cpanel.tables.records.CpanelDescPorIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComEmailRecord;
import org.jooq.Field;
import org.springframework.stereotype.Repository;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static es.onebox.jooq.cpanel.Tables.CPANEL_DESC_POR_IDIOMA;
import static es.onebox.jooq.cpanel.Tables.CPANEL_ELEMENTOS_COM_EMAIL;
import static es.onebox.jooq.cpanel.Tables.CPANEL_EVENTO_CANAL;
import static es.onebox.jooq.cpanel.Tables.CPANEL_IDIOMA;
import static es.onebox.jooq.cpanel.Tables.CPANEL_ITEM_DESC_SEQUENCE;

@Repository
public class EmailCommunicationElementDao extends CommunicationElementDao<EmailCommunicationElementTagType, CpanelElementosComEmailRecord> {

    private static final CpanelElementosComEmail ELEMENTOS_COM_EMAIL = Tables.CPANEL_ELEMENTOS_COM_EMAIL;

    protected EmailCommunicationElementDao() {
        super(ELEMENTOS_COM_EMAIL);
    }

    public CpanelElementosComEmailRecord insertNew() {
        return this.dsl.insertInto(ELEMENTOS_COM_EMAIL)
                .defaultValues()
                .returning(ELEMENTOS_COM_EMAIL.IDINSTANCIA)
                .fetchOne();
    }

    public List<EmailCommElementRecord> getEmailCommElementsEventChannelPromoterBanner(Integer eventId, Integer channelId) {
        return getEmailCommElementByEventIdAndChannelId(CPANEL_ELEMENTOS_COM_EMAIL.BANNERPROMOTOR, eventId, channelId);
    }

    public List<EmailCommElementRecord> getEmailCommElementsEventChannelHeaderBanner(Integer eventId, Integer channelId) {
        return getEmailCommElementByEventIdAndChannelId(CPANEL_ELEMENTOS_COM_EMAIL.BANNERCABECERACANAL, eventId, channelId);
    }

    public List<EmailCommElementRecord> getEmailCommElementsEventChannelChannelBanner(Integer eventId, Integer channelId) {
        return getEmailCommElementByEventIdAndChannelId(CPANEL_ELEMENTOS_COM_EMAIL.BANNERCANAL, eventId, channelId);
    }

    public List<EmailCommElementRecord> getEmailCommElementsEventChannelChannelBannerLink(Integer eventId, Integer channelId) {
        return getEmailCommElementByEventIdAndChannelId(CPANEL_ELEMENTOS_COM_EMAIL.LINKBANNERCANAL, eventId, channelId);
    }

    public List<EmailCommElementRecord> getEmailCommElementsChannelBanner(Integer channelId) {
        return getEmailCommElementByChannelId(Tables.CPANEL_ELEMENTOS_COM_EMAIL.BANNERCANAL, channelId);
    }

    public List<EmailCommElementRecord> getEmailCommElementsChannelBannerLink(Integer channelId) {
        return getEmailCommElementByChannelId(CPANEL_ELEMENTOS_COM_EMAIL.LINKBANNERCANAL, channelId);
    }

    public List<EmailCommElementRecord> getEmailCommElementsChannelHeaderBanner(Integer channelId) {
        return getEmailCommElementByChannelId(Tables.CPANEL_ELEMENTOS_COM_EMAIL.BANNERCABECERACANAL, channelId);
    }


    public Map<EmailCommunicationElementTagType, List<CpanelDescPorIdiomaRecord>> findEventCommunicationElements(final Integer eventId, EmailCommunicationElementFilter filter) {

        CpanelElementosComEmailRecord commElementes = dsl.select()
                .from(Tables.CPANEL_EVENTO)
                .innerJoin(ELEMENTOS_COM_EMAIL).on(Tables.CPANEL_EVENTO.ELEMENTOCOMEMAIL.eq(ELEMENTOS_COM_EMAIL.IDINSTANCIA))
                .where(Tables.CPANEL_EVENTO.IDEVENTO.eq(eventId))
                .fetchOneInto(ELEMENTOS_COM_EMAIL);

        if (commElementes == null) {
            return new HashMap<>();
        }

        Map<EmailCommunicationElementTagType, Integer> tagKeys = this.buildTagKeyMap(commElementes);

        Map<Integer, List<CpanelDescPorIdiomaRecord>> descByItems = dsl
                .select(Tables.CPANEL_DESC_POR_IDIOMA.fields())
                .from(Tables.CPANEL_DESC_POR_IDIOMA)
                .where(buildWhere(filter, tagKeys, EmailCommunicationElementTagType.class))
                .fetchGroups(r -> r.get(Tables.CPANEL_DESC_POR_IDIOMA.IDITEM),
                        r -> r.into(Tables.CPANEL_DESC_POR_IDIOMA));

        return buildTagKeyResult(descByItems, tagKeys, EmailCommunicationElementTagType.class);
    }

    private List<EmailCommElementRecord> getEmailCommElementByEventIdAndChannelId(Field<Integer> filterField, Integer eventId, Integer channelId) {
        return dsl.select(
                        CPANEL_IDIOMA.CODIGO,
                        CPANEL_IDIOMA.IDIDIOMA,
                        CPANEL_DESC_POR_IDIOMA.IDITEM,
                        CPANEL_DESC_POR_IDIOMA.DESCRIPCION,
                        CPANEL_DESC_POR_IDIOMA.ALTTEXT)
                .from(CPANEL_EVENTO_CANAL)
                .innerJoin(CPANEL_ELEMENTOS_COM_EMAIL).on(CPANEL_EVENTO_CANAL.ELEMENTOCOMEMAIL.eq(CPANEL_ELEMENTOS_COM_EMAIL.IDINSTANCIA))
                .innerJoin(CPANEL_ITEM_DESC_SEQUENCE).on(CPANEL_ELEMENTOS_COM_EMAIL.field(filterField).eq(CPANEL_ITEM_DESC_SEQUENCE.IDITEM))
                .innerJoin(CPANEL_DESC_POR_IDIOMA).on(CPANEL_ITEM_DESC_SEQUENCE.IDITEM.eq(CPANEL_DESC_POR_IDIOMA.IDITEM))
                .innerJoin(CPANEL_IDIOMA).on(CPANEL_DESC_POR_IDIOMA.IDIDIOMA.eq(CPANEL_IDIOMA.IDIDIOMA))
                .where(CPANEL_EVENTO_CANAL.IDEVENTO.eq(eventId)
                        .and(CPANEL_EVENTO_CANAL.IDCANAL.eq(channelId)))
                .fetch()
                .map(record -> {
                    EmailCommElementRecord commEl = new EmailCommElementRecord();
                    commEl.setIdIdioma(record.get(CPANEL_IDIOMA.IDIDIOMA));
                    commEl.setLanguageCode(record.get(CPANEL_IDIOMA.CODIGO));
                    commEl.setValue(record.get(CPANEL_DESC_POR_IDIOMA.DESCRIPCION));
                    commEl.setIdItem(record.get(CPANEL_DESC_POR_IDIOMA.IDITEM));
                    commEl.setAltText(record.get(CPANEL_DESC_POR_IDIOMA.ALTTEXT));
                    return commEl;
                });
    }


    private List<EmailCommElementRecord> getEmailCommElementByChannelId(Field<Integer> filterField, int channelId) {
        return dsl.select(
                        Tables.CPANEL_IDIOMA.IDIDIOMA,
                        Tables.CPANEL_IDIOMA.CODIGO,
                        Tables.CPANEL_DESC_POR_IDIOMA.IDITEM,
                        Tables.CPANEL_DESC_POR_IDIOMA.DESCRIPCION,
                        Tables.CPANEL_DESC_POR_IDIOMA.ALTTEXT)
                .from(CpanelCanal.CPANEL_CANAL)
                .innerJoin(Tables.CPANEL_ELEMENTOS_COM_EMAIL).on(CpanelCanal.CPANEL_CANAL.ELEMENTOCOMEMAIL.eq(Tables.CPANEL_ELEMENTOS_COM_EMAIL.IDINSTANCIA))
                .innerJoin(Tables.CPANEL_ITEM_DESC_SEQUENCE).on(Tables.CPANEL_ELEMENTOS_COM_EMAIL.field(filterField).eq(Tables.CPANEL_ITEM_DESC_SEQUENCE.IDITEM))
                .innerJoin(Tables.CPANEL_DESC_POR_IDIOMA).on(Tables.CPANEL_ITEM_DESC_SEQUENCE.IDITEM.eq(Tables.CPANEL_DESC_POR_IDIOMA.IDITEM))
                .innerJoin(Tables.CPANEL_IDIOMA).on(Tables.CPANEL_DESC_POR_IDIOMA.IDIDIOMA.eq(Tables.CPANEL_IDIOMA.IDIDIOMA))
                .where(CpanelCanal.CPANEL_CANAL.IDCANAL.eq(channelId))
                .fetch()
                .map(record -> {
                    EmailCommElementRecord commEl = new EmailCommElementRecord();
                    commEl.setLanguageCode(record.get(Tables.CPANEL_IDIOMA.CODIGO));
                    commEl.setValue(record.get(Tables.CPANEL_DESC_POR_IDIOMA.DESCRIPCION));
                    commEl.setIdIdioma(record.get(Tables.CPANEL_IDIOMA.IDIDIOMA));
                    commEl.setIdItem(record.get(Tables.CPANEL_DESC_POR_IDIOMA.IDITEM));
                    commEl.setAltText(record.get(CPANEL_DESC_POR_IDIOMA.ALTTEXT));
                    return commEl;
                });
    }

    @Override
    public Map<EmailCommunicationElementTagType, Integer> buildTagKeyMap(CpanelElementosComEmailRecord record) {
        EnumMap<EmailCommunicationElementTagType, Integer> keyMap = new EnumMap<>(EmailCommunicationElementTagType.class);
        if (record.getBannercanal() != null) {
            keyMap.put(EmailCommunicationElementTagType.CHANNEL_BANNER, record.getBannercanal());
        }
        if (record.getBannercabeceracanal() != null) {
            keyMap.put(EmailCommunicationElementTagType.CHANNEL_HEADER_BANNER, record.getBannercabeceracanal());
        }
        if (record.getBannerpromotor() != null) {
            keyMap.put(EmailCommunicationElementTagType.PROMOTER_BANNER, record.getBannerpromotor());
        }
        if (record.getLinkbannercanal() != null) {
            keyMap.put(EmailCommunicationElementTagType.CHANNEL_BANNER_LINK, record.getLinkbannercanal());
        }
        if (record.getLinkbannercabeceracanal() != null) {
            keyMap.put(EmailCommunicationElementTagType.CHANNEL_HEADER_BANNER_LINK, record.getLinkbannercabeceracanal());
        }
        return keyMap;
    }
}
