package es.onebox.fever.repository;

import es.onebox.fever.datasource.FeverDatasource;
import es.onebox.fever.dto.city.FeverCityData;
import org.springframework.stereotype.Repository;

@Repository
public class FeverRepository {

    private final FeverDatasource feverDatasource;

    public FeverRepository(FeverDatasource feverDatasource) {
        this.feverDatasource = feverDatasource;
    }

    public FeverCityData getCity(String cityName, String countryCode) {
        return this.feverDatasource.getCity(cityName, countryCode);
    }
}
