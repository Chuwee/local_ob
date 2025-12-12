package es.onebox.common.datasources.webhook.dto.fever.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;
import java.util.Optional;

@JsonNaming(SnakeCaseStrategy.class)
public class EventCommunicationElementFeverDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer tagId;
    private String tag;
    private Integer position;
    private Long id;
    private String language;
    private String value;
    private Optional<String> imageBinary;

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
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
}