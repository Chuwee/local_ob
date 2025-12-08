import { buildHttpParams } from '@OneboxTM/utils-http';
import { FormsField } from '@admin-clients/cpanel/common/utils';
import { DomainSettings } from '@admin-clients/cpanel/shared/data-access';
import { EntityType, EntitiesBaseApi, EntityCategory } from '@admin-clients/shared/common/data-access';
import { IdName } from '@admin-clients/shared/data-access/models';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DonationCampaign } from '../models/donation-campaign.model';
import { EntityBankAccount, PutEntityBankAccount } from '../models/entity-bank-accounts.model';
import { EntityCategoryMappingField } from '../models/entity-category-mapping.model';
import { EntityCommunicationElementImage, EntityCommunicationElementImageType } from '../models/entity-communication-element-image';
import { EntityCommunicationElementText } from '../models/entity-communication-element-text';
import { EntityContent, EntityContentCategory } from '../models/entity-content.model';
import { EntityCookiesSettings, EntityCookiesSettingsReduced } from '../models/entity-cookies-settings.model';
import { EntityCustomerTypeRestrictions } from '../models/entity-customer-type-restriction.model';
import { EntityLoyaltyPoints } from '../models/entity-loyalty-points.model';
import { EntitySecurityPasswordConfig, EntitySecuritySettings } from '../models/entity-security-settings.model';
import { EntityTextContent } from '../models/entity-text-content.model';
import { OriginEntityVisibility } from '../models/entity-visibility.model';
import { EntityWhatsappTemplate } from '../models/entity-whatsapp-template.model';
import { ZoneTemplateContent } from '../models/entity-zone-template-content.model';
import { EntityZoneTemplate, GetZoneTemplatesRequest, PutEntityZoneTemplate } from '../models/entity-zone-template.model';
import { GetEntityZoneTemplatesResponse } from '../models/get-entity-zone-templates-response.model';
import { PostEntity } from '../models/post-entity.model';

@Injectable()
export class EntitiesApi extends EntitiesBaseApi {
    readonly #ZONE_TEMPLATES = 'templates-zones';
    readonly #CUSTOMER_DOMAIN_SETTINGS = 'customers-domain-settings';
    readonly #CONTENTS_SEGMENT = 'contents';

    deleteEntity(id: number): Observable<void> {
        return this.http.delete<void>(`${this.ENTITIES_API}/${id}`);
    }

    postEntity(entity: PostEntity): Observable<{ id: number }> {
        return this.http.post<{ id: number }>(this.ENTITIES_API, entity);
    }

    setEntityType(id: number, entityType: EntityType): Observable<void> {
        return this.http.put<void>(`${this.ENTITIES_API}/${id}/types/${entityType}`, null);
    }

    unsetEntityType(id: number, entityType: EntityType): Observable<void> {
        return this.http.delete<void>(`${this.ENTITIES_API}/${id}/types/${entityType}`);
    }

    getEntityCategory(categoryId: number, entityId?: number): Observable<EntityCategory> {
        const params = buildHttpParams({ entity_id: entityId });
        return this.http.get<EntityCategory>(`${this.ENTITY_CATEGORIES_API}/${categoryId}`, { params });
    }

    postEntityCategory(request: Partial<EntityCategory>, entityId?: number): Observable<{ id: number }> {
        const params = buildHttpParams({ entity_id: entityId });
        return this.http.post<{ id: number }>(`${this.ENTITY_CATEGORIES_API}`, request, { params });
    }

    putEntityCategory(categoryId: number, request: Partial<EntityCategory>, entityId?: number): Observable<void> {
        const params = buildHttpParams({ entity_id: entityId });
        return this.http.put<void>(`${this.ENTITY_CATEGORIES_API}/${categoryId}`, request, { params });
    }

    deleteEntityCategory(categoryId: number, entityId?: number): Observable<void> {
        const params = buildHttpParams({ entity_id: entityId });
        return this.http.delete<void>(`${this.ENTITY_CATEGORIES_API}/${categoryId}`, { params });
    }

    getEntityCategoriesMapping(entityId: number): Observable<EntityCategoryMappingField[]> {
        const params = buildHttpParams({ entity_id: entityId });
        return this.http.get<EntityCategoryMappingField[]>(`${this.ENTITY_CATEGORIES_API}/mapping`, { params });
    }

    updateEntityCategoriesMapping(entityId: number, mapping: EntityCategoryMappingField[]): Observable<void> {
        const params = buildHttpParams({ entity_id: entityId });
        return this.http.put<void>(`${this.ENTITY_CATEGORIES_API}/mapping`, mapping, { params });
    }

    getEntityCookies(entityId: number): Observable<EntityCookiesSettings> {
        return this.http.get<EntityCookiesSettings>(`${this.ENTITIES_API}/${entityId}/cookies`);
    }

