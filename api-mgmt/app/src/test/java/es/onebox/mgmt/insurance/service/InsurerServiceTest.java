package es.onebox.mgmt.insurance.service;


import es.onebox.mgmt.datasources.ms.entity.dto.EntitySearchFilter;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.insurance.dto.Insurer;
import es.onebox.mgmt.datasources.ms.insurance.dto.InsurerBasic;
import es.onebox.mgmt.datasources.ms.insurance.dto.Insurers;
import es.onebox.mgmt.datasources.ms.insurance.dto.ResponseEntities;
import es.onebox.mgmt.datasources.ms.insurance.dto.ResponseEntity;
import es.onebox.mgmt.datasources.ms.insurance.repository.InsurerRepository;
import es.onebox.mgmt.insurance.dto.InsurerCreateDTO;
import es.onebox.mgmt.insurance.dto.InsurerDTO;
import es.onebox.mgmt.insurance.dto.InsurerDTORequest;
import es.onebox.mgmt.insurance.dto.InsurersDTO;
import es.onebox.mgmt.insurance.dto.SearchInsurerFilterDTO;
import es.onebox.mgmt.validation.ValidationService;
import es.onebox.utils.ObjectRandomizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

class InsurerServiceTest {

    @InjectMocks
    private InsurerService insurerService;

    @Mock
    private InsurerRepository insurerRepository;
    @Mock
    private EntitiesRepository entitiesRepository;
    @Mock
    private ValidationService validationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getInsurer() {
        Integer insurerId = ObjectRandomizer.randomInteger();

        Insurer insurerRecord = new Insurer();
        insurerRecord.setId(23);
        insurerRecord.setOperatorId(12);
        insurerRecord.setName("INSURER");
        insurerRecord.setDescription("description");
        insurerRecord.setTaxId("CIF");
        ResponseEntities responseEntities = getResponseEntities();

        Mockito.when(validationService.getAndCheckInsurer(Mockito.anyInt())).thenReturn(insurerRecord);
        Mockito.when(entitiesRepository.getListOfEntities(any(EntitySearchFilter.class))).thenReturn(responseEntities);

        InsurerDTO insurerDTO = insurerService.getInsurer(insurerId);
        assertNotNull(insurerDTO);
        assertEquals("Onebox Test", insurerDTO.getOperator().getName());
    }

    @Test
    void createInsurer() {
        Integer insurerId = ObjectRandomizer.randomInteger();
        int operatorId = 23;
        String name = "Insurer one";
        String taxId = "B1234567";
        String taxName = "Ins S.L.";
        String email = "ins@ins.com";
        String phone = "655256652";
        String address = "Barcelona, Pl.Mayor, ed 1";
        String zipCode = "08001";

        Insurer insurer = new Insurer();
        insurer.setId(insurerId);
        insurer.setOperatorId(operatorId);
        insurer.setName(name);
        insurer.setTaxId(taxId);
        insurer.setTaxName(taxName);
        insurer.setContactEmail(email);
        insurer.setPhone(phone);
        insurer.setAddress(address);
        insurer.setZipCode(zipCode);

        InsurerCreateDTO dto = new InsurerCreateDTO();
        dto.setOperatorId(operatorId);
        dto.setName(name);
        dto.setTaxId(taxId);
        dto.setTaxName(taxName);
        dto.setContactEmail(email);
        dto.setPhone(phone);
        dto.setAddress(address);
        dto.setZipCode(zipCode);

        Mockito.when(insurerRepository.createInsurer(Mockito.any(Insurer.class))).thenReturn(insurer);

        ResponseEntities responseEntities = getResponseEntities();
        Mockito.when(entitiesRepository.getListOfEntities(any(EntitySearchFilter.class))).thenReturn(responseEntities);

        InsurerDTO result = insurerService.createInsurer(dto);

        assertNotNull(result);
        Assertions.assertEquals(insurerId, result.getId());
        Assertions.assertEquals(operatorId, result.getOperator().getId());
        Assertions.assertEquals(name, result.getName());
        Assertions.assertEquals(taxId, result.getTaxId());
        Assertions.assertEquals(taxName, result.getTaxName());
        Assertions.assertEquals(email, result.getContactEmail());
        Assertions.assertEquals(phone, result.getPhone());
        Assertions.assertEquals(address, result.getAddress());
        Assertions.assertEquals(zipCode, result.getZipCode());
    }

