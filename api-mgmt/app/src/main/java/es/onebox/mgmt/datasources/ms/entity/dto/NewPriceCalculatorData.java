package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serializable;
import java.util.Map;

public class NewPriceCalculatorData implements Serializable {

    private String className;

    private Map<String, Object> data;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
