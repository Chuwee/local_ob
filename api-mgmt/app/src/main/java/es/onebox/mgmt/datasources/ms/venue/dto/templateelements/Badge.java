package es.onebox.mgmt.datasources.ms.venue.dto.templateelements;

import java.util.Map;

public class Badge {

    private String backgroundColor;
    private String textColor;
    private Map<String, String> text;

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public Map<String, String> getText() {
        return text;
    }

    public void setText(Map<String, String> text) {
        this.text = text;
    }

}
