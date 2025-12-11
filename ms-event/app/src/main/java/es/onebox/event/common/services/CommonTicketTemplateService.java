package es.onebox.event.common.services;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.event.datasources.ms.ticket.MsTicketDatasource;
import es.onebox.event.datasources.ms.ticket.dto.PassbookTemplate;
import es.onebox.event.events.enums.TicketFormat;
import es.onebox.event.events.enums.TicketTemplateFormatModel;
import es.onebox.event.events.enums.TicketTemplateStatus;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.tickettemplates.dao.TicketTemplateDao;
import es.onebox.event.tickettemplates.dao.TicketTemplateRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommonTicketTemplateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonTicketTemplateService.class);

    private final TicketTemplateDao ticketTemplateDao;
    private final MsTicketDatasource msTicketDatasource;

    @Autowired
    public CommonTicketTemplateService(TicketTemplateDao ticketTemplateDao, MsTicketDatasource msTicketDatasource) {
        this.ticketTemplateDao = ticketTemplateDao;
        this.msTicketDatasource = msTicketDatasource;
    }

    public void createDefaultTicketTemplate(CpanelEventoRecord event) {
        List<TicketTemplateRecord> defaultTemplates = ticketTemplateDao.getDefaultTemplates(event.getIdentidad());
        if (CollectionUtils.isNotEmpty(defaultTemplates)) {
            for (TicketTemplateRecord defaultTemplate : defaultTemplates) {
                if (TicketTemplateFormatModel.STANDARD.getId().equals(defaultTemplate.getModelFormat())) {
                    event.setIdplantillaticket(defaultTemplate.getIdplantilla());
                } else if (TicketTemplateFormatModel.TICKET.getId().equals(defaultTemplate.getModelFormat())) {
                    event.setIdplantillatickettaquilla(defaultTemplate.getIdplantilla());
                } else {
                    LOGGER.warn("Template format with not valid value: {}", defaultTemplate.getModelFormat());
                }
            }
        }
    }

    public void validateUpdateTicketTemplate(Long ticketTemplateId, TicketFormat format, Integer entityId) {
        if (ticketTemplateId != null) {
            TicketTemplateRecord templateRecord = ticketTemplateDao.find(ticketTemplateId.intValue());
            if (templateRecord == null ||
                    templateRecord.getEstado().equals(TicketTemplateStatus.DELETED.getId()) ||
                    isInvalidFormat(format, templateRecord) ||
                    !templateRecord.getIdentidad().equals(entityId)) {
                throw ExceptionBuilder.build(MsEventErrorCode.TICKET_TEMPLATE_NOT_FOUND);
            }
        }
    }

    private static boolean isInvalidFormat(TicketFormat format, TicketTemplateRecord templateRecord) {
        if (TicketFormat.ZPL.equals(format) && TicketFormat.HARD_TICKET_PDF.getFormat().equals(templateRecord.getModelFormat())) {
            return false;
        }
        return TicketFormat.byId(templateRecord.getModelFormat()) != format;
    }

    public void validatePassbookTemplateExists(String passbookTemplateCode, Integer entityId) {
        if (passbookTemplateCode != null) {
            PassbookTemplate passbookTemplate = msTicketDatasource.getPassbookTemplate(passbookTemplateCode, entityId.longValue());
            if (passbookTemplate == null) {
                throw ExceptionBuilder.build(MsEventErrorCode.TICKET_TEMPLATE_NOT_FOUND);
            }
        }
    }
}
