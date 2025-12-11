package es.onebox.event.tickettemplates;

import static es.onebox.core.exception.CoreErrorCode.BAD_PARAMETER;

import java.util.List;
import java.util.Map;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.tickettemplates.dto.CommunicationElementDTO;
import es.onebox.event.tickettemplates.dto.TicketTemplateTagType;
import es.onebox.jooq.cpanel.tables.records.CpanelDescPorIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComTicketRecord;

public class TicketTemplateCommunicationElementUtils {

    private TicketTemplateCommunicationElementUtils() {
    }

    public static CpanelDescPorIdiomaRecord checkAndGetElement(CommunicationElementDTO element, Integer
            languageId, Map<TicketTemplateTagType, List<CpanelDescPorIdiomaRecord>> records) {
        if (element.getTagType() == null) {
            throw new OneboxRestException(BAD_PARAMETER, "tag type is mandatory", null);
        }
        if (element.getLanguage() == null) {
            throw new OneboxRestException(BAD_PARAMETER, "language code id is mandatory", null);
        }
        if (languageId == null) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER_FORMAT, "language code invalid", null);
        }

        if (records.containsKey(element.getTagType())) {
            return records.get(element.getTagType()).stream().
                    filter(e -> e.getIdidioma().equals(languageId)).
                    findFirst().orElse(null);
        }
        return null;
    }

    public static void setTagRecord(CpanelElementosComTicketRecord ticketComElements,
                                    Integer itemId, TicketTemplateTagType tagType) {
        if (TicketTemplateTagType.HEADER.equals(tagType)) {
            ticketComElements.setPathimagencabecera(itemId);
        }
        if (TicketTemplateTagType.BODY.equals(tagType)) {
            ticketComElements.setPathimagencuerpo(itemId);
        }
        if (TicketTemplateTagType.EVENT_LOGO.equals(tagType)) {
            ticketComElements.setPathimagenlogo(itemId);
        }
        if (TicketTemplateTagType.BANNER_MAIN.equals(tagType)) {
            ticketComElements.setPathimagenbanner1(itemId);
        }
        if (TicketTemplateTagType.BANNER_SECONDARY.equals(tagType)) {
            ticketComElements.setPathimagenbanner2(itemId);
        }
        if (TicketTemplateTagType.BANNER_CHANNEL_LOGO.equals(tagType)) {
            ticketComElements.setPathimagenbanner3(itemId);
        }
        if (TicketTemplateTagType.TERMS_AND_CONDITIONS.equals(tagType)) {
            ticketComElements.setTerminos(itemId);
        }
    }

    public static Integer getItemIdByTag(CpanelElementosComTicketRecord ticketComElements,
                                         TicketTemplateTagType tagType) {
        switch(tagType) {
            case HEADER:
                return ticketComElements.getPathimagencabecera();
            case BODY:
                return ticketComElements.getPathimagencuerpo();
            case EVENT_LOGO:
                return ticketComElements.getPathimagenlogo();
            case BANNER_MAIN:
                return ticketComElements.getPathimagenbanner1();
            case BANNER_SECONDARY:
                return ticketComElements.getPathimagenbanner2();
            case BANNER_CHANNEL_LOGO:
                return ticketComElements.getPathimagenbanner3();
            case TERMS_AND_CONDITIONS:
                return ticketComElements.getTerminos();
            default:
                throw new OneboxRestException(BAD_PARAMETER, "tag type is wrong", null);
        }
    }

}
