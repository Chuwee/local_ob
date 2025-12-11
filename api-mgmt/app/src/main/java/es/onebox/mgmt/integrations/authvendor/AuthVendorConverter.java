package es.onebox.mgmt.integrations.authvendor;

import es.onebox.mgmt.datasources.ms.client.dto.AuthVendorConfig;
import es.onebox.mgmt.integrations.authvendor.dto.AuthVendorDTO;

public class AuthVendorConverter {

    private AuthVendorConverter() {
    }

    public static AuthVendorDTO fromMs(AuthVendorConfig authVendor) {
        if (authVendor == null) return null;
        AuthVendorDTO dto = new AuthVendorDTO();
        dto.setId(authVendor.getId());
        dto.setProperties(authVendor.getProperties());
        return dto;
    }
}
