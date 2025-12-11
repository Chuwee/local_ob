package es.onebox.mgmt.validation;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelConfig;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.enums.WhitelabelType;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.repository.CategoriesRepository;
import es.onebox.mgmt.datasources.ms.entity.repository.OperatorsRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventStatus;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventType;
import es.onebox.mgmt.datasources.ms.event.dto.event.Product;
import es.onebox.mgmt.datasources.ms.event.dto.event.Provider;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductAttribute;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductAttributeValue;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSaleRequestDetail;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketStatus;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.ProductsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.mgmt.datasources.ms.event.repository.SessionsRepository;
import es.onebox.mgmt.datasources.ms.insurance.dto.InsurancePolicyV1;
import es.onebox.mgmt.datasources.ms.insurance.dto.Insurer;
import es.onebox.mgmt.datasources.ms.insurance.repository.InsurancePoliciesRepository;
import es.onebox.mgmt.datasources.ms.insurance.repository.InsurerRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateStatus;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ApiMgmtExternalEventErrorCode;
import es.onebox.mgmt.exception.ApiMgmtInsuranceErrorCode;
import es.onebox.mgmt.insurance.enums.PolicyState;
import es.onebox.mgmt.products.enums.ProductState;
import es.onebox.mgmt.secondarymarket.dto.ResalePriceTypeDTO;
import es.onebox.mgmt.secondarymarket.dto.SeasonTicketSecondaryMarketConfigDTO;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.sessions.SessionUtils;
import org.springframework.stereotype.Service;

import java.util.Set;

import static es.onebox.mgmt.exception.ApiMgmtErrorCode.BAD_REQUEST_PARAMETER;
import static es.onebox.mgmt.exception.ApiMgmtErrorCode.CATEGORY_NOT_FOUND;
import static es.onebox.mgmt.exception.ApiMgmtErrorCode.EVENT_IS_NOT_AVET;
import static es.onebox.mgmt.exception.ApiMgmtErrorCode.NOT_FOUND;
import static es.onebox.mgmt.exception.ApiMgmtSessionErrorCode.SESSION_NOT_FOUND;
import static es.onebox.mgmt.exception.ApiMgmtSessionErrorCode.SESSION_NOT_MATCH_EVENT;

@Service
public class ValidationService {

    private final CategoriesRepository categoriesRepository;
    private final SessionsRepository sessionsRepository;
    private final EventsRepository eventsRepository;
    private final ChannelsRepository channelsRepository;
    private final ProductsRepository productsRepository;
    private final VenuesRepository venuesRepository;
    private final SecurityManager securityManager;
    private final OperatorsRepository operatorsRepository;
    private final SeasonTicketRepository seasonTicketRepository;
    private final InsurerRepository insurerRepository;
    private final InsurancePoliciesRepository insurancePoliciesRepository;

    public ValidationService(final CategoriesRepository categoriesRepository, final SessionsRepository sessionsRepository,
                             final EventsRepository eventsRepository, final ProductsRepository productsRepository, final SecurityManager securityManager,
                             VenuesRepository venuesRepository, final OperatorsRepository operatorsRepository,
                             SeasonTicketRepository seasonTicketRepository, InsurerRepository insurerRepository,
                             ChannelsRepository channelsRepository, InsurancePoliciesRepository insurancePoliciesRepository) {
        this.categoriesRepository = categoriesRepository;
        this.sessionsRepository = sessionsRepository;
        this.eventsRepository = eventsRepository;
        this.productsRepository = productsRepository;
        this.securityManager = securityManager;
        this.venuesRepository = venuesRepository;
        this.operatorsRepository = operatorsRepository;
        this.seasonTicketRepository = seasonTicketRepository;
        this.insurerRepository = insurerRepository;
        this.channelsRepository = channelsRepository;
        this.insurancePoliciesRepository = insurancePoliciesRepository;
    }

