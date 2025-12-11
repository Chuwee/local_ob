package es.onebox.mgmt.export;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.file.exporter.status.model.ExportProcess;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.b2b.balance.converter.ClientsBalanceConverter;
import es.onebox.mgmt.b2b.balance.dto.ClientTransactionsExportRequestDTO;
import es.onebox.mgmt.b2b.balance.service.ClientsBalanceService;
import es.onebox.mgmt.b2b.clients.service.ClientsService;
import es.onebox.mgmt.b2b.publishing.converter.B2BPublishingConverter;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingExportFilter;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingsExportRequest;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingsSearchRequest;
import es.onebox.mgmt.b2b.utils.SeatPublishingsFilterHelper;
import es.onebox.mgmt.collectives.CollectivesService;
import es.onebox.mgmt.collectives.collectivecodes.dto.CollectiveCodeExportFileField;
import es.onebox.mgmt.collectives.collectivecodes.dto.request.CollectiveCodesExportRequest;
import es.onebox.mgmt.collectives.collectivecodes.dto.request.CollectiveCodesSearchRequest;
import es.onebox.mgmt.datasources.api.accounting.dto.ClientTransactionsExportFilter;
import es.onebox.mgmt.datasources.api.accounting.repository.BalanceRepository;
import es.onebox.mgmt.datasources.ms.accesscontrol.dto.ExternalBarcodesExportRequest;
import es.onebox.mgmt.datasources.ms.accesscontrol.repository.ExternalBarcodesRepository;
import es.onebox.mgmt.datasources.ms.channel.repositories.VouchersRepository;
import es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.SeatPublishingsFilter;
import es.onebox.mgmt.datasources.ms.client.repositories.B2BPublishingRepository;
import es.onebox.mgmt.datasources.ms.collective.dto.MsCollectiveDetailDTO;
import es.onebox.mgmt.datasources.ms.collective.repository.CollectiveCodesRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.order.dto.SeasonTicketReleasesExportRequest;
import es.onebox.mgmt.datasources.ms.order.repository.OrdersRepository;
import es.onebox.mgmt.datasources.ms.ticket.dto.CapacityExportFilter;
import es.onebox.mgmt.datasources.ms.ticket.repository.TicketsRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.export.converter.CapacityExportConverter;
import es.onebox.mgmt.export.converter.ExportConverter;
import es.onebox.mgmt.export.converter.PriceSimulationExportConverter;
import es.onebox.mgmt.export.converter.SeasonTicketReleasesExportConverter;
import es.onebox.mgmt.export.converter.SeasonTicketRenewalsExportConverter;
import es.onebox.mgmt.export.converter.SeatPublishingExportConverter;
import es.onebox.mgmt.export.dto.ExportFilter;
import es.onebox.mgmt.export.dto.ExportResponse;
import es.onebox.mgmt.export.dto.ExportStatusResponse;
import es.onebox.mgmt.export.enums.ExportType;
import es.onebox.mgmt.salerequests.pricesimulation.dto.PriceSimulationExportFilter;
import es.onebox.mgmt.salerequests.pricesimulation.dto.PriceSimulationExportRequest;
import es.onebox.mgmt.seasontickets.dto.releaseseat.SeasonTicketReleasesExportRequestDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketRenewalFilter;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketRenewalsExportFilter;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketsRenewalsExportRequest;
import es.onebox.mgmt.seasontickets.service.SeasonTicketService;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.sessions.converters.ExternalBarcodeConverter;
import es.onebox.mgmt.sessions.dto.CapacityExportRequest;
import es.onebox.mgmt.sessions.dto.ExternalBarcodesExportRequestDTO;
import es.onebox.mgmt.sessions.dto.WhiteListExportFileField;
import es.onebox.mgmt.sessions.dto.WhiteListExportRequest;
import es.onebox.mgmt.users.dto.UserSelfDTO;
import es.onebox.mgmt.users.service.UsersService;
import es.onebox.mgmt.validation.ValidationService;
import es.onebox.mgmt.venues.dto.VenueTemplatesSeatExportFileField;
import es.onebox.mgmt.venues.dto.VenueTemplatesSeatExportRequest;
import es.onebox.mgmt.venues.dto.VenueTemplatesSectorExportFileField;
import es.onebox.mgmt.venues.dto.VenueTemplatesSectorExportRequest;
import es.onebox.mgmt.venues.dto.VenueTemplatesViewExportFileField;
import es.onebox.mgmt.venues.dto.VenueTemplatesViewExportRequest;
import es.onebox.mgmt.venues.enums.VenueTemplateViewField;
import es.onebox.mgmt.vouchers.dto.VoucherExportFileField;
import es.onebox.mgmt.vouchers.dto.VoucherExportRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExportService {

    private final UsersService usersService;
    private final CollectivesService collectivesService;
    private final ClientsService clientsService;
    private final SeasonTicketService seasonTicketService;
    private final EntitiesRepository entitiesRepository;
    private final TicketsRepository ticketsRepository;
    private final VouchersRepository vouchersRepository;
    private final VenuesRepository venuesRepository;
    private final CollectiveCodesRepository collectiveCodesRepository;
    private final BalanceRepository balanceRepository;
    private final ExternalBarcodesRepository externalBarcodesRepository;
    private final EventsRepository eventsRepository;
    private final OrdersRepository ordersRepository;
    private final ValidationService validationService;
    private final B2BPublishingRepository b2BPublishingRepository;
    private final SeatPublishingsFilterHelper filterHelper;

    @Autowired
    public ExportService(UsersService usersService,
                         CollectivesService collectivesService,
                         ClientsService clientsService,
                         SeasonTicketService seasonTicketService,
                         EntitiesRepository entitiesRepository,
                         TicketsRepository ticketsRepository,
                         VouchersRepository vouchersRepository,
                         VenuesRepository venuesRepository,
                         CollectiveCodesRepository collectiveCodesRepository,
                         BalanceRepository balanceRepository,
                         ExternalBarcodesRepository externalBarcodesRepository,
                         OrdersRepository ordersRepository,
                         ValidationService validationService,
                         EventsRepository eventsRepository,
                         B2BPublishingRepository b2BPublishingRepository,
                         SeatPublishingsFilterHelper filterHelper) {
        this.usersService = usersService;
        this.collectivesService = collectivesService;
        this.clientsService = clientsService;
        this.seasonTicketService = seasonTicketService;
        this.entitiesRepository = entitiesRepository;
        this.ticketsRepository = ticketsRepository;
        this.vouchersRepository = vouchersRepository;
        this.venuesRepository = venuesRepository;
        this.collectiveCodesRepository = collectiveCodesRepository;
        this.balanceRepository = balanceRepository;
        this.externalBarcodesRepository = externalBarcodesRepository;
        this.ordersRepository = ordersRepository;
        this.eventsRepository = eventsRepository;
        this.validationService = validationService;
        this.b2BPublishingRepository = b2BPublishingRepository;
        this.filterHelper = filterHelper;
    }

    public ExportResponse exportSessionWhiteList(final Long sessionId, WhiteListExportRequest body) {
        UserSelfDTO user = usersService.getAuthUser();
        ExportFilter<WhiteListExportFileField> filter = ExportConverter.toFilter(body, user);
        ExportProcess exportProcess = this.ticketsRepository.generateWhiteListReport(sessionId, filter);
        return ExportConverter.toResponse(exportProcess);
    }

    public ExportStatusResponse checkSessionWhitelistStatus(Long sessionId, final String exportId) {
        UserSelfDTO user = usersService.getAuthUser();
        ExportProcess exportProcess = this.ticketsRepository.getWhiteListReportStatus(sessionId, exportId, user.getId());
        return ExportConverter.mapResponse(exportProcess);
    }

    public ExportResponse exportVouchers(Long voucherGroupId, VoucherExportRequest body) {
        UserSelfDTO user = usersService.getAuthUser();
        ExportFilter<VoucherExportFileField> filter = ExportConverter.toFilter(body, user);
        ExportProcess exportProcess = this.vouchersRepository.generateVouchersReport(voucherGroupId, filter);
        return ExportConverter.toResponse(exportProcess);
    }

    public ExportStatusResponse checkVoucherStatus(Long voucherGroupId, String exportId) {
        UserSelfDTO user = usersService.getAuthUser();
        ExportProcess exportProcess = this.vouchersRepository.getVouchersReportStatus(voucherGroupId, exportId, user.getId());
        return ExportConverter.mapResponse(exportProcess);
    }

    public ExportResponse exportCollectiveCodes(Long collectiveId, CollectiveCodesSearchRequest searchFilter,
            CollectiveCodesExportRequest body) {
        MsCollectiveDetailDTO collective = collectivesService.getAndCheckCollective(collectiveId);
        UserSelfDTO user = usersService.getAuthUser();
        Operator operator = entitiesRepository
                .getCachedOperator(collective.getOwnerEntityId() != null ? collective.getOwnerEntityId() : SecurityUtils.getUserEntityId());
        ExportFilter<CollectiveCodeExportFileField> exportFilter = ExportConverter.toFilter(body, user, operator.getTimezone().getValue(),
                searchFilter.getQ());
        ExportProcess exportProcess = this.collectiveCodesRepository.generateCollectiveCodesReport(collectiveId, exportFilter);
        return ExportConverter.toResponse(exportProcess);
    }

    public ExportStatusResponse checkCollectiveCodesStatus(Long collectiveId, String exportId) {
        collectivesService.getAndCheckCollective(collectiveId);
        UserSelfDTO user = usersService.getAuthUser();
        ExportProcess exportProcess = this.collectiveCodesRepository.getCollectiveCodesReportStatus(collectiveId, exportId, user.getId());
        return ExportConverter.mapResponse(exportProcess);
    }

    public ExportResponse exportVenueTemplatesSectors(Long venueTemplateId, VenueTemplatesSectorExportRequest body) {
        VenueTemplate venueTemplate = validationService.getAndCheckVenueTemplate(venueTemplateId);
        isInteractiveVenue(venueTemplate);
        UserSelfDTO user = usersService.getAuthUser();
        ExportFilter<VenueTemplatesSectorExportFileField> exportFilter = ExportConverter.toFilter(body, user);
        ExportProcess exportProcess = this.venuesRepository.generateVenueTemplateSectorsReport(venueTemplateId, exportFilter);
        return ExportConverter.toResponse(exportProcess);
    }

    public ExportResponse exportVenueTemplatesSeats(Long venueTemplateId, VenueTemplatesSeatExportRequest body) {
        VenueTemplate venueTemplate = validationService.getAndCheckVenueTemplate(venueTemplateId);
        isInteractiveVenue(venueTemplate);
        UserSelfDTO user = usersService.getAuthUser();
        ExportFilter<VenueTemplatesSeatExportFileField> exportFilter = ExportConverter.toFilter(body, user);
        ExportProcess exportProcess = this.venuesRepository.generateVenueTemplateSeatsReport(venueTemplateId, exportFilter);
        return ExportConverter.toResponse(exportProcess);
    }

    public ExportResponse exportVenueTemplatesViews(Long venueTemplateId, VenueTemplatesViewExportRequest body) {
        VenueTemplate venueTemplate = validationService.getAndCheckVenueTemplate(venueTemplateId);
        isInteractiveVenue(venueTemplate);
        UserSelfDTO user = usersService.getAuthUser();
        manualViewBodyValidation(body);
        VenueTemplatesViewExportFileField field = new VenueTemplatesViewExportFileField();
        field.setField(VenueTemplateViewField.ROOT);
        body.setFields(List.of(field));
        ExportFilter<VenueTemplatesViewExportFileField> exportFilter = ExportConverter.toFilter(body, user);
        ExportProcess exportProcess = this.venuesRepository.generateVenueTemplateViewsReport(venueTemplateId, exportFilter);
        return ExportConverter.toResponse(exportProcess);
    }

    public ExportResponse exportExternalBarcodes(Long eventId, Long sessionId, ExternalBarcodesExportRequestDTO body) {
        UserSelfDTO user = usersService.getAuthUser();
        ExternalBarcodesExportRequest filter = ExternalBarcodeConverter.toFilter(eventId, sessionId, body, ExportConverter.toFilter(body, user));
        ExportProcess exportProcess = this.externalBarcodesRepository.exportExternalBarcodes(filter);
        return ExportConverter.toResponse(exportProcess);
    }

    public ExportStatusResponse getExternalBarcodesStatus(final String exportId) {
        UserSelfDTO user = usersService.getAuthUser();
        ExportProcess exportProcess = this.externalBarcodesRepository.getExportExternalBarcodesStatus(exportId, user.getId());
        return ExportConverter.mapResponse(exportProcess);
    }

    private void manualViewBodyValidation(VenueTemplatesViewExportRequest body){
        if(body.getDelivery() == null || body.getFormat() == null){
            throw OneboxRestException.builder(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER).setMessage("Delivery or Format parameters are incorrect").build();
        }
        if(body.getDelivery().getType() == null){
            throw OneboxRestException.builder(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER).setMessage("Delivery type parameter is incorrect").build();
        }
        if(body.getTranslations() != null && body.getTranslations().stream().anyMatch(translation -> translation.getKey().isBlank() || translation.getValue() == null)){
            throw OneboxRestException.builder(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER).setMessage("Translation parameters are incorrect or invalid").build();
        }
    }

    private void isInteractiveVenue(VenueTemplate venueTemplate){
        Entity entity = entitiesRepository.getEntity(venueTemplate.getEntityId());

        if (entity.getInteractiveVenue() == null || CommonUtils.isFalse(entity.getInteractiveVenue().getEnabled())) {
            throw new OneboxRestException(ApiMgmtErrorCode.INTERACTIVE_VENUE_TEMPLATE_NOT_CONFIGURED_FROM_ENTITY);
        }
    }

    public ExportStatusResponse checkVenueTemplatesStatus(Long venueTemplateId, String exportId, String type) {
        validationService.getAndCheckVenueTemplate(venueTemplateId);
        UserSelfDTO user = usersService.getAuthUser();
        ExportProcess exportProcess = this.venuesRepository.getVenueTemplatesReportStatus(venueTemplateId, exportId,
                user.getId(), type);
        return ExportConverter.mapResponse(exportProcess);
    }

    public ExportResponse exportClientTransactions(Long clientId, ClientTransactionsExportRequestDTO body) {
        clientsService.validateClient(clientId, body.getEntityId());
        if (body.getEntityId() == null) {
            // the userEntityId has already been validated previously in clientsService.validateClient()
            body.setEntityId(SecurityUtils.getUserEntityId());
        }
        ClientsBalanceService.validateDateRange(body.getFilter().getFrom(), body.getFilter().getTo());
        UserSelfDTO user = usersService.getAuthUser();
        ClientTransactionsExportFilter exportFilter = ClientsBalanceConverter.toFilter(clientId, body,
                ExportConverter.toFilter(body, user, user.getTimezone(), null));
        ExportProcess exportProcess = this.balanceRepository.exportTransactions(exportFilter);
        return ExportConverter.toResponse(exportProcess);
    }

    public ExportStatusResponse checkClientTransactionsStatus(Long clientId, String exportId, Long entityId) {
        clientsService.validateClient(clientId, entityId);
        UserSelfDTO user = usersService.getAuthUser();
        ExportProcess exportProcess = this.balanceRepository.exportTransactionsStatus(exportId, user.getId());
        return ExportConverter.mapResponse(exportProcess);
    }


    public ExportResponse exportSessionCapacity(Long eventId, Long sessionId, Long venueConfigId, CapacityExportRequest body, List<Long> viewIds, List<Long> sectorIds) {
        UserSelfDTO user = usersService.getAuthUser();
        CapacityExportFilter filter = CapacityExportConverter.convert(body, user, sessionId, venueConfigId, viewIds, sectorIds);
        ExportProcess exportProcess = this.ticketsRepository.generateSessionCapacityReport(eventId, sessionId, filter);
        return ExportConverter.toResponse(exportProcess);
    }

    public ExportStatusResponse checkSessionCapacityStatus(Long eventId, Long sessionId, final String exportId) {
        UserSelfDTO user = usersService.getAuthUser();
        ExportProcess exportProcess = this.ticketsRepository.getSessionCapacityReportStatus(eventId, sessionId, exportId, user.getId());
        return ExportConverter.mapResponse(exportProcess);
    }

    public ExportResponse exportSeasonTicketsRenewals(Long seasonTicketId, SeasonTicketRenewalFilter queryParams,
                                                      SeasonTicketsRenewalsExportRequest request) {
        this.seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        UserSelfDTO user = this.usersService.getAuthUser();
        Operator operator = this.entitiesRepository.getCachedOperator(SecurityUtils.getUserOperatorId());
        String operatorTimeZone = operator.getTimezone().getValue();
        SeasonTicketRenewalsExportFilter filter =
                SeasonTicketRenewalsExportConverter.convert(request, user, operatorTimeZone);
        ExportProcess exportProcess = this.eventsRepository.generateSeasonTicketsRenewalsReport(seasonTicketId, filter, queryParams);
        return ExportConverter.toResponse(exportProcess);
    }

    public ExportStatusResponse getSeasonTicketsRenewalsExportStatus(String exportId) {
        UserSelfDTO user = usersService.getAuthUser();
        ExportProcess exportProcess = this.eventsRepository.getExportStatus(exportId, user.getId(), ExportType.SEASON_TICKETS_RENEWALS);
        return ExportConverter.mapResponse(exportProcess);
    }

    public ExportResponse getExportReleases(Long seasonTicketId, SeasonTicketReleasesExportRequestDTO request) {
        SeasonTicket seasonTicket = this.seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        UserSelfDTO user = this.usersService.getAuthUser();
        SeasonTicketReleasesExportRequest filter =
                SeasonTicketReleasesExportConverter.toFilter(request, seasonTicket, ExportConverter.toFilter(request, user, user.getTimezone(), null));
        ExportProcess exportProcess = this.ordersRepository.exportSeasonTicketReleases(filter);
        return ExportConverter.toResponse(exportProcess);
    }

    public ExportStatusResponse getReleaseExportStatus(String exportId) {
        UserSelfDTO user = usersService.getAuthUser();
        ExportProcess exportProcess = this.ordersRepository.getReleaseExportStatus(exportId, user.getId(), ExportType.RELEASES);
        return ExportConverter.mapResponse(exportProcess);
    }

    public ExportResponse exportSeatPublishings(SeatPublishingsSearchRequest filter, SeatPublishingsExportRequest body) {
        SeatPublishingsFilter msFilter = B2BPublishingConverter.toMsFilter(filter);
        filterHelper.addEntityConstraints(msFilter);
        filterHelper.checkEntityFilterConstraints(filter, msFilter);
        UserSelfDTO user = usersService.getAuthUser();
        Operator operator = this.entitiesRepository.getCachedOperator(SecurityUtils.getUserOperatorId());
        String operatorTimeZone = operator.getTimezone().getValue();
        SeatPublishingExportFilter exportFilter = SeatPublishingExportConverter.convert(msFilter, body, user, operatorTimeZone);
        ExportProcess exportProcess = this.b2BPublishingRepository.exportProcess(exportFilter);
        return ExportConverter.mapResponse(exportProcess);
    }

    public ExportStatusResponse checkSeatPublishingsStatus(String exportId) {
        UserSelfDTO user = usersService.getAuthUser();
        ExportProcess exportProcess = this.b2BPublishingRepository.getSeatPublishingsReportStatus(exportId, user.getId());
        return ExportConverter.mapResponse(exportProcess);
    }

    public ExportResponse exportPriceSimulations(Long saleRequestId,
        PriceSimulationExportRequest request) {
        UserSelfDTO user = this.usersService.getAuthUser();
        Operator operator = this.entitiesRepository.getCachedOperator(SecurityUtils.getUserOperatorId());
        String operatorTimeZone = operator.getTimezone().getValue();
        PriceSimulationExportFilter filter =
            PriceSimulationExportConverter.convert(request, user, operatorTimeZone);
        ExportProcess exportProcess = this.eventsRepository.generatePriceSimulationsReport(saleRequestId, filter);
        return ExportConverter.toResponse(exportProcess);
    }

    public ExportStatusResponse getPriceSimulationExportStatus(String exportId) {
        UserSelfDTO user = usersService.getAuthUser();
        ExportProcess exportProcess = this.eventsRepository.getExportStatus(exportId, user.getId(), ExportType.PRICE_SIMULATION);
        return ExportConverter.mapResponse(exportProcess);
    }
}
