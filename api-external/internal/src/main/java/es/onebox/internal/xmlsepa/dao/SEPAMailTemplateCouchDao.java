package es.onebox.internal.xmlsepa.dao;

import es.onebox.core.mail.template.manager.model.TemplateContentDTO;
import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = SEPAMailTemplateCouchDao.PREFIX, bucket = SEPAMailTemplateCouchDao.ONEBOX_OPERATIVE)
public class SEPAMailTemplateCouchDao extends AbstractCouchDao<TemplateContentDTO> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String PREFIX = "mailTemplate";

    private static final String DEFAULT_LANGUAGE = "en_US";

    public TemplateContentDTO getTemplate(String key, String language) {
        TemplateContentDTO template = get(key, language);
        if (template == null) {
            template = get(key, DEFAULT_LANGUAGE);
        }
        return template;
    }
}
