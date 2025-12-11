package es.onebox.mgmt.datasources.ms.channel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class Language implements Serializable {

    private static final long serialVersionUID = -8449577811508955052L;

    @JsonProperty("default")
    private Long defaultLanguageId;
    @JsonProperty("selected")
    private List<Long> selectedLanguages;

    public Long getDefaultLanguageId() {
        return defaultLanguageId;
    }

    public void setDefaultLanguageId(Long defaultLanguageId) {
        this.defaultLanguageId = defaultLanguageId;
    }

    public List<Long> getSelectedLanguages() {
        return selectedLanguages;
    }

    public void setSelectedLanguages(List<Long> selectedLanguages) {
        this.selectedLanguages = selectedLanguages;
    }
}
