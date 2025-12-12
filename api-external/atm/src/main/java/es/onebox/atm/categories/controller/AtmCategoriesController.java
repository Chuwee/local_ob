package es.onebox.atm.categories.controller;


import es.onebox.atm.categories.dto.CategoryDTO;
import es.onebox.atm.categories.service.AtmCategoriesService;
import es.onebox.common.config.ApiConfig;
import es.onebox.common.security.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping(ApiConfig.ATMApiConfig.BASE_URL + "/categories")
public class AtmCategoriesController {

    private final AtmCategoriesService atmCategoriesService;

    @Autowired
    public AtmCategoriesController(AtmCategoriesService atmCategoriesService){
        this.atmCategoriesService = atmCategoriesService;
    }

    @Secured(Role.CHANNEL_INTEGRATION)
    @GetMapping()
    public List<CategoryDTO> getCategories() {
        return atmCategoriesService.getCategories();
    }

}

