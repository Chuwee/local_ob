package es.onebox.mgmt.entities.customertypes;

import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.CustomTypeAssignationTrigger;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.CustomerType;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.CustomerTypeCreateRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.CustomerTypeUpdateRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.enums.AssignationTrigger;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.enums.AssignationType;
import es.onebox.mgmt.entities.customertypes.dto.CreateCustomerTypeDTO;
import es.onebox.mgmt.entities.customertypes.dto.CustomerTypeDTO;
import es.onebox.mgmt.entities.customertypes.dto.CustomerTypeTriggerDTO;
import es.onebox.mgmt.entities.customertypes.dto.CustomerTypesDTO;
import es.onebox.mgmt.entities.customertypes.dto.UpdateCustomerTypeDTO;
import es.onebox.mgmt.entities.customertypes.dto.enums.AssignationTriggerDTO;
import es.onebox.mgmt.entities.customertypes.dto.enums.AssignationTypeDTO;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerTypeConverter {

    private CustomerTypeConverter() {
    }

    public static CustomerTypesDTO fromMs(List<CustomerType> source) {
        if (CollectionUtils.isEmpty(source)) {
            return null;
        }
        List<CustomerTypeDTO> profiles = source.stream().map(CustomerTypeConverter::fromMs).collect(Collectors.toList());
        return new CustomerTypesDTO(profiles);
    }

    public static CustomerTypeDTO fromMs(CustomerType source) {
        if (source == null) {
            return null;
        }
        CustomerTypeDTO target = new CustomerTypeDTO();
        target.setId(source.getId());
        target.setCode(source.getCode());
        target.setName(source.getName());
        target.setAssignationType(AssignationTypeDTO.valueOf(source.getAssignationType().name()));
        if (AssignationTypeDTO.AUTOMATIC.equals(target.getAssignationType())){
            target.setTriggers(toCustomerTypeTriggerDTO(source.getTriggers()));
        }

        return target;
    }

    private static List<CustomerTypeTriggerDTO> toCustomerTypeTriggerDTO(List<CustomTypeAssignationTrigger> source) {
        if (source == null) {
            return null;
        }
        List<CustomerTypeTriggerDTO> target = new ArrayList<>();
        source.forEach(e -> {
            CustomerTypeTriggerDTO triggerDTO = new CustomerTypeTriggerDTO();
            triggerDTO.setTrigger(AssignationTriggerDTO.valueOf(e.getTrigger().name()));
            triggerDTO.setHandler(e.getHandler());
            triggerDTO.setSelected(true);
            target.add(triggerDTO);
        });
        Arrays.stream(AssignationTrigger.values()).toList().stream()
            .filter(e -> source.stream().noneMatch(t -> t.getTrigger().name().equals(e.name())))
            .forEach(e -> {
                CustomerTypeTriggerDTO triggerDTO = new CustomerTypeTriggerDTO();
                triggerDTO.setTrigger(AssignationTriggerDTO.valueOf(e.name()));
                triggerDTO.setSelected(false);
                target.add(triggerDTO);
            });
        return target;
    }

    private static List<AssignationTrigger> toCustomerTypeTrigger(List<AssignationTriggerDTO> source) {
        if (source == null) {
            return null;
        }
        List<AssignationTrigger> target = new ArrayList<>();
        source.forEach(e -> target.add(AssignationTrigger.valueOf(e.name())));
        return target;
    }

    public static CustomerTypeUpdateRequest toMs(UpdateCustomerTypeDTO source) {
        return new CustomerTypeUpdateRequest(source.getName(), source.getCode());
    }

    public static CustomerTypeCreateRequest toMs(CreateCustomerTypeDTO source) {
        return new CustomerTypeCreateRequest(source.getName(), source.getCode(), AssignationType.valueOf(source.getAssignationType().name()), toCustomerTypeTrigger(source.getTriggers()));
    }
}
