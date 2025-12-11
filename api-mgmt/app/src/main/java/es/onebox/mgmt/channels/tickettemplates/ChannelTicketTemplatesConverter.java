package es.onebox.mgmt.channels.tickettemplates;

import es.onebox.mgmt.channels.tickettemplates.dto.ChannelTemplateTicketType;
import es.onebox.mgmt.channels.tickettemplates.dto.ChannelTicketTemplateDTO;
import es.onebox.mgmt.channels.tickettemplates.dto.ChannelTicketTemplateFormat;

import java.util.Collections;
import java.util.List;

public class ChannelTicketTemplatesConverter {

    private ChannelTicketTemplatesConverter() {
    }

    public static List<ChannelTicketTemplateDTO> convert(String passbookTemplate) {
        if (passbookTemplate == null) {
            return Collections.EMPTY_LIST;
        }
        ChannelTicketTemplateDTO out = new ChannelTicketTemplateDTO();
        out.setId(passbookTemplate);
        out.setFormat(ChannelTicketTemplateFormat.PASSBOOK);
        out.setType(ChannelTemplateTicketType.SINGLE);
        return Collections.singletonList(out);
    }

}
