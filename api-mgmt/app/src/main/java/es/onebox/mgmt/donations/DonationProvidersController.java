package es.onebox.mgmt.donations;

import es.onebox.audit.core.Audit;
import es.onebox.core.security.Roles;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.donations.dto.DonationProvidersDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(value = DonationProvidersController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class DonationProvidersController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/donation-providers";
    private static final String AUDIT_COLLECTION = "DONATION_PROVIDERS";

    private final DonationProvidersService donationProvidersService;

    @Autowired
    public DonationProvidersController(DonationProvidersService donationProvidersService) {
        this.donationProvidersService = donationProvidersService;
    }
    @Secured({Roles.Codes.ROLE_OPR_MGR, Roles.Codes.ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public DonationProvidersDTO getDonationProviders(){
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return donationProvidersService.getDonationProviders();
    }

}
