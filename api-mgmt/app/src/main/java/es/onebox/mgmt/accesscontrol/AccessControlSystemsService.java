package es.onebox.mgmt.accesscontrol;

import es.onebox.mgmt.accesscontrol.converter.AccessControlSystemsConverter;
import es.onebox.mgmt.accesscontrol.dto.AccessControlSystemsDTO;
import es.onebox.mgmt.datasources.ms.accesscontrol.repository.AccessControlSystemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccessControlSystemsService {

    private final AccessControlSystemsRepository accessControlSystemsRepository;

    @Autowired
    public AccessControlSystemsService(final AccessControlSystemsRepository accessControlSystemsRepository){
        this.accessControlSystemsRepository = accessControlSystemsRepository;
    }

    public AccessControlSystemsDTO getAvailableSystems(){
        return AccessControlSystemsConverter.convertFrom(this.accessControlSystemsRepository.findAll());
    }
}
