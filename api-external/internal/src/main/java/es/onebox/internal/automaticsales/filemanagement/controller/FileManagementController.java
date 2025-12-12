package es.onebox.internal.automaticsales.filemanagement.controller;

import es.onebox.internal.automaticsales.export.dto.ExportResponse;
import es.onebox.internal.automaticsales.filemanagement.dto.AutomaticSalesExportRequest;
import es.onebox.internal.automaticsales.filemanagement.dto.FileInfoDTO;
import es.onebox.common.security.Role;
import es.onebox.internal.automaticsales.processsales.service.ProcessSalesService;
import es.onebox.internal.config.InternalApiConfig;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(InternalApiConfig.AutomaticSales.BASE_URL + "/sessions/{sessionId}/file")
public class FileManagementController {

    @Autowired
    private ProcessSalesService processSalesService;

    @Secured(Role.OPERATOR_MANAGER)
    @GetMapping
    public List<FileInfoDTO> getAutomaticSalesFileList(@PathVariable(value = "sessionId") Long sessionId) {
        return processSalesService.getAutomaticSalesFileList(sessionId);
    }

    @Secured(Role.OPERATOR_MANAGER)
    @PostMapping("/exports")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ExportResponse exportFile(@PathVariable(value = "sessionId") Long sessionId,
                                     @Valid @RequestBody AutomaticSalesExportRequest request) {
        return processSalesService.exportFile(sessionId, request);
    }

}
