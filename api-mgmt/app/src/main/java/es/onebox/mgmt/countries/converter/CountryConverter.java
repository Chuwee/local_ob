package es.onebox.mgmt.countries.converter;

import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.mgmt.countries.dto.CountryDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.CountryWithTaxCalculationDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.MasterdataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CountryConverter {

    private CountryConverter() {
    }

    public static IdNameCodeDTO fromEntity(MasterdataValue entity) {
        if (entity == null) {
            return null;
        }
        IdNameCodeDTO country = new IdNameCodeDTO();
        country.setCode(entity.getCode());
        country.setId(entity.getId());
        country.setName(entity.getName());

        return country;
    }

    public static List<IdNameCodeDTO> fromEntities(List<MasterdataValue> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }
        return entities.stream().map(CountryConverter::fromEntity).collect(Collectors.toList());
    }

    public static CountryDTO fromCountryWithTaxCalculation(CountryWithTaxCalculationDTO entity) {
        if (entity == null) {
            return null;
        }
        CountryDTO country = new CountryDTO();
        country.setId(entity.getId());
        country.setCode(entity.getCode());
        country.setName(entity.getName());
        country.setTaxCalculation(entity.getTaxCalculation());
        return country;
    }

    public static List<CountryDTO> fromCountriesWithTaxCalculation(List<CountryWithTaxCalculationDTO> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }
        return entities.stream().map(CountryConverter::fromCountryWithTaxCalculation).collect(Collectors.toList());
    }

}
