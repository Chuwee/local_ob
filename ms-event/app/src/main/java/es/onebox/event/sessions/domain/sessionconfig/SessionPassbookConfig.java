package es.onebox.event.sessions.domain.sessionconfig;

import java.io.Serializable;
import java.util.Map;

public class SessionPassbookConfig implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Map<String, String> title;
    private Map<String, String> stripImage;
    private Map<String, String> additionalData1;
    private Map<String, String> additionalData2;
    private Map<String, String> additionalData3;


    public Map<String, String> getTitle() {
        return title;
    }

    public void setTitle(Map<String, String> title) {
        this.title = title;
    }

    public Map<String, String> getAdditionalData1() {
        return additionalData1;
    }

    public void setAdditionalData1(Map<String, String> additionalData1) {
        this.additionalData1 = additionalData1;
    }

    public Map<String, String> getAdditionalData2() {
        return additionalData2;
    }

    public void setAdditionalData2(Map<String, String> additionalData2) {
        this.additionalData2 = additionalData2;
    }

    public Map<String, String> getAdditionalData3() {
        return additionalData3;
    }

    public void setAdditionalData3(Map<String, String> additionalData3) {
        this.additionalData3 = additionalData3;
    }

    public Map<String, String> getStripImage() {
        return stripImage;
    }

    public void setStripImage(Map<String, String> stripImage) {
        this.stripImage = stripImage;
    }
}
