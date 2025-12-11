package es.onebox.mgmt.collectives.collectivecodes;

import es.onebox.mgmt.collectives.CollectivesService;
import es.onebox.mgmt.collectives.collectivecodes.converter.CollectiveCodesConverter;
import es.onebox.mgmt.collectives.collectivecodes.dto.CollectiveCodeDTO;
import es.onebox.mgmt.collectives.collectivecodes.dto.CollectiveCodesDTO;
import es.onebox.mgmt.collectives.collectivecodes.dto.request.CollectiveCodesSearchRequest;
import es.onebox.mgmt.collectives.collectivecodes.dto.request.CreateCollectiveCodeRequest;
import es.onebox.mgmt.collectives.collectivecodes.dto.request.CreateCollectiveCodesBulkRequest;
import es.onebox.mgmt.collectives.collectivecodes.dto.request.DeleteCollectiveCodesBulkRequest;
import es.onebox.mgmt.collectives.collectivecodes.dto.request.UpdateCollectiveCodeRequest;
import es.onebox.mgmt.collectives.collectivecodes.dto.request.UpdateCollectiveCodesBulkUnifiedRequest;
import es.onebox.mgmt.datasources.ms.collective.dto.MsCollectiveCodeDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.MsCollectiveCodesDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsCollectiveCodesSearchRequest;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsCreateCollectiveCodeDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsCreateCollectiveCodesDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsDeleteCollectiveCodesBulkDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsUpdateCollectiveCodeDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsUpdateCollectiveCodesBulkUnifiedDTO;
import es.onebox.mgmt.datasources.ms.collective.repository.CollectiveCodesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CollectiveCodesService {
    private final CollectiveCodesRepository collectiveCodesRepository;
    private final CollectivesService collectivesService;

    @Autowired
    public CollectiveCodesService(CollectivesService collectivesService,
                                  CollectiveCodesRepository collectiveCodesRepository){
        this.collectivesService = collectivesService;
        this.collectiveCodesRepository = collectiveCodesRepository;
    }

    public CollectiveCodesDTO getCollectiveCodes(Long collectiveId, CollectiveCodesSearchRequest request){
        collectivesService.getAndCheckCollective(collectiveId);

        MsCollectiveCodesSearchRequest msCollectiveCodesSearchRequest = CollectiveCodesConverter.toMsCollectiveCodesSearchRequest(request);
        MsCollectiveCodesDTO msCodes = collectiveCodesRepository.getCollectiveCodes(collectiveId,msCollectiveCodesSearchRequest);

        return CollectiveCodesConverter.toCollectiveCodesDTO(msCodes);
    }

    public CollectiveCodeDTO getCollectiveCode(Long collectiveId, String code){
        collectivesService.getAndCheckCollective(collectiveId);

        MsCollectiveCodeDTO msCode = collectiveCodesRepository.getCollectiveCode(collectiveId, code);

        return CollectiveCodesConverter.toCollectiveCodeDTO(msCode);
    }

    public void createCollectiveCode(Long collectiveId, CreateCollectiveCodeRequest request) {
        collectivesService.getAndCheckCollective(collectiveId);

        MsCreateCollectiveCodeDTO msRequest = CollectiveCodesConverter.toMsCreateCollectiveCodeDTO(request);
        collectiveCodesRepository.createCollectiveCode(collectiveId, msRequest);
    }

    public void createCollectiveCodes(Long collectiveId, CreateCollectiveCodesBulkRequest request) {
        collectivesService.getAndCheckCollective(collectiveId);

        MsCreateCollectiveCodesDTO msRequest = CollectiveCodesConverter.toMsCreateCollectiveCodeDTO(request);
        collectiveCodesRepository.createCollectiveCodes(collectiveId, msRequest);
    }

    public void updateCollectiveCode(Long collectiveId, String code, UpdateCollectiveCodeRequest request) {
        collectivesService.getAndCheckCollective(collectiveId);

        MsUpdateCollectiveCodeDTO msRequest = CollectiveCodesConverter.toMsUpdateCollectiveCodeDTO(request);
        collectiveCodesRepository.updateCollectiveCode(collectiveId, code, msRequest);
    }

    public void updateCollectiveCodes(Long collectiveId,
                                      CollectiveCodesSearchRequest filter,
                                      UpdateCollectiveCodesBulkUnifiedRequest request) {
        collectivesService.getAndCheckCollective(collectiveId);

        MsCollectiveCodesSearchRequest msFilter = new MsCollectiveCodesSearchRequest();
        msFilter.setQ(filter.getQ());
        MsUpdateCollectiveCodesBulkUnifiedDTO msRequest = CollectiveCodesConverter.toMsUpdateCollectiveCodesBulkUnifiedDTO(request);

        collectiveCodesRepository.updateCollectiveCodes(collectiveId, msFilter, msRequest);
    }

    public void deleteCollectiveCode(Long collectiveId, String code) {
        collectivesService.getAndCheckCollective(collectiveId);

        collectiveCodesRepository.deleteCollectiveCode(collectiveId, code);
    }

    public void deleteCollectiveCodes(Long collectiveId,
                                      CollectiveCodesSearchRequest filter,
                                      DeleteCollectiveCodesBulkRequest request) {
        collectivesService.getAndCheckCollective(collectiveId);

        MsCollectiveCodesSearchRequest msFilter = new MsCollectiveCodesSearchRequest();
        msFilter.setQ(filter.getQ());
        MsDeleteCollectiveCodesBulkDTO msRequest = CollectiveCodesConverter.toMsDeleteCollectiveCodesBulkDTO(request);

        collectiveCodesRepository.deleteCollectiveCodes(collectiveId, msFilter, msRequest);
    }
}
