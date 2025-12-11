package es.onebox.event.attributes;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.datasources.ms.entity.dto.Attribute;
import es.onebox.event.datasources.ms.entity.dto.AttributeSelectionType;
import es.onebox.event.datasources.ms.entity.dto.AttributeTexts;
import es.onebox.event.datasources.ms.entity.dto.AttributeType;
import es.onebox.event.datasources.ms.entity.dto.AttributeValue;
import es.onebox.event.exception.MsEventErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

public class AttributeServiceTest {

    @InjectMocks
    private AttributeService service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void checkNumericValue_valid() {
        Attribute attribute = getAttribute(1L, 5, 10, AttributeType.NUMERIC);

        AttributeRequestValueDTO requestValueDTO = new AttributeRequestValueDTO();

        requestValueDTO.setValue("8");
        service.checkNumericValue(attribute, requestValueDTO);

        requestValueDTO.setValue("5");
        service.checkNumericValue(attribute, requestValueDTO);

        requestValueDTO.setValue("10");
        service.checkNumericValue(attribute, requestValueDTO);

        attribute = getAttribute(1L, 5, null, AttributeType.NUMERIC);

        requestValueDTO.setValue("5");
        service.checkNumericValue(attribute, requestValueDTO);

        requestValueDTO.setValue("50");
        service.checkNumericValue(attribute, requestValueDTO);

        attribute = getAttribute(1L, null, 10, AttributeType.NUMERIC);

        requestValueDTO.setValue("10");
        service.checkNumericValue(attribute, requestValueDTO);

        requestValueDTO.setValue("1");
        service.checkNumericValue(attribute, requestValueDTO);
    }

    @Test
    public void checkNumericValue_invalid() {
        Attribute attribute = getAttribute(1L, 5, 10, AttributeType.NUMERIC);

        AttributeRequestValueDTO requestValueDTO = new AttributeRequestValueDTO();

        try {
            requestValueDTO.setValue("4");
            service.checkNumericValue(attribute, requestValueDTO);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventErrorCode.ATTRIBUTE_INVALID_VALUE.getErrorCode(), e.getErrorCode());
        }

