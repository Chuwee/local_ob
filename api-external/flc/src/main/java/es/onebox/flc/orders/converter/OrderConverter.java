package es.onebox.flc.orders.converter;

import es.onebox.common.datasources.ms.order.dto.AttributeInfo;
import es.onebox.common.datasources.ms.order.dto.AttributeValueInfo;
import es.onebox.common.datasources.ms.order.dto.BaseOrderDTO;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.common.datasources.ms.order.dto.OrderGroup;
import es.onebox.common.datasources.ms.order.dto.OrderGroupAttribute;
import es.onebox.common.datasources.ms.order.dto.OrderGroupDTO;
import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;
import es.onebox.common.datasources.ms.order.dto.PreOrderDTO;
import es.onebox.core.utils.optional.OptionalCollection;
import es.onebox.flc.orders.dto.Order;
import es.onebox.flc.orders.dto.OrderState;
import es.onebox.flc.orders.dto.OrderType;
import es.onebox.flc.orders.dto.Product;
import es.onebox.flc.orders.dto.ProductState;
import es.onebox.flc.orders.dto.ProductType;
import es.onebox.flc.orders.dto.groups.AttributeInfoDTO;
import es.onebox.flc.orders.dto.groups.AttributeScopes;
import es.onebox.flc.orders.dto.groups.AttributeSelectionTypes;
import es.onebox.flc.orders.dto.groups.AttributeValueDTO;
import es.onebox.flc.orders.dto.groups.AttributeValueInfoDTO;
import es.onebox.flc.orders.dto.groups.AttributeValueTypes;
import es.onebox.flc.orders.dto.groups.AttributeValuesDTO;
import es.onebox.flc.orders.dto.groups.DomainValueDTO;
import es.onebox.flc.orders.dto.groups.VisitorGroupDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderConverter {

    public static Order convert(BaseOrderDTO baseOrderDTO) {
        Order order = new Order();

        if (baseOrderDTO instanceof OrderDTO) {
            order.setCode(((OrderDTO) baseOrderDTO).getCode());
        } else if (baseOrderDTO instanceof PreOrderDTO) {
            order.setCode(((PreOrderDTO)baseOrderDTO).getCode());
        }

        if (baseOrderDTO.getStatus() != null) {
            if (baseOrderDTO.getStatus().getType() != null) {
                order.setType(OrderType.get(baseOrderDTO.getStatus().getType().getId()));
            }
            if (baseOrderDTO.getStatus().getState() != null) {
                order.setState(OrderState.get(baseOrderDTO.getStatus().getState().getId()));
            }
        }

        order.setChannelId(baseOrderDTO.getOrderData().getChannelId());

        if (baseOrderDTO.getDate().getPurchased() != null) {
            order.setOrderDate(baseOrderDTO.getDate().getPurchased());
            order.setTimeZone(baseOrderDTO.getDate().getTimeZone());
        }

        if (OptionalCollection.of(baseOrderDTO.getGroups()).hasElements()) {
            order.setGroups(getGroups(baseOrderDTO.getGroups()));
        }

        order.setRelatedOriginalCode(baseOrderDTO.getRelatedOriginalCode());
        order.setRelatedModicationCode(baseOrderDTO.getRelatedModicationCode());

        if (OptionalCollection.of(baseOrderDTO.getProducts()).hasElements()) {
            order.setProducts(getProducts(baseOrderDTO.getProducts()));
            order.setProductsNumber(baseOrderDTO.getProducts().size());
            order.setSessionGroups(getSessionGroups(baseOrderDTO.getProducts()));
            order.setEventSessions(getEventSessions(baseOrderDTO.getProducts()));
        }

        return order;
    }

    private static Map<Integer, List<Long>> getSessionGroups(List<OrderProductDTO> products) {
        Map<Integer, List<Long>> result = new HashMap<>();
        for (OrderProductDTO product : products) {
            if (product.getGroupId() != null) {
                Integer sessionId = product.getSessionId();
                if (!result.containsKey(sessionId)) {
                    result.put(sessionId, new ArrayList<>());
                }
                result.get(sessionId).add(product.getGroupId());
            }
        }
        return result;
    }

    private static Map<Integer, Set<Integer>> getEventSessions(List<OrderProductDTO> products) {
        return products.stream().collect(
                Collectors.groupingBy(OrderProductDTO::getEventId,
                        Collectors.mapping(OrderProductDTO::getSessionId, Collectors.toSet())));
    }

    private static List<Long> getGroups(List<OrderGroupDTO> groups) {
        return groups.stream()
                .map(OrderGroupDTO::getId)
                .collect(Collectors.toList());
    }

    private static List<Product> getProducts(List<OrderProductDTO> products) {
        List<Product> result = null;
        if (products != null) {
            result = products
                    .stream()
                    .map(OrderConverter::getProduct)
                    .collect(Collectors.toList());
        }
        return result;
    }

    private static Product getProduct(OrderProductDTO orderProductDTO) {
        Product product = new Product();

        product.setType(ProductType.get(orderProductDTO.getType().getId()));
        product.setEventId(orderProductDTO.getEventId());
        product.setSessionId(orderProductDTO.getSessionId());
        product.setGroupId(orderProductDTO.getGroupId());
        product.setRelatedBookingCode(orderProductDTO.getRelatedBookingCode());
        product.setRelatedRefundCode(orderProductDTO.getRelatedRefundCode());

        if (orderProductDTO.getRelatedProductState() != null) {
            product.setRelatedProductState(ProductState.get(orderProductDTO.getRelatedProductState().getId()));
        }

        return product;
    }

    public static VisitorGroupDTO from(OrderGroup orderGroup) {
        VisitorGroupDTO visitorGroup = null;
        if (orderGroup != null) {
            visitorGroup = new VisitorGroupDTO();
            visitorGroup.setIdGroup(orderGroup.getGroupId());
            visitorGroup.setName(orderGroup.getName());
            visitorGroup.setNumAttendants(orderGroup.getNumAttendants());
            visitorGroup.setNumAccompanists(orderGroup.getNumAccompanists());
            visitorGroup.setAttributeValues(new ArrayList<>());
            if (orderGroup.getOrderGroupAttributes() != null && !orderGroup.getOrderGroupAttributes().isEmpty()) {
                for (OrderGroupAttribute orderGroupAttribute : orderGroup.getOrderGroupAttributes()) {
                    visitorGroup.getAttributeValues().add(toAttributeValuesDTO(orderGroupAttribute));
                }
            }
        }
        return visitorGroup;
    }

    private static AttributeValuesDTO toAttributeValuesDTO(OrderGroupAttribute orderGroupAttribute){
        AttributeValuesDTO attributeValues = null;
        if( orderGroupAttribute != null ){
            attributeValues = new AttributeValuesDTO();
            attributeValues.setIdAttribute(orderGroupAttribute.getAttributeId());
            if( orderGroupAttribute.getAttributeInfoDTO() != null ){
                attributeValues.setDescription(orderGroupAttribute.getAttributeInfoDTO().getName());
            }
            attributeValues.setAttributeValue(Arrays.asList(from(orderGroupAttribute)));
            attributeValues.setAttributeInfo(toAttributeInfoDTO(orderGroupAttribute.getAttributeInfoDTO()));
        }
        return attributeValues;
    }

    private static AttributeValueDTO from(OrderGroupAttribute orderGroupAttribute){
        AttributeValueDTO attributeValue =null;
        if( orderGroupAttribute != null ){
            attributeValue = new AttributeValueDTO();
            if( orderGroupAttribute.getValueId() != null ){
                for (AttributeValueInfo attributeValueInfoDTO : orderGroupAttribute.getAttributeInfoDTO().getAttributeValueInfos()) {
                    if( attributeValueInfoDTO.getId().equals(orderGroupAttribute.getValueId())){
                        attributeValue.setDomainValue(toDomainValueDTO(attributeValueInfoDTO));
                        break;
                    }
                }
            }else if( orderGroupAttribute.getValue() != null ){
                attributeValue.setUserInputValue( orderGroupAttribute.getValue() );
            }
        }
        return attributeValue;
    }

    private static DomainValueDTO toDomainValueDTO(AttributeValueInfo attributeValueInfoDTO) {
        DomainValueDTO domainValue = null;
        if (attributeValueInfoDTO != null) {
            domainValue = new DomainValueDTO();
            domainValue.setIdValue(attributeValueInfoDTO.getId());
            domainValue.setValue(attributeValueInfoDTO.getName());
        }
        return domainValue;
    }

    private static AttributeInfoDTO toAttributeInfoDTO(AttributeInfo coreAttributeInfoDTO) {
        AttributeInfoDTO attributeInfo = null;
        if (coreAttributeInfoDTO != null) {
            attributeInfo = new AttributeInfoDTO();
            attributeInfo.setAttributeId(coreAttributeInfoDTO.getId());
            attributeInfo.setEntityId(coreAttributeInfoDTO.getEntityId());
            attributeInfo.setName(coreAttributeInfoDTO.getName());
            attributeInfo.setSelectionType(AttributeSelectionTypes.byId(coreAttributeInfoDTO.getSelectionType().getId()));
            attributeInfo.setScope(AttributeScopes.byId(coreAttributeInfoDTO.getScope().getId()));
            attributeInfo.setValueType(AttributeValueTypes.byId(coreAttributeInfoDTO.getValueType().getId()));
            attributeInfo.setCode(coreAttributeInfoDTO.getCode());
            //attributeValueInfos
            attributeInfo.setAttributeValuesInfos(
                    OptionalCollection
                            .ofZeroElements(coreAttributeInfoDTO.getAttributeValueInfos())
                            .orElseGet(ArrayList::new)
                            .stream()
                            .map(OrderConverter::toAttributeValueInfoDTO)
                            .collect(Collectors.toList())
            );
        }
        return attributeInfo;
    }

    private static AttributeValueInfoDTO toAttributeValueInfoDTO(AttributeValueInfo coreAttributeValueInfo){
        AttributeValueInfoDTO result = null;
        if( coreAttributeValueInfo != null ){
            result = new AttributeValueInfoDTO();
            result.setValueId(coreAttributeValueInfo.getId());
            result.setAttributeId(coreAttributeValueInfo.getAttributeId());
            result.setName(coreAttributeValueInfo.getName());
            result.setDefaultAttribute(coreAttributeValueInfo.getDefaultAttribute());
            result.setCode(coreAttributeValueInfo.getCode());
        }
        return result;
    }

}
