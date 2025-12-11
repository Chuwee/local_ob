package es.onebox.mgmt.datasources.common.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

public class BaseCommunicationElement implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String tag;
    private String language;
    private String value;
    private Optional<String> imageBinary;
    private String altText;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Optional<String> getImageBinary() {
        return imageBinary;
    }

    public void setImageBinary(Optional<String> imageBinary) {
        this.imageBinary = imageBinary;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }
}
