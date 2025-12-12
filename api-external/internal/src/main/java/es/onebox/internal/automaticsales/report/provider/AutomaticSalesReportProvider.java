package es.onebox.internal.automaticsales.report.provider;

import es.onebox.internal.automaticsales.filemanagement.service.FileManagementService;
import es.onebox.internal.automaticsales.report.converter.AutomaticSalesReportConverter;
import es.onebox.internal.automaticsales.report.dto.AutomaticSaleDTO;
import es.onebox.internal.automaticsales.report.dto.AutomaticSalesDTO;
import es.onebox.internal.automaticsales.report.dto.AutomaticSalesSearchFilter;
import es.onebox.internal.automaticsales.report.model.AutomaticSaleReport;
import es.onebox.internal.automaticsales.report.model.AutomaticSalesReportFilter;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.file.exporter.generator.provider.ExportProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AutomaticSalesReportProvider extends ExportProvider<AutomaticSaleReport, AutomaticSalesReportFilter> {

    public static final Long PAGE_SIZE = 2000L;

    private final int maxReportSize;

    private final FileManagementService fileManagementService;

    @Autowired
    public AutomaticSalesReportProvider(@Value("${exports.automatic-sales.max-size:150000}") int maxReportSize,
                                        FileManagementService fileManagementService) {
        this.maxReportSize = maxReportSize;
        this.fileManagementService = fileManagementService;
    }

    @Override
    public List<AutomaticSaleReport> fetchAll(final AutomaticSalesReportFilter filter) {
        AutomaticSalesSearchFilter searchFilter = new AutomaticSalesSearchFilter();
        searchFilter.setQ(filter.getQ());
        searchFilter.setLimit(PAGE_SIZE);

        List<AutomaticSaleDTO> automaticSaleDTOS = new ArrayList<>();
        AutomaticSalesDTO sales;
        do {
            sales = fileManagementService.getAutomaticSalesFile(filter);
            automaticSaleDTOS.addAll(sales.getData().stream().map(AutomaticSalesReportConverter::toMs).collect(Collectors.toList()));
            searchFilter.setOffset(searchFilter.getOffset() + searchFilter.getLimit());
        } while (recordsToFetch(Long.valueOf(sales.getMetadata().getTotal()), automaticSaleDTOS));
        log.info("exportId: " + filter.getExportId() + " - Fetched {} records to process", automaticSaleDTOS.size());

        return automaticSaleDTOS.stream()
                .map(AutomaticSalesReportConverter::toReport)
                .collect(Collectors.toList());
    }

    @Override
    public void validate(final AutomaticSalesReportFilter filter) {
        AutomaticSalesSearchFilter searchFilter = new AutomaticSalesSearchFilter();
        searchFilter.setQ(filter.getQ());
        searchFilter.setLimit(0L);

        Long totalHits = Long.valueOf(fileManagementService.getAutomaticSalesFile(filter).getMetadata().getTotal());
        if (totalHits == null || totalHits > maxReportSize) {
            throw ExceptionBuilder.build(ApiExternalErrorCode.EXPORT_WITH_TOO_MANY_RECORDS, maxReportSize);
        }
    }
}