    public void checkCategory(Integer categoryId) {
        try {
            categoriesRepository.getCategory(categoryId);
        } catch (OneboxRestException e) {
            if (ApiMgmtErrorCode.NOT_FOUND.getErrorCode().equals(e.getErrorCode())) {
                throw new OneboxRestException(CATEGORY_NOT_FOUND, "No category found with id: " + categoryId, null);
            }
            throw e;
        }
    }

    public VenueTemplate getAndCheckVenueTemplate(Long venueTemplateId) {
        return getAndCheckVenueTemplate(venueTemplateId, false);
    }

    public VenueTemplate getAndCheckWriteVenueTemplate(Long venueTemplateId) {
        VenueTemplate template = getAndCheckVenueTemplate(venueTemplateId, false);
        Long eventId = template.getEventId();
        if (eventId != null) {
            Event event = eventsRepository.getEvent(eventId);
            if (event != null && event.getInventoryProvider() != null && !event.getInventoryProvider().equals(Provider.ITALIAN_COMPLIANCE)) {
                String msg = String.format("External event supply defined by %s ", event.getInventoryProvider());
                throw ExceptionBuilder.build(ApiMgmtExternalEventErrorCode.EXTERNAL_EVENT_UNSUPPORTED_OPERATION, msg);
            }
        }
        return template;
    }

    public VenueTemplate getAndCheckVenueTemplate(Long venueTemplateId, boolean withVisibility) {
        VenueTemplate venueTemplate = venuesRepository.getVenueTemplate(venueTemplateId);

        if (venueTemplate == null || venueTemplate.getStatus().equals(VenueTemplateStatus.DELETED)) {
            throw new OneboxRestException(ApiMgmtErrorCode.VENUE_TEMPLATE_NOT_FOUND,
                    "No venueTemplate with id: " + venueTemplateId, null);
        }

        if (withVisibility) {
            securityManager.checkEntityAccessibleWithVisibility(venueTemplate.getEntityId());
        } else {
            securityManager.checkEntityAccessible(venueTemplate.getEntityId());
        }

        return venueTemplate;
    }

    public void checkCustomCategory(Long entityId, Long customCategoryId) {
        try {
            categoriesRepository.getEntityCategory(entityId, customCategoryId);
        } catch (OneboxRestException e) {
            if (ApiMgmtErrorCode.NOT_FOUND.getErrorCode().equals(e.getErrorCode())) {
                throw new OneboxRestException(CATEGORY_NOT_FOUND, "No custom category found with id: "
                        + customCategoryId, null);
            }
            throw e;
        }
    }