    putEntityCookies(entityId: number, cookies: EntityCookiesSettingsReduced): Observable<void> {
        return this.http.put<void>(`${this.ENTITIES_API}/${entityId}/cookies`, cookies);
    }

    getEntityTextContents(entityId: number, languageId: string): Observable<EntityTextContent[]> {
        return this.http.get<EntityTextContent[]>(`${this.ENTITIES_API}/${entityId}/text-contents/languages/${languageId}`);
    }

    postEntityTextContents(entityId: number, languageId: string, contents: EntityTextContent[]): Observable<void> {
        return this.http.post<void>(`${this.ENTITIES_API}/${entityId}/text-contents/languages/${languageId}`, contents);
    }

    getEntitySecurity(entityId: number): Observable<EntitySecuritySettings> {
        return this.http.get<EntitySecuritySettings>(`${this.ENTITIES_API}/${entityId}/security-config`);
    }

    putEntitySecurity(entityId: number, config: EntitySecurityPasswordConfig): Observable<void> {
        return this.http.put<void>(`${this.ENTITIES_API}/${entityId}/security-config`, { password_config: config });
    }

    getEntityVisibility(entityId: number): Observable<OriginEntityVisibility> {
        return this.http.get<OriginEntityVisibility>(`${this.ENTITIES_API}/${entityId}/visibility`);
    }

    postEntityVisibility(entityId: number, entityVisibility: OriginEntityVisibility): Observable<void> {
        return this.http.post<void>(`${this.ENTITIES_API}/${entityId}/visibility`, entityVisibility);
    }

    getDonationsProviders(entityId: number): Observable<IdName[]> {
        return this.http.get<IdName[]>(`${this.ENTITIES_API}/${entityId}/donation-providers`);
    }

    getDonationsCampaigns(entityId: number, providerId: number): Observable<DonationCampaign[]> {
        return this.http.get<DonationCampaign[]>(`${this.ENTITIES_API}/${entityId}/donation-providers/${providerId}/campaigns`);
    }

    getWhatsappTemplates(entityId: number): Observable<IdName[]> {
        return this.http.get<EntityWhatsappTemplate[]>(`${this.ENTITIES_API}/${entityId}/whatsapp-templates`);
    }

    getEntityForm(entityId: number, formName: string): Observable<FormsField[][]> {
        return this.http.get<FormsField[][]>(`${this.ENTITIES_API}/${entityId}/forms/${formName}`);
    }

    putEntityForm(entityId: number, formName: string, form: FormsField[][]): Observable<void> {
        return this.http.put<void>(`${this.ENTITIES_API}/${entityId}/forms/${formName}`, form);
    }

    getEntityLoyaltyProgramConfig(entityId: number): Observable<EntityLoyaltyPoints> {
        return this.http.get<EntityLoyaltyPoints>(`${this.ENTITIES_API}/${entityId}/loyalty-program/config`);
    }

    putEntityLoyaltyProgramConfig(entityId: number, form: EntityLoyaltyPoints): Observable<void> {
        return this.http.put<void>(`${this.ENTITIES_API}/${entityId}/loyalty-program/config`, form);
    }

    resetEntityLoyaltyProgram(entityId: number): Observable<void> {
        return this.http.post<void>(`${this.ENTITIES_API}/${entityId}/loyalty-program/reset`, {});
    }

    getEntityCustomerTypesRestrictions(entityId: number): Observable<EntityCustomerTypeRestrictions> {
        return this.http.get<EntityCustomerTypeRestrictions>(`${this.ENTITIES_API}/${entityId}/customer-config`);
    }

    putEntityCustomerTypesRestrictions(entityId: number, customerTypeRestrictions: EntityCustomerTypeRestrictions): Observable<void> {
        return this.http.put<void>(`${this.ENTITIES_API}/${entityId}/customer-config`, customerTypeRestrictions);
    }

    getEntityContents(entityId: number, category: EntityContentCategory, language: string): Observable<EntityContent[]> {
        const params = buildHttpParams({ language });
        return this.http.get<EntityContent[]>(`${this.ENTITIES_API}/${entityId}/contents/${category}`, { params });
    }

    putEntityContents(entityId: number, category: EntityContentCategory, contents: EntityContent[]): Observable<void> {
        return this.http.put<void>(`${this.ENTITIES_API}/${entityId}/contents/${category}`, contents);
    }

    getEntityCommunicationElementImages(entityId: number): Observable<EntityCommunicationElementImage[]> {
        return this.http.get<EntityCommunicationElementImage[]>(`${this.ENTITIES_API}/${entityId}/communication-elements/images`);
    }

    postEntityCommunicationElementImages(entityId: number, contents: EntityCommunicationElementImage[]): Observable<void> {
        return this.http.post<void>(`${this.ENTITIES_API}/${entityId}/communication-elements/images`, contents);
    }

