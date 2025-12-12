package es.onebox.common.datasources.ms.crm.dto;

import java.io.Serializable;
import java.util.List;

public class CrmResponse implements Serializable {

    private String version;
    private String href;
    private String trace_id;
    private String request_date;
    private String channel_id;
    private Integer hits;
    private List<CrmLink> links;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getTrace_id() {
        return trace_id;
    }

    public void setTrace_id(String trace_id) {
        this.trace_id = trace_id;
    }

    public String getRequest_date() {
        return request_date;
    }

    public void setRequest_date(String request_date) {
        this.request_date = request_date;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public List<CrmLink> getLinks() {
        return links;
    }

    public void setLinks(List<CrmLink> links) {
        this.links = links;
    }

    public Integer getHits() {
        return hits;
    }

    public void setHits(Integer hits) {
        this.hits = hits;
    }
}
