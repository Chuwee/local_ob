package es.onebox.common.datasources.ms.accesscontrol.repository;

import es.onebox.common.datasources.ms.accesscontrol.MsAccessControlDatasource;
import es.onebox.common.datasources.ms.accesscontrol.dto.ImportExternalBarcode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ExternalBarcodeRepository {

    @Autowired
    private MsAccessControlDatasource msAccessControlDatasource;


    public void importExternalBarcodes(ImportExternalBarcode importExternalBarcode) {
        msAccessControlDatasource.importExternalBarcodes(importExternalBarcode);
    }

}
