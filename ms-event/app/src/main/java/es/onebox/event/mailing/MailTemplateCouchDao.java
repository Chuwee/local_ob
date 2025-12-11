package es.onebox.event.mailing;

import es.onebox.core.mail.template.manager.model.TemplateContentDTO;
import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = MailTemplateCouchDao.PREFIX, bucket = MailTemplateCouchDao.ONEBOX_OPERATIVE)
public class MailTemplateCouchDao extends AbstractCouchDao<TemplateContentDTO> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String PREFIX = "mailTemplate";
    public static final String FAILED_KEY = "failed";

    private static final String DEFAULT_LANGUAGE = "en_US";

    public TemplateContentDTO getTemplate(String key, String language) {
        TemplateContentDTO template = get(key, language);
        if (template == null) {
            template = get(key, DEFAULT_LANGUAGE);
        }
        return template;
    }

    public TemplateContentDTO getFailedTemplate(String key, String language) {
        TemplateContentDTO template = get(FAILED_KEY, key, language);
        if (template == null) {
            template = get(key, DEFAULT_LANGUAGE);
        }
        return template;
    }

}
