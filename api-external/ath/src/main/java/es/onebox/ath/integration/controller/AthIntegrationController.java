package es.onebox.ath.integration.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.onebox.ath.integration.dto.ConsultRequestDTO;
import es.onebox.ath.integration.dto.ConsultResponseDTO;
import es.onebox.ath.integration.dto.LoginRequestDTO;
import es.onebox.ath.integration.dto.LoginResponseDTO;
import es.onebox.ath.integration.dto.PayloadRequestDTO;
import es.onebox.ath.integration.dto.SeatManagementResponseDTO;
import es.onebox.ath.integration.service.AthIntegrationService;
import es.onebox.common.config.ApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping(value = AthIntegrationController.BASE_URI)
public class AthIntegrationController {
    public static final String BASE_URI = ApiConfig.ATHApiConfig.BASE_URL;

    private final AthIntegrationService athIntegrationService;

    @Autowired
    public AthIntegrationController(AthIntegrationService athIntegrationService) {
        this.athIntegrationService = athIntegrationService;
    }

    @PostMapping(value = "/login")
    public LoginResponseDTO login(@RequestBody @Valid LoginRequestDTO body) throws JsonProcessingException {
        return athIntegrationService.login(body);
    }

    @PostMapping(value = "/release")
    public SeatManagementResponseDTO release(@RequestBody @Valid PayloadRequestDTO body) throws JsonProcessingException {
        return athIntegrationService.release(body);
    }

    @PostMapping(value = "/recover")
    public SeatManagementResponseDTO recover(@RequestBody @Valid PayloadRequestDTO body) throws JsonProcessingException {
        return athIntegrationService.recover(body);
    }

    @GetMapping(value = "/transfers-list")
    public ConsultResponseDTO getTransfersList(@RequestBody @Valid ConsultRequestDTO body) throws JsonProcessingException {
        return athIntegrationService.getTransfersList(body);
    }

}
