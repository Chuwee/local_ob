import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { CategoriesService } from '@admin-clients/cpanel/organizations/data-access';
import { ExternalEntityService } from '@admin-clients/cpanel/organizations/entities/feature';
import { PromotersExternalProviderService } from '@admin-clients/cpanel/promoters/data-access';
import { EventFieldsRestriction, EventsService, PostEvent } from '@admin-clients/cpanel/promoters/events/data-access';
import { ProducersService } from '@admin-clients/cpanel/promoters/producers/data-access';
import {
    Entity, Category, EntitiesBaseService, EntitiesFilterFields, EventType,
    EntitiesBaseState, EntitiesBaseApi,
    ExternalInventoryProviders
} from '@admin-clients/shared/common/data-access';
import { DialogSize, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { LocalCurrenciesFullTranslation$Pipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import {
    StdVenueTplsApi, StdVenueTplService, StdVenueTplsState
} from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { venueTemplatesProviders, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { Currency } from '@admin-clients/shared-utility-models';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, ElementRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest } from 'rxjs';
import { distinctUntilChanged, filter, first, map, shareReplay, switchMap, tap } from 'rxjs/operators';
import { AvetFieldsForm, NewEventAvetFieldsComponent } from './avet-fields/new-event-avet-fields.component';
import { NewEventSgaFieldsComponent, SgaFieldsForm } from './sga-fields/new-event-sga-fields.component';

@Component({
    selector: 'app-new-event-dialog',
    templateUrl: './new-event-dialog.component.html',
    styleUrls: ['./new-event-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe, ReactiveFormsModule, NewEventAvetFieldsComponent, SelectSearchComponent, FormControlErrorsComponent,
        TranslatePipe, LocalCurrenciesFullTranslation$Pipe, NewEventSgaFieldsComponent, MatIconButton, MatDialogTitle,
        MatDialogContent, MatDialogActions, MatFormFieldModule, MatSelectModule, MatProgressSpinner, MatIcon, MatButton,
        MatInputModule

    ],
    providers: [
        EntitiesBaseState,
        EntitiesBaseApi,
        EntitiesBaseService,
        StdVenueTplsState,
        StdVenueTplsApi,
        StdVenueTplService,
        TranslatePipe,
        venueTemplatesProviders
    ]
})
export class NewEventDialogComponent implements OnInit {
    readonly #dialogRef = inject(MatDialogRef<NewEventDialogComponent>);
    readonly #authSrv = inject(AuthenticationService);
    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #producersService = inject(ProducersService);
    readonly #categoryService = inject(CategoriesService);
    readonly #eventsService = inject(EventsService);
    readonly #venueTemplatesService = inject(VenueTemplatesService);
    readonly #stdVenueTplSrv = inject(StdVenueTplService);
    readonly #fb = inject(FormBuilder);
    readonly #elemRef = inject(ElementRef);
    readonly #destroyRef = inject(DestroyRef);
    readonly #externalSrv = inject(ExternalEntityService);
    readonly #promotersExternalSrv = inject(PromotersExternalProviderService);
    readonly #translatePipe = inject(TranslatePipe);

    readonly canSelectEntity$ = this.#authSrv.canReadMultipleEntities$();
    readonly form = this.#fb.group({
        entity: [null as Entity, Validators.required],
        producer: [{ value: null, disabled: true }, Validators.required],
        name: [null,
            [Validators.required,
            Validators.maxLength(EventFieldsRestriction.eventNameLength)]
        ],
        category: [null as Category, Validators.required],
        type: EventType.normal,
        // TODO (MULTICURRENCY): Delete disabled when all operators are multicurrency
        currency: [{ value: null as Currency, disabled: true }, Validators.required],
        avet: this.#fb.group<AvetFieldsForm>({} as AvetFieldsForm),
        inventory: ['internal' as 'internal' | ExternalInventoryProviders, Validators.required],
        sga: this.#fb.group<SgaFieldsForm>({} as SgaFieldsForm)
    });

    readonly eventType = EventType;

    readonly entities$ = combineLatest([
        this.#authSrv.getLoggedUser$().pipe(first(Boolean)),
        this.canSelectEntity$
    ]).pipe(
        switchMap(([user, canSelectEntity]) => {
            if (canSelectEntity) {
                this.#entitiesService.entityList.load({
                    limit: 999,
                    sort: 'name:asc',
                    fields: [
                        EntitiesFilterFields.name,
                        EntitiesFilterFields.allowActivityEvents,
                        EntitiesFilterFields.allowAvetIntegration
                    ],
                    type: 'EVENT_ENTITY'
                });
                return this.#entitiesService.entityList.getData$();
            } else {
                this.#entitiesService.loadEntity(user.entity.id);
                return this.#entitiesService.getEntity$().pipe(
                    first(Boolean),
                    map(entity => [entity])
                );
            }
        }),
        tap((entities: Entity[]) => {
            if (entities && entities.length === 1) {
                this.form.patchValue({ entity: entities[0] });
            }
        }),
        takeUntilDestroyed(this.#destroyRef),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    readonly producers$ = this.#producersService.getProducersListData$();
    readonly categories$ = this.#categoryService.getCategories$().pipe(
        map(categories => categories?.map(cat => ({
            ...cat,
            description: this.#translatePipe.transform(`ENTITY.CATEGORY_OPTS.${cat.code}`)
        })))
    );

    readonly currencies$ = this.#authSrv.getLoggedUser$()
        .pipe(first(), map(AuthenticationService.operatorCurrencies));

    readonly types$ = this.form.controls.entity.valueChanges
        .pipe(
            distinctUntilChanged(),
            filter(value => value !== null),
            map(entity => {
                let result: EventType[];
                if (entity.settings?.allow_activity_events || entity.settings?.allow_avet_integration) {
                    result = [EventType.normal];
                }
                if (entity.settings?.allow_activity_events) {
                    result.push(EventType.activity, EventType.themePark);
                }
                if (entity.settings?.allow_avet_integration) {
                    result.push(EventType.avet);
                }
                // reset to normal event type when we change the entity
                this.form.patchValue({ type: EventType.normal });
                this.#externalSrv.inventoryProviders.clear();
                this.#externalSrv.inventoryProviders.load(entity?.id);

                return result;
            }),
            takeUntilDestroyed(this.#destroyRef),
            shareReplay(1)
        );

    readonly enableTypeSelector$ = this.form.controls.entity.valueChanges.pipe(
        distinctUntilChanged(),
        filter(value => value !== null),
        map(entity => entity.settings?.allow_avet_integration || entity.settings?.allow_activity_events),
        takeUntilDestroyed(this.#destroyRef),
        shareReplay(1)
    );

    readonly enableAvetFields$ = this.enableTypeSelector$.pipe(
        filter(isEnabled => isEnabled),
        switchMap(() => this.form.controls.type.valueChanges),
        map(type => EventType.avet === type)
    );

    readonly inventoryProviders$ = this.#externalSrv.inventoryProviders.get$().pipe(
        filter(Boolean),
        map(inv => inv?.inventory_providers)
    );

    readonly externalInventoryProviders = ExternalInventoryProviders;

    readonly isInProgress$ = booleanOrMerge([
        this.#entitiesService.entityList.inProgress$(),
        this.#entitiesService.isEntityLoading$(),
        this.#producersService.isProducersListLoading$(),
        this.#categoryService.isCategoriesLoading$(),
        this.#eventsService.event.inProgress$(),
        this.#venueTemplatesService.isVenueTemplatesListLoading$(),
        this.#stdVenueTplSrv.isVenueTemplateAvetCompetitionInProgress$(),
        this.#promotersExternalSrv.providerEvents.loading$(),
        this.#externalSrv.inventoryProviders.loading$()
    ]);

    ngOnInit(): void {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;
        this.#externalSrv.inventoryProviders.clear();

        this.form.controls.entity.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(entity => {
                if (entity) {
                    this.form.controls.producer.enable();
                    this.#producersService.loadProducersList(999, 0, 'name:asc', '', null, entity.id);
                }
            });

        this.#categoryService.loadCategories();
        this.entities$.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe();

        this.currencies$
            .pipe(first())
            .subscribe(currencies => {
                if (currencies?.length > 1) {
                    this.form.get('currency').enable({ emitEvent: false });
                } else {
                    this.form.get('currency').disable({ emitEvent: false });
                }
                this.form.get('currency').updateValueAndValidity();
            });

    }

    createEvent(): void {
        if (this.form.valid) {
            this.#authSrv.getLoggedUser$().pipe(first()).subscribe(user => {
                const postEvent: PostEvent = {
                    name: this.form.value.name,
                    type: this.form.value.type,
                    entity_id: this.form.value.entity.id,
                    producer_id: this.form.value.producer.id,
                    category_id: this.form.value.category.id
                };
                if (postEvent.type === EventType.avet) {
                    const { competition, connection, template } = this.form.controls.avet.controls;
                    postEvent.reference = competition.value.description;
                    postEvent.additional_config = {
                        avet_competition_id: competition.value.id,
                        avet_config: connection.value,
                        venue_template_id: template.value.id
                    };
                }
                if (this.form.value.inventory !== 'internal') {
                    postEvent.additional_config = {
                        inventory_provider: this.form.value.inventory
                    };
                }
                if (this.form.value.inventory === ExternalInventoryProviders.sga && this.form.value.type === EventType.normal) {
                    const { template, event } = this.form.controls.sga.controls;
                    postEvent.additional_config = {
                        venue_template_id: template.value.id,
                        inventory_provider: this.form.value.inventory,
                        external_event_id: event.value.id,
                        standalone: event.value.standalone
                    };
                }
                const currencies = AuthenticationService.operatorCurrencyCodes(user);
                //TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
                if (currencies?.length > 1) {
                    postEvent.currency_code = this.form.value.currency.code;
                } else {
                    postEvent.currency_code = currencies?.length === 1 ? currencies[0] : user.currency;
                }
                this.#eventsService.event.create(postEvent).subscribe(id => this.close(id));
            });
        } else {
            this.form.markAllAsTouched();
            // update validity for avet form group workaround
            if (this.form.controls.avet?.controls.connection) {
                this.form.controls.avet.controls.connection.setValue(this.form.controls.avet.controls.connection.value);
            }
            scrollIntoFirstInvalidFieldOrErrorMsg(this.#elemRef.nativeElement);
        }
    }

    close(eventId: number = null): void {
        this.#dialogRef.close(eventId);
    }
}
