package es.onebox.mgmt.oneboxinvoicing;

import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.events.dto.EventSearchFilterDTO;
import es.onebox.mgmt.oneboxinvoicing.dto.CreateOneboxInvoiceEntityRequestDTO;
import es.onebox.mgmt.oneboxinvoicing.dto.EntityInvoiceConfigurationSearchFilterDTO;
import es.onebox.mgmt.oneboxinvoicing.dto.GenerateOneboxInvoiceRequestDTO;
import es.onebox.mgmt.oneboxinvoicing.dto.OneboxInvoiceEntitiesDTO;
import es.onebox.mgmt.oneboxinvoicing.dto.OneboxInvoiceEntitiesFilterDTO;
import es.onebox.mgmt.oneboxinvoicing.dto.OneboxInvoiceEventsResponse;
import es.onebox.mgmt.oneboxinvoicing.dto.UpdateOneboxInvoiceEntityRequestDTO;
import es.onebox.mgmt.oneboxinvoicing.enums.OneboxInvoiceType;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_SYS_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;

@RestController
@Validated
@RequestMapping(OneboxInvoicingController.BASE_URL)
public class OneboxInvoicingController {

    public static final String BASE_URL = ApiConfig.BASE_URL + "/onebox-invoicing";

    private final OneboxInvoicingService oneboxInvoicingService;
    private final OneboxInvoicingConfigurationService oneboxInvoicingConfigurationService;

    @Autowired
    public OneboxInvoicingController(OneboxInvoicingService oneboxInvoicingService, OneboxInvoicingConfigurationService oneboxInvoicingConfigurationService) {
        this.oneboxInvoicingService = oneboxInvoicingService;
        this.oneboxInvoicingConfigurationService = oneboxInvoicingConfigurationService;
    }

    @Secured({ROLE_SYS_MGR, ROLE_SYS_ANS})
    @RequestMapping(method = RequestMethod.POST)
    public void generateInvoice(@Valid @RequestBody GenerateOneboxInvoiceRequestDTO request) {
        oneboxInvoicingService.generateInvoice(request);
    }

    @Secured({ROLE_SYS_MGR, ROLE_SYS_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/entities-filter")
    public OneboxInvoiceEntitiesFilterDTO getEntitiesFilter() {
        return oneboxInvoicingService.getEntitiesFilter();
    }

    @Secured({ROLE_SYS_MGR, ROLE_SYS_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/entities")
    public OneboxInvoiceEntitiesDTO getEntitiesInvoiceConfiguration(@BindUsingJackson @Valid EntityInvoiceConfigurationSearchFilterDTO filter) {
        return oneboxInvoicingConfigurationService.getEntitiesInvoiceConfiguration(filter);
    }

    @Secured({ROLE_SYS_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/entities/{entityId}")
    public void createEntityInvoiceConfiguration(@PathVariable Long entityId, @Valid @RequestBody CreateOneboxInvoiceEntityRequestDTO request) {
        oneboxInvoicingConfigurationService.createEntityInvoiceConfiguration(entityId, request);
    }

    @Secured({ROLE_SYS_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/entities/{entityId}/type/{type}")
    public void updateEntityInvoiceConfiguration(@PathVariable Long entityId,
                                                 @PathVariable OneboxInvoiceType type,
                                                 @Valid @RequestBody UpdateOneboxInvoiceEntityRequestDTO request) {
        oneboxInvoicingConfigurationService.updateEntityInvoiceConfiguration(entityId, type, request);
    }

    @Secured({ROLE_SYS_MGR, ROLE_SYS_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/entities/{entityId}/events")
    public OneboxInvoiceEventsResponse getInvoiceEvents(@PathVariable Long entityId,
                                                        @BindUsingJackson @Valid EventSearchFilterDTO filter) {
        return oneboxInvoicingConfigurationService.getEvents(entityId, filter);
    }
}
