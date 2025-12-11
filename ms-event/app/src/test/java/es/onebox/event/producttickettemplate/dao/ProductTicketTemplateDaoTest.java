package es.onebox.event.producttickettemplate.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

import es.onebox.event.producttickettemplate.controller.request.ProductTicketTemplateFilter;
import es.onebox.event.producttickettemplate.dao.mapper.ProductTicketTemplateMapper;
import es.onebox.event.producttickettemplate.domain.dto.ProductTicketTemplateDTO;
import es.onebox.jooq.dao.test.DaoImplTest;

class ProductTicketTemplateDaoTest extends DaoImplTest {

	@InjectMocks
	private ProductTicketTemplateDao productTicketTemplateDao;

	private final ProductTicketTemplateMapper productTicketTemplateMapper = new ProductTicketTemplateMapper();

	@Override
	@BeforeEach
	public void setUp() {
		super.setUp();
		ReflectionTestUtils.setField(productTicketTemplateDao, "productTicketTemplateMapper",
				productTicketTemplateMapper);
	}

	@Override
	protected String getDatabaseFile() {
		return "/dao/ProductTicketTemplateDao.sql";
	}

	@Test
	void findOneById() {

		Optional<ProductTicketTemplateDTO> template = productTicketTemplateDao.findOneById(100);

		assertTrue(template.isPresent());
		assertEquals("Basic Web Hosting Support", template.get().name());
	}

	@Test
	void find() {

		ProductTicketTemplateFilter filter = new ProductTicketTemplateFilter();
		filter.setLimit(10L);
		filter.setOffset(0L);
		List<ProductTicketTemplateDTO> page = productTicketTemplateDao.find(filter);

		assertNotNull(page);
		assertEquals(10, page.size());
	}

	@Test
	void getTotalCount() {

		ProductTicketTemplateFilter filter = new ProductTicketTemplateFilter();
		Long count = productTicketTemplateDao.getTotalCount(filter);

		assertNotNull(count);
		assertEquals(29, count);
	}

	@Test
	void nameAlreadySetForEntity() {

		assertTrue(productTicketTemplateDao.nameAlreadySetForEntity("Basic Web Hosting Support", 1));

	}
}