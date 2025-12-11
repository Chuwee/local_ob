package es.onebox.mgmt.datasources.ms.venue.dto.templateelements;


import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.enums.FeatureAction;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.enums.FeatureType;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class Feature implements Serializable {

    @Serial
    private static final long serialVersionUID = 7680312031762725101L;

    @NotNull
    private FeatureType type;
    @NotNull
    private String text;
    private String url;
    private FeatureAction action;

    public Feature() {
    }

    public Feature(FeatureType type, String text, String url, FeatureAction action) {
        this.type = type;
        this.text = text;
        this.url = url;
        this.action = action;
    }

    public FeatureType getType() {
        return type;
    }

    public void setType(FeatureType type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public FeatureAction getAction() {
        return action;
    }

    public void setAction(FeatureAction action) {
        this.action = action;
    }
}
