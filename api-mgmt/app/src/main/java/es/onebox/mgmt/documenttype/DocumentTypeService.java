package es.onebox.mgmt.documenttype;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentTypeService {

    private final SecurityManager securityManager;
    private final DocumentTypeRepository documentTypeRepository;

    @Autowired
    public DocumentTypeService(SecurityManager securityManager,
                               DocumentTypeRepository documentTypeRepository) {
        this.securityManager = securityManager;
        this.documentTypeRepository = documentTypeRepository;
    }

    public List<String> getDocumentTypesByEntityId(Long entityId) {
        securityManager.checkEntityAccessible(entityId);
        Long operatorId = SecurityUtils.getUserOperatorId();
        List<IdNameDTO> idNameDTOList = documentTypeRepository.getDocumentTypesByOperatorId(operatorId);
        if (CollectionUtils.isNotEmpty(idNameDTOList)) {
           return idNameDTOList.stream().map(IdNameDTO::getName).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

}
