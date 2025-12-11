package es.onebox.mgmt.datasources.ms.entity.repository;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.entity.MsEntityDatasource;
import es.onebox.mgmt.datasources.ms.entity.dto.CreateOperatorRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.CreateOperatorTaxRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.CreateOperatorsResponse;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityTax;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.dto.Operators;
import es.onebox.mgmt.datasources.ms.entity.dto.OperatorsSearchFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.UpdateOperatorRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.UpdateOperatorTaxesRequest;
import es.onebox.mgmt.operators.dto.OperatorCurrencies;
import es.onebox.mgmt.operators.dto.UpdateOperatorCurrencyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OperatorsRepository {

    private final MsEntityDatasource msEntityDatasource;

    @Autowired
    public OperatorsRepository(MsEntityDatasource msEntityDatasource) {
        this.msEntityDatasource = msEntityDatasource;
    }

    public Operator getOperator(Long operatorId) {
        return msEntityDatasource.getOperator(operatorId);
    }

    public Operators searchOperators(OperatorsSearchFilter filter) {
        return msEntityDatasource.searchOperators(filter);
    }

    public CreateOperatorsResponse createOperator(CreateOperatorRequest request) {
        return msEntityDatasource.createOperator(request);
    }
    public void updateOperator(Long operatorId, UpdateOperatorRequest request) {
        msEntityDatasource.updateOperator(operatorId, request);
    }

    public OperatorCurrencies getOperatorCurrency(Long operatorId){
       return msEntityDatasource.getOperatorCurrency(operatorId);
    }

    public void addOperatorCurrencies(Long operatorId, UpdateOperatorCurrencyRequest request){
        msEntityDatasource.addOperatorCurrencies(operatorId,request);
    }

    public List<EntityTax> getOperatorTaxes(Long operatorId){
        return msEntityDatasource.getOperatorTaxes(operatorId);
    }

    public IdDTO createOperatorTax(Long operatorId, CreateOperatorTaxRequest createOperatorTaxRequest) {
        return msEntityDatasource.createOperatorTax(operatorId, createOperatorTaxRequest);
    }

    public void updateOperatorTaxes(Long operatorId, UpdateOperatorTaxesRequest updateOperatorTaxesRequest) {
        msEntityDatasource.updateOperatorTaxes(operatorId, updateOperatorTaxesRequest);
    }
}
