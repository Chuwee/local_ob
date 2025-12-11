package es.onebox.mgmt.producttickettemplate.controller;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.producttickettemplate.controller.request.CreateProductTicketTemplate;
import es.onebox.mgmt.producttickettemplate.controller.request.ProductTicketTemplateFilterRequest;
import es.onebox.mgmt.producttickettemplate.controller.request.UpdateProductTicketTemplate;
import es.onebox.mgmt.producttickettemplate.domain.dto.ProductTicketModelDTO;
import es.onebox.mgmt.producttickettemplate.domain.dto.ProductTicketTemplateDetailDTO;
import es.onebox.mgmt.producttickettemplate.domain.dto.ProductTicketTemplateLanguageDTO;
import es.onebox.mgmt.producttickettemplate.domain.dto.ProductTicketTemplateSearchPageDTO;
import es.onebox.mgmt.producttickettemplate.service.ProductTicketTemplateService;
import es.onebox.mgmt.tickettemplates.dto.CloneTicketTemplateRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@RequestMapping(value = ApiConfig.BASE_URL + "/product-ticket-templates")
public class ProductTicketTemplateController {

    private final ProductTicketTemplateService productTicketTemplateService;

    public ProductTicketTemplateController(ProductTicketTemplateService productTicketTemplateService) {
        this.productTicketTemplateService = productTicketTemplateService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping
    public ResponseEntity<ProductTicketTemplateSearchPageDTO> search(
            @BindUsingJackson @Valid ProductTicketTemplateFilterRequest filter) {
        return ResponseEntity.ok(productTicketTemplateService.search(filter));
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{templateId}")
    public ResponseEntity<ProductTicketTemplateDetailDTO> getById(@PathVariable Long templateId) {
        return ResponseEntity.ok(productTicketTemplateService.getById(templateId));
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping
    public ResponseEntity<IdDTO> create(@RequestBody @Valid CreateProductTicketTemplate request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new IdDTO(productTicketTemplateService.create(request)));
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping("/{templateId}")
    public ResponseEntity<Void> update(@PathVariable Long templateId,
                                       @RequestBody @Valid UpdateProductTicketTemplate request) {

        productTicketTemplateService.update(templateId, request);
        return ResponseEntity.noContent().build();
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping("/{templateId}")
    public ResponseEntity<Void> delete(@PathVariable Long templateId) {

        productTicketTemplateService.delete(templateId);
        return ResponseEntity.noContent().build();
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{templateId}/clone")
    public IdDTO clone(@PathVariable @Min(value = 1, message = "templateId must be above 0") Long templateId,
                       @Valid @RequestBody CloneTicketTemplateRequestDTO body) {
        return productTicketTemplateService.clone(templateId, body);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/models")
    public List<ProductTicketModelDTO> getAllModels() {
        return productTicketTemplateService.getAllModels();
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{templateId}/languages")
    public List<ProductTicketTemplateLanguageDTO> getProductTicketTemplateLanguages(@PathVariable Long templateId) {
        return productTicketTemplateService.getProductTicketTemplateLanguages(templateId);
    }
}
