package es.onebox.mgmt.channels.sharing;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.sharing.converter.SharingSettingsConverter;
import es.onebox.mgmt.channels.sharing.dto.BookingCheckoutPaymentSettingsDTO;
import es.onebox.mgmt.channels.sharing.dto.BookingCheckoutSettingsDTO;
import es.onebox.mgmt.channels.sharing.dto.SharingSettingsDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.BookingCheckoutPaymentSettings;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelType;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.payment.ApiPaymentDatasource;
import es.onebox.mgmt.datasources.ms.payment.dto.ChannelGatewayConfig;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SharingSettingsService {

    private final ChannelsRepository channelsRepository;
    private final ApiPaymentDatasource apiPaymentDatasource;

    private final ChannelsHelper channelsHelper;

    @Autowired
    public SharingSettingsService(ChannelsRepository channelsRepository, ChannelsHelper channelsHelper,
                                  ApiPaymentDatasource apiPaymentDatasource) {
        this.channelsRepository = channelsRepository;
        this.channelsHelper = channelsHelper;
        this.apiPaymentDatasource = apiPaymentDatasource;
    }

    public SharingSettingsDTO getSharingSettings(Long channelId) {
        checkChannel(channelId);
        return SharingSettingsConverter.toDTO(channelsRepository.getChannelConfig(channelId));
    }

    public void updateSharingSettings(Long channelId, SharingSettingsDTO sharingSettingsDTO) {
        checkSharingAndBookingCheckoutChannels(channelId, sharingSettingsDTO.getBookingCheckout());
        channelsRepository.updateChannelConfig(channelId, SharingSettingsConverter.toMS(sharingSettingsDTO));
    }

    private void checkChannel(Long channelId) {
        ChannelResponse channelResponse = channelsHelper.getAndCheckChannel(channelId);
        if(!ChannelSubtype.PORTAL_B2B.equals(channelResponse.getSubtype())) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.SHARING_SETTINGS_NOT_ALLOWED_FOR_CHANNEL_TYPE);
        }
    }

    private void checkSharingAndBookingCheckoutChannels(Long mainChannelId, BookingCheckoutSettingsDTO bookingCheckout) {
        ChannelResponse mainChannel = channelsHelper.getAndCheckChannel(mainChannelId);

        if (!ChannelSubtype.PORTAL_B2B.equals(mainChannel.getSubtype())) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.SHARING_SETTINGS_NOT_ALLOWED_FOR_CHANNEL_TYPE);
        }

        if (bookingCheckout != null && BooleanUtils.isTrue(bookingCheckout.getEnabled())) {
            ChannelResponse checkoutChannel = channelsHelper.getAndCheckChannel(bookingCheckout.getChannelId());

            if (!ChannelType.OB_PORTAL.equals(checkoutChannel.getType())) {
                throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_BOOKING_CHECKOUT_NOT_ALLOWED_FOR_CHANNEL_TYPE);
            }

            if (!Objects.equals(mainChannel.getEntityId(), checkoutChannel.getEntityId())) {
                throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_BOOKING_CHECKOUT_DIFFERENT_ENTITY);
            }

            bookingCheckout.setChannelName(checkoutChannel.getName());
        }
    }

}
