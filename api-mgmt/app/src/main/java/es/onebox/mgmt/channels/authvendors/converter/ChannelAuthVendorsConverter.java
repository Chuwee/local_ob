package es.onebox.mgmt.channels.authvendors.converter;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.authvendors.dto.ChannelAuthVendorDTO;
import es.onebox.mgmt.channels.authvendors.dto.ChannelAuthVendorUserDataDTO;
import es.onebox.mgmt.channels.authvendors.enums.ChannelAuthVendorsType;
import es.onebox.mgmt.datasources.ms.channel.dto.authvendor.ChannelAuthVendor;
import es.onebox.mgmt.datasources.ms.channel.dto.authvendor.ChannelAuthVendorSso;
import es.onebox.mgmt.datasources.ms.channel.dto.authvendor.ChannelAuthVendorUserData;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;

public class ChannelAuthVendorsConverter {

    private ChannelAuthVendorsConverter() {}

    public static ChannelAuthVendorDTO toDTO(ChannelAuthVendor source, ChannelAuthVendorsType type) {
        switch (type) {
            case USER_DATA: return toDTOUserData(source);
            case SSO: return toDTOSso(source);
            default:
                throw new OneboxRestException(ApiMgmtErrorCode.AUTH_VENDOR_TYPE_NOT_FOUND);
        }
    }

    private static ChannelAuthVendorUserDataDTO toDTOUserData(ChannelAuthVendor source) {
        ChannelAuthVendorUserDataDTO target = new ChannelAuthVendorUserDataDTO();
        target.setVendors(source.getVendors());
        target.setAllowed(source.getUserDataConfig().getAllowed());
        target.setEditableData(source.getUserDataConfig().getEditableData());
        target.setMandatoryLogin(source.getUserDataConfig().getMandatoryLogin());
        return target;
    }

    private static ChannelAuthVendorDTO toDTOSso(ChannelAuthVendor source) {
        ChannelAuthVendorDTO target = new ChannelAuthVendorDTO();
        target.setVendors(source.getSsoVendors());
        target.setAllowed(source.getSsoConfig().getAllowed());
        return target;
    }

    public static ChannelAuthVendor toMS(ChannelAuthVendorDTO source, ChannelAuthVendorsType type) {
        switch (type) {
            case USER_DATA: return toMsUserData((ChannelAuthVendorUserDataDTO) source);
            case SSO: return toMsSso(source);
            default:
                throw new OneboxRestException(ApiMgmtErrorCode.AUTH_VENDOR_TYPE_NOT_FOUND);
        }
    }

    private static ChannelAuthVendor toMsUserData(ChannelAuthVendorUserDataDTO source) {
        ChannelAuthVendor target = new ChannelAuthVendor();
        target.setVendors(source.getVendors());
        target.setUserDataConfig(new ChannelAuthVendorUserData());
        target.getUserDataConfig().setEditableData(source.getEditableData());
        target.getUserDataConfig().setAllowed(source.getAllowed());
        target.getUserDataConfig().setMandatoryLogin(source.getMandatoryLogin());
        return target;
    }

    private static ChannelAuthVendor toMsSso(ChannelAuthVendorDTO source) {
        ChannelAuthVendor target = new ChannelAuthVendor();
        target.setSsoVendors(source.getVendors());
        target.setSsoConfig(new ChannelAuthVendorSso());
        target.getSsoConfig().setAllowed(source.getAllowed());
        return target;
    }
}
