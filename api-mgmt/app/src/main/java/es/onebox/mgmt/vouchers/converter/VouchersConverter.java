package es.onebox.mgmt.vouchers.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.common.AggregationMetric;
import es.onebox.mgmt.common.AggregationType;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.LimitlessValueDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.CreateVoucher;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.SendEmailVoucher;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.SendEmailVoucherType;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.UpdateVouchersBulk;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.Voucher;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherTransaction;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.Vouchers;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.vouchers.dto.CreateVoucherBulkRequestDTO;
import es.onebox.mgmt.vouchers.dto.CreateVoucherRequestDTO;
import es.onebox.mgmt.vouchers.dto.SendEmailVoucherDTO;
import es.onebox.mgmt.vouchers.dto.UpdateVoucherRequestDTO;
import es.onebox.mgmt.vouchers.dto.UpdateVouchersBulkDTO;
import es.onebox.mgmt.vouchers.dto.VoucherAggregationDataDTO;
import es.onebox.mgmt.vouchers.dto.VoucherAggregationDataTypeDTO;
import es.onebox.mgmt.vouchers.dto.VoucherAggregationTypeDTO;
import es.onebox.mgmt.vouchers.dto.VoucherDTO;
import es.onebox.mgmt.vouchers.dto.VoucherExpirationDTO;
import es.onebox.mgmt.vouchers.dto.VoucherSearchFilter;
import es.onebox.mgmt.vouchers.dto.VoucherStatus;
import es.onebox.mgmt.vouchers.dto.VoucherTransactionDTO;
import es.onebox.mgmt.vouchers.dto.VoucherUsageDTO;
import es.onebox.mgmt.vouchers.dto.VouchersDTO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class VouchersConverter {

    private enum Metrics {
        CODES("codes", AggregationType.COUNT),
        BALANCE("balance", AggregationType.SUM);

        private String name;
        private AggregationType type;

        Metrics(String name, AggregationType type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public AggregationType getType() {
            return type;
        }

    }

    private VouchersConverter() {
    }

    public static VouchersDTO fromMsChannel(Vouchers vouchers, String operatorTZ) {
        VouchersDTO response = new VouchersDTO();
        response.setData(vouchers.getData().stream().
                map(v -> VouchersConverter.fromMsChannel(v, operatorTZ)).collect(Collectors.toList()));
        response.setMetadata(vouchers.getMetadata());

        if (vouchers.getSummaryData() != null) {
            VoucherAggregationDataDTO agg = new VoucherAggregationDataDTO();
            fillAggOverall(vouchers, agg);
            fillAggByType(vouchers, agg);
            response.setAggregations(agg);
        }

        return response;
    }

    private static void fillAggOverall(Vouchers vouchers, VoucherAggregationDataDTO agg) {
        AggregationMetric codes = new AggregationMetric();
        codes.setName(Metrics.CODES.getName());
        codes.setType(Metrics.CODES.getType());
        codes.setValue(vouchers.getSummaryData().getTotalCodes().doubleValue());

        AggregationMetric balance = new AggregationMetric();
        balance.setName(Metrics.BALANCE.getName());
        balance.setType(Metrics.BALANCE.getType());
        balance.setValue(vouchers.getSummaryData().getTotalBalance());

        agg.setOverall(List.of(codes, balance));
    }

    private static void fillAggByType(Vouchers vouchers, VoucherAggregationDataDTO agg) {
        agg.setType(new ArrayList<>());
        agg.getType().add(fillAggType(VoucherAggregationTypeDTO.ACTIVE,
                vouchers.getSummaryData().getActiveCodes(), vouchers.getSummaryData().getActiveBalance()));
        agg.getType().add(fillAggType(VoucherAggregationTypeDTO.ACTIVE_WITH_REDEEMS,
                vouchers.getSummaryData().getActiveCodesWithRedeems(), vouchers.getSummaryData().getActiveRedeemedBalance()));
        agg.getType().add(fillAggType(VoucherAggregationTypeDTO.ACTIVE_WITHOUT_REDEEMS,
                vouchers.getSummaryData().getActiveCodesWithoutRedeems(), vouchers.getSummaryData().getActivePendingBalance()));
        agg.getType().add(fillAggType(VoucherAggregationTypeDTO.INACTIVE,
                vouchers.getSummaryData().getInactiveCodes(), vouchers.getSummaryData().getInactiveBalance()));
        agg.getType().add(fillAggType(VoucherAggregationTypeDTO.INACTIVE_WITH_REDEEMS,
                vouchers.getSummaryData().getInactiveCodesWithRedeems(), vouchers.getSummaryData().getInactiveRedeemedBalance()));
        agg.getType().add(fillAggType(VoucherAggregationTypeDTO.INACTIVE_WITHOUT_REDEEMS,
                vouchers.getSummaryData().getInactiveCodesWithoutRedeems(), vouchers.getSummaryData().getInactivePendingBalance()));
    }

    private static VoucherAggregationDataTypeDTO fillAggType(VoucherAggregationTypeDTO aggValue, Long codes, Double balance) {
        VoucherAggregationDataTypeDTO aggActiveType = new VoucherAggregationDataTypeDTO();
        aggActiveType.setAggValue(aggValue);
        aggActiveType.setAggMetric(new ArrayList<>());

        AggregationMetric metric = new AggregationMetric();
        metric.setName(Metrics.CODES.getName());
        metric.setType(Metrics.CODES.getType());
        metric.setValue(codes.doubleValue());
        aggActiveType.getAggMetric().add(metric);

        metric = new AggregationMetric();
        metric.setName(Metrics.BALANCE.getName());
        metric.setType(Metrics.BALANCE.getType());
        metric.setValue(balance);
        aggActiveType.getAggMetric().add(metric);

        return aggActiveType;
    }

    public static VoucherDTO fromMsChannel(Voucher voucher, String operatorTZ) {
        VoucherDTO response = new VoucherDTO();
        response.setCode(voucher.getCode());
        response.setVoucherGroup(new IdNameDTO(voucher.getVoucherGroupId()));
        response.setStatus(fromMsChannel(voucher.getStatus()));
        response.setPin(voucher.getPin());
        response.setEmail(voucher.getEmail());
        response.setBalance(voucher.getBalance());
        response.setUsage(new VoucherUsageDTO(voucher.getUsageUsed(), new LimitlessValueDTO(voucher.getUsageLimit())));
        response.setExpiration(fromMsChannel(voucher));
        response.setOperatorTZ(operatorTZ);
        response.setConsolidatedBalance(voucher.getConsolidatedBalance());
        if (!CommonUtils.isEmpty(voucher.getTransactions())) {
            response.setTransactions(voucher.getTransactions().stream().
                    sorted(Comparator.comparing(VoucherTransaction::getDate)).
                    map(tx -> {
                        VoucherTransactionDTO responseTx = new VoucherTransactionDTO();
                        responseTx.setDate(tx.getDate());
                        responseTx.setAmount(tx.getAmount());
                        responseTx.setBalance(tx.getBalance());
                        responseTx.setType(tx.getType());
                        responseTx.setCode(tx.getCode());
                        return responseTx;
                    }).collect(Collectors.toList()));
        }
        return response;
    }

    public static VoucherExpirationDTO fromMsChannel(Voucher voucher) {
        VoucherExpirationDTO expirationDTO = new VoucherExpirationDTO();
        expirationDTO.setDate(voucher.getExpiration());
        expirationDTO.setEnable(voucher.getEnableExpiration());
        return expirationDTO;
    }

    public static Voucher toMsChannel(UpdateVoucherRequestDTO request) {
        Voucher updateRequest = new Voucher();
        updateRequest.setStatus(toMsChannel(request.getStatus()));
        updateRequest.setPin(request.getPin());
        updateRequest.setEmail(request.getEmail());
        if (request.getUsage() != null) {
            updateRequest.setUsageLimit(ConverterUtils.getIntLimitlessValue(request.getUsage().getLimit()));
        }
        if (request.getExpiration() != null) {
            updateRequest.setEnableExpiration(request.getExpiration().getEnable());
            updateRequest.setExpiration(request.getExpiration().getDate());
        }
        return updateRequest;
    }

    public static UpdateVouchersBulk toMsChannel(UpdateVouchersBulkDTO request) {
        UpdateVouchersBulk updateRequest = new UpdateVouchersBulk();
        updateRequest.setCodes(request.getCodes());
        updateRequest.setStatus(request.getStatus());
        if (request.getUsage() != null) {
            updateRequest.setUsageLimit(ConverterUtils.getIntLimitlessValue(request.getUsage().getLimit()));
        }
        updateRequest.setExpiration(request.getExpiration());
        return updateRequest;
    }

    public static VoucherFilter convertFilter(VoucherSearchFilter request) {
        VoucherFilter filter = new VoucherFilter();
        filter.setStatus(request.getStatus());
        filter.setPin(request.getPin());
        filter.setEmail(request.getEmail());
        filter.setFreeSearch(request.getFreeSearch());
        filter.setLimit(request.getLimit());
        filter.setOffset(request.getOffset());
        filter.setAggs(request.getAggs());
        return filter;
    }

    public static List<CreateVoucher> convertCreate(CreateVoucherBulkRequestDTO request) {
        return request.stream().map(VouchersConverter::convertCreate).collect(Collectors.toList());
    }

    public static CreateVoucher convertCreate(CreateVoucherRequestDTO request) {
        CreateVoucher createRequest = new CreateVoucher();
        createRequest.setPin(request.getPin());
        createRequest.setEmail(request.getEmail());
        createRequest.setBalance(request.getBalance());
        createRequest.setUsageLimit(ConverterUtils.getIntLimitlessValue(request.getUsageLimit()));
        createRequest.setExpiration(request.getExpiration());
        createRequest.setUserId(SecurityUtils.getUserId());
        return createRequest;
    }

    public static SendEmailVoucher toMs(SendEmailVoucherDTO in) {
        SendEmailVoucherType type = SendEmailVoucherType.valueOf(in.type().name());
        return new SendEmailVoucher(type, in.email(), in.subject(), in.body(), ConverterUtils.toLocale(in.language()));
    }

    private static VoucherStatus fromMsChannel(es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherStatus in) {
        if (in == null) {
            return null;
        }
        return VoucherStatus.valueOf(in.name());
    }

    private static es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherStatus toMsChannel(VoucherStatus in) {
        if (in == null) {
            return null;
        }
        return es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherStatus.valueOf(in.name());
    }

}
