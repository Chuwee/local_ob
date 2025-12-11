package es.onebox.mgmt.insurance.service;

import es.onebox.mgmt.datasources.ms.insurance.dto.InsuranceRange;
import es.onebox.mgmt.datasources.ms.insurance.repository.InsuranceRangesRepository;
import es.onebox.mgmt.insurance.converter.InsuranceRangeConverter;
import es.onebox.mgmt.insurance.dto.InsuranceRangeDTO;
import es.onebox.mgmt.insurance.dto.InsuranceRangeDTOList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;

class InsuranceRangeServiceTest {

    @Mock
    private InsuranceRangesRepository insuranceRangesRepository;

    @InjectMocks
    private InsuranceRangeService insuranceRangeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getRangesByPolicyId() {

        InsuranceRange insuranceRange = new InsuranceRange();
        insuranceRange.setId(1);
        insuranceRange.setPolicyId(2);
        insuranceRange.setName("one");
        insuranceRange.setMin(0.0);
        insuranceRange.setMax(100.0);
        insuranceRange.setChargeFix(10.0);
        insuranceRange.setChargeMin(1.0);
        insuranceRange.setChargeMax(90.0);
        insuranceRange.setChargePercent(15.5);

        List<InsuranceRange> rangeList = List.of(insuranceRange);
        Mockito.when(insuranceRangesRepository.getRangesByPolicyId(anyInt(), anyInt()))
                .thenReturn(rangeList);


        List<InsuranceRangeDTO> resultList = insuranceRangeService.getRangesByPolicyId(1, 2);
        InsuranceRangeDTO resultDTO = resultList.get(0);

        Assertions.assertEquals(0.0, resultDTO.getFrom());
        Assertions.assertEquals(100.0, resultDTO.getTo());
        Assertions.assertEquals(10.0, resultDTO.getValues().getFixed());
        Assertions.assertEquals(1.0, resultDTO.getValues().getMin());
        Assertions.assertEquals(90.0, resultDTO.getValues().getMax());
        Assertions.assertEquals(15.5, resultDTO.getValues().getPercentage());
    }

    @Test
    void updateRangesByPolicyId() {

        InsuranceRange insuranceRange1 = new InsuranceRange();
        insuranceRange1.setId(1);
        insuranceRange1.setPolicyId(2);
        insuranceRange1.setName("one");
        insuranceRange1.setMin(0.0);
        insuranceRange1.setMax(100.0);
        insuranceRange1.setChargeFix(10.0);
        insuranceRange1.setChargeMin(1.0);
        insuranceRange1.setChargeMax(90.0);

        InsuranceRange insuranceRange2 = new InsuranceRange();
        insuranceRange2.setId(12);
        insuranceRange2.setPolicyId(22);
        insuranceRange2.setName("two");
        insuranceRange2.setMin(100.0);
        insuranceRange2.setMax(0.0);
        insuranceRange2.setChargeMin(1.2);
        insuranceRange2.setChargeMax(90.2);
        insuranceRange2.setChargePercent(15.52);


        List<InsuranceRange> insuranceRangeList = List.of(insuranceRange1, insuranceRange2);
        Mockito.when(insuranceRangesRepository.updateRangesByPolicyId(anyInt(), anyInt(), anyList()))
                .thenReturn(insuranceRangeList);


        InsuranceRangeDTOList request = new InsuranceRangeDTOList();
        request.add(InsuranceRangeConverter.toDTO(insuranceRange1));
        request.add(InsuranceRangeConverter.toDTO(insuranceRange2));

        List<InsuranceRangeDTO> resultList = insuranceRangeService.updateRangesByPolicyId(1, 2, request);


        InsuranceRangeDTO resultDTO1 = resultList.get(0);
        InsuranceRangeDTO resultDTO2 = resultList.get(1);

        Assertions.assertEquals(0.0, resultDTO1.getFrom());
        Assertions.assertEquals(100.0, resultDTO1.getTo());
        Assertions.assertEquals(10.0, resultDTO1.getValues().getFixed());
        Assertions.assertEquals(1.0, resultDTO1.getValues().getMin());
        Assertions.assertEquals(90.0, resultDTO1.getValues().getMax());

        Assertions.assertEquals(100.0, resultDTO2.getFrom());
        Assertions.assertEquals(0.0, resultDTO2.getTo());
        Assertions.assertEquals(1.2, resultDTO2.getValues().getMin());
        Assertions.assertEquals(90.2, resultDTO2.getValues().getMax());
        Assertions.assertEquals(15.52, resultDTO2.getValues().getPercentage());
    }
}
