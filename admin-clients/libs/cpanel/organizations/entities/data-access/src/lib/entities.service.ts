import { getListData, getMetadata, StateManager } from '@OneboxTM/utils-state';
import { FormsField } from '@admin-clients/cpanel/common/utils';
import { IntegrationsApi } from '@admin-clients/cpanel/organizations/data-access';
import { DomainSettings } from '@admin-clients/cpanel/shared/data-access';
import { EntitiesBaseService, EntityCategory, EntityType } from '@admin-clients/shared/common/data-access';
import { inject, Injectable } from '@angular/core';
import { combineLatest, Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';
import { EntitiesApi } from './api/entities.api';
import { PutEntityBankAccount } from './models/entity-bank-accounts.model';
import { EntityCategoryMappingField } from './models/entity-category-mapping.model';
import { EntityCommunicationElementImage, EntityCommunicationElementImageType } from './models/entity-communication-element-image';
import { EntityCommunicationElementText } from './models/entity-communication-element-text';
import { EntityContent, EntityContentCategory } from './models/entity-content.model';
import { EntityCookiesSettingsReduced } from './models/entity-cookies-settings.model';
import { CustomerForms } from './models/entity-customer-forms.model';
import { EntityCustomerTypeRestrictions } from './models/entity-customer-type-restriction.model';
import { EntityLoyaltyPoints } from './models/entity-loyalty-points.model';
import { EntitySecurityPasswordConfig } from './models/entity-security-settings.model';
import { EntityTextContent } from './models/entity-text-content.model';
import { OriginEntityVisibility } from './models/entity-visibility.model';
import { ZoneTemplateContent } from './models/entity-zone-template-content.model';
import { EntityZoneTemplate, GetZoneTemplatesRequest, PutEntityZoneTemplate } from './models/entity-zone-template.model';
import { PostEntity } from './models/post-entity.model';
import { EntitiesState } from './state/entities.state';

@Injectable()
export class EntitiesService extends EntitiesBaseService {
    readonly #integrationsApi = inject(IntegrationsApi);

    // Entity
    readonly entity = Object.freeze({
        create: (entity: PostEntity): Observable<number> =>
            StateManager.inProgress(
                this._entitiesState.entity,
                this._entitiesApi.postEntity(entity).pipe(map(result => result.id))
            ),
        setType: (id: number, setOrUnset: boolean, entityType: EntityType): Observable<void> =>
            StateManager.inProgress(
                this._entitiesState.entity,
                setOrUnset ? this._entitiesApi.setEntityType(id, entityType) : this._entitiesApi.unsetEntityType(id, entityType)
            ),
        delete: (id: number) => this._entitiesApi.deleteEntity(id),
        error$: () => this._entitiesState.entity.getError$()
    });

    // Entity Category
    readonly entityCategory = Object.freeze({
        load: (categoryId: number, entityId?: number): void =>
            StateManager.load(
                this._entitiesState.entityCategory,
                this._entitiesApi.getEntityCategory(categoryId, entityId)
            ),
        create: (request: Partial<EntityCategory>, entityId?: number): Observable<number> =>
            StateManager.inProgress(
                this._entitiesState.entityCategory,
                this._entitiesApi.postEntityCategory(request, entityId).pipe(map(result => result.id))
            ),
        update: (categoryId: number, request: Partial<EntityCategory>, entityId?: number): Observable<void> =>
            StateManager.inProgress(
                this._entitiesState.entityCategory,
                this._entitiesApi.putEntityCategory(categoryId, request, entityId)
            ),
        delete: (categoryId: number, entityId?: number): Observable<void> => this._entitiesApi.deleteEntityCategory(categoryId, entityId),
        get$: () => this._entitiesState.entityCategory.getValue$(),
        clear: () => this._entitiesState.entityCategory.setValue(null),
        inProgress$: () => this._entitiesState.entityCategory.isInProgress$()
    });

    // Entity visibilities
    readonly entityVisibility = Object.freeze({
        load: (id: number): void => StateManager.load(
            this._entitiesState.entityVisibility,
            this._entitiesApi.getEntityVisibility(id)
        ),
        update: (id: number, visibility: OriginEntityVisibility): Observable<void> =>
            StateManager.inProgress(
                this._entitiesState.entityVisibility,
                this._entitiesApi.postEntityVisibility(id, visibility)
            ),
        get$: () => this._entitiesState.entityVisibility.getValue$(),
        error$: () => this._entitiesState.entityVisibility.getError$(),
        inProgress$: () => this._entitiesState.entityVisibility.isInProgress$(),
        clear: () => this._entitiesState.entityVisibility.setValue(null)
    });

    // Entity cookies
    readonly entityCookies = Object.freeze({
        load: (id: number): void => StateManager.load(
            this._entitiesState.entityCookies,
            this._entitiesApi.getEntityCookies(id)
        ),
        update: (id: number, cookies: EntityCookiesSettingsReduced): Observable<void> =>
            StateManager.inProgress(
                this._entitiesState.entityCookies,
                this._entitiesApi.putEntityCookies(id, cookies)
            ),
        getEntityCookies$: () => this._entitiesState.entityCookies.getValue$(),
        error$: () => this._entitiesState.entityCookies.getError$(),
        inProgress$: () => this._entitiesState.entityCookies.isInProgress$(),
        clear: () => this._entitiesState.entityCookies.setValue(null)
    });

    // Entity text contents
    readonly entityTextContents = Object.freeze({
        load: (id: number, languageId: string): void => StateManager.load(
            this._entitiesState.entityTextContents,
            this._entitiesApi.getEntityTextContents(id, languageId).pipe(
                map(contents => contents?.sort((a, b) => a.key.localeCompare(b.key))))
        ),
        update: (id: number, languageId: string, entityContents: EntityTextContent[]): Observable<void> =>
            StateManager.inProgress(
                this._entitiesState.entityTextContents,
                this._entitiesApi.postEntityTextContents(id, languageId, entityContents)
            ),
        get$: () => this._entitiesState.entityTextContents.getValue$(),
        error$: () => this._entitiesState.entityTextContents.getError$(),
        inProgress$: () => this._entitiesState.entityTextContents.isInProgress$(),
        clear: () => this._entitiesState.entityTextContents.setValue(null)
    });

    // Entity security config
    readonly entitySecurity = Object.freeze({
        load: (id: number): void => StateManager.load(
            this._entitiesState.entitySecurity,
            this._entitiesApi.getEntitySecurity(id)
        ),
        update: (id: number, security: EntitySecurityPasswordConfig): Observable<void> =>
            StateManager.inProgress(
                this._entitiesState.entitySecurity,
                this._entitiesApi.putEntitySecurity(id, security)
            ),
        getEntitySecurity$: () => this._entitiesState.entitySecurity.getValue$(),
        error$: () => this._entitiesState.entitySecurity.getError$(),
        inProgress$: () => this._entitiesState.entitySecurity.isInProgress$(),
        clear: () => this._entitiesState.entitySecurity.setValue(null)
    });

    // Entity Loyalty Points
    readonly entityLoyaltyProgram = Object.freeze({
        load: (entityId: number): void => StateManager.load(
            this._entitiesState.entityLoyaltyProgram,
            this._entitiesApi.getEntityLoyaltyProgramConfig(entityId)
        ),
        update: (entityId: number, form: EntityLoyaltyPoints): Observable<void> =>
            StateManager.inProgress(
                this._entitiesState.entityLoyaltyProgram,
                this._entitiesApi.putEntityLoyaltyProgramConfig(entityId, form)
            ),
        reset: (entityId: number): Observable<void> =>
            StateManager.inProgress(
                this._entitiesState.resetEntityLoyaltyPoints,
                this._entitiesApi.resetEntityLoyaltyProgram(entityId)
            ),
        resetInProgress$: () => this._entitiesState.resetEntityLoyaltyPoints.isInProgress$(),
        get$: () => this._entitiesState.entityLoyaltyProgram.getValue$(),
        error$: () => this._entitiesState.entityLoyaltyProgram.getError$(),
        inProgress$: () => this._entitiesState.entityLoyaltyProgram.isInProgress$(),
        clear: () => this._entitiesState.entityLoyaltyProgram.setValue(null)
    });

    // Entity Custom Types Restrictions
    readonly entityCustomerTypesRestrictions = Object.freeze({
        load: (entityId: number): void => StateManager.load(
            this._entitiesState.entityCustomTypesRestrictions,
            this._entitiesApi.getEntityCustomerTypesRestrictions(entityId)
        ),
        save: (entityId: number, form: EntityCustomerTypeRestrictions): Observable<void> =>
            StateManager.inProgress(
                this._entitiesState.entityCustomTypesRestrictions,
                this._entitiesApi.putEntityCustomerTypesRestrictions(entityId, form)
            ),
        get$: () => this._entitiesState.entityCustomTypesRestrictions.getValue$(),
        error$: () => this._entitiesState.entityCustomTypesRestrictions.getError$(),
        inProgress$: () => this._entitiesState.entityCustomTypesRestrictions.isInProgress$(),
        clear: () => this._entitiesState.entityCustomTypesRestrictions.setValue(null)
    });

    // Donations providers
    readonly donationsProviders = Object.freeze({
        load: (entityId: number): void => StateManager.load(
            this._entitiesState.donationsProviders,
            this._entitiesApi.getDonationsProviders(entityId)
        ),
        get$: () => this._entitiesState.donationsProviders.getValue$(),
        error$: () => this._entitiesState.donationsProviders.getError$(),
        inProgress$: () => this._entitiesState.donationsProviders.isInProgress$(),
        clear: () => this._entitiesState.donationsProviders.setValue(null)
    });

    // Donations campaigns
    readonly donationsCampaigns = Object.freeze({
        load: (entityId: number, providerId: number): void => StateManager.load(
            this._entitiesState.donationsCampaigns,
            this._entitiesApi.getDonationsCampaigns(entityId, providerId)
        ),
        get$: () => this._entitiesState.donationsCampaigns.getValue$(),
        error$: () => this._entitiesState.donationsCampaigns.getError$(),
        inProgress$: () => this._entitiesState.donationsCampaigns.isInProgress$(),
        clear: () => this._entitiesState.donationsCampaigns.setValue(null)
    });

    // Whatsapp templates
    readonly whatsappTemplates = Object.freeze({
        load: (entityId: number): void => StateManager.load(
            this._entitiesState.whatsappTemplates,
            this._entitiesApi.getWhatsappTemplates(entityId)
        ),
        get$: () => this._entitiesState.whatsappTemplates.getValue$(),
        error$: () => this._entitiesState.whatsappTemplates.getError$(),
        inProgress$: () => this._entitiesState.whatsappTemplates.isInProgress$(),
        clear: () => this._entitiesState.whatsappTemplates.setValue(null)
    });

    // Admincustomer form
    readonly customerAdminForm = Object.freeze({
        load: (entityId: number): void => StateManager.load(
            this._entitiesState.customerAdminForm,
            combineLatest([
                this._entitiesApi.getEntityForm(entityId, 'admincustomer-sign-in'),
                this._entitiesApi.getEntityForm(entityId, 'admincustomer')
            ]).pipe(
                map(([signInForms, profileForms]) => ({
                    signIn: signInForms,
                    profile: profileForms
                }))
            )
        ),
        update: (entityId: number, customerForms: CustomerForms): Observable<void> =>
            StateManager.inProgress(
                this._entitiesState.customerAdminForm,
                combineLatest([
                    this._entitiesApi.putEntityForm(entityId, 'admincustomer-sign-in', customerForms.signIn),
                    this._entitiesApi.putEntityForm(entityId, 'admincustomer', customerForms.profile)
                ]).pipe(map(() => void 0))
            ),
        get$: () => this._entitiesState.customerAdminForm.getValue$(),
        error$: () => this._entitiesState.customerAdminForm.getError$(),
        inProgress$: () => this._entitiesState.customerAdminForm.isInProgress$(),
        clear: () => this._entitiesState.customerAdminForm.setValue(null)
    });

    // Customer form
    readonly customerUserForm = Object.freeze({
        load: (entityId: number): void => StateManager.load(
            this._entitiesState.customerUserForm,
            combineLatest([
                this._entitiesApi.getEntityForm(entityId, 'customer-sign-in'),
                this._entitiesApi.getEntityForm(entityId, 'customer')
            ]).pipe(
                map(([signInForms, profileForms]) => ({
                    signIn: signInForms,
                    profile: profileForms
                }))
            )
        ),
        update: (entityId: number, customerForms: CustomerForms): Observable<void> =>
            StateManager.inProgress(
                this._entitiesState.customerUserForm,
                combineLatest([
                    this._entitiesApi.putEntityForm(entityId, 'customer-sign-in', customerForms.signIn),
                    this._entitiesApi.putEntityForm(entityId, 'customer', customerForms.profile)
                ]).pipe(map(() => void 0))
            ),
        get$: () => this._entitiesState.customerUserForm.getValue$(),
        error$: () => this._entitiesState.customerUserForm.getError$(),
        inProgress$: () => this._entitiesState.customerUserForm.isInProgress$(),
        clear: () => this._entitiesState.customerUserForm.setValue(null)
    });

    // Entity form
    readonly payoutForm = Object.freeze({
        load: (entityId: number): void => StateManager.load(
            this._entitiesState.payoutForm,
            this._entitiesApi.getEntityForm(entityId, 'payout')
        ),
        update: (entityId: number, form: FormsField[][]): Observable<void> =>
            StateManager.inProgress(
                this._entitiesState.payoutForm,
                this._entitiesApi.putEntityForm(entityId, 'payout', form)
            ),
        get$: () => this._entitiesState.payoutForm.getValue$(),
        error$: () => this._entitiesState.payoutForm.getError$(),
        inProgress$: () => this._entitiesState.payoutForm.isInProgress$().pipe(map(value => value ?? true)),
        clear: () => this._entitiesState.payoutForm.setValue(null)
    });

    // Entity Categories Mapping
    readonly entityCategoriesMapping = Object.freeze({
        load: (entityId: number) =>
            StateManager.load(
                this._entitiesState.entityCategoriesMapping,
                this._entitiesApi.getEntityCategoriesMapping(entityId)
            ),
        update: (entityId: number, mapping: EntityCategoryMappingField[]) =>
            StateManager.inProgress(
                this._entitiesState.entityCategoriesMapping,
                this._entitiesApi.updateEntityCategoriesMapping(entityId, mapping)
            ),
        get$: () => this._entitiesState.entityCategoriesMapping.getValue$(),
        loading$: () => this._entitiesState.entityCategoriesMapping.isInProgress$()
    });

    // Auth Vendors
    readonly authVendors = Object.freeze({
        load: () => StateManager.load(
            this._entitiesState.authVendors,
            this.#integrationsApi.getAuthVendors()
        ),
        get$: () => this._entitiesState.authVendors.getValue$(),
        loading$: () => this._entitiesState.authVendors.isInProgress$(),
        clear: () => this._entitiesState.authVendors.setValue(null)
    });

    readonly authVendor = Object.freeze({
        load: (vendorId: string) => StateManager.load(
            this._entitiesState.authVendor,
            this.#integrationsApi.getAuthVendor(vendorId)
        ),
        get$: () => this._entitiesState.authVendor.getValue$(),
        loading$: () => this._entitiesState.authVendor.isInProgress$(),
        clear: () => this._entitiesState.authVendor.setValue(null)
    });

    // Barcode Formats
    readonly barcodeFormats = Object.freeze({
        load: () => StateManager.load(
            this._entitiesState.barcodeFormats,
            this.#integrationsApi.getBarcodeFormats()
        ),
        get$: () => this._entitiesState.barcodeFormats.getValue$(),
        loading$: () => this._entitiesState.barcodeFormats.isInProgress$(),
        clear: () => this._entitiesState.barcodeFormats.setValue(null)
    });

    // Entity contents
    readonly entityContents = Object.freeze({
        load: (id: number, category: EntityContentCategory, language: string): void => StateManager.load(
            this._entitiesState.entityContents,
            this._entitiesApi.getEntityContents(id, category, language)
        ),
        update: (id: number, category: EntityContentCategory, entityContents: EntityContent[]): Observable<void> =>
            StateManager.inProgress(
                this._entitiesState.entityContents,
                this._entitiesApi.putEntityContents(id, category, entityContents)
            ),
        get$: () => this._entitiesState.entityContents.getValue$(),
        error$: () => this._entitiesState.entityContents.getError$(),
        inProgress$: () => this._entitiesState.entityContents.isInProgress$(),
        clear: () => this._entitiesState.entityContents.setValue(null)
    });

    readonly entityCommunicationElementImages = Object.freeze({
        load: (entityId: number): void =>
            StateManager.load(
                this._entitiesState.entityCommunicationElementImages,
                this._entitiesApi.getEntityCommunicationElementImages(entityId)
            ),
        create$: (entityId: number, images: EntityCommunicationElementImage[]): Observable<void> =>
            StateManager.inProgress(
                this._entitiesState.entityCommunicationElementImages,
                this._entitiesApi.postEntityCommunicationElementImages(entityId, images)
            ),
        delete$: (entityId: number, language: string, type: EntityCommunicationElementImageType): Observable<void> =>
            StateManager.inProgress(
                this._entitiesState.entityCommunicationElementImages,
                this._entitiesApi.deleteEntityCommunicationElementImage(entityId, language, type)
            ),
        get$: () => this._entitiesState.entityCommunicationElementImages.getValue$(),
        error$: () => this._entitiesState.entityCommunicationElementImages.getError$(),
        inProgress$: () => this._entitiesState.entityCommunicationElementImages.isInProgress$(),
        clear: () => this._entitiesState.entityCommunicationElementImages.setValue(null)
    });

    readonly entityCommunicationElementTexts = Object.freeze({
        load: (entityId: number): void =>
            StateManager.load(
                this._entitiesState.entityCommunicationElementTexts,
                this._entitiesApi.getEntityCommunicationElementTexts(entityId)
            ),
        create$: (entityId: number, texts: EntityCommunicationElementText[]): Observable<void> =>
            StateManager.inProgress(
                this._entitiesState.entityCommunicationElementTexts,
                this._entitiesApi.postEntityCommunicationElementTexts(entityId, texts)
            ),
        get$: () => this._entitiesState.entityCommunicationElementTexts.getValue$(),
        error$: () => this._entitiesState.entityCommunicationElementTexts.getError$(),
        inProgress$: () => this._entitiesState.entityCommunicationElementTexts.isInProgress$(),
        clear: () => this._entitiesState.entityCommunicationElementTexts.setValue(null)
    });

    readonly entityBankAccountList = Object.freeze({
        load: (entityId: number): void => StateManager.load(
            this._entitiesState.entityBankAccountList,
            this._entitiesApi.getEntityBankAccountList(entityId)
        ),
        get$: () => this._entitiesState.entityBankAccountList.getValue$(),
        error$: () => this._entitiesState.entityBankAccountList.getError$(),
        inProgress$: () => this._entitiesState.entityBankAccountList.isInProgress$(),
        clear: () => this._entitiesState.entityBankAccountList.setValue(null),
        create: (entityId: number, bankAccount: PutEntityBankAccount): Observable<void> =>
            StateManager.inProgress(
                this._entitiesState.entityBankAccountList,
                this._entitiesApi.postEntityBankAccount(entityId, bankAccount)
            )
    });

    readonly entityBankAccount = Object.freeze({
        load: (entityId: number, bankAccountId: number): void => StateManager.load(
            this._entitiesState.entityBankAccount,
            this._entitiesApi.getEntityBankAccount(entityId, bankAccountId)
        ),
        update: (entityId: number, bankAccountId: number, bankAccount: PutEntityBankAccount): Observable<void> =>
            StateManager.inProgress(
                this._entitiesState.entityBankAccount,
                this._entitiesApi.putEntityBankAccount(entityId, bankAccountId, bankAccount)
            ),
        delete: (entityId: number, bankAccountId: number): Observable<void> =>
            StateManager.inProgress(
                this._entitiesState.entityBankAccount,
                this._entitiesApi.deleteEntityBankAccount(entityId, bankAccountId)
            ),
        get$: () => this._entitiesState.entityBankAccount.getValue$(),
        error$: () => this._entitiesState.entityBankAccount.getError$(),
        inProgress$: () => this._entitiesState.entityBankAccount.isInProgress$(),
        clear: () => this._entitiesState.entityBankAccount.setValue(null)
    });

    readonly zoneTemplates = Object.freeze({
        load: (entityId: number, req: GetZoneTemplatesRequest) => StateManager.load(
            this._entitiesState.zoneTemplates,
            this._entitiesApi.getEntityZoneTemplates(entityId, req)
        ),
        loadMore: (entityId: number, req: GetZoneTemplatesRequest) =>
            StateManager.loadMore(req, this._entitiesState.zoneTemplates, r => this._entitiesApi.getEntityZoneTemplates(entityId, r)),
        post: (entityId: number, zoneTemplate: Partial<EntityZoneTemplate>) => StateManager.inProgress(
            this._entitiesState.zoneTemplates,
            this._entitiesApi.postEntityZoneTemplate(entityId, zoneTemplate)
        ),
        getData$: () => this._entitiesState.zoneTemplates.getValue$().pipe(getListData()),
        getMetadata$: () => this._entitiesState.zoneTemplates.getValue$().pipe(getMetadata()),
        loading$: () => this._entitiesState.zoneTemplates.isInProgress$(),
        clear: () => this._entitiesState.zoneTemplates.setValue(null),
        error$: () => this._entitiesState.zoneTemplates.getError$()
    });

    readonly zoneTemplate = Object.freeze({
        load: (entityId: number, zoneTemplateId: number): void => StateManager.load(
            this._entitiesState.zoneTemplate,
            this._entitiesApi.getEntityZoneTemplate(entityId, zoneTemplateId)
        ),
        delete: (entityId: number, zoneTemplateId: number) =>
            StateManager.inProgress(
                this._entitiesState.zoneTemplate,
                this._entitiesApi.deleteEntityZoneTemplate(entityId, zoneTemplateId)
            ),
        get$: () => this._entitiesState.zoneTemplate.getValue$(),
        error$: () => this._entitiesState.zoneTemplate.getError$(),
        loading$: () => this._entitiesState.zoneTemplate.isInProgress$(),
        update: (entityId: number, zoneTemplateId: number, params: Partial<PutEntityZoneTemplate>) => StateManager.inProgress(
            this._entitiesState.zoneTemplate,
            this._entitiesApi.putEntityZoneTemplate(entityId, zoneTemplateId, params)
        ),
        clear: () => this._entitiesState.zoneTemplate.setValue(null),
        contents: Object.freeze({
            load: (entityId: number, templateId: number, category: string, lang?: string) =>
                StateManager.load(
                    this._entitiesState.zoneTemplateContents,
                    this._entitiesApi.getZoneTemplateContents(entityId, templateId, category, lang)
                ),
            get$: () => this._entitiesState.zoneTemplateContents.getValue$(),
            inProgress$: () => this._entitiesState.zoneTemplateContents.isInProgress$(),
            update: (entityId: number, templateId: number, category: string, contents: ZoneTemplateContent[], lang?: string) =>
                StateManager.inProgress(
                    this._entitiesState.zoneTemplateContents,
                    this._entitiesApi.putZoneTemplateContents(entityId, templateId, category, contents, lang)
                ),
            clear: () => this._entitiesState.zoneTemplateContents.setValue(null),
            error$: () => this._entitiesState.zoneTemplateContents.getError$()
        })
    });

    readonly customerDomainSettings = Object.freeze({
        load: (entityId: number): void => StateManager.load(
            this._entitiesState.customerDomainSettings,
            this._entitiesApi.getEntityCustomerDomainSettings(entityId)
        ),
        upsert: (entityId: number, settings: Partial<DomainSettings>): Observable<void> =>
            StateManager.inProgress(
                this._entitiesState.customerDomainSettings,
                this._entitiesApi.postEntityCustomerDomainSettings(entityId, settings)
            ),
        get$: () => this._entitiesState.customerDomainSettings.getValue$(),
        error$: () => this._entitiesState.customerDomainSettings.getError$(),
        loading$: () => this._entitiesState.customerDomainSettings.isInProgress$(),
        clear: () => this._entitiesState.customerDomainSettings.setValue(null)
    });

    constructor(
        private _entitiesApi: EntitiesApi,
        private _entitiesState: EntitiesState
    ) {
        super(_entitiesApi, _entitiesState);
    }

    clearEntityTaxes(): void {
        this._entitiesState.entityTaxes.setValue(null);
    }

    loadEntityCategoriesMapping(entityId: number): void {
        this._entitiesState.entityCategoriesMapping.setInProgress(true);
        this._entitiesApi.getEntityCategoriesMapping(entityId)
            .pipe(finalize(() => this._entitiesState.entityCategoriesMapping.setInProgress(false)))
            .subscribe(entityCategoriesMapping => this._entitiesState.entityCategoriesMapping.setValue(entityCategoriesMapping));
    }
}
