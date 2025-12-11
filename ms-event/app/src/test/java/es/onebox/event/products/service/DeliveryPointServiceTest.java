package es.onebox.event.products.service;

import es.onebox.utils.ObjectRandomizer;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.datasources.ms.entity.dto.CountryDTO;
import es.onebox.event.datasources.ms.entity.dto.CountrySubdivisionDTO;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.dao.DeliveryPointDao;
import es.onebox.event.products.dao.ProductSessionDao;
import es.onebox.event.products.domain.DeliveryPointRecord;
import es.onebox.event.products.dto.CreateDeliveryPointDTO;
import es.onebox.event.products.dto.DeliveryPointDTO;
import es.onebox.event.products.dto.DeliveryPointsDTO;
import es.onebox.event.products.dto.SearchDeliveryPointFilterDTO;
import es.onebox.event.products.dto.UpdateDeliveryPointDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelDeliveryPointRecord;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

class DeliveryPointServiceTest {
    @Mock
    DeliveryPointDao deliveryPointDao;
    @Mock
    EntitiesRepository entitiesRepository;
    @Mock
    RefreshDataService refreshDataService;
    @Mock
    ProductSessionDao productSessionDao;

    @InjectMocks
    DeliveryPointService deliveryPointService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProductDeliveryPoints() {

        Mockito.when(entitiesRepository.getEntity(anyInt())).thenReturn(null);
        Mockito.when(deliveryPointDao.findDeliveryPoints(anyLong(), anyString())).thenReturn(null);

        CreateDeliveryPointDTO createProductDeliveryPointDTO = ObjectRandomizer.random(CreateDeliveryPointDTO.class);
        try {
            deliveryPointService.createDeliveryPoint(createProductDeliveryPointDTO);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.ENTITY_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        EntityDTO entityDTO = ObjectRandomizer.random(EntityDTO.class);
        Mockito.when(entitiesRepository.getEntity(anyInt())).thenReturn(entityDTO);

        Mockito.when(entitiesRepository.getCountry(anyInt())).thenReturn(null);
        try {
            deliveryPointService.createDeliveryPoint(createProductDeliveryPointDTO);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.COUNTRY_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        CountryDTO countryDTO = ObjectRandomizer.random(CountryDTO.class);
        Mockito.when(entitiesRepository.getCountry(anyInt())).thenReturn(countryDTO);

        Mockito.when(entitiesRepository.getCountrySubdivision(anyInt())).thenReturn(null);
        try {
            deliveryPointService.createDeliveryPoint(createProductDeliveryPointDTO);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.COUNTRY_SUBDIVISION_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        CountrySubdivisionDTO countrySubdivisionDTO = ObjectRandomizer.random(CountrySubdivisionDTO.class);
        countrySubdivisionDTO.setZipCode("08");
        Mockito.when(entitiesRepository.getCountrySubdivision(anyInt())).thenReturn(countrySubdivisionDTO);
        createProductDeliveryPointDTO.getLocation().setZipCode("25620");

        try {
            deliveryPointService.createDeliveryPoint(createProductDeliveryPointDTO);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.INVALID_POSTAL_CODE_COUNTRY_SUBDIVISION.getErrorCode(), e.getErrorCode());
        }

        createProductDeliveryPointDTO.getLocation().setZipCode("08023");

        CpanelDeliveryPointRecord cpanelDeliveryPointRecord = new CpanelDeliveryPointRecord();
        cpanelDeliveryPointRecord.setDeliverypointid(2);
        cpanelDeliveryPointRecord.setEntityid(entityDTO.getId());
        cpanelDeliveryPointRecord.setName("dp 1");
        cpanelDeliveryPointRecord.setCountryid(21);
        cpanelDeliveryPointRecord.setCountrysubdivisionid(221);
        cpanelDeliveryPointRecord.setAddress("dp address 1");
        cpanelDeliveryPointRecord.setDeliverypointstatus(1);
        Mockito.when(deliveryPointDao.insert(any())).thenReturn(cpanelDeliveryPointRecord);

        deliveryPointService.createDeliveryPoint(createProductDeliveryPointDTO);
    }

    @Test
    void getProductDeliveryPoints() {

        Mockito.when(deliveryPointDao.getProductDeliveryPoints(any(), Mockito.anyLong())).thenReturn(null);
        try {
            deliveryPointService.getDeliveryPoint(ObjectRandomizer.randomLong());
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_DELIVERY_POINT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        List<DeliveryPointRecord> result = new ArrayList<>();
        DeliveryPointRecord productDeliveryPointRecord = new DeliveryPointRecord();
        productDeliveryPointRecord.setDeliverypointid(12);
        productDeliveryPointRecord.setDeliverypointstatus(0);
        productDeliveryPointRecord.setCountryid(1);
        productDeliveryPointRecord.setCountryName("dsfsdf");
        productDeliveryPointRecord.setAddress("2w3");
        productDeliveryPointRecord.setCountrysubdivisionid(2);
        productDeliveryPointRecord.setCountrySubdivisionName("dfsdf");
        productDeliveryPointRecord.setName("xdfsgdgf");
        productDeliveryPointRecord.setEntityid(21);
        result.add(productDeliveryPointRecord);
        Mockito.when(deliveryPointDao.getProductDeliveryPoints(any(), Mockito.anyLong())).thenReturn(result);

        DeliveryPointDTO productDeliveryPointDTO = deliveryPointService.getDeliveryPoint(ObjectRandomizer.randomLong());
        assertNotNull(productDeliveryPointDTO);
    }

    @Test
    void searchProductDeliveryPoints() {

        Mockito.when(deliveryPointDao.getProductDeliveryPoints(any(), Mockito.anyLong())).thenReturn(null);

        SearchDeliveryPointFilterDTO filter = ObjectRandomizer.random(SearchDeliveryPointFilterDTO.class);
        try {
            deliveryPointService.searchDeliveryPoint(filter);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_DELIVERY_POINT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        List<DeliveryPointRecord> result = new ArrayList<>();
        DeliveryPointRecord productDeliveryPointRecord = new DeliveryPointRecord();
        productDeliveryPointRecord.setDeliverypointid(12);
        productDeliveryPointRecord.setDeliverypointstatus(0);
        productDeliveryPointRecord.setCountryid(1);
        productDeliveryPointRecord.setCountryName("dsfsdf");
        productDeliveryPointRecord.setAddress("2w3");
        productDeliveryPointRecord.setCountrysubdivisionid(2);
        productDeliveryPointRecord.setCountrySubdivisionName("dfsdf");
        productDeliveryPointRecord.setName("xdfsgdgf");
        productDeliveryPointRecord.setEntityid(21);
        result.add(productDeliveryPointRecord);
        DeliveryPointRecord productDeliveryPointRecord2 = new DeliveryPointRecord();
        productDeliveryPointRecord2.setDeliverypointid(21);
        productDeliveryPointRecord2.setDeliverypointstatus(1);
        productDeliveryPointRecord2.setCountryid(1);
        productDeliveryPointRecord2.setCountryName("dsfsdf");
        productDeliveryPointRecord2.setAddress("sdfdsf");
        productDeliveryPointRecord2.setCountrysubdivisionid(4);
        productDeliveryPointRecord2.setCountrySubdivisionName("sdgfsdf");
        productDeliveryPointRecord2.setName("fsgsdfgs");
        productDeliveryPointRecord2.setEntityid(21);
        result.add(productDeliveryPointRecord2);

        Mockito.when(deliveryPointDao.getProductDeliveryPoints(any(), Mockito.isNull())).thenReturn(result);

        SearchDeliveryPointFilterDTO searchProductDeliveryPointFilterDTO = ObjectRandomizer.random(SearchDeliveryPointFilterDTO.class);
        DeliveryPointsDTO productDeliveryPointsDTO = deliveryPointService.searchDeliveryPoint(searchProductDeliveryPointFilterDTO);

        assertNotNull(productDeliveryPointsDTO);
        assertNotNull(productDeliveryPointsDTO.getData());
        assertEquals(2, productDeliveryPointsDTO.getData().size());
    }

    @Test
    void deleteProductDeliveryPoints() {
        Mockito.when(deliveryPointDao.findById(any())).thenReturn(null);
        try {
            deliveryPointService.deleteDeliveryPoint(Mockito.anyLong());
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_DELIVERY_POINT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        CpanelDeliveryPointRecord deliveryPointRecord = new CpanelDeliveryPointRecord();
        deliveryPointRecord.setDeliverypointid(12);
        deliveryPointRecord.setDeliverypointstatus(0);
        deliveryPointRecord.setCountryid(1);
        deliveryPointRecord.setAddress("2w3");
        deliveryPointRecord.setCountrysubdivisionid(2);
        deliveryPointRecord.setName("xdfsgdgf");
        deliveryPointRecord.setEntityid(21);
        Mockito.when(deliveryPointDao.findById(any())).thenReturn(deliveryPointRecord);

        Mockito.when(deliveryPointDao.update(Mockito.any())).thenReturn(deliveryPointRecord);
        deliveryPointService.deleteDeliveryPoint(Mockito.anyLong());
    }

    @Test
    void updateProductDeliveryPoints() {
        UpdateDeliveryPointDTO updateProductDeliveryPointDTO = ObjectRandomizer.random(UpdateDeliveryPointDTO.class);

        Mockito.when(deliveryPointDao.findDeliveryPoints(anyLong(), anyString())).thenReturn(null);
        Mockito.when(deliveryPointDao.findById(any())).thenReturn(null);
        try {
            deliveryPointService.updateDeliveryPoint(ObjectRandomizer.randomLong(), updateProductDeliveryPointDTO);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_DELIVERY_POINT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        CpanelDeliveryPointRecord deliveryPointRecord = new CpanelDeliveryPointRecord();
        deliveryPointRecord.setDeliverypointid(12);
        deliveryPointRecord.setDeliverypointstatus(0);
        deliveryPointRecord.setCountryid(1);
        deliveryPointRecord.setAddress("2w3");
        deliveryPointRecord.setCountrysubdivisionid(2);
        deliveryPointRecord.setName("xdfsgdgf");
        deliveryPointRecord.setEntityid(21);
        Mockito.when(deliveryPointDao.findById(any())).thenReturn(deliveryPointRecord);

        Mockito.when(deliveryPointDao.update(Mockito.any())).thenReturn(deliveryPointRecord);

        List<DeliveryPointRecord> result = new ArrayList<>();
        DeliveryPointRecord productDeliveryPointRecord1 = new DeliveryPointRecord();
        productDeliveryPointRecord1.setDeliverypointid(12);
        productDeliveryPointRecord1.setDeliverypointstatus(0);
        productDeliveryPointRecord1.setCountryid(1);
        productDeliveryPointRecord1.setCountryName("dsfsdf");
        productDeliveryPointRecord1.setAddress("2w3");
        productDeliveryPointRecord1.setCountrysubdivisionid(2);
        productDeliveryPointRecord1.setCountrySubdivisionName("dfsdf");
        productDeliveryPointRecord1.setName("xdfsgdgf");
        productDeliveryPointRecord1.setEntityid(21);
        result.add(productDeliveryPointRecord1);
        Mockito.when(deliveryPointDao.getProductDeliveryPoints(any(), anyLong())).thenReturn(result);
        deliveryPointService.updateDeliveryPoint(ObjectRandomizer.randomLong(), updateProductDeliveryPointDTO);
    }

}
