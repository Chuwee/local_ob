package es.onebox.mgmt.channels.taxes;

import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.taxes.converter.ChannelTaxesConverter;
import es.onebox.mgmt.channels.taxes.dto.ChannelSurchargesTaxesDTO;
import es.onebox.mgmt.channels.taxes.dto.ChannelSurchargesTaxesUpdateDTO;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsTaxesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChannelsTaxesService {

    private final ChannelsHelper channelsHelper;
    private final ChannelsTaxesRepository channelsTaxesRepository;

    @Autowired
    public ChannelsTaxesService(ChannelsHelper channelsHelper, ChannelsTaxesRepository channelsTaxesRepository) {
        this.channelsHelper = channelsHelper;
        this.channelsTaxesRepository = channelsTaxesRepository;
    }

    public ChannelSurchargesTaxesDTO getChannelsSurchargesTaxes(Long channelId) {
        channelsHelper.getAndCheckChannel(channelId);
        return ChannelTaxesConverter.toDTO(channelsTaxesRepository.getChannelSurchargesTaxes(channelId));
    }

    public void updateChannelsSurchargesTaxes(Long channelId, ChannelSurchargesTaxesUpdateDTO body) {
        channelsHelper.getAndCheckChannel(channelId);
        channelsTaxesRepository.updateChannelSurchargesTaxes(channelId, ChannelTaxesConverter.toMS(body));
    }

}
