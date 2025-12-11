package es.onebox.event.entity.templateszones.dto;

import es.onebox.event.catalog.dto.CatalogCommunicationElementDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class EntityTemplateZonesDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -359263967684125609L;

    private Integer id;
    private String name;
    private String code;
    private Map<String, List<CatalogCommunicationElementDTO>> contentsTexts;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, List<CatalogCommunicationElementDTO>> getContentsTexts() {
        return contentsTexts;
    }

    public void setContentsTexts(Map<String, List<CatalogCommunicationElementDTO>> contentsTexts) {
        this.contentsTexts = contentsTexts;
    }
}