    @Test
    void updateInsurer() {
        Integer insurerId = ObjectRandomizer.randomInteger();

        Insurer insurerRecord = new Insurer();
        insurerRecord.setId(23);
        insurerRecord.setOperatorId(12);
        insurerRecord.setName("INSURER");
        insurerRecord.setDescription("description");
        insurerRecord.setTaxId("CIF");

        InsurerDTORequest insurerDTORequest = new InsurerDTORequest();
        insurerDTORequest.setId(23);
        insurerDTORequest.setName("INSURER_UPDATED");
        insurerDTORequest.setDescription("description_updated");
        insurerDTORequest.setTaxId("CIF_UPDATED");

        Mockito.when(validationService.getAndCheckInsurer(Mockito.anyInt())).thenReturn(insurerRecord);
        Mockito.when(entitiesRepository.getListOfEntities(Mockito.any())).thenReturn(getResponseEntities());
        Mockito.when(insurerRepository.updateInsurer(any(Insurer.class))).thenReturn(insurerRecord);

        InsurerDTO insurerDTOResult = insurerService.updateInsurer(insurerId, insurerDTORequest);
        assertNotNull(insurerDTOResult);
        assertEquals("Onebox Test", insurerDTOResult.getOperator().getName());
        assertEquals("INSURER_UPDATED", insurerDTOResult.getName());
        assertEquals("description_updated", insurerDTOResult.getDescription());
        assertEquals("CIF_UPDATED", insurerDTOResult.getTaxId());
    }

    @Test
    void searchInsurers() {
        ResponseEntities responseEntities = getResponseEntities();
        Insurers insurers = new Insurers();
        List<InsurerBasic> result = getInsurerRecords();
        insurers.setData(result);

        SearchInsurerFilterDTO filter = new SearchInsurerFilterDTO();
        filter.setQ("test");
        filter.setOperatorId(1L);

        Mockito.when(insurerRepository.searchInsurers(any())).thenReturn(insurers);
        Mockito.when(entitiesRepository.getListOfEntities(any(EntitySearchFilter.class))).thenReturn(responseEntities);

        InsurersDTO insurersDTO = insurerService.searchInsurers(filter);

        assertNotNull(insurersDTO);
        assertNotNull(insurersDTO.getData());
        assertEquals(2, insurersDTO.getData().size());
        assertEquals(result.get(0).getOperatorId(), result.get(1).getOperatorId());
        assertEquals("Onebox Test", insurersDTO.getData().get(0).getOperator().getName());
    }

    private ResponseEntities getResponseEntities() {
        ResponseEntities responseEntities = new ResponseEntities();
        List<ResponseEntity> operatorEntitiesResult = List.of(new ResponseEntity(1, "Onebox Test"));
        responseEntities.setData(operatorEntitiesResult);
        return responseEntities;
    }


    private List<InsurerBasic> getInsurerRecords() {
        List<InsurerBasic> result = new ArrayList<>();
        InsurerBasic insurerRecord = new Insurer();
        insurerRecord.setId(1);
        insurerRecord.setOperatorId(1);
        insurerRecord.setName("Insurer 1");
        insurerRecord.setTaxName("taxName 1");
        insurerRecord.setContactEmail("contactEmail 1");
        insurerRecord.setPhone("phone 1");
        result.add(insurerRecord);
        InsurerBasic insurerRecord2 = new Insurer();
        insurerRecord2.setId(2);
        insurerRecord2.setOperatorId(1);
        insurerRecord2.setName("Insurer 2");
        insurerRecord2.setTaxName("taxName 2");
        insurerRecord2.setContactEmail("contactEmail 2");
        insurerRecord2.setPhone("phone 2");
        result.add(insurerRecord2);

        return result;
    }
}