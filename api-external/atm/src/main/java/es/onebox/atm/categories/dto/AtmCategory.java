package es.onebox.atm.categories.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.couchbase.annotations.Id;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@CouchDocument
@JsonIgnoreProperties(ignoreUnknown = true)
public class AtmCategory implements Serializable {

    @Serial
    private static final long serialVersionUID = -6025256216369371355L;

    @Id
    private String id;

    private final Map<String, Map<String, CategoryAdditonalData>> categoryAdditionalInfo = new HashMap<>();

    @JsonAnySetter
    public void putDynamic(String key, Map<String, CategoryAdditonalData> categoryAdditonalDataMap) {
        categoryAdditionalInfo.put(key, categoryAdditonalDataMap);
    }

    @JsonAnyGetter
    public Map<String, Map<String, CategoryAdditonalData>> any() {
        return categoryAdditionalInfo;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Map<String, Map<String, CategoryAdditonalData>> getCategoryAdditionalInfo() {
        return categoryAdditionalInfo;
    }
}
