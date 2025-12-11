package es.onebox.mgmt.operators.controller;


import es.onebox.core.security.Roles;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.operators.dto.OperatorCurrenciesDTO;
import es.onebox.mgmt.operators.dto.UpdateOperatorCurrencyRequestDTO;
import es.onebox.mgmt.operators.service.OperatorCurrenciesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_SYS_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;

@RestController
@Validated
@RequestMapping(value = OperatorCurrenciesController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class OperatorCurrenciesController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/operators/{operatorId}/currencies";

    private final OperatorCurrenciesService operatorCurrenciesService;

    @Autowired
    public OperatorCurrenciesController(OperatorCurrenciesService operatorCurrenciesService) {
        this.operatorCurrenciesService = operatorCurrenciesService;
    }

    @Secured({Roles.Codes.ROLE_CNL_MGR, Roles.Codes.ROLE_EVN_MGR, Roles.Codes.ROLE_ENT_MGR, Roles.Codes.ROLE_ENT_ANS,
            Roles.Codes.ROLE_OPR_MGR, Roles.Codes.ROLE_OPR_ANS, Roles.Codes.ROLE_SYS_MGR, ROLE_SYS_ANS, Roles.Codes.ROLE_CNL_SAC})
    @GetMapping
    public OperatorCurrenciesDTO getOperatorCurrencies(@PathVariable Long operatorId) {
        return operatorCurrenciesService.getOperatorCurrency(operatorId);
    }
    @Secured({ROLE_SYS_MGR, ROLE_SYS_ANS})
    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addOperatorCurrencies(@PathVariable Long operatorId,
                                      @RequestBody UpdateOperatorCurrencyRequestDTO operatorCurrencyRequestDTO) {
        operatorCurrenciesService.addOperatorCurrencies(operatorId, operatorCurrencyRequestDTO);
    }
}
