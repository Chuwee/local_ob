package es.onebox.fever.dto.city;

import java.io.Serializable;

public class FeverCityData implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private FeverCitiesDTO data;

    public FeverCitiesDTO getData() {
        return data;
    }

    public void setData(FeverCitiesDTO data) {
        this.data = data;
    }
}
