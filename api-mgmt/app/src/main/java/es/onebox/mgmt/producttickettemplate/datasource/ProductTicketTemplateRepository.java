package es.onebox.mgmt.producttickettemplate.datasource;

import es.onebox.mgmt.producttickettemplate.datasource.ms.event.ProductTicketDatasource;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.CreateProductTicketTemplateRequest;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.ProductTicketModelResponse;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.ProductTicketTemplateFilter;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.ProductTicketTemplateLanguages;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.ProductTicketTemplatePageResponse;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.ProductTicketTemplateResponse;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.UpdateProductTicketTemplateRequest;
import es.onebox.mgmt.tickettemplates.dto.CloneTemplateRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductTicketTemplateRepository {

	private final ProductTicketDatasource datasource;

	public ProductTicketTemplateRepository(ProductTicketDatasource datasource) {
		this.datasource = datasource;
	}

	public ProductTicketTemplatePageResponse search(ProductTicketTemplateFilter filter) {

		return datasource.search(filter);
	}

	public ProductTicketTemplateResponse getById(Long productTicketTemplateId) {

		return datasource.getById(productTicketTemplateId);
	}

	public Long create(CreateProductTicketTemplateRequest createRequest) {

		return datasource.create(createRequest);
	}

	public void update(Long productTicketTemplateId, UpdateProductTicketTemplateRequest updateRequest) {

		datasource.update(productTicketTemplateId, updateRequest);
	}

	public void delete(Long productTicketTemplateId) {

		datasource.delete(productTicketTemplateId);
	}

	public List<ProductTicketModelResponse> getAllModels() {

		return datasource.getAllModels();
	}

	public Long cloneProductTicketTemplate(Long templateId, CloneTemplateRequest out) {
		return datasource.cloneProductTicketTemplate(templateId, out);
	}

	public ProductTicketTemplateLanguages getProductTicketTemplateLanguages(Long templateId) {
		return datasource.getProductTicketTemplateLanguages(templateId);
	}
}
