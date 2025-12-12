package es.onebox.internal.xmlsepa.service.sepa;

import com.oneboxtds.datasource.s3.ObjectPolicy;
import com.oneboxtds.datasource.s3.S3BinaryRepository;
import es.onebox.common.datasources.catalog.repository.CatalogRepository;
import es.onebox.common.datasources.ms.client.dto.Customer;
import es.onebox.common.datasources.ms.client.repository.CustomerRepository;
import es.onebox.common.datasources.ms.entity.dto.EntityBankAccount;
import es.onebox.common.datasources.ms.entity.dto.Language;
import es.onebox.common.datasources.ms.entity.dto.User;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.datasources.ms.entity.repository.MasterDataRepository;
import es.onebox.common.datasources.ms.entity.repository.UsersRepository;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketDTO;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketPrice;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketRenewalConfigDTO;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketRenewalDTO;
import es.onebox.common.datasources.ms.event.dto.SeasonTicketRenewalsDTO;
import es.onebox.common.datasources.ms.event.dto.SeatRenewalStatus;
import es.onebox.common.datasources.ms.event.dto.UpdateRenewalRequestItem;
import es.onebox.common.datasources.ms.event.dto.XMLSEPAConfigData;
import es.onebox.common.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.mail.template.manager.TemplateResolver;
import es.onebox.core.mail.template.manager.model.TemplateContentDTO;
import es.onebox.core.mail.template.manager.model.TemplateScope;
import es.onebox.core.utils.common.DateUtils;
import es.onebox.internal.xmlsepa.converter.SEPAConverter;
import es.onebox.internal.xmlsepa.dao.SEPAMailTemplateCouchDao;
import es.onebox.internal.xmlsepa.sepa.SEPA;
import es.onebox.internal.xmlsepa.sepa.SEPADirectDebit;
import es.onebox.internal.xmlsepa.sepa.SEPATransaction;
import es.onebox.internal.xmlsepa.service.mailing.SEPAMailingService;
import es.onebox.internal.xmlsepa.validator.exception.SEPAValidatorIBANFormatException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SEPADirectDebitService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SEPADirectDebitService.class);

    public static final String BASE_FILENAME = "sepa";
    public static final String SEASON_TICKETS_PATH = "seasonTickets/";
    public static final String SEPA_PATH = "sepa/";
    public static final String ENTITIES_PATH = "entities/";
    public static final String FOLDER_SEPARATOR = "/";
    public static final String EXTENSION_XML = ".xml";
    private static final String REPORT = "report";
    private static final String EXPORT = "exportXmlSEPA";
    private static final String SEPA_SUBSTATUS = "REMITTANCE";

    private static final Long LIMIT = 20L;
    private static final Long OFFSET = 0L;

    private final S3BinaryRepository s3AutomaticSalesRepository;
    private final SeasonTicketRepository seasonTicketRepository;
    private final SEPAMailingService SEPAMailingService;
    private final SEPAMailTemplateCouchDao SEPAMailTemplateCouchDao;
    private final TemplateResolver templateResolver;
    private final UsersRepository usersRepository;
    private final MasterDataRepository masterDataRepository;
    private final EntitiesRepository entitiesRepository;
    private final CustomerRepository customerRepository;

    public SEPADirectDebitService(S3BinaryRepository s3AutomaticSalesRepository, SeasonTicketRepository seasonTicketRepository,
                                  SEPAMailingService SEPAMailingService, SEPAMailTemplateCouchDao SEPAMailTemplateCouchDao,
                                  TemplateResolver templateResolver, UsersRepository usersRepository,
                                  MasterDataRepository masterDataRepository, EntitiesRepository entitiesRepository,
                                  CustomerRepository customerRepository) {
        this.s3AutomaticSalesRepository = s3AutomaticSalesRepository;
        this.seasonTicketRepository = seasonTicketRepository;
        this.SEPAMailingService = SEPAMailingService;
        this.SEPAMailTemplateCouchDao = SEPAMailTemplateCouchDao;
        this.templateResolver = templateResolver;
        this.usersRepository = usersRepository;
        this.masterDataRepository = masterDataRepository;
        this.entitiesRepository = entitiesRepository;
        this.customerRepository = customerRepository;
    }

    public void processSEPADirectDebit(Long seasonTicketId, Long userId) {
        SeasonTicketRenewalConfigDTO renewalConfig = seasonTicketRepository.getSeasonTicketRenewalConfig(seasonTicketId);
        if (renewalConfig == null || renewalConfig.getBankAccountId() == null) {
            LOGGER.info("[AUTOMATIC RENEWALS] [XML SEPA] - No SEPA configuration found for season ticket ID: {}", seasonTicketId);
            return;
        }
        SeasonTicketDTO seasonTicket = seasonTicketRepository.getSeasonTicket(seasonTicketId);
        EntityBankAccount bankAccount = entitiesRepository.getEntityBankAccount(seasonTicket.getEntityId(), renewalConfig.getBankAccountId());
        if (!isValidBankAccount(bankAccount, seasonTicketId)) {
            return;
        }
        XMLSEPAConfigData config = SEPAConverter.toXMLSEPAConfigData(bankAccount);
        List<SEPATransaction> transactions = getSepaTransactions(seasonTicketId, config.getName());
        if (CollectionUtils.isEmpty(transactions)) {
            LOGGER.info("[AUTOMATIC RENEWALS] [XML SEPA] - No transactions found for season ticket ID: {}", seasonTicketId);
            return;
        }
        String path = getFilePath(seasonTicket);
        uploadFileS3(path, getSepa(config, transactions));
        ObjectPolicy reportPolicy = createReportPolicy();
        sendMail(s3AutomaticSalesRepository.getPublicSignedUrl(path, reportPolicy), userId);
    }

    private List<SEPATransaction> getSepaTransactions(Long seasonTicketId, String receiverName) {
        List<SEPATransaction> transactions = new ArrayList<>();
        Date date = new Date();
        long offset = OFFSET;
        int renewalCount = 1;
        List<SeasonTicketPrice> seasonTicketPrices = seasonTicketRepository.getSeasonTicketPrices(seasonTicketId);

        List<SeasonTicketRenewalDTO> data;
        do {
            data = fetchRenewals(seasonTicketId, offset);
            if (CollectionUtils.isNotEmpty(data)) {
                List<UpdateRenewalRequestItem> updateItems = new ArrayList<>();
                for (SeasonTicketRenewalDTO renewalItem : data) {
                    try {
                        if (SEPA_SUBSTATUS.equals(renewalItem.getRenewalSubstatus())) {
                            LOGGER.info("[AUTOMATIC RENEWALS] [XML SEPA] - Renewal with ID {} has already been processed, skipping.", renewalItem.getId());
                            continue;
                        }
                        Customer customer = customerRepository.getCustomer(renewalItem.getUserId());
                        if (customer != null && customer.getManaged() && customer.getManager() != null) {
                            Customer manager = customerRepository.getCustomer(customer.getManager());
                            if (manager != null) {
                                renewalItem.setIban(manager.getIban());
                                renewalItem.setBic(manager.getBic());
                            }
                        }
                        transactions.add(SEPAConverter.createTransaction(renewalItem, receiverName, renewalCount++, date,
                                BigDecimal.valueOf(getRenewalPrice(seasonTicketPrices, renewalItem.getActualSeat().getPrizeZoneId(), renewalItem.getActualRateId()))));
                        updateItems.add(SEPAConverter.toUpdate(renewalItem));
                    } catch (SEPAValidatorIBANFormatException e) {
                        LOGGER.info("[AUTOMATIC RENEWALS] [XML SEPA] - Renewal with ID {} has an invalid IBAN format: {}", renewalItem.getId(), e.getMessage());
                    } catch (OneboxRestException e) {
                        LOGGER.info("[AUTOMATIC RENEWALS] [XML SEPA] - Renewal with ID {} could not be processed due to missing price: {}", renewalItem.getId(), e.getMessage());
                    }
                }
                offset += LIMIT;
                if (CollectionUtils.isNotEmpty(updateItems)) {
                    seasonTicketRepository.updateSeasonTicketRenewals(seasonTicketId, SEPAConverter.toUpdate(updateItems));
                }
            }
        } while (data.size() == LIMIT);

        return transactions;
    }

    private List<SeasonTicketRenewalDTO> fetchRenewals(Long seasonTicketId, Long offset) {
        SeasonTicketRenewalsDTO renewals = seasonTicketRepository.getSeasonTicketRenewals(seasonTicketId, SEPAConverter.toFilter(LIMIT, offset, SeatRenewalStatus.NOT_RENEWED));
        return (renewals != null) ? renewals.getData() : Collections.emptyList();
    }

    private SEPA getSepa(XMLSEPAConfigData config, List<SEPATransaction> transactions) {
        return new SEPADirectDebit(
                SEPA.PaymentMethods.DirectDebit,
                SEPAConverter.createReceiverBankAccount(config),
                transactions,
                config.getCreditorId()
        );
    }

    private String getFilePath(SeasonTicketDTO seasonTicket) {
        validateSeasonTicket(seasonTicket);
        String filename = String.format("%s_%s", BASE_FILENAME, System.currentTimeMillis());
        return ENTITIES_PATH + seasonTicket.getEntityId() + FOLDER_SEPARATOR
                + SEASON_TICKETS_PATH + seasonTicket.getId() + FOLDER_SEPARATOR
                + SEPA_PATH + filename + EXTENSION_XML;
    }

    private void uploadFileS3(String path, SEPA sepa) {
        String strSEPAData = sepa.toString();
        InputStream targetStream = new ByteArrayInputStream(strSEPAData.getBytes());
        ObjectPolicy objectPolicy = ObjectPolicy.builder().contentType("text/xml").build();

        s3AutomaticSalesRepository.upload(path, targetStream, false, objectPolicy);
        LOGGER.info("[AUTOMATIC RENEWALS] [XML SEPA] - SEPA uploaded to S3");
    }

    private void validateSeasonTicket(SeasonTicketDTO seasonTicket) {
        if (BooleanUtils.isNotTrue(seasonTicket.getAllowRenewal())) {
            throw new OneboxRestException(ApiExternalErrorCode.RENEWAL_NOT_ENABLED);
        }
        if (BooleanUtils.isNotTrue(seasonTicket.getRenewal().getAutoRenewal())) {
            throw new OneboxRestException(ApiExternalErrorCode.AUTOMATIC_RENEWAL_NOT_ENABLED);
        }
    }

    private void sendMail(String s3Url, Long userId) {
        User user = usersRepository.getByIdCached(userId);
        Language language = masterDataRepository.getLanguage(user.getLanguageId().longValue());
        TemplateContentDTO template = SEPAMailTemplateCouchDao.getTemplate(EXPORT, language.getCode());
        String body = this.templateResolver
                .context()
                .of(TemplateScope.CPANEL, REPORT)
                .withDocument(template)
                .withParams(Map.of("download_link", s3Url))
                .build();
        SEPAMailingService.sendXMLSEPA(user.getEmail(), body, template.getSubject());
    }

    private static ObjectPolicy createReportPolicy() {
        final Date expirationTime = Date.from(DateUtils.now().plusDays(1L).toInstant());
        return ObjectPolicy.builder().expiration(expirationTime).contentEncoding(ObjectPolicy.ContentEncoding.GZIP)
                .build();
    }

    private boolean isValidBankAccount(EntityBankAccount bankAccount, Long seasonTicketId) {
        if (bankAccount == null || bankAccount.getIban() == null || bankAccount.getBic() == null || bankAccount.getName() == null || bankAccount.getCc() == null) {
            LOGGER.info("[AUTOMATIC RENEWALS] [XML SEPA] - Incomplete bank account SEPA configuration for season ticket ID: {} (iban={}, bic={}, name={}, cc={})",
                    seasonTicketId,
                    bankAccount != null ? bankAccount.getIban() : null,
                    bankAccount != null ? bankAccount.getBic() : null,
                    bankAccount != null ? bankAccount.getName() : null,
                    bankAccount != null ? bankAccount.getCc() : null);
            return false;
        }
        return true;
    }

    private Double getRenewalPrice(List<SeasonTicketPrice> seasonTicketPrices, Long priceZoneId, Long rateId) {
        return seasonTicketPrices.stream()
                .filter(price -> price.getPriceTypeId().equals(priceZoneId))
                .filter(price -> price.getRateId().equals(rateId.intValue()))
                .map(SeasonTicketPrice::getPrice)
                .findAny()
                .orElseThrow(() -> new OneboxRestException(ApiExternalErrorCode.RENEWAL_WITHOUT_PRICE));
    }
}
