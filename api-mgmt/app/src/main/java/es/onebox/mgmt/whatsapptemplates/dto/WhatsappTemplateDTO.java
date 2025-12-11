package es.onebox.mgmt.whatsapptemplates.dto;

import java.io.Serial;
import java.io.Serializable;

public class WhatsappTemplateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private String type;
    private String preview;

    public WhatsappTemplateDTO(Integer id, String name, String type, String preview) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.preview = preview;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }
}
