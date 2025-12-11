package es.onebox.mgmt.datasources.ms.entity.repository;

import es.onebox.mgmt.datasources.ms.entity.MsEntityDatasource;
import es.onebox.mgmt.datasources.ms.entity.dto.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RolesRepository {

    private final MsEntityDatasource msEntityDatasource;

    private List<Role> allRoles;

    @Autowired
    public RolesRepository(MsEntityDatasource msEntityDatasource) {
        this.msEntityDatasource = msEntityDatasource;
    }

    public List<Role> getAllRoles() {
        if (allRoles == null) {
            allRoles = msEntityDatasource.getAllRoles();
        }
        return allRoles;
    }

}
