package es.onebox.fever.controller;

import es.onebox.common.config.ApiConfig;
import es.onebox.common.security.Role;
import es.onebox.fever.dto.UpdateExtReferenceRequest;
import es.onebox.fever.service.ExternalReferenceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = ApiConfig.FeverApiConfig.BASE_URL)
public class ExternalReferenceController {


  private final ExternalReferenceService externalReferenceService;

  @Autowired
  public ExternalReferenceController(ExternalReferenceService externalReferenceService) {
    this.externalReferenceService = externalReferenceService;
  }

  @Secured({Role.CHANNEL_INTEGRATION, Role.OPERATOR_MANAGER, Role.ENTITY_MANAGER})
  @PostMapping("/external-reference")
  public void updateExternalReference(@RequestBody @Valid UpdateExtReferenceRequest request) {
    externalReferenceService.updateExternalReference(request);
  }
}
