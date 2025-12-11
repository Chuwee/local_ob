package es.onebox.mgmt.accesscontrol.converter;

import es.onebox.core.serializer.dto.common.NameDTO;
import es.onebox.mgmt.accesscontrol.dto.AccessControlSystemsDTO;
import es.onebox.mgmt.accesscontrol.enums.AccessControlSystem;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class AccessControlSystemsConverter {

    private AccessControlSystemsConverter(){
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static AccessControlSystemsDTO convertFrom(final List<AccessControlSystem> systems){
        AccessControlSystemsDTO result = new AccessControlSystemsDTO();
        if(CollectionUtils.isNotEmpty(systems)){
           result.addAll(systems.stream()
                   .map(AccessControlSystem::getApiName)
                   .map(NameDTO::new)
                   .collect(Collectors.toList()));
        }
        return result;
    }
}
