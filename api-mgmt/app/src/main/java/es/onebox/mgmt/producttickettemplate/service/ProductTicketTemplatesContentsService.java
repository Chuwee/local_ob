package es.onebox.mgmt.producttickettemplate.service;

import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;

import es.onebox.mgmt.datasources.ms.event.repository.ProductTicketTemplateContentsRepository;
import es.onebox.mgmt.products.converter.ProductTicketTemplatesConverter;
import es.onebox.mgmt.producttickettemplate.converter.ProductTicketTemplateConverter;
import es.onebox.mgmt.producttickettemplate.datasource.ProductTicketTemplateRepository;
import es.onebox.mgmt.producttickettemplate.domain.dto.ProductTicketTemplateDTO;
import es.onebox.mgmt.producttickettemplate.dto.ProductTicketTemplateContentLiteralDTO;
import es.onebox.mgmt.producttickettemplate.dto.ProductTicketTemplateContentLiteralFilter;
import es.onebox.mgmt.producttickettemplate.dto.ProductTicketTemplateContentLiteralListDTO;
import es.onebox.mgmt.producttickettemplate.dto.ProductTicketTemplateLiteralElementFilter;
import es.onebox.mgmt.producttickettemplate.dto.ProductTicketTemplateLiterals;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProductTicketTemplatesContentsService {

    private final ProductTicketTemplateContentsRepository productTicketTemplateContentsRepository;
    private final ProductTicketTemplateRepository productTicketTemplateRepository;
    private final SecurityManager securityManager;
    private final MasterdataService masterdataService;

    public ProductTicketTemplatesContentsService(ProductTicketTemplateContentsRepository productTicketTemplateContentsRepository, ProductTicketTemplateRepository productTicketTemplateRepository, SecurityManager securityManager, MasterdataService masterdataService) {
        this.productTicketTemplateContentsRepository = productTicketTemplateContentsRepository;
        this.productTicketTemplateRepository = productTicketTemplateRepository;
        this.securityManager = securityManager;
        this.masterdataService = masterdataService;
    }

    public ProductTicketTemplateLiterals getProductTicketTemplateLiterals(Long ticketTemplateId, ProductTicketTemplateContentLiteralFilter filter) {
        ProductTicketTemplateLiteralElementFilter literalsFilter = ProductTicketTemplatesConverter.fromProductTicketTemplateTextFilter(filter);

        return productTicketTemplateContentsRepository.getProductTicketTemplatesLiterals(ticketTemplateId, literalsFilter);
    }

    public void updateTicketContentLiterals(Long productTicketTemplateId, ProductTicketTemplateContentLiteralListDTO literalListDTO) {
        ProductTicketTemplateDTO productTicketTemplate = getAndCheckTicketTemplate(productTicketTemplateId);

        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        for (ProductTicketTemplateContentLiteralDTO literalDTO : literalListDTO) {
            literalDTO.setLanguage(
                    ChannelContentsUtils.checkElementLanguageForProductTicketTemplate(productTicketTemplate, languages, literalDTO.getLanguage()));
        }

        productTicketTemplateContentsRepository.updateProductTicketTemplateLiterals(productTicketTemplateId, ProductTicketTemplatesConverter.toMsProductTicketLiteralList(literalListDTO));
    }


    private ProductTicketTemplateDTO getAndCheckTicketTemplate(Long productTicketTemplateId) {
        ProductTicketTemplateDTO productTicketTemplate = ProductTicketTemplateConverter.toDomain(
                productTicketTemplateRepository.getById(productTicketTemplateId));
        securityManager.checkEntityAccessible(productTicketTemplate.entity().getId());
        return productTicketTemplate;
    }
}
