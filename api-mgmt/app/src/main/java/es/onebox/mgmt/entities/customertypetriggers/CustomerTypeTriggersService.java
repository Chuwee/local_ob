package es.onebox.mgmt.entities.customertypetriggers;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.common.MasterdataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerTypeTriggersService {

    private final MasterdataService masterdataService;

    @Autowired
    public CustomerTypeTriggersService(MasterdataService masterdataService) {
        this.masterdataService = masterdataService;
    }

    public List<IdNameDTO> getCustomerTypeTriggers() {
        return masterdataService.getCustomerTypeTriggers();
    }
}