    public Session getAndCheckSession(Long eventId, Long sessionId) {
        if ((eventId == null || eventId < 0) || (sessionId == null || sessionId < 0)) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "eventId and sessionId must be a positive integer", null);
        }
        getAndCheckEvent(eventId);
        return getAndCheckOnlySession(eventId, sessionId);
    }


    public Session getAndCheckSessionExternal(Long eventId, Long sessionId) {
        getAndCheckEventExternal(eventId);
        if (sessionId == null || sessionId < 0) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "sessionId must be a positive integer", null);
        }
        return getAndCheckOnlySession(eventId, sessionId);
    }

    public Session getAndCheckOnlySession(Long eventId, Long sessionId) {
        Session session = sessionsRepository.getSession(eventId, sessionId);
        validateSession(eventId, sessionId, session);
        return session;
    }

    public Session getAndCheckVisibilitySession(Long sessionId) {
        Session session = sessionsRepository.getSession(sessionId);
        if (session == null || es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus.DELETED.equals(session.getStatus())) {
            throw new OneboxRestException(SESSION_NOT_FOUND, "No session found with id: " + sessionId, null);
        }
        securityManager.checkEntityAccessible(session.getEntityId());
        return session;
    }

    public static void validateSession(Long eventId, Long sessionId, Session session) {
        if (session == null || es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus.DELETED.equals(session.getStatus())) {
            throw new OneboxRestException(SESSION_NOT_FOUND, "No session found with id: " + sessionId, null);
        } else if (!session.getEventId().equals(eventId)) {
            throw new OneboxRestException(SESSION_NOT_MATCH_EVENT);
        }
    }

    public Event getAndCheckEvent(Long eventId) {
        if (eventId == null || eventId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_EVENT_ID);
        }
        Event event = eventsRepository.getEvent(eventId);
        if (event == null || event.getStatus().equals(EventStatus.DELETED)) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_NOT_FOUND);
        }
        securityManager.checkEntityAccessible(event.getEntityId());
        return event;
    }

    public SeasonTicket getAndCheckSeasonTicket(Long seasonTicketId) {
        SeasonTicket seasonTicket = seasonTicketRepository.getSeasonTicket(seasonTicketId);
        if (seasonTicket == null || SeasonTicketStatus.DELETED.equals(seasonTicket.getStatus())) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_NOT_FOUND);
        }
        securityManager.checkEntityAccessible(seasonTicket.getEntityId());
        return seasonTicket;
    }

    public Product getAndCheckProduct(Long productId) {
        Product product = productsRepository.getProduct(productId);
        if (product == null || product.getProductState().equals(ProductState.DELETED)) {
            //We can only delete products that does not have associated purchases
            throw new OneboxRestException(ApiMgmtErrorCode.PRODUCT_NOT_FOUND);
        }
        securityManager.checkEntityAccessible(product.getEntity().getId());

        return product;
    }

    public ProductSaleRequestDetail getAndCheckSaleRequest(Long saleRequestId) {
        ProductSaleRequestDetail saleRequestDetail = channelsRepository.getProductSaleRequestDetail(saleRequestId);
        if (saleRequestDetail == null) {
            //We can only delete products that does not have associated purchases
            throw new OneboxRestException(ApiMgmtErrorCode.SALE_REQUEST_NOT_FOUND);
        }
        Long channelEntityId = saleRequestDetail.getChannel().getEntity().getId();
        securityManager.checkEntityAccessible(channelEntityId);

        return saleRequestDetail;
    }

    public Insurer getAndCheckInsurer(Integer insurerId) {
        Insurer insurer = insurerRepository.getInsurer(insurerId);
        if (insurer == null) {
            throw new OneboxRestException(ApiMgmtInsuranceErrorCode.INSURER_NOT_FOUND);
        }
        return insurer;
    }

    public Product getAndCheckProductAttribute(Long productId, Long attributeId, Long valueId) {
        Product product = productsRepository.getProduct(productId);
        if (product == null || product.getProductState().equals(ProductState.DELETED)) {
            //We can only delete products that does not have associated purchases
            throw new OneboxRestException(ApiMgmtErrorCode.PRODUCT_NOT_FOUND);
        }
        if (attributeId != null) {
            ProductAttribute productAttribute = productsRepository.getProductAttribute(productId, attributeId);
            if (productAttribute == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.PRODUCT_ATTRIBUTE_NOT_FOUND);
            }
        }
        if (valueId != null) {
            ProductAttributeValue productAttributeValue = productsRepository.getProductAttributeValue(productId, attributeId, valueId);
            if (productAttributeValue == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.PRODUCT_ATTRIBUTE_VALUE_NOT_FOUND);
            }
        }
        securityManager.checkEntityAccessible(product.getEntity().getId());
        return product;
    }

    public Event getAndCheckEventExternal(Long eventId) {
        if (eventId == null || eventId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_EVENT_ID);
        }
        Event event = eventsRepository.getEvent(eventId);
        if (event == null || event.getStatus().equals(EventStatus.DELETED)) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_NOT_FOUND);
        }
        if (event.getInventoryProvider() != null && !event.getInventoryProvider().equals(Provider.ITALIAN_COMPLIANCE)) {
            String msg = String.format("External event supply defined by %s ", event.getInventoryProvider());
            throw ExceptionBuilder.build(ApiMgmtExternalEventErrorCode.EXTERNAL_EVENT_UNSUPPORTED_OPERATION, msg);
        }
        securityManager.checkEntityAccessible(event.getEntityId());
        return event;
    }

    public Event getAndCheckEventExternalWithoutSecurity(Long eventId) {
        if (eventId == null || eventId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_EVENT_ID);
        }
        Event event = eventsRepository.getEvent(eventId);
        if (event == null || event.getStatus().equals(EventStatus.DELETED)) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_NOT_FOUND);
        }
        if (event.getInventoryProvider() != null && !event.getInventoryProvider().equals(Provider.ITALIAN_COMPLIANCE)) {
            String msg = String.format("External event supply defined by %s ", event.getInventoryProvider());
            throw ExceptionBuilder.build(ApiMgmtExternalEventErrorCode.EXTERNAL_EVENT_UNSUPPORTED_OPERATION, msg);
        }
        return event;
    }


    public Event checkAvetEventAccessibility(Long eventId) {
        if (eventId == null || eventId <= 0) {
            throw OneboxRestException.builder(BAD_REQUEST_PARAMETER).setMessage("event id is mandatory").build();
        }
        Event event = eventsRepository.getEvent(eventId);
        if (event == null || EventStatus.DELETED.equals(event.getStatus())) {
            throw OneboxRestException.builder(NOT_FOUND)
                    .setMessage("no event found with id: " + eventId)
                    .build();
        }
        if (event.getType() == null || !EventType.AVET.equals(event.getType()) && !SessionUtils.isSgaEvent(event.getInventoryProvider())) {
            throw OneboxRestException.builder(EVENT_IS_NOT_AVET)
                    .setMessage("Event is not avet: " + eventId)
                    .build();
        }

        securityManager.checkEntityAccessible(event.getEntityId());

        return event;
    }

    public static String convertAndCheckLanguageTag(String languageTag, Set<String> availableLocales) {
        String result = ConverterUtils.toLocale(languageTag);
        if (result == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_LANGUAGE_FORMAT);
        }
        if (!availableLocales.contains(result)) {
            throw new OneboxRestException(ApiMgmtErrorCode.LANGUAGE_NOT_IN_EVENT);
        }
        return result;
    }

    public Operator getAndCheckOperator(Long operatorId) {
        return operatorsRepository.getOperator(operatorId);
    }

    public Boolean getAndCheckExternalWhitelabel(ChannelConfig channelConfig, ChannelResponse channel) {
        return channelConfig != null && channelConfig.getWhitelabelType() != null &&
                (WhitelabelType.EXTERNAL.equals(channelConfig.getWhitelabelType()));
    }

    public void validatePriceType(ResalePriceTypeDTO priceType) {
        if (priceType.equals(ResalePriceTypeDTO.PRORATED)) {
            throw new IllegalArgumentException("Price type PRORATED is not allowed");
        }
    }

    public void validateNumberOfSessions(SeasonTicketSecondaryMarketConfigDTO seasonTicketSecondaryMarketConfig) {
        if (seasonTicketSecondaryMarketConfig.getPrice() != null &&
                ResalePriceTypeDTO.PRORATED.equals(seasonTicketSecondaryMarketConfig.getPrice().getType()) &&
                (seasonTicketSecondaryMarketConfig.getNumSessions() == null || seasonTicketSecondaryMarketConfig.getNumSessions() <= 0)) {
            throw new IllegalArgumentException("When price type is PRORATED, number of sessions must be a minimum of 1");
        }
    }

    public void getAndCheckPolicy(Integer insurerId, Integer policyId) {
        InsurancePolicyV1 policy = insurancePoliciesRepository.getPolicyDetails(insurerId, policyId);
        if (policy == null || policy.getState().equals(PolicyState.DELETED)) {
            throw new OneboxRestException(ApiMgmtErrorCode.INSURANCE_POLICY_NOT_FOUND);
        }
    }
}
