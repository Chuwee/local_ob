package es.onebox.event.sessions.converter;

import es.onebox.event.sessions.dto.SessionTaxDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelImpuestoRecord;

public class SessionTaxConverter {

    private SessionTaxConverter() {
    }

    public static SessionTaxDTO taxToSessionTaxDTO(CpanelImpuestoRecord tax, SessionTaxDTO.SessionTaxType type) {
        SessionTaxDTO dto = new SessionTaxDTO();
        dto.setId(tax.getIdimpuesto());
        dto.setName(tax.getNombre());
        dto.setValue(tax.getValor());
        dto.setType(type.name());
        return dto;
    }
}
