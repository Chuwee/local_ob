package es.onebox.fcb.tickets;

import es.onebox.common.datasources.ms.order.enums.OrderType;
import es.onebox.fcb.dao.OrderCodeCouchDao;
import es.onebox.fcb.dao.OrderCodeCounterCouchDao;
import es.onebox.fcb.domain.OrderCode;
import es.onebox.fcb.tickets.dto.OperationIdRequest;
import es.onebox.fcb.utils.FcbDateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Service
public class OperationCodeService {

    @Value("${onebox.environment}")
    private String environment;

    private OrderCodeCouchDao orderCodeCouchDao;
    private OrderCodeCounterCouchDao orderCodeCounterCouchDao;

    private static final String GATEWAY = "0";
    private static final String SELL = "V";
    private static final String REFUND = "W";
    private static final String PRE_ENVIRONMENT = "pre";
    private static final Long INITIAL_VALUE_PRE_ENVIRONMENT = 2000000000L;

    public OperationCodeService(OrderCodeCouchDao orderCodeCouchDao, OrderCodeCounterCouchDao orderCodeCounterCouchDao) {
        this.orderCodeCouchDao = orderCodeCouchDao;
        this.orderCodeCounterCouchDao = orderCodeCounterCouchDao;
    }

    public String getOperationId(String code) {
        OrderCode orderCode = orderCodeCouchDao.get(code);
        return orderCode == null ? null : orderCode.getExternalCode();
    }

    public String getOrGenerateOperationId(OperationIdRequest data) {
        String operationId = getOperationId(data.getCode());
        if (operationId == null) {
            OperationIdRequest request = new OperationIdRequest();
            request.setCode(data.getCode());
            request.setOrderType(data.getOrderType());
            request.setPurchaseDate(data.getPurchaseDate());
            request.setTimeZone(data.getTimeZone());
            operationId = registerOperationId(request);
        }
        return operationId;
    }

    public String registerOperationId(OperationIdRequest request) {
        TimeZone tz = TimeZone.getTimeZone(request.getTimeZone());
        if (request.getPurchaseDate() == null) {
            request.setPurchaseDate(ZonedDateTime.now());
        }
        Date date = FcbDateUtils.convertDateTimeZone(Date.from(request.getPurchaseDate().toInstant()), tz);
        Calendar purchaseDate = Calendar.getInstance();
        purchaseDate.setTime(date);
        purchaseDate.add(Calendar.MONTH, 6);

        Long counter = getAutoIncrementCounter(purchaseDate);

        String counterHex = StringUtils.leftPad(Long.toHexString(counter), 8, '0');

        String operationId = StringUtils.leftPad(String.valueOf(purchaseDate.get(Calendar.YEAR) % 100), 2, '0') +
                GATEWAY + counterHex.toUpperCase() +
                (OrderType.REFUND.name().equals(request.getOrderType()) ? REFUND : SELL);

        OrderCode orderCode = new OrderCode();
        orderCode.setCode(request.getCode());
        orderCode.setExternalCode(operationId);
        orderCodeCouchDao.upsert(null, orderCode);

        return operationId;
    }

    private Long getAutoIncrementCounter(Calendar purchaseDate) {
        Long counter = orderCodeCounterCouchDao.autoIncrementCounter(String.valueOf(purchaseDate.get(Calendar.YEAR)));
        if (PRE_ENVIRONMENT.equalsIgnoreCase(environment)) {
            counter += INITIAL_VALUE_PRE_ENVIRONMENT;
        }
        return counter;
    }

    public void storeOperationId(String code, String operationId) {
        OrderCode orderCode = new OrderCode();
        orderCode.setCode(code);
        orderCode.setExternalCode(operationId);
        orderCodeCouchDao.upsert(null, orderCode);
    }

    public String getRefundOperationId(String originalCode) {
        String operationId = getOperationId(originalCode);
        if (operationId.endsWith(SELL)) {
            operationId = operationId.substring(0, operationId.length() - 1) + REFUND;
        }
        return operationId;
    }
}
