package es.onebox.mgmt.collectives.collectivecodes.converter;

import es.onebox.core.serializer.dto.request.LimitedFilter;
import es.onebox.mgmt.collectives.collectivecodes.dto.CollectiveCodeDTO;
import es.onebox.mgmt.collectives.collectivecodes.dto.CollectiveCodeUsageDTO;
import es.onebox.mgmt.collectives.collectivecodes.dto.CollectiveCodesDTO;
import es.onebox.mgmt.collectives.collectivecodes.dto.request.CollectiveCodesSearchRequest;
import es.onebox.mgmt.collectives.collectivecodes.dto.request.CreateCollectiveCodeRequest;
import es.onebox.mgmt.collectives.collectivecodes.dto.request.CreateCollectiveCodesBulkRequest;
import es.onebox.mgmt.collectives.collectivecodes.dto.request.DeleteCollectiveCodesBulkRequest;
import es.onebox.mgmt.collectives.collectivecodes.dto.request.UpdateCollectiveCodeRequest;
import es.onebox.mgmt.collectives.collectivecodes.dto.request.UpdateCollectiveCodesBulkUnifiedData;
import es.onebox.mgmt.collectives.collectivecodes.dto.request.UpdateCollectiveCodesBulkUnifiedRequest;
import es.onebox.mgmt.collectives.dto.ValidationMethod;
import es.onebox.mgmt.common.BaseValidityPeriodDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.MsCollectiveCodeDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.MsCollectiveCodesDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsCollectiveCodesSearchRequest;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsCreateCollectiveCodeDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsCreateCollectiveCodesDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsDeleteCollectiveCodesBulkDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsUpdateCollectiveCodeDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsUpdateCollectiveCodesBulkUnifiedDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsUpdateCollectiveCodesBulkUnifiedData;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CollectiveCodesConverter {

    private CollectiveCodesConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static MsCollectiveCodesSearchRequest toMsCollectiveCodesSearchRequest(CollectiveCodesSearchRequest requestIn){

        MsCollectiveCodesSearchRequest requestOut = new MsCollectiveCodesSearchRequest();
        requestOut.setQ(requestIn.getQ());
        requestOut.setOffset(requestIn.getOffset()!=null ? requestIn.getOffset() : 0L);
        requestOut.setLimit(requestIn.getLimit()!=null ? requestIn.getLimit() : LimitedFilter.DEFAULT_LIMIT);

        return requestOut;
    }

    public static CollectiveCodesDTO toCollectiveCodesDTO(MsCollectiveCodesDTO codesIn){
        CollectiveCodesDTO codesOut = new CollectiveCodesDTO();

        List<CollectiveCodeDTO> codesData = codesIn.getData().stream()
                .map(CollectiveCodesConverter::toCollectiveCodeDTO)
                .collect(Collectors.toList());

        codesOut.setMetadata(codesIn.getMetadata());
        codesOut.setData(codesData);
        return codesOut;
    }

    public static CollectiveCodeDTO toCollectiveCodeDTO(MsCollectiveCodeDTO codeIn){
        CollectiveCodeDTO codeOut = new CollectiveCodeDTO();

        BaseValidityPeriodDTO validityPeriod = new BaseValidityPeriodDTO();
        validityPeriod.setFrom(codeIn.getStartDate());
        validityPeriod.setTo(codeIn.getEndDate());

        CollectiveCodeUsageDTO usage = new CollectiveCodeUsageDTO();
        usage.setLimit(codeIn.getUsageLimit());
        usage.setCurrent(codeIn.getUsages());

        codeOut.setCode(codeIn.getCode());
        codeOut.setKey(codeIn.getPassword());
        codeOut.setValidationMethod(ValidationMethod.valueOf(codeIn.getType().name()));
        codeOut.setValidityPeriod(validityPeriod);
        codeOut.setUsage(usage);

        return codeOut;
    }

    public static MsCreateCollectiveCodesDTO toMsCreateCollectiveCodeDTO(CreateCollectiveCodesBulkRequest requestIn){
        MsCreateCollectiveCodesDTO requestOut = new MsCreateCollectiveCodesDTO();

        requestOut.addAll(requestIn.stream().map(CollectiveCodesConverter::toMsCreateCollectiveCodeDTO)
                                            .collect(Collectors.toList()));

        return requestOut;
    }

    public static MsCreateCollectiveCodeDTO toMsCreateCollectiveCodeDTO(CreateCollectiveCodeRequest requestIn){
        MsCreateCollectiveCodeDTO requestOut = new MsCreateCollectiveCodeDTO();

        requestOut.setCode(requestIn.getCode());
        requestOut.setPassword(requestIn.getKey());
        requestOut.setUsageLimit(requestIn.getUsageLimit());
        if(Objects.nonNull(requestIn.getValidityPeriod())){
            requestOut.setStartDate(requestIn.getValidityPeriod().getFrom());
            requestOut.setEndDate(requestIn.getValidityPeriod().getTo());
        }

        return requestOut;
    }

    public static MsUpdateCollectiveCodeDTO toMsUpdateCollectiveCodeDTO(UpdateCollectiveCodeRequest requestIn){
        MsUpdateCollectiveCodeDTO requestOut = new MsUpdateCollectiveCodeDTO();

        requestOut.setUsageLimit(requestIn.getUsageLimit());
        if(Objects.nonNull(requestIn.getValidityPeriod())){
            requestOut.setStartDate(requestIn.getValidityPeriod().getFrom());
            requestOut.setEndDate(requestIn.getValidityPeriod().getTo());
        }

        return requestOut;
    }

    public static MsUpdateCollectiveCodesBulkUnifiedDTO toMsUpdateCollectiveCodesBulkUnifiedDTO(UpdateCollectiveCodesBulkUnifiedRequest requestIn){
        MsUpdateCollectiveCodesBulkUnifiedDTO requestOut = new MsUpdateCollectiveCodesBulkUnifiedDTO();

        requestOut.setCodes(requestIn.getCodes());
        requestOut.setData(toMsUpdateCollectiveCodesBulkUnifiedData(requestIn.getData()));

        return requestOut;
    }

    private static MsUpdateCollectiveCodesBulkUnifiedData toMsUpdateCollectiveCodesBulkUnifiedData(UpdateCollectiveCodesBulkUnifiedData dataIn) {
        MsUpdateCollectiveCodesBulkUnifiedData dataOut = new MsUpdateCollectiveCodesBulkUnifiedData();

        dataOut.setUsageLimit(dataIn.getUsageLimit());
        if(dataIn.getValidityPeriod() != null){
            dataOut.setStartDate(dataIn.getValidityPeriod().getFrom());
            dataOut.setEndDate(dataIn.getValidityPeriod().getTo());
        }

        return dataOut;
    }

    public static MsDeleteCollectiveCodesBulkDTO toMsDeleteCollectiveCodesBulkDTO(DeleteCollectiveCodesBulkRequest requestIn){
        MsDeleteCollectiveCodesBulkDTO requestOut = new MsDeleteCollectiveCodesBulkDTO();
        requestOut.setCodes(requestIn.getCodes());

        return requestOut;
    }
}
