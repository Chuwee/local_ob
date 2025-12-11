package es.onebox.mgmt.datasources.ms.entity.dto.user.realm;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UserRealmConfigCreateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1380790824312239358L;

    private List<String> resources;

    public List<String> getResources() {
        return resources;
    }

    public void setResources(List<String> resources) {
        this.resources = resources;
    }
}
