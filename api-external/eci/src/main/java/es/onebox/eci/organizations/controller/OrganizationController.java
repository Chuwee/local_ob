package es.onebox.eci.organizations.controller;

import es.onebox.common.config.ApiConfig;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.eci.common.GenericRequest;
import es.onebox.eci.organizations.dto.Organization;
import es.onebox.eci.organizations.service.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping(ApiConfig.ECIApiConfig.BASE_URL + "/{channelIdentifier}/organizations")
public class OrganizationController {


    private final OrganizationService organizationService;

    @Autowired
    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping()
    public List<Organization> getOrganizations(@RequestParam(value = "session_start_date[gte]", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime gte,
                                               @RequestParam(value = "session_start_date[lte]", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime lte,
                                               @PathVariable("channelIdentifier") String channelIdentifier,
                                        final @Valid @BindUsingJackson GenericRequest request) {

        return organizationService.getOrganizations(gte, lte, request.getLimit(), request.getOffset(), channelIdentifier);
    }

    @GetMapping(value = "/{organizationId}")
    public Organization getOrganization(@PathVariable("channelIdentifier") String channelIdentifier,
                                        @PathVariable("organizationId") String organizationId) {

        return organizationService.getOrganization(channelIdentifier, organizationId);
    }
}
