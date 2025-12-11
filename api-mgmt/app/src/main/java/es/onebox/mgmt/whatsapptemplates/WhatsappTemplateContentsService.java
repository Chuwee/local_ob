package es.onebox.mgmt.whatsapptemplates;

import es.onebox.mgmt.channels.contents.converter.ChannelContentsConverter;
import es.onebox.mgmt.datasources.ms.channel.dto.whatsapptemplates.WhatsappTemplates;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.whatsapptemplates.dto.WhatsappTemplatesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WhatsappTemplateContentsService {

    private final EntitiesRepository entitiesRepository;

    @Autowired
    public WhatsappTemplateContentsService(EntitiesRepository entitiesRepository) {
        this.entitiesRepository = entitiesRepository;
    }

    public WhatsappTemplatesDTO getWhatsappTemplatesContents(Long entityId) {
        WhatsappTemplates result = entitiesRepository.getWhatsappTemplatesContents(entityId);
        return ChannelContentsConverter.toDTO(result);
    }

}
