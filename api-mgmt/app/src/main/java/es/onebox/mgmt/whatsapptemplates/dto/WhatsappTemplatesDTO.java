package es.onebox.mgmt.whatsapptemplates.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

public class WhatsappTemplatesDTO extends HashSet<WhatsappTemplateDTO> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public WhatsappTemplatesDTO(Collection<WhatsappTemplateDTO> in) {
        super(in);
    }
}
