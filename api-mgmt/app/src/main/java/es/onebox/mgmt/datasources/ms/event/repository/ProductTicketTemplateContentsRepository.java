package es.onebox.mgmt.datasources.ms.event.repository;

import es.onebox.mgmt.datasources.ms.event.MsEventDatasource;
import es.onebox.mgmt.datasources.ms.event.dto.products.producttickettemplate.ProductTicketTemplateLiteral;
import es.onebox.mgmt.producttickettemplate.dto.ProductTicketTemplateLiterals;
import es.onebox.mgmt.producttickettemplate.dto.ProductTicketTemplateLiteralElementFilter;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductTicketTemplateContentsRepository {
    private final MsEventDatasource msEventDatasource;

    public ProductTicketTemplateContentsRepository(MsEventDatasource msEventDatasource) {
        this.msEventDatasource = msEventDatasource;
    }

    public ProductTicketTemplateLiterals getProductTicketTemplatesLiterals(Long ticketTemplateId,
                                                                           ProductTicketTemplateLiteralElementFilter filter) {
        return msEventDatasource.getProductTicketTemplateLiterals(ticketTemplateId, filter);
    }

    public void updateProductTicketTemplateLiterals(Long productTicketTemplate, List<ProductTicketTemplateLiteral> literalListDTO) {
        msEventDatasource.updateProductTicketTemplateLiterals(productTicketTemplate, literalListDTO);
    }
}