        try {
            requestValueDTO.setValue("11");
            service.checkNumericValue(attribute, requestValueDTO);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventErrorCode.ATTRIBUTE_INVALID_VALUE.getErrorCode(), e.getErrorCode());
        }

        attribute = getAttribute(1L, 5, null, AttributeType.NUMERIC);

        try {
            requestValueDTO.setValue("4");
            service.checkNumericValue(attribute, requestValueDTO);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventErrorCode.ATTRIBUTE_INVALID_VALUE.getErrorCode(), e.getErrorCode());
        }

        attribute = getAttribute(1L, null, 10, AttributeType.NUMERIC);

        try {
            requestValueDTO.setValue("11");
            service.checkNumericValue(attribute, requestValueDTO);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventErrorCode.ATTRIBUTE_INVALID_VALUE.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    public void checkStringValue_valid() {
        Attribute attribute = getAttribute(1L, 5, 10, AttributeType.ALPHANUMERIC);

        AttributeRequestValueDTO requestValueDTO = new AttributeRequestValueDTO();

        requestValueDTO.setValue("abcdefgh");
        service.checkStringValue(attribute, requestValueDTO);

        requestValueDTO.setValue("abcde");
        service.checkStringValue(attribute, requestValueDTO);

        requestValueDTO.setValue("abcdefghij");
        service.checkStringValue(attribute, requestValueDTO);

        attribute = getAttribute(1L, 5, null, AttributeType.NUMERIC);

        requestValueDTO.setValue("abcde");
        service.checkStringValue(attribute, requestValueDTO);

        requestValueDTO.setValue("abcdefghijabcdefghijabcdefghijabcdefghijabcdefghij");
        service.checkStringValue(attribute, requestValueDTO);

        attribute = getAttribute(1L, null, 10, AttributeType.NUMERIC);

        requestValueDTO.setValue("abcdefghij");
        service.checkStringValue(attribute, requestValueDTO);

        requestValueDTO.setValue("a");
        service.checkStringValue(attribute, requestValueDTO);
    }

    @Test
    public void checkStringValue_invalid() {
        Attribute attribute = getAttribute(1L, 5, 10, AttributeType.NUMERIC);

        AttributeRequestValueDTO requestValueDTO = new AttributeRequestValueDTO();

        try {
            requestValueDTO.setValue("abcd");
            service.checkStringValue(attribute, requestValueDTO);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventErrorCode.ATTRIBUTE_INVALID_VALUE.getErrorCode(), e.getErrorCode());
        }

        try {
            requestValueDTO.setValue("abcdefghijk");
            service.checkStringValue(attribute, requestValueDTO);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventErrorCode.ATTRIBUTE_INVALID_VALUE.getErrorCode(), e.getErrorCode());
        }

        attribute = getAttribute(1L, 5, null, AttributeType.NUMERIC);

        try {
            requestValueDTO.setValue("abcd");
            service.checkStringValue(attribute, requestValueDTO);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventErrorCode.ATTRIBUTE_INVALID_VALUE.getErrorCode(), e.getErrorCode());
        }

        attribute = getAttribute(1L, null, 10, AttributeType.NUMERIC);

        try {
            requestValueDTO.setValue("abcdefghijk");
            service.checkStringValue(attribute, requestValueDTO);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventErrorCode.ATTRIBUTE_INVALID_VALUE.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    public void checkDefinedValue_valid() {
        Attribute attribute = getDefinedAttribute(1L, AttributeSelectionType.SINGLE, Arrays.asList(1L, 2L, 3L));

        AttributeRequestValueDTO requestValueDTO = new AttributeRequestValueDTO();
        requestValueDTO.setSelected(new ArrayList<>());
        requestValueDTO.getSelected().add(2L);
        service.checkDefinedType(attribute, requestValueDTO);

        attribute = getDefinedAttribute(1L, AttributeSelectionType.MULTIPLE, Arrays.asList(1L, 2L, 3L));

        requestValueDTO = new AttributeRequestValueDTO();
        requestValueDTO.setSelected(new ArrayList<>());
        requestValueDTO.getSelected().add(2L);
        requestValueDTO.getSelected().add(3L);
        service.checkDefinedType(attribute, requestValueDTO);
    }

    @Test
    public void checkDefinedValue_invalid() {
        Attribute attribute = getDefinedAttribute(1L, AttributeSelectionType.SINGLE, Arrays.asList(1L, 2L, 3L));

        AttributeRequestValueDTO requestValueDTO = new AttributeRequestValueDTO();

        try {
            requestValueDTO = new AttributeRequestValueDTO();
            requestValueDTO.setSelected(new ArrayList<>());
            requestValueDTO.getSelected().add(2L);
            requestValueDTO.getSelected().add(3L);
            service.checkDefinedType(attribute, requestValueDTO);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventErrorCode.ATTRIBUTE_INVALID_VALUE.getErrorCode(), e.getErrorCode());
        }

        try {
            requestValueDTO = new AttributeRequestValueDTO();
            requestValueDTO.setSelected(new ArrayList<>());
            requestValueDTO.getSelected().add(4L);
            service.checkDefinedType(attribute, requestValueDTO);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventErrorCode.ATTRIBUTE_INVALID_VALUE.getErrorCode(), e.getErrorCode());
        }

        attribute = getDefinedAttribute(1L, AttributeSelectionType.MULTIPLE, Arrays.asList(1L, 2L, 3L));

        try {
            requestValueDTO = new AttributeRequestValueDTO();
            requestValueDTO.setSelected(new ArrayList<>());
            requestValueDTO.getSelected().add(3L);
            requestValueDTO.getSelected().add(4L);
            service.checkDefinedType(attribute, requestValueDTO);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventErrorCode.ATTRIBUTE_INVALID_VALUE.getErrorCode(), e.getErrorCode());
        }

        try {
            requestValueDTO = new AttributeRequestValueDTO();
            requestValueDTO.setSelected(new ArrayList<>());
            requestValueDTO.getSelected().add(4L);
            service.checkDefinedType(attribute, requestValueDTO);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(MsEventErrorCode.ATTRIBUTE_INVALID_VALUE.getErrorCode(), e.getErrorCode());
        }
    }

    private Attribute getDefinedAttribute(Long id, AttributeSelectionType attributeSelectionType, List<Long> valueIds) {
        Attribute attribute = new Attribute();
        attribute.setId(id);
        attribute.setSelectionType(attributeSelectionType.getId());
        attribute.setType(AttributeType.DEFINED.getId());
        attribute.setTexts(new AttributeTexts());
        attribute.getTexts().setValues(new ArrayList<>());

        AttributeValue attributeValue;
        for (Long valueId : valueIds) {
            attributeValue = new AttributeValue();
            attributeValue.setId(valueId);
            attribute.getTexts().getValues().add(attributeValue);
        }

        return attribute;
    }

    private Attribute getAttribute(Long id, Integer min, Integer max, AttributeType attributeType) {
        Attribute attribute = new Attribute();
        attribute.setId(id);
        attribute.setMin(min);
        attribute.setMax(max);
        attribute.setType(attributeType.getId());
        return attribute;
    }

}
