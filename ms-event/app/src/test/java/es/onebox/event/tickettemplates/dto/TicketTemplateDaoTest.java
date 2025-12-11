package es.onebox.event.tickettemplates.dto;

import es.onebox.core.serializer.dto.request.Direction;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.event.tickettemplates.dao.TicketTemplateDao;
import es.onebox.event.tickettemplates.dao.TicketTemplateRecord;
import es.onebox.jooq.dao.test.DaoImplTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TicketTemplateDaoTest extends DaoImplTest {

    @InjectMocks
    private TicketTemplateDao ticketTemplateDao;

    protected String getDatabaseFile() {
        return "dao/TicketTemplateDao.sql";
    }

    private TicketTemplatesFilter ticketTemplateFilter;


    @BeforeEach
    public void setUp() {
        super.setUp();
        ticketTemplateFilter = new TicketTemplatesFilter();

        SortOperator<String> sort = new SortOperator<>();
        sort.addDirection(Direction.ASC, "date");
        ticketTemplateFilter.setSort(sort);
    }

    @Test
    public void getTicketTemplateByEntityAdmin() {
        ticketTemplateFilter.setEntityAdminId(6707L);
        List<TicketTemplateRecord> ticketTemplate = ticketTemplateDao.find(ticketTemplateFilter);

        assertEquals(2, ticketTemplate.size());

    }

    @Test
    public void countTicketTemplateByEntityAdmin() {
        ticketTemplateFilter.setEntityAdminId(6707L);
        Long ticketTemplateCount = ticketTemplateDao.countByFilter(ticketTemplateFilter);

        assertEquals(2, ticketTemplateCount);
    }
}
