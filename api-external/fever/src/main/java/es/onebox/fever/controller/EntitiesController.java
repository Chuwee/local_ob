package es.onebox.fever.controller;

import es.onebox.common.config.ApiConfig;
import es.onebox.common.datasources.ms.entity.dto.EntitySearchFilterDTO;
import es.onebox.common.datasources.ms.entity.dto.SearchEntitiesResponse;
import es.onebox.common.security.Role;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.fever.service.EntitiesService;
import jakarta.validation.Valid;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConfig.FeverApiConfig.BASE_URL + "/entities")
public class EntitiesController {

    private final EntitiesService entitiesService;

    public EntitiesController(EntitiesService entitiesService) {
        this.entitiesService = entitiesService;
    }

//    @Secured(Role.ROLE_FV_REPORTING)
    @GetMapping(value = "/fv-zone")
    public SearchEntitiesResponse getEntities(@BindUsingJackson @Valid EntitySearchFilterDTO filter) {
        return entitiesService.getEntities(filter);
    }
}
