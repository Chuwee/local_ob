import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { CategoriesService } from '@admin-clients/cpanel/organizations/data-access';
import { ExternalEntityService } from '@admin-clients/cpanel/organizations/entities/feature';
import { ExternalProviderEvents } from '@admin-clients/cpanel/promoters/data-access';
import { ProducersService } from '@admin-clients/cpanel/promoters/producers/data-access';
import {
    PostSeasonTicket, SeasonTicketFieldsRestrictions, SeasonTicketsService
} from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    EntitiesBaseService, EntitiesBaseState, Entity, EntitiesBaseApi, EntityCategory, ExternalInventoryProviders
} from '@admin-clients/shared/common/data-access';
import { DateTimePickerComponent, DialogSize, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { ObFormFieldLabelDirective } from '@admin-clients/shared/common/ui/ob-material';
import { VenueAccessControlSystems } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { LocalCurrenciesFullTranslation$Pipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge, dateIsAfter, dateTimeValidator } from '@admin-clients/shared/utility/utils';
import {
    VenueTemplate,
    VenueTemplateScope, venueTemplatesProviders, VenueTemplatesService, VenueTemplateStatus, VenueTemplateType
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnDestroy, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatOption } from '@angular/material/core';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSelect } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest } from 'rxjs';
import { filter, first, map, shareReplay, startWith, switchMap, tap } from 'rxjs/operators';
import { NewSeasonTicketSgaFieldsComponent, SgaStFieldsForm } from './sga-fields/new-st-sga-fields.component';

