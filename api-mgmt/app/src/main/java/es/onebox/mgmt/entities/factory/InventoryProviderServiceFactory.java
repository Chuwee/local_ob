package es.onebox.mgmt.entities.factory;

import es.onebox.mgmt.entities.ItalianComplianceInventoryProviderService;
import es.onebox.mgmt.entities.OneboxInventoryProviderService;
import es.onebox.mgmt.entities.SgaInventoryProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InventoryProviderServiceFactory extends InventoryProviderFactorySupport {

    @Autowired
    private OneboxInventoryProviderService oneboxInventoryProviderService;

    @Autowired
    private SgaInventoryProviderService sgaInventoryProviderService;

    @Autowired
    private ItalianComplianceInventoryProviderService italianComplianceInventoryProviderService;

    @Override
    protected InventoryProviderService getSgaIntegration() {
        return sgaInventoryProviderService;
    }

    @Override
    protected InventoryProviderService getItalianComplianceIntegration() {
        return italianComplianceInventoryProviderService;
    }

    @Override
    protected InventoryProviderService getOneboxIntegration() {
        return oneboxInventoryProviderService;
    }

    public InventoryProviderService getIntegrationService(Long entityId, String provider) {
        return super.getExternalInventoryProviderService(entityId, provider);
    }
}
