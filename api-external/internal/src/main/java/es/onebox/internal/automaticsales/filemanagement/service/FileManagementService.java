package es.onebox.internal.automaticsales.filemanagement.service;

import com.oneboxtds.datasource.s3.S3BinaryRepository;
import com.oneboxtds.datasource.s3.enums.ListKeysSortBy;
import es.onebox.internal.automaticsales.processsales.dto.SaleDTO;
import es.onebox.internal.automaticsales.report.dto.AutomaticSaleDTO;
import es.onebox.internal.automaticsales.report.dto.AutomaticSalesDTO;
import es.onebox.internal.automaticsales.report.model.AutomaticSalesReportFilter;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.internal.automaticsales.report.provider.AutomaticSalesReportProvider;
import es.onebox.internal.utils.CsvParseUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import static es.onebox.internal.automaticsales.report.provider.AutomaticSalesReportProvider.PAGE_SIZE;

@Service
public class FileManagementService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileManagementService.class);
    private final S3BinaryRepository s3AutomaticSalesRepository;

    @Autowired
    public FileManagementService(@Qualifier("s3AutomaticSalesRepository") S3BinaryRepository s3AutomaticSalesRepository) {
        this.s3AutomaticSalesRepository = s3AutomaticSalesRepository;
    }

    public AutomaticSalesDTO getAutomaticSalesFile(AutomaticSalesReportFilter filter) {

        String filename = filter.getQ();
        List<String> fileList = listKeysByPrefix(filename);

        if(CollectionUtils.isEmpty(fileList)) {
            LOGGER.error("[PROCESS SALES] - File not found: " + filename);
            throw new OneboxRestException(ApiExternalErrorCode.AUTOMATIC_SALES_FILE_NOT_FOUND);
        }

        String lastFile = fileList.get(0);
        List<SaleDTO> downloadedSalesList;

        try(InputStream inputStream = new ByteArrayInputStream(s3AutomaticSalesRepository.download(lastFile))) {
            downloadedSalesList = CsvParseUtils.fromCSV(inputStream, SaleDTO.class);
        } catch (Exception e) {
            LOGGER.error("[PROCESS SALES] - Error processing sales csv File");
            throw new OneboxRestException(ApiExternalErrorCode.ERROR_PROCESSING_SALES);
        }

        List <AutomaticSaleDTO> salesList = downloadedSalesList.stream().map(this::prepareAutomaticSaleDTO).collect(Collectors.toList());
        Metadata metadata = setMetadata(filter, (long) salesList.size());

        AutomaticSalesDTO automaticSalesDTO = new AutomaticSalesDTO();
        automaticSalesDTO.setData(salesList);
        automaticSalesDTO.setMetadata(metadata);
        return automaticSalesDTO;
    }

    public List<String> listKeysByPrefix(String prefix) {
        try{
            return s3AutomaticSalesRepository.listKeysByPrefix(prefix, ListKeysSortBy.DATE_ADDED_DESC);
        } catch (Exception e){
            LOGGER.error("[FileManagementService] Error listing automatic sales files by prefix: {}, cause: {}", prefix, e.getCause(), e);
            throw e;
        }
    }

    private AutomaticSaleDTO prepareAutomaticSaleDTO(SaleDTO sale) {
        AutomaticSaleDTO automaticSaleDTO = new AutomaticSaleDTO();
        automaticSaleDTO.setDni(sale.getDni());
        automaticSaleDTO.setOwner(sale.isOwner());
        automaticSaleDTO.setEmail(sale.getEmail());
        automaticSaleDTO.setLanguage(sale.getLanguage());
        automaticSaleDTO.setFirstSurname(sale.getFirstSurname());
        automaticSaleDTO.setGroup(sale.getGroup());
        automaticSaleDTO.setNum(sale.getNum());
        automaticSaleDTO.setName(sale.getName());
        automaticSaleDTO.setPhone(sale.getPhone());
        automaticSaleDTO.setProcessed(sale.isProcessed());
        automaticSaleDTO.setOrderId(sale.getOrderId());
        automaticSaleDTO.setTraceId(sale.getTraceId());
        automaticSaleDTO.setOriginalLocator(sale.getOriginalLocator());
        automaticSaleDTO.setSecondSurname(sale.getSecondSurname());
        automaticSaleDTO.setSector(sale.getSector());
        automaticSaleDTO.setPriceZone(sale.getPriceZone());
        automaticSaleDTO.setErrorDescription(sale.getErrorDescription());
        automaticSaleDTO.setSeatId(sale.getSeatId());
        automaticSaleDTO.setErrorCode(sale.getErrorCode());
        automaticSaleDTO.setExtraField(sale.getExtraField());

        return automaticSaleDTO;
    }

    private Metadata setMetadata(AutomaticSalesReportFilter filter, Long count){
        Metadata metadata = new Metadata();
        metadata.setLimit(AutomaticSalesReportProvider.PAGE_SIZE);
        metadata.setOffset(0L);
        metadata.setTotal(count);

        return metadata;
    }
}

