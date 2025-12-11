package es.onebox.mgmt.entities.converter;

import es.onebox.mgmt.datasources.integration.dispatcher.dto.InventoriesList;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.Inventory;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.InventoryDTO;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;

public class EntitySgaConverter {

    private EntitySgaConverter() {
    }

    public static List<InventoryDTO> fromMsInventory(InventoriesList inventoriesList) {
        if (CollectionUtils.isEmpty(inventoriesList)) {
            return Collections.emptyList();
        }
        return inventoriesList.stream().map(EntitySgaConverter::fromDTO).toList();
    }

    private static InventoryDTO fromDTO(Inventory inventory) {
        if (inventory == null) {
            return null;
        }
        InventoryDTO inventoryDTO = new InventoryDTO();
        inventoryDTO.setId(inventory.getId());
        inventoryDTO.setName(inventory.getName());

        return inventoryDTO;
    }
}
