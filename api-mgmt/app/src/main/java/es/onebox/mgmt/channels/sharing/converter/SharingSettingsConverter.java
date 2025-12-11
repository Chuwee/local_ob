package es.onebox.mgmt.channels.sharing.converter;

import es.onebox.mgmt.channels.sharing.dto.BookingCheckoutPaymentSettingsDTO;
import es.onebox.mgmt.channels.sharing.dto.BookingCheckoutSettingsDTO;
import es.onebox.mgmt.channels.sharing.dto.SharingSettingsDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.BookingCheckoutPaymentSettings;
import es.onebox.mgmt.datasources.ms.channel.dto.BookingCheckoutSettings;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelConfig;
import es.onebox.mgmt.datasources.ms.channel.dto.SharingSettings;

import java.util.ArrayList;
import java.util.List;

public class SharingSettingsConverter {

    private SharingSettingsConverter() {}

    public static SharingSettingsDTO toDTO(ChannelConfig channelConfig) {
        SharingSettingsDTO out = new SharingSettingsDTO();

        if (channelConfig != null && channelConfig.getSharingSettings() != null) {
            SharingSettings sharingSettings = channelConfig.getSharingSettings();

            out.setAllowBookingSharing(sharingSettings.getAllowBookingSharing());

            if (sharingSettings.getBookingCheckout() != null) {
                BookingCheckoutSettings source = sharingSettings.getBookingCheckout();

                BookingCheckoutSettingsDTO checkoutDTO = new BookingCheckoutSettingsDTO();
                checkoutDTO.setEnabled(source.getEnabled());
                checkoutDTO.setChannelId(source.getChannelId());
                checkoutDTO.setChannelName(source.getChannelName());

                if (source.getPaymentSettings() != null) {
                    List<BookingCheckoutPaymentSettingsDTO> bookingPaymentSettingsList = getBookingCheckoutPaymentSettingsDTO(source);
                    checkoutDTO.setPaymentSettings(bookingPaymentSettingsList);
                }

                out.setBookingCheckout(checkoutDTO);
            }
        } else {
            out.setAllowBookingSharing(false);
        }

        return out;
    }

    public static ChannelConfig toMS(SharingSettingsDTO sharingSettingsDTO) {
        ChannelConfig out = new ChannelConfig();
        SharingSettings sharingSettings = new SharingSettings();
        sharingSettings.setAllowBookingSharing(sharingSettingsDTO.getAllowBookingSharing());

        if(sharingSettingsDTO.getBookingCheckout() != null) {
            BookingCheckoutSettingsDTO checkout = sharingSettingsDTO.getBookingCheckout();

            BookingCheckoutSettings bookingCheckout = new BookingCheckoutSettings();
            bookingCheckout.setEnabled(checkout.getEnabled());
            bookingCheckout.setChannelId(checkout.getChannelId());
            bookingCheckout.setChannelName(checkout.getChannelName());


            if (checkout.getPaymentSettings() != null) {
                List<BookingCheckoutPaymentSettings> bookingPaymentSettingsList = getBookingCheckoutPaymentSettings(checkout);
                bookingCheckout.setPaymentSettings(bookingPaymentSettingsList);
            }
            sharingSettings.setBookingCheckout(bookingCheckout);
        }

        out.setSharingSettings(sharingSettings);
        return out;
    }

    private static List<BookingCheckoutPaymentSettings> getBookingCheckoutPaymentSettings(BookingCheckoutSettingsDTO checkout) {
        List<BookingCheckoutPaymentSettings> bookingPaymentSettingsList = new ArrayList<>();
        List<BookingCheckoutPaymentSettingsDTO> bookingPaymentSettingsListDTO = checkout.getPaymentSettings();
        bookingPaymentSettingsListDTO.forEach(bookingPaymentSetting -> {
            BookingCheckoutPaymentSettings bookingCheckoutPaymentSettings = new BookingCheckoutPaymentSettings();
            bookingCheckoutPaymentSettings.setDefault(bookingPaymentSetting.getDefault());
            bookingCheckoutPaymentSettings.setActive(bookingPaymentSetting.getActive());
            bookingCheckoutPaymentSettings.setConfSid(bookingPaymentSetting.getConfSid());
            bookingCheckoutPaymentSettings.setGatewaySid(bookingPaymentSetting.getGatewaySid());

            bookingPaymentSettingsList.add(bookingCheckoutPaymentSettings);
        });
        return bookingPaymentSettingsList;
    }

    private static List<BookingCheckoutPaymentSettingsDTO> getBookingCheckoutPaymentSettingsDTO(BookingCheckoutSettings checkout) {
        List<BookingCheckoutPaymentSettings> bookingPaymentSettingsList = checkout.getPaymentSettings();
        List<BookingCheckoutPaymentSettingsDTO> bookingPaymentSettingsListDTO = new ArrayList<>();
        bookingPaymentSettingsList.forEach(bookingPaymentSetting -> {
            BookingCheckoutPaymentSettingsDTO bookingCheckoutPaymentSettingsDTO = new BookingCheckoutPaymentSettingsDTO();
            bookingCheckoutPaymentSettingsDTO.setDefault(bookingPaymentSetting.getDefault());
            bookingCheckoutPaymentSettingsDTO.setActive(bookingPaymentSetting.getActive());
            bookingCheckoutPaymentSettingsDTO.setConfSid(bookingPaymentSetting.getConfSid());
            bookingCheckoutPaymentSettingsDTO.setGatewaySid(bookingPaymentSetting.getGatewaySid());

            bookingPaymentSettingsListDTO.add(bookingCheckoutPaymentSettingsDTO);
        });
        return bookingPaymentSettingsListDTO;
    }
}
