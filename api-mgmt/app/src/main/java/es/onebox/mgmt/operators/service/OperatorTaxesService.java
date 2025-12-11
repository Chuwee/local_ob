package es.onebox.mgmt.operators.service;


import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.CreateOperatorTaxRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityTax;
import es.onebox.mgmt.datasources.ms.entity.dto.UpdateOperatorTaxesRequest;
import es.onebox.mgmt.datasources.ms.entity.repository.OperatorsRepository;
import es.onebox.mgmt.entities.converter.EntityConverter;
import es.onebox.mgmt.entities.dto.EntityTaxApiDTO;
import es.onebox.mgmt.operators.converter.OperatorsConverter;
import es.onebox.mgmt.operators.dto.CreateOperatorTaxRequestDTO;
import es.onebox.mgmt.operators.dto.UpdateOperatorTaxesRequestDTO;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperatorTaxesService {

    private final OperatorsRepository operatorsRepository;
    private final ValidationService validationService;

    @Autowired
    public OperatorTaxesService(OperatorsRepository operatorsRepository, ValidationService validationService) {
        this.operatorsRepository = operatorsRepository;
        this.validationService = validationService;
    }

    public List<EntityTaxApiDTO> getOperatorTaxes(Long operatorId) {
        validationService.getAndCheckOperator(operatorId);

        List<EntityTax> result = operatorsRepository.getOperatorTaxes(operatorId);
        return EntityConverter.fromDTO(result);
    }

    public IdDTO createOperatorTax(Long operatorId, CreateOperatorTaxRequestDTO createOperatorTaxRequestDTO) {
        validationService.getAndCheckOperator(operatorId);

        CreateOperatorTaxRequest createOperatorTaxRequest = OperatorsConverter.toMs(createOperatorTaxRequestDTO);
        return operatorsRepository.createOperatorTax(operatorId, createOperatorTaxRequest);
    }

    public void updateOperatorTaxes(Long operatorId, UpdateOperatorTaxesRequestDTO updateOperatorTaxesRequestDTO) {
        validationService.getAndCheckOperator(operatorId);

        UpdateOperatorTaxesRequest updateOperatorTaxesRequest = OperatorsConverter.toMs(updateOperatorTaxesRequestDTO);
        operatorsRepository.updateOperatorTaxes(operatorId, updateOperatorTaxesRequest);
    }

}
