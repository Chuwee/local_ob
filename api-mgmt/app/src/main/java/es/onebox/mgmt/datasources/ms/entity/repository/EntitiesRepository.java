package es.onebox.mgmt.datasources.ms.entity.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.core.security.EntityTypes;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.common.dto.AuthConfig;
import es.onebox.mgmt.datasources.common.dto.Surcharge;
import es.onebox.mgmt.datasources.common.enums.SurchargeType;
import es.onebox.mgmt.customdomains.common.dto.DomainSettings;
import es.onebox.mgmt.datasources.ms.channel.dto.whatsapptemplates.WhatsappTemplates;
import es.onebox.mgmt.datasources.ms.entity.MsEntityDatasource;
import es.onebox.mgmt.datasources.ms.entity.dto.Attribute;
import es.onebox.mgmt.datasources.ms.entity.dto.AttributeTexts;
import es.onebox.mgmt.datasources.ms.entity.dto.Calendar;
import es.onebox.mgmt.datasources.ms.entity.dto.CalendarDayType;
import es.onebox.mgmt.datasources.ms.entity.dto.CookieSettings;
import es.onebox.mgmt.datasources.ms.entity.dto.CookieSettingsBase;
import es.onebox.mgmt.datasources.ms.entity.dto.CreateProducerInvoicePrefix;
import es.onebox.mgmt.datasources.ms.entity.dto.Entities;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityCustomContents;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityGatewayConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityProfile;
import es.onebox.mgmt.datasources.ms.entity.dto.EntitySearchFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityTax;
import es.onebox.mgmt.datasources.ms.entity.dto.ExternalConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.Form;
import es.onebox.mgmt.datasources.ms.entity.dto.InvoicePrefix;
import es.onebox.mgmt.datasources.ms.entity.dto.LoyaltyPointsConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.dto.Producer;
import es.onebox.mgmt.datasources.ms.entity.dto.ProducerFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.ProducerInoivcePrefixFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.ProducerInvoicePrefix;
import es.onebox.mgmt.datasources.ms.entity.dto.Producers;
import es.onebox.mgmt.datasources.ms.entity.dto.Tax;
import es.onebox.mgmt.datasources.ms.entity.dto.UpdateEntityCustomContents;
import es.onebox.mgmt.datasources.ms.entity.dto.UpdateLoyaltyPointsConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.UpdateProducerInvoicePrefix;
import es.onebox.mgmt.datasources.ms.entity.dto.UserLimits;
import es.onebox.mgmt.datasources.ms.entity.dto.customerconfig.CustomerConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.customerconfig.UpdateCustomerConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.membercounter.MemberCounter;
import es.onebox.mgmt.datasources.ms.entity.dto.membercounter.UpdateMemberCounter;
import es.onebox.mgmt.datasources.ms.entity.enums.TaxType;
import es.onebox.mgmt.entities.dto.AttributeSearchFilter;
import es.onebox.mgmt.datasources.ms.insurance.dto.ResponseEntities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EntitiesRepository {

    private static final String CACHE_ENTITY_KEY = "entities.entity";
    private static final String CACHE_ENTITY_TAX_KEY = "entities.entityTax";
    private static final String CACHE_OPERATOR_KEY = "entities.operator";
    private static final String CACHE_ENTITY_ADMIN_KEY = "entities.entity_admin";
    private static final int CACHE_ENTITY_TTL = 10;
    private static final int CACHE_ENTITY_TAX_TTL = 10;
    private static final int CACHE_ENTITY_ADMIN_TTL = 10;
    private static final int CACHE_OPERATOR_TTL = 600;

    private final MsEntityDatasource msEntityDatasource;

    @Autowired
    public EntitiesRepository(MsEntityDatasource msEntityDatasource) {
        this.msEntityDatasource = msEntityDatasource;
    }

    public Entity getEntity(Long entityId) {
        return msEntityDatasource.getEntity(entityId);
    }

    @Cached(key = CACHE_ENTITY_TAX_KEY, expires = CACHE_ENTITY_TAX_TTL)
    public List<Tax> getEntityTaxes(@CachedArg Long entityId, @CachedArg Long eventId, @CachedArg Long venueId, @CachedArg TaxType taxType) {
        return msEntityDatasource.getEntityTaxes(entityId, eventId, venueId, taxType);
    }

    @Cached(key = CACHE_ENTITY_KEY, expires = CACHE_ENTITY_TTL)
    public Entity getCachedEntity(@CachedArg Long entityId) {
        return msEntityDatasource.getEntity(entityId);
    }

    @Cached(key = CACHE_OPERATOR_KEY, expires = CACHE_OPERATOR_TTL)
    public Operator getCachedOperator(@CachedArg Long entityId) {
        Entity entity = getCachedEntity(entityId);
        return msEntityDatasource.getOperator(entity.getOperator().getId());
    }

    public Operator getOperator(Long operatorId) {
        return msEntityDatasource.getOperator(operatorId);
    }

    public Entities getEntities(EntitySearchFilter entitySearchFilter) {
        return msEntityDatasource.getEntities(entitySearchFilter);
    }

    public Entities getEntityAdminEntities(EntitySearchFilter filter) {
        return msEntityDatasource.getEntities(filter);
    }

    @Cached(key = CACHE_ENTITY_ADMIN_KEY, expires = CACHE_ENTITY_ADMIN_TTL)
    public List<Long> getCachedEntityAdminEntities(@CachedArg Long entityAdminId) {
        EntitySearchFilter filter = new EntitySearchFilter();
        filter.setEntityAdminId(entityAdminId);
        filter.setFields(List.of("id"));
        Entities entities = msEntityDatasource.getEntities(filter);
        return entities.getData().stream().map(Entity::getId).toList();
    }

    public Long create(Entity entity) {
        return msEntityDatasource.createEntity(entity);
    }

    public void update(Entity entity) {
        msEntityDatasource.updateEntity(entity);
    }

    public List<EntityTypes> getEntityTypes(Long entityId) {
        return msEntityDatasource.getEntityTypes(entityId);
    }

    public void setEntityType(Long entityId, EntityTypes entityType) {
        msEntityDatasource.setEntityType(entityId, entityType);
    }

    public void unsetEntityType(Long entityId, EntityTypes entityType) {
        msEntityDatasource.unsetEntityType(entityId, entityType);
    }

    public Producers getProducers(ProducerFilter filter) {
        return msEntityDatasource.getProducers(filter);
    }

    public Producers getProducersByEntityId(Long entityId) {
        return msEntityDatasource.getProducersByEntityId(entityId);
    }

    public Producer getProducer(Long producerId) {
        return msEntityDatasource.getProducer(producerId);
    }

    public Long createProducer(Producer producer) {
        return msEntityDatasource.createProducer(producer);
    }

    public void updateProducer(Producer producer) {
        msEntityDatasource.updateProducer(producer);
    }

    public List<EntityTax> getTaxes(Long entityId) {
        return msEntityDatasource.getTaxes(entityId);
    }

    public Calendar getCalendar(Long entityId, Long calendarId) {
        return msEntityDatasource.getCalendar(entityId, calendarId);
    }

    public List<Calendar> getCalendars(Long entityId) {
        return msEntityDatasource.getCalendars(entityId);
    }

    public Long createCalendar(Long entityId, String name, List<CalendarDayType> calendarDayTypes) {
        Calendar calendar = new Calendar();
        calendar.setName(name);
        calendar.setDayTypes(calendarDayTypes);

        return msEntityDatasource.createCalendar(entityId, calendar);
    }

    public void updateCalendar(long entityId, Calendar calendar) {
        msEntityDatasource.updateCalendar(entityId, calendar);
    }

    public void deleteCalendar(long entityId, Long calendarId) {
        msEntityDatasource.deleteCalendar(entityId, calendarId);
    }

    public Attribute getAttribute(long entityId, long attributeId) {
        return msEntityDatasource.getAttribute(entityId, attributeId);
    }

    public List<Attribute> getAttributes(Long entityId, AttributeSearchFilter attributeSearchFilter) {
        return msEntityDatasource.getAttributes(entityId, attributeSearchFilter);
    }

    public Long createAttribute(Long entityId, String name, AttributeTexts texts) {
        Attribute attribute = new Attribute();
        attribute.setName(name);
        attribute.setTexts(texts);

        return msEntityDatasource.createAttribute(entityId, attribute);
    }

    public void updateAttribute(long entityId, Attribute attribute) {
        msEntityDatasource.updateAttribute(entityId, attribute);
    }

    public void deleteAttribute(long entityId, Long attributeId) {
        msEntityDatasource.deleteAttribute(entityId, attributeId);
    }

    public void setSurcharges(Long entityId, List<Surcharge> requests) {
        msEntityDatasource.setSurcharge(entityId, requests);
    }

    public List<Surcharge> getSurcharges(Long entityId, List<SurchargeType> surchargeTypes, List<Long> currencyIds) {
        return msEntityDatasource.getSurcharges(entityId, surchargeTypes, currencyIds);
    }

    public List<Long> getVisibleEntities(Long entityId) {
        return msEntityDatasource.getVisibleEntities(entityId);
    }

    public List<EntityProfile> getEntityProfiles(Long entityId) {
        return msEntityDatasource.getEntityProfiles(entityId);
    }

    public void updateEntityProfile(Long entityId, Long profileId, EntityProfile profile) {
        msEntityDatasource.updateEntityProfile(entityId, profileId, profile);
    }

    public IdDTO createEntityProfile(Long entityId, EntityProfile profile) {
        return msEntityDatasource.createEntityProfile(entityId, profile);
    }

    public EntityProfile getEntityProfile(Long entityId, Long profileId) {
        return msEntityDatasource.getEntityProfile(entityId, profileId);
    }

    public void deleteEntityProfile(Long entityId, Long profileId) {
        msEntityDatasource.deleteEntityProfile(entityId, profileId);
    }

    public ProducerInvoicePrefix getProducerInvoicePrefixes(Long producerId, ProducerInoivcePrefixFilter filter) {
        return msEntityDatasource.getProducerInvoicePrefixes(producerId, filter);
    }

    public IdDTO createProducerInvoicePrefix(Long producerId, CreateProducerInvoicePrefix createProducerInvoicePrefix) {
        return msEntityDatasource.createProducerInvoicePrefix(producerId, createProducerInvoicePrefix);
    }

    public void updateProducerInvoicePrefix(Long producerId, Long invoicePrefixId, UpdateProducerInvoicePrefix updateProducerInvoicePrefix) {
        msEntityDatasource.updateProducerInvoicePrefix(producerId, invoicePrefixId, updateProducerInvoicePrefix);
    }

    public InvoicePrefix getInvoicePrefix(Long producerId, Long invoicePrefixId) {
        return msEntityDatasource.getInvoicePrefix(producerId, invoicePrefixId);
    }

    public List<EntityCustomContents> getCustomContents(Long entityId) {
        return msEntityDatasource.getCustomContents(entityId);
    }

    public void setCustomContents(Long entityId, List<UpdateEntityCustomContents> updateEntityCustomContents) {
        msEntityDatasource.setCustomContents(entityId, updateEntityCustomContents);
    }

    public void deleteCustomContents(Long entityId, String tag) {
        msEntityDatasource.deleteCustomContents(entityId, tag);
    }

    public UserLimits getUserLimits() {
        return msEntityDatasource.getUserLimits();
    }

    public CookieSettings getCookieSettings(Long entityId) {
        return msEntityDatasource.getCookieSettings(entityId);
    }

    public void updateCookieSettings(Long entityId, CookieSettingsBase cookieSettings) {
        msEntityDatasource.updateCookieSettings(entityId, cookieSettings);
    }

    public ExternalConfig getExternalConfig(Long entityId) {
        return msEntityDatasource.getExternalConfig(entityId);
    }

    public void updateExternalConfig(Long entityId, ExternalConfig externalConfig) {
        msEntityDatasource.updateExternalConfig(entityId, externalConfig);
    }

    public WhatsappTemplates getWhatsappTemplatesContents(Long entityId) {
        return msEntityDatasource.getWhatsappTemplatesContents(entityId);
    }

    public AuthConfig getAuthConfig(Long entityId) {
        return msEntityDatasource.getAuthConfig(entityId);
    }

    public void updateAuthConfig(Long entityId, AuthConfig authConfig) {
        msEntityDatasource.updateAuthConfig(entityId, authConfig);
    }

    public Form getForm(Long entityId, String name) {
        return msEntityDatasource.getForm(entityId, name);
    }

    public void updateForm(Long entityId, Form updateForm, String name) {
        msEntityDatasource.updateForm(entityId, updateForm, name);
    }

    public LoyaltyPointsConfig getLoyaltyPoints(Long entityId) {
        return msEntityDatasource.getLoyaltyPoints(entityId);
    }

    public void updateLoyaltyPoints(Long entityId, UpdateLoyaltyPointsConfig updateLoyaltyPointsConfig) {
        msEntityDatasource.updateLoyaltyPoints(entityId, updateLoyaltyPointsConfig);
    }

    public CustomerConfig getCustomerConfig(Long entityId) {
        return msEntityDatasource.getCustomerConfig(entityId);
    }

    public void updateCustomerConfig(Long entityId, UpdateCustomerConfig customerConfig) {
        msEntityDatasource.updateCustomerConfig(entityId, customerConfig);
    }

    public List<EntityGatewayConfig> getEntityGatewaysConfig(Long entityId) {
        return msEntityDatasource.getEntityGatewaysConfig(entityId);
    }

    public MemberCounter getMemberCounter(Long entityId) {
        return msEntityDatasource.getMemberCounter(entityId);
    }

    public void updateMemberCounter(Long entityId, UpdateMemberCounter request) {
        msEntityDatasource.updateMemberCounter(entityId, request);
    }

    public ResponseEntities getListOfEntities(EntitySearchFilter searchFilter) {
        return msEntityDatasource.getListOfEntities(searchFilter);
    }

    public DomainSettings getCustomersDomainSettings(Long entityId) {
        return msEntityDatasource.getCustomersDomainSettings(entityId);
    }

    public void upsertCustomersDomainSettings(Long entityId, DomainSettings channelDomainSettings) {
        msEntityDatasource.upsertCustomersDomainSettings(entityId, channelDomainSettings);
    }

    public void disableCustomersDomainSettings(Long entityId) {
        msEntityDatasource.disableCustomersDomainSettings(entityId);
    }
}