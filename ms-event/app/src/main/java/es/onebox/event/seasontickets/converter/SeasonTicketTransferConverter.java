package es.onebox.event.seasontickets.converter;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.datasources.ms.entity.dto.CustomerTypeSearchFilter;
import es.onebox.event.datasources.ms.entity.dto.CustomerTypes;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketTransferConfig;
import es.onebox.event.seasontickets.dto.transferseat.CustomerTypeDTO;
import es.onebox.event.seasontickets.dto.transferseat.SeasonTicketTransferConfigDTO;
import es.onebox.event.seasontickets.dto.transferseat.SeasonTicketTransferConfigUpdateDTO;

import java.util.List;
import java.util.stream.Collectors;

public class SeasonTicketTransferConverter {

    public static SeasonTicketTransferConfigDTO toDTO(SeasonTicketTransferConfig config, Integer entityId, EntitiesRepository entitiesRepository) {
        SeasonTicketTransferConfigDTO dto = new SeasonTicketTransferConfigDTO();

        dto.setTransferPolicy(config.getTransferPolicy());
        dto.setTransferTicketMaxDelayTime(config.getTransferTicketMaxDelayTime());
        dto.setTransferTicketMinDelayTime(config.getTransferTicketMinDelayTime());
        dto.setRecoveryTicketMaxDelayTime(config.getRecoveryTicketMaxDelayTime());
        dto.setEnableMaxTicketTransfers(config.getEnableMaxTicketTransfers());
        dto.setMaxTicketTransfers(config.getMaxTicketTransfers());
        dto.setExcludedSessions(config.getExcludedSessions());
        dto.setEnableBulk(config.getEnableBulk());
        dto.setBulkCustomerTypes(getCustomerTypes(config.getBulkCustomerTypes(), entityId, entitiesRepository));

        return dto;
    }

    public static void fromDTO(SeasonTicketTransferConfig target, SeasonTicketTransferConfigUpdateDTO source) {
        target.setTransferPolicy(source.getTransferPolicy());
        target.setTransferTicketMaxDelayTime(source.getTransferTicketMaxDelayTime());
        target.setTransferTicketMinDelayTime(source.getTransferTicketMinDelayTime());
        target.setRecoveryTicketMaxDelayTime(source.getRecoveryTicketMaxDelayTime());
        target.setEnableMaxTicketTransfers(source.getEnableMaxTicketTransfers());
        target.setMaxTicketTransfers(source.getMaxTicketTransfers());
        target.setExcludedSessions(source.getExcludedSessions());
        target.setEnableBulk(source.getEnableBulk());
        target.setBulkCustomerTypes(source.getBulkCustomerTypes());
    }

    private static List<CustomerTypeDTO> getCustomerTypes(List<Long> customerTypeIds, Integer entityId, EntitiesRepository entitiesRepository) {
        if (CommonUtils.isEmpty(customerTypeIds)) {
            return null;
        }

        CustomerTypeSearchFilter filter = new CustomerTypeSearchFilter();
        filter.setId(customerTypeIds.stream()
                .map(Long::intValue)
                .toList());

        CustomerTypes customerTypes = entitiesRepository.getCustomerTypes(entityId, filter);
        if (customerTypes == null || CommonUtils.isEmpty(customerTypes.getData())) {
            return null;
        }

        return customerTypes.getData().stream()
                .map(ct -> new CustomerTypeDTO(ct.getId(), ct.getName(), ct.getCode()))
                .collect(Collectors.toList());
    }

}
