package es.onebox.event.producttickettemplate.controller;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.products.dto.ProductTicketLiteralsDTO;
import es.onebox.event.producttickettemplate.controller.request.CreateProductTicketTemplate;
import es.onebox.event.producttickettemplate.controller.request.ProductTicketTemplateFilter;
import es.onebox.event.producttickettemplate.controller.request.ProductTicketTemplateLiteralElementFilter;
import es.onebox.event.producttickettemplate.controller.request.UpdateProductTicketTemplate;
import es.onebox.event.producttickettemplate.domain.dto.CloneProductTicketTemplateDTO;
import es.onebox.event.producttickettemplate.domain.dto.ProductTicketModelDTO;
import es.onebox.event.producttickettemplate.domain.dto.ProductTicketTemplateDTO;
import es.onebox.event.producttickettemplate.domain.dto.ProductTicketTemplateLanguagesDTO;
import es.onebox.event.producttickettemplate.domain.dto.ProductTicketTemplateLiteralsDTO;
import es.onebox.event.producttickettemplate.domain.dto.ProductTicketTemplatePageDTO;
import es.onebox.event.producttickettemplate.service.ProductTicketTemplateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/product-ticket-templates")
public class ProductTicketTemplateController {

    private final ProductTicketTemplateService productTicketTemplateService;

    public ProductTicketTemplateController(ProductTicketTemplateService productTicketTemplateService) {
        this.productTicketTemplateService = productTicketTemplateService;
    }

    @GetMapping
    public ResponseEntity<ProductTicketTemplatePageDTO> find(@Valid ProductTicketTemplateFilter filter) {
        return ResponseEntity.ok(productTicketTemplateService.find(filter));
    }

    @GetMapping("/{templateId}")
    public ResponseEntity<ProductTicketTemplateDTO> getById(@PathVariable Integer templateId) {
        return ResponseEntity.ok(productTicketTemplateService.getById(templateId));
    }

    @GetMapping("/{templateId}/literals")
    public ResponseEntity<ProductTicketLiteralsDTO> getLiterals(@PathVariable Integer templateId,
                                                                @Valid ProductTicketTemplateLiteralElementFilter filter) {
        return ResponseEntity.ok(productTicketTemplateService.getLiterals(templateId, filter));
    }

    @GetMapping("/{templateId}/literals/")
    public ResponseEntity<ProductTicketLiteralsDTO> updateLiterals(@PathVariable Integer templateId,
                                                                @Valid ProductTicketTemplateLiteralElementFilter filter) {
        return ResponseEntity.ok(productTicketTemplateService.getLiterals(templateId, filter));
    }

    @PostMapping(value = "/{templateId}/literals")
    public ResponseEntity<Void> updateTicketTemplatesLiterals(@PathVariable Integer templateId,
                                              @Valid @RequestBody ProductTicketTemplateLiteralsDTO literalListDTO) {
        productTicketTemplateService.updateLiteral(templateId, literalListDTO);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<IdDTO> create(@RequestBody @Valid CreateProductTicketTemplate request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new IdDTO(productTicketTemplateService.create(request).longValue()));
    }

    @PutMapping("/{templateId}")
    public ResponseEntity<Void> update(@PathVariable Integer templateId,
                                       @RequestBody @Valid UpdateProductTicketTemplate request) {

        productTicketTemplateService.update(templateId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{templateId}")
    public ResponseEntity<Void> delete(@PathVariable Integer templateId) {

        productTicketTemplateService.delete(templateId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/models")
    public List<ProductTicketModelDTO> getAllModels() {
        return productTicketTemplateService.getAllModels();
    }

    @GetMapping("/{templateId}/languages")
    public ResponseEntity<ProductTicketTemplateLanguagesDTO> getTicketTemplateLanguages(@PathVariable Long templateId) {
        ProductTicketTemplateLanguagesDTO productTicketTemplateLanguagesDTO =
                productTicketTemplateService.getProductTicketTemplateLanguages(templateId);
        return new ResponseEntity<>(productTicketTemplateLanguagesDTO, HttpStatus.OK);
    }

    @PostMapping("/{templateId}/clone")
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO cloneTicketTemplate(@PathVariable Integer templateId,
                                     @Valid @RequestBody CloneProductTicketTemplateDTO body) {
        return productTicketTemplateService.cloneTemplate(templateId, body);
    }
}
