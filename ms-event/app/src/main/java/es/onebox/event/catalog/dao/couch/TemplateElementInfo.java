package es.onebox.event.catalog.dao.couch;

import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.couchbase.annotations.Id;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@CouchDocument
public class TemplateElementInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = -359263967684125609L;

    @Id
    private Long templateId;
    @Id(index = 1)
    private String type;
    @Id(index = 2)
    private Long id; //view or nnz or price type
    private List<String> tags;
    private List<SessionTemplateInfo> sessionTemplateInfoList;
    private AggregatedInfo defaultInfo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<SessionTemplateInfo> getSessionTemplateInfoList() {
        return sessionTemplateInfoList;
    }

    public AggregatedInfo getDefaultInfo() {
        return defaultInfo;
    }

    public void setDefaultInfo(AggregatedInfo defaultInfo) {
        this.defaultInfo = defaultInfo;
    }

    public void setSessionTemplateInfoList(List<SessionTemplateInfo> sessionTemplateInfoList) {
        this.sessionTemplateInfoList = sessionTemplateInfoList;
    }

}
