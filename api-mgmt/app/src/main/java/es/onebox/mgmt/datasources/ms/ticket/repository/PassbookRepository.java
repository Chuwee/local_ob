package es.onebox.mgmt.datasources.ms.ticket.repository;

import es.onebox.core.serializer.dto.common.CodeDTO;
import es.onebox.mgmt.datasources.ms.ticket.MsTicketDatasource;
import es.onebox.mgmt.datasources.ms.ticket.dto.AvailablePassbookField;
import es.onebox.mgmt.datasources.ms.ticket.dto.CreatePassbookTemplate;
import es.onebox.mgmt.datasources.ms.ticket.dto.PassbookRequestFilter;
import es.onebox.mgmt.datasources.ms.ticket.dto.PassbookTemplate;
import es.onebox.mgmt.datasources.ms.ticket.dto.PassbookTemplateList;
import es.onebox.mgmt.datasources.ms.ticket.dto.UpdatePassbookTemplate;
import es.onebox.mgmt.datasources.ms.ticket.enums.PassbookTemplateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class PassbookRepository {

    @Autowired
    private MsTicketDatasource msTicketDatasource;

    public PassbookTemplateList searchPassbookTemplates(PassbookRequestFilter filter) {
        return msTicketDatasource.searchPassbookTemplates(filter);
    }

    public CodeDTO createPassbookTemplates(CreatePassbookTemplate cpt) {
        return msTicketDatasource.createPassbookTemplate(cpt);
    }

    public void deletePassbookTemplate(String code, Long entityId) {
        msTicketDatasource.deletePassbookTemplate(code, entityId);
    }

    public PassbookTemplate getPassbookTemplate(String code, Long entityId) {
        return msTicketDatasource.getPassbookTemplate(code, entityId);
    }

    public void updatePassbookTemplate(String code, Long entityId, UpdatePassbookTemplate updatePassbookTemplate) {
        msTicketDatasource.updatePassbookTemplate(code, entityId, updatePassbookTemplate);
    }

    public Map<String, String> getPassbookLiterals(Long entityId, String code, String langCode) {
        return msTicketDatasource.getPassbookLiterals(entityId, code, langCode);
    }

    public void updatePassbookLiterals(String passbookCode, Long entityId, String langCode, Map<String, String> literals) {
        msTicketDatasource.updatePassbookLiterals(passbookCode, entityId, langCode, literals);
    }

    public List<AvailablePassbookField> availablePassbookFields(PassbookTemplateType type) {
        return msTicketDatasource.availablePassbookFields(type);
    }

    public List<String> availableDataPlaceholders(PassbookTemplateType type) {
        return msTicketDatasource.availableDataPlaceholders(type);
    }

    public List<String> getPassbookAvailableLiteralKeys() {
        return msTicketDatasource.availableLiteralKeys();
    }
}
