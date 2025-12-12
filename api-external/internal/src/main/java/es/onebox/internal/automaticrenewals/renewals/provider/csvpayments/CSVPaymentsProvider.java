package es.onebox.internal.automaticrenewals.renewals.provider.csvpayments;

import com.oneboxtds.datasource.s3.ObjectPolicy;
import com.oneboxtds.datasource.s3.S3BinaryRepository;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketRenewalConfigDTO;
import es.onebox.common.datasources.ms.event.dto.SeatRenewalStatus;
import es.onebox.common.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.internal.automaticrenewals.renewals.provider.AutomaticRenewalsProvider;
import es.onebox.internal.automaticrenewals.renewals.provider.RenewalItem;
import es.onebox.internal.automaticrenewals.renewals.provider.RenewalSession;
import es.onebox.common.datasources.distribution.dto.order.PaymentRequest;
import es.onebox.common.datasources.distribution.dto.order.PaymentType;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketDTO;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketRenewalsDTO;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketRenewalsFilter;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.internal.utils.CsvParseUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
public class CSVPaymentsProvider implements AutomaticRenewalsProvider<InputCSVPaymentData, String> {

    public static final String BASE_FILENAME = "payments";
    public static final String AUTOMATIC_RENEWALS = "automaticRenewals/";
    public static final String SEASON_TICKETS_PATH = "seasonTickets/";
    public static final String ENTITIES_PATH = "entities/";
    public static final String FOLDER_SEPARATOR = "/";
    public static final String EXTENSION_CSV = ".csv";

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVPaymentsProvider.class);

    private final S3BinaryRepository s3AutomaticSalesRepository;
    private final SeasonTicketRepository seasonTicketRepository;

    public CSVPaymentsProvider(S3BinaryRepository s3AutomaticSalesRepository, SeasonTicketRepository seasonTicketRepository) {
        this.s3AutomaticSalesRepository = s3AutomaticSalesRepository;
        this.seasonTicketRepository = seasonTicketRepository;
    }

    @Override
    public String prepare(SeasonTicketDTO seasonTicket, InputCSVPaymentData data) {
        LOGGER.info("[AUTOMATIC RENEWALS] [CSV PAYMENTS] - Preparing data...");
        String path = getFilePath(seasonTicket);
        List<CSVPaymentDataRow> rows = data.stream()
                .filter(it -> StringUtils.isNotBlank(it.renewalId()))
                .map(it -> new CSVPaymentDataRow(it.renewalId(), it.reference())).toList();
        uploadFileS3(path, rows);
        return path;
    }

    @Override
    public RenewalSession<String> createSession(Long seasonTicketId, String data) {
        if (!s3AutomaticSalesRepository.existObject(data)) {
            LOGGER.warn("[AUTOMATIC RENEWALS] [CSV PAYMENTS] - CSV {} not found in S3", data);
        }
        try (InputStream inputStream = new ByteArrayInputStream(s3AutomaticSalesRepository.download(data))) {
            List<CSVPaymentDataRow> rows = CsvParseUtils.fromCSV(inputStream, CSVPaymentDataRow.class);
            SeasonTicketRenewalConfigDTO renewalConfig = seasonTicketRepository.getSeasonTicketRenewalConfig(seasonTicketId);
            return new CSVPaymentRenewalSession(seasonTicketId, rows, 15, BooleanUtils.isTrue(renewalConfig.getGroupByReference()));
        } catch (Exception e) {
            LOGGER.error("[AUTOMATIC RENEWALS] [CSV PAYMENTS] - Error processing csv {} file", data);
            throw new OneboxRestException(ApiExternalErrorCode.ERROR_PROCESSING_RENEWALS);
        }
    }

    @Override
    public PaymentRequest createPayment(Object additionalData, Double price) {
        return new PaymentRequest(PaymentType.EXTERNAL, price, String.valueOf(additionalData), null, null);
    }

    private String getFilePath(SeasonTicketDTO seasonTicket) {
        String filename = String.format("%s_%s", BASE_FILENAME, System.currentTimeMillis());
        return ENTITIES_PATH + seasonTicket.getEntityId() + FOLDER_SEPARATOR + SEASON_TICKETS_PATH + seasonTicket.getId() + FOLDER_SEPARATOR +
                AUTOMATIC_RENEWALS + filename + EXTENSION_CSV;
    }

    private void uploadFileS3(String path, List<CSVPaymentDataRow> sales) {
        String strCsvData = CsvParseUtils.toCsv(sales, CSVPaymentDataRow.class);
        InputStream targetStream = new ByteArrayInputStream(strCsvData.getBytes());
        ObjectPolicy objectPolicy = ObjectPolicy.builder().contentType("text/csv").build();
        s3AutomaticSalesRepository.upload(path, targetStream, false, objectPolicy);
        LOGGER.info("[AUTOMATIC RENEWALS] [CSV PAYMENTS] - CSV uploaded to S3");
    }

    private static class CSVPaymentRenewalSession implements RenewalSession<String> {

        private final Long seasonTicketId;
        private final Map<String, List<CSVPaymentDataRow>> groups;
        private final Boolean groupByReference;
        private final Integer batchSize;
        private Integer index;

        public CSVPaymentRenewalSession(Long seasonTicketId, List<CSVPaymentDataRow> rows, Integer batchSize, boolean groupByReference) {
            this.seasonTicketId = seasonTicketId;
            this.groupByReference = groupByReference;
            this.batchSize = batchSize;
            this.index = 0;
            if (groupByReference) {
                this.groups = rows.stream().filter(it -> StringUtils.isNotBlank(it.getReference()))
                        .collect(Collectors.groupingBy(CSVPaymentDataRow::getReference));
            } else {
                this.groups = Map.of(StringUtils.EMPTY, rows);
            }
        }

        @Override
        public List<RenewalItem<String>> nextBatch(BiFunction<Long, SeasonTicketRenewalsFilter, SeasonTicketRenewalsDTO> renewalsGetter) {
            if (!hasMore()) return List.of();

            if (!groupByReference) return nextBatchNoGroups(renewalsGetter);

            List<String> references = groups.keySet().stream().toList().subList(index, index + Math.min(batchSize, groups.size()- index));

            List<RenewalItem<String>> renewalItems = new ArrayList<>();
            for (String reference : references) {
                List<CSVPaymentDataRow> items = groups.get(reference);
                SeasonTicketRenewalsFilter filter = new SeasonTicketRenewalsFilter();
                filter.setLimit(batchSize.longValue());
                filter.setOffset(0L);
                filter.setAutoRenewal(Boolean.TRUE);
                filter.setRenewalIds(items.stream().map(CSVPaymentDataRow::getRenewalId).toList());
                filter.setRenewalStatus(SeatRenewalStatus.NOT_RENEWED);
                SeasonTicketRenewalsDTO renewals = renewalsGetter.apply(seasonTicketId, filter);
                renewalItems.add(new RenewalItem<>(renewals.getData(), reference));
            }
            index += batchSize;
            return renewalItems;
        }

        private List<RenewalItem<String>> nextBatchNoGroups(BiFunction<Long, SeasonTicketRenewalsFilter, SeasonTicketRenewalsDTO> renewalsGetter) {
            List<CSVPaymentDataRow> items = groups.get(StringUtils.EMPTY);
            Map<String, String> batchData = items.subList(index, index + Math.min(batchSize, items.size() - index)).stream()
                    .collect(Collectors.toMap(CSVPaymentDataRow::getRenewalId, CSVPaymentDataRow::getReference));

            SeasonTicketRenewalsFilter filter = new SeasonTicketRenewalsFilter();
            filter.setLimit(batchSize.longValue());
            filter.setOffset(0L);
            filter.setAutoRenewal(Boolean.TRUE);
            filter.setRenewalIds(batchData.keySet());
            SeasonTicketRenewalsDTO renewals = renewalsGetter.apply(seasonTicketId, filter);

            index += batchSize;
            return renewals.getData().stream().map(renewal -> new RenewalItem<>(List.of(renewal), batchData.get(renewal.getId()))).toList();
        }

        @Override
        public boolean hasMore() {
            if (groupByReference) {
                return index < groups.size();
            } else {
                return index < groups.get(StringUtils.EMPTY).size();
            }
        }

        @Override
        public Integer getProgress() {
            if (groupByReference) {
                return  Math.min(index, groups.size()) * 100 / groups.size();
            } else {
                return  Math.min(index, groups.get(StringUtils.EMPTY).size()) * 100 / groups.get(StringUtils.EMPTY).size();
            }
        }
    }
}