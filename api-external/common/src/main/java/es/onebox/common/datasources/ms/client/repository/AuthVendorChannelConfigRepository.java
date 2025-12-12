package es.onebox.common.datasources.ms.client.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.common.datasources.ms.client.MsClientDatasource;
import es.onebox.common.datasources.ms.client.dto.AuthVendorChannelConfig;
import es.onebox.common.datasources.ms.client.dto.AuthVendorConfig;
import es.onebox.common.datasources.ms.client.dto.AuthVendorUserData;
import es.onebox.common.datasources.ms.client.dto.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class AuthVendorChannelConfigRepository {

    private final MsClientDatasource msClientDatasource;

    @Autowired
    public AuthVendorChannelConfigRepository(MsClientDatasource msClientDatasource) {
        this.msClientDatasource = msClientDatasource;
    }

    @Cached(key = "ms-client_getChannelsByAuthVendor", expires = 10 * 60)
    public List<Long> getChannelsByAuthVendor(@CachedArg String vendorId) {
        return msClientDatasource.getAuthVendorChannels(vendorId);
    }

    @Cached(key = "ms-client_getAuthVendorConfiguration", expires = 10 * 60)
    public AuthVendorConfig getAuthVendorConfiguration(@CachedArg String vendorId) {
        return msClientDatasource.getAuthVendorConfiguration(vendorId);
    }

    public AuthVendorUserData getUserData(String vendorId, Map<String, Object> vendorPayload) {
        return msClientDatasource.getUserData(vendorId, vendorPayload);
    }

    public List<AuthVendorUserData> getRelatdUsers(String vendorId, Map<String, Object> vendorPayload) {
        return msClientDatasource.getRelatedUsers(vendorId, vendorPayload);
    }

    @Cached(key = "ms-client_getAuthVendorChannelConfiguration", expires = 10 * 60)
    public AuthVendorChannelConfig getAuthVendorChannelConfiguration(@CachedArg Long channelId) {
        return msClientDatasource.getAuthVendorChannelConfiguration(channelId);
    }

}
