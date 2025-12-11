package es.onebox.mgmt.datasources.ms.channel.dto.whatsapptemplates;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class WhatsappTemplate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String type;
    private List<Integer> entityVisibility;
    private String preview;
    private Map<String, String> parameters;
    private List<Content> contents;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Integer> getEntityVisibility() {
        return entityVisibility;
    }

    public void setEntityVisibility(List<Integer> entityVisibility) {
        this.entityVisibility = entityVisibility;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }
}
