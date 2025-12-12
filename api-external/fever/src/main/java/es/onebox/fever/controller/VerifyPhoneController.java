package es.onebox.fever.controller;

import es.onebox.common.config.ApiConfig;
import es.onebox.common.security.Role;
import es.onebox.fever.dto.CustomerPhoneVerificationRequestDTO;
import es.onebox.fever.dto.CustomerVerifyRequestDTO;
import es.onebox.fever.service.VerifyPhoneService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = ApiConfig.FeverApiConfig.BASE_URL)
public class VerifyPhoneController {

  private final VerifyPhoneService verifyPhoneService;

  @Autowired
  public VerifyPhoneController(VerifyPhoneService verifyPhoneService) {
    this.verifyPhoneService = verifyPhoneService;
  }

  @Secured(Role.CHANNEL_INTEGRATION)
  @PostMapping("/phone-verification-code")
  public void requestPhoneVerificationCode(@RequestBody @Valid CustomerPhoneVerificationRequestDTO request) {
      verifyPhoneService.requestPhoneVerificationCode(request);
  }

  @Secured(Role.CHANNEL_INTEGRATION)
  @PostMapping("/verify-phone")
  public void verifyPhone(@RequestBody @Valid CustomerVerifyRequestDTO request) {
    verifyPhoneService.verifyPhone(request);
  }

}
