package es.onebox.mgmt.realms;

import es.onebox.mgmt.datasources.ms.entity.repository.RolesRepository;
import es.onebox.mgmt.realms.converter.RoleConverter;
import es.onebox.mgmt.realms.dto.RoleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolesService {

    @Autowired
    private RolesRepository rolesRepository;

    public List<RoleDTO> getAllRoles() {
        return RoleConverter.convertRoles(rolesRepository.getAllRoles());
    }

}
