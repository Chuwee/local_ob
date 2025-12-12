package es.onebox.fusionauth;



import es.onebox.fusionauth.config.ApiConfig;
import es.onebox.fusionauth.dto.FusionAuthNotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping(value = FusionAuthController.BASE_URI)
public class FusionAuthController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/fusionauth";
    private static final String SIGNATURE_HEADER = "X-FusionAuth-Signature-JWT";

    private final FusionAuthService fusionAuthService;

    @Autowired
    public FusionAuthController(FusionAuthService fusionAuthService) {
        this.fusionAuthService = fusionAuthService;
    }

    @PostMapping("/webhook")
    public void registerOperation(@RequestBody FusionAuthNotificationDTO notification,
                                  @RequestHeader(SIGNATURE_HEADER) String receivedSignature){
        fusionAuthService.registerEvent(notification, receivedSignature);
    }

}
