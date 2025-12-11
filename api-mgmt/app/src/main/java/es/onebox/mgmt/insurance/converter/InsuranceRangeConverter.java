package es.onebox.mgmt.insurance.converter;

import es.onebox.mgmt.datasources.ms.insurance.dto.InsuranceRange;
import es.onebox.mgmt.insurance.dto.InsuranceRangeDTO;
import es.onebox.mgmt.insurance.dto.RangeValueDTO;

import java.util.ArrayList;
import java.util.List;

public class InsuranceRangeConverter {

    private InsuranceRangeConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static List<InsuranceRangeDTO> toDTOs(List<InsuranceRange> insuranceRangeList) {
        List<InsuranceRangeDTO> insuranceRangeDTOs = new ArrayList<>();
        insuranceRangeList.forEach(range -> insuranceRangeDTOs.add(InsuranceRangeConverter.toDTO(range)));
        return insuranceRangeDTOs;
    }

    public static List<InsuranceRange> toEntities(List<InsuranceRangeDTO> insuranceRangeList) {
        List<InsuranceRange> insuranceRanges = new ArrayList<>();
        insuranceRangeList.forEach(range -> insuranceRanges.add(InsuranceRangeConverter.toEntity(range)));
        return insuranceRanges;
    }


    public static InsuranceRangeDTO toDTO(InsuranceRange insuranceRange) {
        if (insuranceRange == null) {
            return null;
        }
        InsuranceRangeDTO result = new InsuranceRangeDTO();
        RangeValueDTO valueDTO = new RangeValueDTO();
        result.setValues(valueDTO);

        result.setFrom(insuranceRange.getMin());
        result.setTo(insuranceRange.getMax());
        valueDTO.setFixed(insuranceRange.getChargeFix());
        valueDTO.setPercentage(insuranceRange.getChargePercent());
        valueDTO.setMin(insuranceRange.getChargeMin());
        valueDTO.setMax(insuranceRange.getChargeMax());

        return result;
    }

    public static InsuranceRange toEntity(InsuranceRangeDTO insuranceRangeDTO) {
        if (insuranceRangeDTO == null) {
            return null;
        }
        InsuranceRange result = new InsuranceRange();

        result.setMin(insuranceRangeDTO.getFrom());
        result.setMax(insuranceRangeDTO.getTo());
        result.setChargeFix(insuranceRangeDTO.getValues().getFixed());
        result.setChargePercent(insuranceRangeDTO.getValues().getPercentage());
        result.setChargeMin(insuranceRangeDTO.getValues().getMin());
        result.setChargeMax(insuranceRangeDTO.getValues().getMax());

        return result;
    }
}
