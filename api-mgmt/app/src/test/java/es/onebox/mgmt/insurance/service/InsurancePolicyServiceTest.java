package es.onebox.mgmt.insurance.service;


import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.entity.dto.IdValueCodeDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.insurance.dto.InsurancePolicyLanguage;
import es.onebox.mgmt.datasources.ms.insurance.dto.InsurancePolicyV1;
import es.onebox.mgmt.datasources.ms.insurance.dto.Insurer;
import es.onebox.mgmt.datasources.ms.insurance.repository.InsurancePoliciesRepository;
import es.onebox.mgmt.insurance.dto.InsurancePolicyCreateDTO;
import es.onebox.mgmt.insurance.dto.InsurancePolicyDTO;
import es.onebox.mgmt.insurance.enums.PolicyState;
import es.onebox.mgmt.validation.ValidationService;
import es.onebox.utils.ObjectRandomizer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InsurancePolicyServiceTest {

    @InjectMocks
    private InsurancePolicyService insurancePolicyService;

    @Mock
    private InsurancePoliciesRepository insurancePoliciesRepository;

    @Mock
    private MasterdataService masterdataService;

    @Mock
    private ValidationService validationService;

    @Mock
    private EntitiesRepository entitiesRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPolicy() {
        Integer insurerId = ObjectRandomizer.randomInteger();

        InsurancePolicyV1 policyToGet = getInsurancePolicyV1(insurerId);

        Map<Long, String> languageMap = Map.of(1L, "es", 2L, "en");
        Mockito.when(masterdataService.getLanguagesByIds()).thenReturn(languageMap);

        Mockito.when(insurancePoliciesRepository.getPolicyDetails(Mockito.anyInt(), Mockito.anyInt())).thenReturn(policyToGet);

        InsurancePolicyDTO policyDTO = insurancePolicyService.getPolicyDetails(insurerId, 1);
        assertNotNull(policyDTO);
        assertEquals(1, policyDTO.getId());
        assertEquals(insurerId, policyDTO.getInsurerId());
        assertEquals("test name", policyDTO.getName());
        assertEquals("000-001", policyDTO.getPolicyNumber());
        assertEquals("description", policyDTO.getDescription());
        assertEquals(2, policyDTO.getDaysAheadLimit());
        assertEquals(2.2, policyDTO.getInsurerBenefitsFix());
        assertEquals(3.3, policyDTO.getInsurerBenefitsPercent());
        assertEquals(4.4, policyDTO.getOperatorBenefitsFix());
        assertEquals(5.5, policyDTO.getOperatorBenefitsPercent());
        assertEquals(6.6, policyDTO.getTaxes());
        assertEquals(PolicyState.ACTIVE, policyDTO.getPolicyState());
        
        assertNotNull(policyDTO.getLanguages());
        assertEquals("es", policyDTO.getLanguages().getDefaultLanguage());
        assertEquals(2, policyDTO.getLanguages().getSelected().size());
        assertEquals(List.of("es", "en"), policyDTO.getLanguages().getSelected());
    }

    @NotNull
    private static InsurancePolicyV1 getInsurancePolicyV1(Integer insurerId) {
        InsurancePolicyLanguage defaultLanguage = new InsurancePolicyLanguage();
        defaultLanguage.setId(1);
        defaultLanguage.setIsDefault(true);

        InsurancePolicyLanguage secondLanguage = new InsurancePolicyLanguage();
        secondLanguage.setId(2);
        secondLanguage.setIsDefault(false);

        List<InsurancePolicyLanguage> languages = List.of(defaultLanguage, secondLanguage);

        InsurancePolicyV1 policyToGet = new InsurancePolicyV1();
        policyToGet.setId(1);
        policyToGet.setInsurerId(insurerId);
        policyToGet.setName("test name");
        policyToGet.setPolicyNumber("000-001");
        policyToGet.setDescription("description");
        policyToGet.setDaysAheadLimit(2);
        policyToGet.setInsurerBenefitsFix(2.2);
        policyToGet.setInsurerBenefitsPercent(3.3);
        policyToGet.setOperatorBenefitsFix(4.4);
        policyToGet.setOperatorBenefitsPercent(5.5);
        policyToGet.setTaxes(6.6);
        policyToGet.setState(PolicyState.ACTIVE);
        policyToGet.setLanguages(languages);
        return policyToGet;
    }

    @Test
    void createPolicy() {
        Integer insurerId = ObjectRandomizer.randomInteger();
        Integer operatorId = 12;

        Insurer insurer = new Insurer();
        insurer.setId(insurerId);
        insurer.setOperatorId(operatorId);
        Mockito.when(validationService.getAndCheckInsurer(insurerId)).thenReturn(insurer);

        Operator operator = new Operator();
        IdValueCodeDTO defaultLanguage = new IdValueCodeDTO(1L);
        IdValueCodeDTO secondLanguage = new IdValueCodeDTO(2L);
        operator.setLanguage(defaultLanguage);
        operator.setSelectedLanguages(List.of(defaultLanguage, secondLanguage));
        Mockito.when(entitiesRepository.getOperator(operatorId.longValue())).thenReturn(operator);

        InsurancePolicyV1 policyToGet = new InsurancePolicyV1();
        policyToGet.setId(1);
        policyToGet.setInsurerId(insurerId);
        policyToGet.setName("test name");
        policyToGet.setPolicyNumber("000-001");
        policyToGet.setTaxes(6.6);
        policyToGet.setState(PolicyState.INACTIVE);

        InsurancePolicyCreateDTO createDTO = new InsurancePolicyCreateDTO();
        createDTO.setName("test name");
        createDTO.setPolicyNumber("000-001");
        createDTO.setTaxes(6.6);

        Mockito.when(insurancePoliciesRepository.createPolicy(
                Mockito.eq(insurerId), Mockito.any(InsurancePolicyV1.class))
        ).thenReturn(policyToGet);

        InsurancePolicyDTO policyDTO =
                insurancePolicyService.createPolicy(insurerId, createDTO);

        assertNotNull(policyDTO);
        assertEquals(1, policyDTO.getId());
        assertEquals(insurerId, policyDTO.getInsurerId());
        assertEquals("test name", policyDTO.getName());
        assertEquals("000-001", policyDTO.getPolicyNumber());
        assertEquals(6.6, policyDTO.getTaxes());
        assertEquals(PolicyState.INACTIVE, policyDTO.getPolicyState());
        
        Mockito.verify(validationService).getAndCheckInsurer(insurerId);
        Mockito.verify(entitiesRepository).getOperator(operatorId.longValue());
        Mockito.verify(insurancePoliciesRepository).createPolicy(
                Mockito.eq(insurerId), 
                Mockito.argThat(policy -> 
                    policy.getLanguages() != null && 
                    !policy.getLanguages().isEmpty() &&
                    policy.getLanguages().size() == 2 &&
                    policy.getLanguages().stream().anyMatch(l -> l.getIsDefault() != null && l.getIsDefault())
                )
        );
    }
}