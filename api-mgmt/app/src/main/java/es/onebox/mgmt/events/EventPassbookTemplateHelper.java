package es.onebox.mgmt.events;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.event.dto.EventPassbookTemplates;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.ticket.dto.PassbookRequestFilter;
import es.onebox.mgmt.datasources.ms.ticket.dto.PassbookTemplateList;
import es.onebox.mgmt.datasources.ms.ticket.repository.PassbookRepository;
import es.onebox.mgmt.events.enums.EventTicketTemplateType;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventPassbookTemplateHelper {

    @Autowired
    private EventsRepository eventsRepository;
    @Autowired
    private PassbookRepository passbookRepository;


    public String getPassbookCodeForEvent(Long eventId, Long entityId, EventTicketTemplateType type) {
        EventPassbookTemplates passbookTemplateCode = eventsRepository.getEventPassbookTemplateCode(eventId);
        String code = getCodeByType(passbookTemplateCode, type);
        if (StringUtils.isNotEmpty(code)) {
            return code;
        }
        PassbookRequestFilter filter = new PassbookRequestFilter();
        filter.setEntityId(entityId);
        filter.setDefaultTemplate(true);
        PassbookTemplateList templateList = passbookRepository.searchPassbookTemplates(filter);
        if (templateList != null && templateList.getData() != null && templateList.getData().size() == 1) {
            return templateList.getData().get(0).getCode();
        }
        throw new OneboxRestException(ApiMgmtErrorCode.PASSBOOK_TEMPLATE_NOT_FOUND);
    }

    private String getCodeByType(EventPassbookTemplates passbookTemplateCode, EventTicketTemplateType type) {
        if (passbookTemplateCode == null) {
            return null;
        }
        return switch (type) {
            case GROUP -> passbookTemplateCode.getGroupPassbookTemplate();
            case GROUP_INVITATION -> passbookTemplateCode.getGroupInvitationPassbookTemplate();
            case SINGLE_INVITATION -> passbookTemplateCode.getIndividualInvitationPassbookTemplate();
            case SEASON_PACK -> passbookTemplateCode.getSessionPackPassbookTemplate();
            default -> passbookTemplateCode.getIndividualPassbookTemplate();
        };
    }

}