    deleteEntityCommunicationElementImage(entityId: number, language: string, type: EntityCommunicationElementImageType): Observable<void> {
        return this.http.delete<void>(`${this.ENTITIES_API}/${entityId}/communication-elements/images/languages/${language}/types/${type}`);
    }

    getEntityCommunicationElementTexts(entityId: number): Observable<EntityCommunicationElementText[]> {
        return this.http.get<EntityCommunicationElementText[]>(`${this.ENTITIES_API}/${entityId}/communication-elements/texts`);
    }

    postEntityCommunicationElementTexts(entityId: number, contents: EntityCommunicationElementText[]): Observable<void> {
        return this.http.post<void>(`${this.ENTITIES_API}/${entityId}/communication-elements/texts`, contents);
    }

    getEntityBankAccountList(entityId: number): Observable<EntityBankAccount[]> {
        return this.http.get<EntityBankAccount[]>(`${this.ENTITIES_API}/${entityId}/bank-accounts`);
    }

    postEntityBankAccount(entityId: number, bankAccount: PutEntityBankAccount): Observable<void> {
        return this.http.post<void>(`${this.ENTITIES_API}/${entityId}/bank-accounts`, bankAccount);
    }

    getEntityBankAccount(entityId: number, bankAccountId: number): Observable<EntityBankAccount> {
        return this.http.get<EntityBankAccount>(`${this.ENTITIES_API}/${entityId}/bank-accounts/${bankAccountId}`);
    }

    putEntityBankAccount(entityId: number, bankAccountId: number, bankAccount: PutEntityBankAccount): Observable<void> {
        return this.http.put<void>(`${this.ENTITIES_API}/${entityId}/bank-accounts/${bankAccountId}`, bankAccount);
    }

    deleteEntityBankAccount(entityId: number, bankAccountId: number): Observable<void> {
        return this.http.delete<void>(`${this.ENTITIES_API}/${entityId}/bank-accounts/${bankAccountId}`);
    }

    getEntityZoneTemplates(entityId: number, req: GetZoneTemplatesRequest): Observable<GetEntityZoneTemplatesResponse> {
        const params = buildHttpParams({
            sort: req.sort,
            offset: req.offset,
            limit: req.limit,
            q: req.q,
            status: req.status
        });
        return this.http.get<GetEntityZoneTemplatesResponse>(`${this.ENTITIES_API}/${entityId}/${this.#ZONE_TEMPLATES}`, { params });
    }

    postEntityZoneTemplate(entityId: number, template: Partial<EntityZoneTemplate>): Observable<{ id: number }> {
        return this.http.post<{ id: number }>(`${this.ENTITIES_API}/${entityId}/${this.#ZONE_TEMPLATES}`, template);
    }

    getEntityZoneTemplate(entityId: number, templateId: number): Observable<EntityZoneTemplate> {
        return this.http.get<EntityZoneTemplate>(`${this.ENTITIES_API}/${entityId}/${this.#ZONE_TEMPLATES}/${templateId}`);
    }

    deleteEntityZoneTemplate(entityId: number, templateId: number): Observable<void> {
        return this.http.delete<void>(`${this.ENTITIES_API}/${entityId}/${this.#ZONE_TEMPLATES}/${templateId}`);
    }

    putEntityZoneTemplate(entityId: number, templateId: number, params: Partial<PutEntityZoneTemplate>): Observable<void> {
        return this.http.put<void>(`${this.ENTITIES_API}/${entityId}/${this.#ZONE_TEMPLATES}/${templateId}`, params);
    }

    getEntityCustomerDomainSettings(entityId: number): Observable<DomainSettings> {
        return this.http.get<DomainSettings>(`${this.ENTITIES_API}/${entityId}/${this.#CUSTOMER_DOMAIN_SETTINGS}`);
    }

    postEntityCustomerDomainSettings(entityId: number, settings: Partial<DomainSettings>): Observable<void> {
        return this.http.post<void>(`${this.ENTITIES_API}/${entityId}/${this.#CUSTOMER_DOMAIN_SETTINGS}`, settings);
    }

    getZoneTemplateContents(entityId: number, templateId: number, category: string, language?: string): Observable<ZoneTemplateContent[]> {
        const params = language ? buildHttpParams({ language }) : {};
        return this.http.get<ZoneTemplateContent[]>(
            `${this.ENTITIES_API}/${entityId}/${this.#ZONE_TEMPLATES}/${templateId}/${this.#CONTENTS_SEGMENT}/${category}`, { params }
        );
    }

    putZoneTemplateContents(
        entityId: number, templateId: number, category: string, contents: ZoneTemplateContent[], languageId?: string
    ): Observable<void> {
        const params = languageId ? contents.map(content => ({
            ...content,
            language: languageId
        })) : contents;
        return this.http.put<void>(
            `${this.ENTITIES_API}/${entityId}/${this.#ZONE_TEMPLATES}/${templateId}/${this.#CONTENTS_SEGMENT}/${category}`, params);
    }
}