@Component({
    selector: 'app-new-season-tickets-dialog',
    templateUrl: './new-season-tickets-dialog.component.html',
    styleUrls: ['./new-season-tickets-dialog.component.scss'],
    providers: [
        EntitiesBaseState,
        EntitiesBaseApi,
        EntitiesBaseService,
        TranslatePipe,
        venueTemplatesProviders
    ],
    imports: [
        AsyncPipe, ReactiveFormsModule, TranslatePipe, SelectSearchComponent, MatError,
        FormControlErrorsComponent, LocalCurrenciesFullTranslation$Pipe, EllipsifyDirective,
        MatIcon, MatFormField, MatInput, MatSelect, MatOption, MatButton, MatDialogModule, MatLabel,
        MatTooltip, MatProgressSpinner, NewSeasonTicketSgaFieldsComponent, MatIconButton, ObFormFieldLabelDirective,
        DateTimePickerComponent
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class NewSeasonTicketsDialogComponent implements OnInit, OnDestroy {
    readonly #dialogRef = inject(MatDialogRef<NewSeasonTicketsDialogComponent>);
    readonly #authSrv = inject(AuthenticationService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #producersSrv = inject(ProducersService);
    readonly #categorySrv = inject(CategoriesService);
    readonly #venuesTemplateSrv = inject(VenueTemplatesService);
    readonly #seasonTicketsSrv = inject(SeasonTicketsService);
    readonly #fb = inject(FormBuilder);
    readonly #destroyRef = inject(DestroyRef);
    readonly #externalSrv = inject(ExternalEntityService);
    readonly #translatePipe = inject(TranslatePipe);
    readonly form = this.#fb.group({
        entityId: [null as number, Validators.required],
        producerId: [{ value: null as number, disabled: true }, Validators.required],
        name: [
            null as string,
            [
                Validators.required,
                Validators.maxLength(SeasonTicketFieldsRestrictions.seasonTicketNameLength),
                Validators.pattern(SeasonTicketFieldsRestrictions.seasonTicketNamePattern)
            ]
        ],
        categoryId: [null as number, Validators.required],
        currencyCode: [{ value: null as string, disabled: true }, Validators.required],
        taxId: [null as number, Validators.required],
        chargesTaxId: [null as number, Validators.required],
        venueConfigId: [{ value: null as number, disabled: true }, Validators.required],
        inventory: ['internal' as 'internal' | ExternalInventoryProviders],
        additionalConfig: this.#fb.group<SgaStFieldsForm>({
            template: this.#fb.control<VenueTemplate>(null as VenueTemplate, Validators.required),
            event: this.#fb.control<ExternalProviderEvents>(null as ExternalProviderEvents, Validators.required)
        }),
        fortressConfig: this.#fb.group({
            customCategoryId: [null as number],
            startDate: [null as string],
            endDate: [null as string]
        })
    });

    readonly currencies$ = this.#authSrv.getLoggedUser$()
        .pipe(first(), map(AuthenticationService.operatorCurrencies));

    readonly canSelectEntity$ = this.#authSrv.canReadMultipleEntities$();
    readonly entities$ = combineLatest([
        this.#authSrv.getLoggedUser$().pipe(first(Boolean)),
        this.canSelectEntity$
    ]).pipe(
        switchMap(([user, canSelectEntity]) => {
            if (canSelectEntity) {
                this.#entitiesSrv.entityList.load({
                    limit: 999,
                    sort: 'name:asc',
                    type: 'EVENT_ENTITY'
                });
                return this.#entitiesSrv.entityList.getData$();
            } else {
                this.#entitiesSrv.loadEntity(user.entity.id);
                return this.#entitiesSrv.getEntity$().pipe(
                    first(Boolean),
                    map(entity => [entity])
                );
            }
        }),
        tap((entities: Entity[]) => {
            if (entities && entities.length === 1) {
                this.form.patchValue({ entityId: entities[0].id });
            }
        }),
        takeUntilDestroyed(this.#destroyRef),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    readonly producers$ = this.#producersSrv.getProducersListData$();
    readonly categories$ = this.#categorySrv.getCategories$().pipe(
        map(categories => categories?.map(cat => ({
            ...cat,
            description: this.#translatePipe.transform(`ENTITY.CATEGORY_OPTS.${cat.code}`)
        })))
    );

    readonly entityCategories$ = this.#entitiesSrv.getEntityCategories$().pipe(
        first(Boolean),
        map(categories =>
            categories
                .reduce<EntityCategory[]>((result, currentCategory, _, categoriesArray) => {
                    if (currentCategory.parent_id) {
                        const parentCategory = categoriesArray.find(
                            parentCategory => parentCategory.id === currentCategory.parent_id
                        );
                        if (parentCategory) {
                            currentCategory.description =
                                parentCategory.description + ' - ' + currentCategory.description;
                        }
                    }
                    if (
                        currentCategory.parent_id ||
                        categoriesArray.every(category => category.parent_id !== currentCategory.id)
                    ) {
                        result.push(currentCategory);
                    }
                    return result;
                }, [])
                .sort((a, b) => (a.description > b.description ? 1 : b.description > a.description ? -1 : 0))
        ),
        shareReplay(1)
    );

    readonly entityTaxes$ = this.#entitiesSrv.getEntityTaxes$();
    readonly venueTemplates$ = this.#venuesTemplateSrv.getVenueTemplatesList$().pipe(map(value => value?.data));
    readonly isLoading$ = booleanOrMerge([
        this.#seasonTicketsSrv.seasonTicket.inProgress$(),
        this.#externalSrv.inventoryProviders.loading$()
    ]).pipe(shareReplay({ bufferSize: 1, refCount: true }));

    readonly $inventoryProviders = toSignal(this.#externalSrv.inventoryProviders.get$().pipe(
        filter(Boolean),
        map(inv => inv?.inventory_providers)
    ));

    readonly $hasFortress = toSignal(
        combineLatest([
            this.venueTemplates$,
            this.form.controls.venueConfigId.valueChanges.pipe(startWith(null))
        ]).pipe(
            map(([templates, venueId]) => {
                if (!venueId || !templates) return false;
                const selectedTemplate = templates.find(t => t.id === venueId);
                return selectedTemplate?.venue?.access_control_systems?.some(
                    system => system?.name === VenueAccessControlSystems.fortressBRISTOL) || false;
            }),
            tap(hasFortress => {
                const fortressGroup = this.form.controls.fortressConfig;

                if (hasFortress) {
                    fortressGroup.enable({ emitEvent: false });
                    fortressGroup.controls.customCategoryId.setValidators(Validators.required);
                    fortressGroup.controls.startDate.setValidators(Validators.required);
                    fortressGroup.controls.endDate.setValidators([
                        Validators.required,
                        dateTimeValidator(dateIsAfter, 'endDateBeforeStartDate', fortressGroup.controls.startDate)
                    ]);
                } else {
                    fortressGroup.disable({ emitEvent: false });
                    fortressGroup.reset(null, { emitEvent: false });
                }

                fortressGroup.updateValueAndValidity({ emitEvent: false });
            })
        ),
        { initialValue: false }
    );

    ngOnInit(): void {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;
        this.#externalSrv.inventoryProviders.clear();

        this.form.controls.entityId.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(entity => {
                if (entity) {
                    this.form.controls.producerId.reset({ value: null, disabled: false });
                    this.form.controls.inventory.reset('internal');
                    this.#producersSrv.loadProducersList(999, 0, 'name:asc', '', null, entity);
                    this.#entitiesSrv.loadEntityTaxes(entity);
                    this.#entitiesSrv.loadEntityCategories(entity);
                    this.#externalSrv.inventoryProviders.reload(entity);

                    this.form.controls.venueConfigId.reset({ value: null, disabled: false });
                    const req = {
                        limit: 999,
                        offset: 0,
                        sort: 'name:asc',
                        entityId: entity,
                        status: [VenueTemplateStatus.active],
                        scope: VenueTemplateScope.standard,
                        type: VenueTemplateType.normal,
                        graphic: true
                    };
                    this.#venuesTemplateSrv.loadVenueTemplatesList(req);
                }
            });

        this.form.controls.inventory.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(inventory => {
                if (inventory === ExternalInventoryProviders.sga) {
                    this.form.controls.venueConfigId.disable({ emitEvent: false });
                    this.form.controls.additionalConfig.enable({ emitEvent: false });
                } else {
                    this.form.controls.additionalConfig.disable({ emitEvent: false });
                    this.form.controls.venueConfigId.enable({ emitEvent: false });
                }
            });

        this.form.controls.fortressConfig.controls.startDate.valueChanges
            .pipe(
                takeUntilDestroyed(this.#destroyRef),
                filter(() => this.form.controls.fortressConfig.enabled))
            .subscribe(() => {
                this.form.controls.fortressConfig.controls.endDate.updateValueAndValidity();
            });

        this.#categorySrv.loadCategories();
        this.entities$.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe();

        this.currencies$
            .pipe(first())
            .subscribe(currencies => {
                if (currencies?.length > 1) {
                    this.form.controls.currencyCode.enable({ emitEvent: false });
                } else {
                    this.form.controls.currencyCode.disable({ emitEvent: false });
                }
                this.form.controls.currencyCode.updateValueAndValidity();
            });

        this.producers$
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(producers => {
                if (producers?.length === 1) {
                    this.form.patchValue({ producerId: producers[0].id });
                }
            });
    }

    ngOnDestroy(): void {
        this.#producersSrv.clearProducersList();
        this.#entitiesSrv.clearEntityCategories();
        this.#categorySrv.clearCategories();
    }

    createSeasonTicket(): void {
        if (this.form.valid) {
            this.#authSrv.getLoggedUser$()
                .pipe(first())
                .subscribe(user => {
                    const postSeasonTicket: PostSeasonTicket = {
                        name: this.form.value.name,
                        entityId: this.form.value.entityId,
                        producerId: this.form.value.producerId,
                        categoryId: this.form.value.categoryId,
                        taxId: this.form.value.taxId,
                        chargesTaxId: this.form.value.chargesTaxId,
                        venueConfigId: this.form.value.venueConfigId,
                        currencyCode: this.form.value.currencyCode
                    };
                    const currencies = AuthenticationService.operatorCurrencyCodes(user);
                    //TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
                    if (currencies?.length > 1) {
                        postSeasonTicket.currencyCode = this.form.value.currencyCode;
                    } else {
                        postSeasonTicket.currencyCode = currencies?.length === 1 ? currencies[0] : user.currency;
                    }
                    if (this.form.value.inventory === ExternalInventoryProviders.sga) {
                        const sgaFields = this.form.controls.additionalConfig.value;
                        postSeasonTicket.additionalConfig = {
                            inventory_provider: ExternalInventoryProviders.sga,
                            venue_template_id: sgaFields.template.id,
                            external_event_id: sgaFields.event.id
                        };
                        // if SGA the venueConfigId control is not displayed,
                        // because it's the same as the venue_template_id
                        // so we need to set the value manually
                        postSeasonTicket.venueConfigId = sgaFields.template.id;
                    }
                    if (this.form.controls.fortressConfig.enabled) {
                        const fortressConfig = this.form.value.fortressConfig;
                        postSeasonTicket.customCategoryId = fortressConfig.customCategoryId;
                        postSeasonTicket.startDate = fortressConfig.startDate;
                        postSeasonTicket.endDate = fortressConfig.endDate;
                    }

                    this.#seasonTicketsSrv.seasonTicket.create(postSeasonTicket)
                        .subscribe(({ id }) => this.close(id));
                });
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
        }
    }

    close(seasonTicketId: number = null): void {
        this.#dialogRef.close(seasonTicketId);
    }
}
