package es.onebox.mgmt.oneboxinvoicing;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.entity.dto.GenerateOneboxInvoiceRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.OneboxInvoiceEntitiesFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.entity.repository.OneboxInvoicingRepository;
import es.onebox.mgmt.exception.ApiMgmtEntitiesErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.oneboxinvoicing.converters.GenerateInvoiceConverter;
import es.onebox.mgmt.oneboxinvoicing.converters.OneboxInvoiceEntitiesConverter;
import es.onebox.mgmt.oneboxinvoicing.dto.GenerateOneboxInvoiceRequestDTO;
import es.onebox.mgmt.oneboxinvoicing.dto.OneboxInvoiceEntitiesFilterDTO;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;


@Service
public class OneboxInvoicingService {

    private final OneboxInvoicingRepository oneboxInvoicingRepository;
    private final EntitiesRepository entitiesRepository;

    @Autowired
    public OneboxInvoicingService(OneboxInvoicingRepository oneboxInvoicingRepository, EntitiesRepository entitiesRepository) {
        this.oneboxInvoicingRepository = oneboxInvoicingRepository;
        this.entitiesRepository = entitiesRepository;
    }

    public void generateInvoice(GenerateOneboxInvoiceRequestDTO request) {
        checkRequestInfo(request);
        GenerateOneboxInvoiceRequest invoiceRequest = GenerateInvoiceConverter.toMs(request);

        //Modify request date to operator timezone in order to ensure backward compatibility with legacy consumer
        Operator operator = entitiesRepository.getCachedOperator(SecurityUtils.getUserOperatorId());
        invoiceRequest.setFrom(invoiceRequest.getFrom().withZoneSameInstant(ZoneId.of(operator.getTimezone().getValue())));
        invoiceRequest.setTo(invoiceRequest.getTo().withZoneSameInstant(ZoneId.of(operator.getTimezone().getValue())));

        oneboxInvoicingRepository.generateInvoice(invoiceRequest);
    }

    public OneboxInvoiceEntitiesFilterDTO getEntitiesFilter() {
        OneboxInvoiceEntitiesFilter filter = oneboxInvoicingRepository.getEntitiesFilter();
        return OneboxInvoiceEntitiesConverter.toDTO(filter);
    }

    private void checkRequestInfo(GenerateOneboxInvoiceRequestDTO request) {
        if (request.getFrom().isAfter(ZonedDateTime.now()) || request.getTo().isAfter(ZonedDateTime.now())) {
            throw new OneboxRestException(ApiMgmtEntitiesErrorCode.DATE_CANNOT_BE_AFTER_NOW);
        }

        if (ObjectUtils.isNotEmpty(request.getEventIds())) {
            // Filter by event id, only when operatorId and entityId have a single value
            if (ObjectUtils.isEmpty(request.getOperatorId()) || ObjectUtils.isEmpty(request.getEntitiesId()) || request.getEntitiesId().size() > 1) {
                throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
            }
        }
    }
}
