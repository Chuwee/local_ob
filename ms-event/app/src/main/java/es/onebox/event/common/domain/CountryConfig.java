package es.onebox.event.common.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Created by cgalindo on 15/06/2015.
 */
public class CountryConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private boolean active;
    private List<String> countries;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<String> getCountries() {
        return countries;
    }

    public void setCountries(List<String> countries) {
        this.countries = countries;
    }

}
