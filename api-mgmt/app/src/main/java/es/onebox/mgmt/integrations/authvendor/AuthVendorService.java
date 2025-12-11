package es.onebox.mgmt.integrations.authvendor;

import es.onebox.mgmt.datasources.ms.client.dto.AuthVendorConfig;
import es.onebox.mgmt.datasources.ms.client.repositories.AuthVendorRepository;
import es.onebox.mgmt.integrations.authvendor.dto.AuthVendorDTO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthVendorService {

    @Autowired
    private AuthVendorRepository authVendorRepository;

    public List<AuthVendor> getAuthVendors() {
        List<AuthVendorConfig> authVendors = authVendorRepository.getAuthVendors();
        if (CollectionUtils.isNotEmpty(authVendors)) {
            return authVendors.stream()
                    .map(authVendorConfig -> new AuthVendor(authVendorConfig.getId()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public AuthVendorDTO getAuthVendor(String authVendor) {
        AuthVendorConfig authVendors = authVendorRepository.getAuthVendors(authVendor);
        return AuthVendorConverter.fromMs(authVendors);
    }

}
