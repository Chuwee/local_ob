package es.onebox.mgmt.packs.dto.ticketcontents;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PackUrlDTO {

    @JsonProperty("select_link")
    private String selectLink;

    @JsonProperty("detail_link")
    private String detailLink;

    private String language;

    public String getSelectLink() {
        return selectLink;
    }

    public void setSelectLink(String selectLink) {
        this.selectLink = selectLink;
    }

    public String getDetailLink() {
        return detailLink;
    }

    public void setDetailLink(String detailLink) {
        this.detailLink = detailLink;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
