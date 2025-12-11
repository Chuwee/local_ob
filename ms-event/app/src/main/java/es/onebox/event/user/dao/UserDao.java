package es.onebox.event.user.dao;

import es.onebox.event.events.dao.record.MailReceiverUserRecord;
import es.onebox.event.events.dao.record.MailSenderUserRecord;
import es.onebox.event.events.domain.NotificationType;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelUsuario;
import es.onebox.jooq.cpanel.tables.records.CpanelUsuarioRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Field;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao extends DaoImpl<CpanelUsuarioRecord, Integer> {

    private static final int DELETED_USER = 0;

    protected UserDao() {
        super(Tables.CPANEL_USUARIO);
    }

    private static final Field[] RECEIVER_FIELDS = new Field[]{
            Tables.CPANEL_USUARIO.NOMBRE,
            Tables.CPANEL_USUARIO.EMAIL,
            Tables.CPANEL_IDIOMA.CODIGO
    };

    private static final Field[] SENDER_FIELDS = new Field[]{
            Tables.CPANEL_USUARIO.NOMBRE,
            Tables.CPANEL_ENTIDAD.NOMBRE
    };

    public String getUserNameById(Integer userId) {
        return dsl.select(CpanelUsuario.CPANEL_USUARIO.NOMBRE)
                .from(Tables.CPANEL_USUARIO)
                .where(CpanelUsuario.CPANEL_USUARIO.IDUSUARIO.eq(userId))
                .fetchOne(0, String.class);
    }

    public List<MailReceiverUserRecord> getUsersToNotify(NotificationType notificationType, Integer entityId) {
        return dsl.select(RECEIVER_FIELDS)
                .from(Tables.CPANEL_USUARIO)
                .innerJoin(Tables.CPANEL_IDIOMA).on(Tables.CPANEL_IDIOMA.IDIDIOMA.eq(Tables.CPANEL_USUARIO.IDIOMA))
                .innerJoin(Tables.CPANEL_NOTIFICACION_USUARIO).on(Tables.CPANEL_USUARIO.IDUSUARIO.eq(Tables.CPANEL_NOTIFICACION_USUARIO.IDUSUARIO).and(Tables.CPANEL_NOTIFICACION_USUARIO.IDNOTIFICACIONUSUARIO.eq(notificationType.getId())))
                .where(Tables.CPANEL_USUARIO.ESTADO.ne(DELETED_USER))
                .and(Tables.CPANEL_USUARIO.IDENTIDAD.eq(entityId))
                .fetch()
                .map(this::buildMailReceiverRecord);
    }

    public MailSenderUserRecord getMailSender(Integer userId) {
        return dsl.select(SENDER_FIELDS)
                .from(Tables.CPANEL_USUARIO)
                .innerJoin(Tables.CPANEL_ENTIDAD).on(Tables.CPANEL_USUARIO.IDENTIDAD.eq(Tables.CPANEL_ENTIDAD.IDENTIDAD))
                .where(Tables.CPANEL_USUARIO.IDUSUARIO.eq(userId))
                .fetchOne()
                .map(this::buildMailSenderRecord);
    }

    private MailReceiverUserRecord buildMailReceiverRecord(Record record) {
        MailReceiverUserRecord result = new MailReceiverUserRecord();
        result.setName(record.get(Tables.CPANEL_USUARIO.NOMBRE));
        result.setEmail(record.get(Tables.CPANEL_USUARIO.EMAIL));
        result.setLocale(record.get(Tables.CPANEL_IDIOMA.CODIGO));
        return result;
    }

    private MailSenderUserRecord buildMailSenderRecord(Record record) {
        MailSenderUserRecord result = new MailSenderUserRecord();
        result.setName(record.get(Tables.CPANEL_USUARIO.NOMBRE));
        result.setEntityName(record.get(Tables.CPANEL_ENTIDAD.NOMBRE));
        return result;
    }

}
