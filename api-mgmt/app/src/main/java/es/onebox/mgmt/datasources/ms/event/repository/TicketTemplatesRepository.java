package es.onebox.mgmt.datasources.ms.event.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.event.MsEventDatasource;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplate;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateDesign;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateLiteral;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateLiteralElementFilter;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateTagType;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplates;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplatesFilter;
import es.onebox.mgmt.tickettemplates.dto.CloneTemplateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.function.Predicate;

@Repository
public class TicketTemplatesRepository {

    private static final String CACHE_TEMPLATE_MODELS_KEY = "ticket_templates.models";
    private static final int CACHE_TEMPLATE_MODELS_TTL = 10;

    private final MsEventDatasource msEventDatasource;

    @Autowired
    public TicketTemplatesRepository(MsEventDatasource msEventDatasource) {
        this.msEventDatasource = msEventDatasource;
    }

    public TicketTemplate getTicketTemplate(Long ticketTemplateId) {
        return msEventDatasource.getTicketTemplate(ticketTemplateId);
    }

    public TicketTemplates getTicketTemplates (TicketTemplatesFilter filter) {
        return msEventDatasource.getTicketTemplates(filter);
    }

    public Long createTicketTemplate(String name, Long entityId, Long entityDefaultLangId, Long designId) {
        return msEventDatasource.createTicketTemplate(name, entityId, entityDefaultLangId, designId);
    }

    public void updateTicketTemplate(Long ticketTemplateId, TicketTemplate out) {
        msEventDatasource.updateTicketTemplate(ticketTemplateId, out);
    }

    public Long cloneTicketTemplate(Long ticketTemplateId, CloneTemplateRequest out) {
        return msEventDatasource.cloneTicketTemplate(ticketTemplateId, out);
    }

    public void deleteTicketTemplate(Long ticketTemplateId) {
        msEventDatasource.deleteTicketTemplate(ticketTemplateId);
    }

    @Cached(key = CACHE_TEMPLATE_MODELS_KEY, expires = CACHE_TEMPLATE_MODELS_TTL)
    public List<TicketTemplateDesign> getTicketTemplateDesigns() {
        return msEventDatasource.getTicketTemplateModels();
    }

    public List<TicketTemplateCommunicationElement> getTicketTemplatesCommunicationElements(Long ticketTemplateId,
            CommunicationElementFilter<TicketTemplateTagType> filter, Predicate<TicketTemplateTagType> condition) {
        ChannelContentsUtils.addTicketTemplateTagsToFilter(filter, condition);
        return msEventDatasource.getTicketTemplateCommunicationElements(ticketTemplateId, filter);
    }


    public void updateTicketTemplateCommunicationElements(Long ticketTemplateId, List<TicketTemplateCommunicationElement> elements) {
        msEventDatasource.updateTicketTemplateCommunicationElements(ticketTemplateId, elements);
    }

    public List<TicketTemplateLiteral> getTicketTemplatesLiterals(Long ticketTemplateId,
                                                                  TicketTemplateLiteralElementFilter filter) {
        return msEventDatasource.getTicketTemplateLiterals(ticketTemplateId, filter);
    }

    public void updateTicketTemplateLiterals(Long ticketTemplateId, List<TicketTemplateLiteral> elements) {
        msEventDatasource.updateTicketTemplateLiterals(ticketTemplateId, elements);
    }
}
