package es.onebox.flc.orders.service;

import es.onebox.common.datasources.ms.order.dto.BaseOrderDTO;
import es.onebox.common.datasources.ms.order.dto.OrderGroup;
import es.onebox.common.datasources.ms.order.dto.VisitorGroupParam;
import es.onebox.common.datasources.ms.order.repository.MsOrderRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.flc.orders.converter.OrderConverter;
import es.onebox.flc.orders.dto.Order;
import es.onebox.flc.orders.dto.groups.VisitorGroupDTO;
import es.onebox.flc.utils.AuthenticationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final MsOrderRepository msOrderRepository;

    @Autowired
    public OrderService(MsOrderRepository msOrderRepository) {
        this.msOrderRepository = msOrderRepository;
    }


    public Order getOrder(String code) {

        Integer entityId = getEntityId();

        if (StringUtils.isBlank(code) || entityId == null) {
            throw OneboxRestException.builder(CoreErrorCode.BAD_PARAMETER).build();
        }

        BaseOrderDTO orderDTO = msOrderRepository.getOrderInfo(code, entityId);

        if (orderDTO == null) {
            orderDTO = msOrderRepository.getPreOrderInfo(code, entityId);
        }

        if (orderDTO == null) {
            throw ExceptionBuilder.build(ApiExternalErrorCode.ORDER_NOT_FOUND);
        }

        return OrderConverter.convert(orderDTO);
    }


    public static Integer getEntityId() {
        return (Integer) AuthenticationUtils.getAttribute("entityId");
    }


    public List<VisitorGroupDTO> searchGroups(List<Long> groupIds) {
        List<VisitorGroupDTO> result = new ArrayList<>();
        VisitorGroupParam visitorGroupParam = new VisitorGroupParam();
        visitorGroupParam.setGroupIds(groupIds);
        List<OrderGroup> visitorGroups = msOrderRepository.searchGroups(visitorGroupParam);
        Integer entityId = getEntityId();
        visitorGroups = visitorGroups.stream()
                .filter(orderGroup -> visibleGroup(orderGroup, entityId))
                .collect(Collectors.toList());

        if (!visitorGroups.isEmpty()) {
            result.addAll(visitorGroups.stream().map(OrderConverter::from).toList());
        }
        return result;
    }

    public VisitorGroupDTO getGroup(Long groupId) {
        OrderGroup orderGroup = msOrderRepository.getGroup(groupId);
        if (visibleGroup(orderGroup, getEntityId())) {
            return OrderConverter.from(orderGroup);
        }
        return null;
    }

    private static boolean visibleGroup(OrderGroup group, Integer entityId) {
        return group.getOrderGroupAttributes().stream()
                .anyMatch(orderGroupAttribute -> orderGroupAttribute.getAttributeInfoDTO().getEntityId().equals(entityId));
    }
}
