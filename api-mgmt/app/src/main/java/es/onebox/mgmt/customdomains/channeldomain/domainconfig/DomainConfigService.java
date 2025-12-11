package es.onebox.mgmt.customdomains.channeldomain.domainconfig;


import es.onebox.mgmt.customdomains.channeldomain.domainconfig.dto.DomainConfigDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.domainconfig.DomainConfig;
import es.onebox.mgmt.datasources.ms.channel.repositories.AdminChannelsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainConfigService {

    private final AdminChannelsRepository adminChannelsRepository;

    @Autowired
    public DomainConfigService(AdminChannelsRepository adminChannelsRepository) {
        this.adminChannelsRepository = adminChannelsRepository;
    }

    public DomainConfigDTO getDomainConfig(String domain) {
        DomainConfig config  = adminChannelsRepository.getDomainConfig(domain);
        return DomainConfigConverter.fromMs(config);
    }

    public void putDomainConfig(String domain, DomainConfigDTO in) {
        DomainConfig config  = DomainConfigConverter.toMs(in);
        adminChannelsRepository.updateDomainConfig(domain, config);
    }
}
