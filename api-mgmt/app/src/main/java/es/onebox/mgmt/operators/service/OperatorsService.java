package es.onebox.mgmt.operators.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.entity.dto.CreateOperatorsResponse;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.dto.Operators;
import es.onebox.mgmt.datasources.ms.entity.repository.OperatorsRepository;
import es.onebox.mgmt.exception.ApiMgmtEntitiesErrorCode;
import es.onebox.mgmt.operators.converter.OperatorsConverter;
import es.onebox.mgmt.operators.dto.CreateOperatorRequestDTO;
import es.onebox.mgmt.operators.dto.CreateOperatorsResponseDTO;
import es.onebox.mgmt.operators.dto.OperatorDTO;
import es.onebox.mgmt.operators.dto.OperatorsDTO;
import es.onebox.mgmt.operators.dto.OperatorsSearchRequestDTO;
import es.onebox.mgmt.operators.dto.UpdateOperatorRequestDTO;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OperatorsService {

    private final OperatorsRepository operatorsRepository;
    private final ValidationService validationService;

    static final private String SPACE = " ";

    @Autowired
    public OperatorsService(OperatorsRepository operatorsRepository, ValidationService validationService) {
        this.operatorsRepository = operatorsRepository;
        this.validationService = validationService;
    }

    public OperatorDTO getOperator(Long operatorId) {
        Operator operator = validationService.getAndCheckOperator(operatorId);

        return OperatorsConverter.toDTO(operator);
    }

    public OperatorsDTO searchOperators(OperatorsSearchRequestDTO filter) {
        Operators operators = operatorsRepository.searchOperators(OperatorsConverter.toMs(filter));
        return OperatorsConverter.toDTO(operators.getData(), operators.getMetadata());
    }

    public CreateOperatorsResponseDTO createOperator(CreateOperatorRequestDTO request) {
        if (request.shortName().contains(SPACE)) {
            throw OneboxRestException.builder(ApiMgmtEntitiesErrorCode.SHORT_NAME_MUST_NOT_CONTAIN_SPACES).build();
        }
        CreateOperatorsResponse response =  operatorsRepository.createOperator(OperatorsConverter.toMs(request));
        return OperatorsConverter.toDTO(response);
    }

    public void updateOperator(Long operatorId, UpdateOperatorRequestDTO request) {
        validationService.getAndCheckOperator(operatorId);
        operatorsRepository.updateOperator(operatorId, OperatorsConverter.toMs(request));
    }
}
