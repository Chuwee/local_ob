package es.onebox.mgmt.operators.service;


import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.entity.repository.OperatorsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.operators.converter.OperatorsConverter;
import es.onebox.mgmt.operators.dto.OperatorCurrencies;
import es.onebox.mgmt.operators.dto.OperatorCurrenciesDTO;
import es.onebox.mgmt.operators.dto.UpdateOperatorCurrencyRequest;
import es.onebox.mgmt.operators.dto.UpdateOperatorCurrencyRequestDTO;
import es.onebox.mgmt.security.SecurityManager;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OperatorCurrenciesService {

    private final OperatorsRepository operatorsRepository;
    private final SecurityManager securityManager;

    @Autowired
    public OperatorCurrenciesService(OperatorsRepository operatorsRepository, SecurityManager securityManager) {
        this.operatorsRepository = operatorsRepository;
        this.securityManager = securityManager;
    }

    public OperatorCurrenciesDTO getOperatorCurrency(Long operatorId) {
        securityManager.checkOperatorAccessible(operatorId);
        operatorsRepository.getOperator(operatorId);

        OperatorCurrencies operatorCurrencies = operatorsRepository.getOperatorCurrency(operatorId);
        return OperatorsConverter.toCurrenciesDTO(operatorCurrencies);
    }

    public void addOperatorCurrencies(Long operatorId, UpdateOperatorCurrencyRequestDTO updateCurrencyDTO) {
        securityManager.checkOperatorAccessible(operatorId);
        operatorsRepository.getOperator(operatorId);


        if(updateCurrencyDTO == null || CollectionUtils.isEmpty(updateCurrencyDTO.currencyCodes())){
            throw new OneboxRestException(ApiMgmtErrorCode.ERROR_OPERATOR_CURRENCIES_ARE_MANDATORY);
        }

        UpdateOperatorCurrencyRequest request = OperatorsConverter.toCurrenciesMs(updateCurrencyDTO);
        operatorsRepository.addOperatorCurrencies(operatorId,request);
    }
}
