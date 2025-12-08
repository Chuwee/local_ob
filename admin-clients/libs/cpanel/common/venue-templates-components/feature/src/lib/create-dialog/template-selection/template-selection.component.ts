import { TranslateFormErrorPipe } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService, UserRoles, User } from '@admin-clients/cpanel/core/data-access';
import { ExternalEntityService } from '@admin-clients/cpanel/organizations/entities/feature';
import { Event } from '@admin-clients/cpanel/promoters/events/data-access';
import { TemplateTableComponent } from '@admin-clients/cpanel/shared/ui/components';
import { VenuesService, venuesProviders } from '@admin-clients/cpanel/venues/data-access';
import { EntitiesBaseService, Entity, EventType, ExternalInventoryProviders } from '@admin-clients/shared/common/data-access';
import { ContextNotificationComponent, SearchInputComponent } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats, Venue } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { LocalDateTimePipe, LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import {
    GetVenueTemplatesRequest, VenueTemplateScope, VenueTemplateStatus, VenueTemplateType, VenueTemplatesService, VenueTemplatesState
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnDestroy, OnInit, effect, inject, input } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, ValidatorFn, Validators } from '@angular/forms';
import { MatOption } from '@angular/material/core';
import { MatDialogContent } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatFormField, MatLabel, MatSuffix } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatListOption, MatSelectionList } from '@angular/material/list';
import { PageEvent, MatPaginator } from '@angular/material/paginator';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatSelect } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable } from 'rxjs';
import { filter, first, map, startWith, take, tap } from 'rxjs/operators';
import { NewVenueTemplateDialogMode } from '../models/new-venue-template-dialog-mode.enum';
import { TemplateSelectionForm } from '../models/new-venue-template-form.model';
import { NewVenueTplType } from '../models/new-venue-tpl-type.enum';
import { NewVenueViewType } from '../models/new-venue-view-type.enum';

@Component({
    selector: 'app-template-selection',
    templateUrl: './template-selection.component.html',
    styleUrls: ['./template-selection.component.scss'],
    imports: [
        TranslatePipe, ReactiveFormsModule, AsyncPipe, MatFormField, MatInput, MatLabel, MatOption,
        MatDialogContent, MatSelect, EllipsifyDirective, MatTooltip, MatRadioGroup, MatRadioButton,
        MatDivider, MatPaginator, SearchInputComponent, ContextNotificationComponent, MatListOption,
        MatSelectionList, LocalDateTimePipe, LocalNumberPipe, TemplateTableComponent, MatProgressSpinner,
        TranslateFormErrorPipe, MatSuffix, MatIcon
    ],
    providers: [
        venuesProviders,
        VenueTemplatesService, VenueTemplatesState
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TemplateSelectionComponent implements OnInit, OnDestroy {
    readonly #venuesService = inject(VenuesService);
    readonly #venueTplsService = inject(VenueTemplatesService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #entitySrv = inject(EntitiesBaseService);
    readonly #externalSrv = inject(ExternalEntityService);
    readonly #fb = inject(FormBuilder);
    readonly #destroyRef = inject(DestroyRef);

    readonly PAGE_SIZE = 20;

    readonly dateTimeFormats = DateTimeFormats;
    readonly eventType = EventType;
    readonly newVenueTplType = NewVenueTplType;
    readonly newVenueViewType = NewVenueViewType;
    readonly venueTemplateType = VenueTemplateType;
    readonly newVenueTemplateDialogMode = NewVenueTemplateDialogMode;

    readonly $form = input<TemplateSelectionForm>(null, { alias: 'form' });
    readonly $venue = input<Venue>(null, { alias: 'venue' });
    readonly $event = input<Event>(null, { alias: 'event' });
    readonly $mode = input<NewVenueTemplateDialogMode>(null, { alias: 'mode' });

    readonly $user = toSignal(this.#authSrv.getLoggedUser$());
    readonly $venueTplsListData = toSignal(this.#venueTplsService.getVenueTemplatesListData$());
    readonly $venueTplsListMetadata = toSignal(this.#venueTplsService.getVenueTemplatesListMetadata$());
    readonly $entityExternalCapacities = toSignal(this.#entitySrv.getEntityExternalCapacities$());
    readonly $venueSpaces = toSignal(this.#venuesService.getVenue$().pipe(
        filter(Boolean),
        take(1),
        map(venue => venue.spaces),
        tap(spaces => this.$form().get('venueSpace').setValue(spaces[0]))
    ));

    availableTemplateTypes$: Observable<VenueTemplateType[]>;
    entity: Entity;

    readonly $reqInProgress = toSignal(booleanOrMerge([
        this.#venueTplsService.isVenueTemplatesListLoading$(),
        this.#venuesService.isVenueLoading$(),
        this.#entitySrv.isEntityExternalCapacitiesLoading$(),
        this.#externalSrv.inventoryProviders.loading$(),
        this.#externalSrv.inventories.loading$(),
        this.#entitySrv.externalVenueTemplates.loading$()
    ]));

    readonly $inventoryProviders = toSignal(this.#externalSrv.inventoryProviders.get$().pipe(
        filter(Boolean),
        take(1),
        map(inv => inv?.inventory_providers)
    ));

    readonly $externalVenueTemplates = toSignal(this.#entitySrv.externalVenueTemplates.get$().pipe(filter(Boolean)));
    readonly $externalInventories = toSignal(this.#externalSrv.inventories.get$().pipe(filter(Boolean)));

    constructor() {
        effect(() => {
            const inventProviders = this.$inventoryProviders();
            if (inventProviders) {
                const controlValue = { value: 'internal', disabled: false };
                const controlValidators: ValidatorFn[] = [];
                if (!!inventProviders.length) {
                    controlValidators.push(Validators.required);
                } else {
                    controlValue.disabled = true;
                }
                this.$form().addControl('inventory', this.#fb.control(controlValue, controlValidators));
            } else {
                this.$form().removeControl('inventory');
            }
        });
    }

    ngOnInit(): void {
        this.$form().addControl('externalInventory', this.#fb.control('', Validators.required));
        this.$form().controls.externalInventory.disable();
        this.#entitySrv.clearEntity();
        this.#externalSrv.inventoryProviders.clear();
        this.#externalSrv.inventories.clear();
        // data load
        this.#venuesService.loadVenue(this.$venue().id);
        if (this.$mode() === NewVenueTemplateDialogMode.venueTemplate) {
            this.#venuesService.loadVenue(this.$venue().id);
            this.$form().controls.tplType.setValue(VenueTemplateType.normal);
            this.$form().controls.newVenueTplType.enable();
            this.$form().controls.newVenueTplType.setValue(NewVenueTplType.new);
            this.$form().controls.selectedTpl.disable();
            this.$form().controls.tplViewType.enable();
        } else if (this.$mode() === NewVenueTemplateDialogMode.eventTemplate) {
            if (AuthenticationService.isSomeRoleInUserRoles(this.$user(), [UserRoles.ENT_ANS]) && this.$event()) {
                this.#venuesService.loadVenue(this.$venue().id, this.$event().entity.id);
            } else {
                this.#venuesService.loadVenue(this.$venue().id);
            }
            this.#loadVenueTemplateList();
        } else if (this.$mode() === NewVenueTemplateDialogMode.promoterTemplate) {
            combineLatest([
                this.#authSrv.getLoggedUser$().pipe(first(Boolean)),
                this.#entitySrv.getEntity$().pipe(startWith(null as Entity)),
                this.$form().controls.tplType.valueChanges
            ])
                .pipe(takeUntilDestroyed(this.#destroyRef))
                .subscribe(([user, selectedEntity, templateType]) => {
                    if (AuthenticationService.isSomeRoleInUserRoles(this.$user(), [UserRoles.ENT_ANS]) && selectedEntity) {
                        this.#venuesService.loadVenue(this.$venue().id, selectedEntity.id);
                    } else {
                        this.#venuesService.loadVenue(this.$venue().id);
                    }
                    this.#loadVenueTemplateList({ templateType });
                    if (templateType === VenueTemplateType.avet) {
                        this.#loadEntityExternalCapacity(user, selectedEntity);
                    }
                });
            this.availableTemplateTypes$ = combineLatest([
                this.#authSrv.getLoggedUser$().pipe(first(Boolean)),
                this.#entitySrv.getEntity$().pipe(startWith(null as Entity))
            ])
                .pipe(map(([user, selectedEntity]) => this.#getAvailableTypes(user, selectedEntity)));
        }
        this.$form().controls.keyword.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => this.#loadVenueTemplateList());

        this.#entitySrv.getEntity$().pipe(
            takeUntilDestroyed(this.#destroyRef),
            filter(Boolean)
        ).subscribe(entity => {
            this.entity = entity;
            this.#externalSrv.inventoryProviders.load(this.entity.id);
        });
    }

    ngOnDestroy(): void {
        this.#externalSrv.inventoryProviders.clear();
    }

    changeKeyword(keyword: string): void {
        this.$form().controls.keyword.setValue(keyword);
    }

    loadPaginatedList(pageEvent: PageEvent): void {
        const offset = pageEvent ? pageEvent.pageIndex * pageEvent.pageSize : 0;
        this.#loadVenueTemplateList({ offset });
    }

    onInventoryChange(providerId: string): void {
        this.#externalSrv.inventories.clear();
        if (providerId === ExternalInventoryProviders.sga) {
            this.#externalSrv.inventories.load(this.entity?.id, providerId.toLowerCase(), { skip_used: true });
            this.$form().controls.externalInventory.enable();
            this.$form().controls.selectedTpl.disable();
            this.$form().controls.selectedTpl?.clearValidators();
            this.$form().controls.tplViewType.disable();
            this.$form().controls.newVenueTplType.disable();
        } else if (providerId === ExternalInventoryProviders.italianCompliance) {
            this.#entitySrv.externalVenueTemplates.load(this.entity?.id, this.$venue().external_id);
            this.$form().controls.externalInventory.enable();
            this.$form().controls.selectedTpl.disable();
            this.$form().controls.selectedTpl?.clearValidators();
            this.$form().controls.tplViewType.enable();
            this.$form().controls.newVenueTplType.enable();
        } else {
            this.$form().controls.selectedTpl.enable();
            this.$form().controls.tplViewType.enable();
            this.$form().controls.newVenueTplType.enable();
            this.$form().controls.selectedTpl?.setValidators([Validators.required]);
            this.$form().controls.externalInventory.disable();
        }
    }

    #loadVenueTemplateList({ templateType, offset }: { templateType?: VenueTemplateType; offset?: number } = { offset: 0 }): void {
        this.#venueTplsService.clearVenueTemplateList();
        const req: GetVenueTemplatesRequest = {
            limit: this.PAGE_SIZE,
            offset,
            sort: 'name:asc',
            venueId: this.$venue().id,
            status: [VenueTemplateStatus.active],
            scope: this.$event() ? VenueTemplateScope.standard : VenueTemplateScope.archetype,
            type: this.$event() ? this.#getTemplateEventType(this.$event().type) : templateType,
            filter: this.$form().controls.keyword.value || ''
        };
        if (this.$mode() === NewVenueTemplateDialogMode.promoterTemplate && req.type !== VenueTemplateType.avet) {
            req.includeThirdPartyTemplates = true;
        }
        this.#venueTplsService.loadVenueTemplatesList(req);
    }

    #getAvailableTypes(user: User, selectedEntity: Entity): VenueTemplateType[] {
        const result: VenueTemplateType[] = [VenueTemplateType.normal];
        if (this.$event()) {
            return null;
        }
        const entity = AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.OPR_MGR]) ?
            selectedEntity : user?.entity;
        if (entity) {
            if (entity.settings?.allow_activity_events) {
                result.push(VenueTemplateType.activity, VenueTemplateType.themePark);
            }
            if (entity.settings?.allow_avet_integration) {
                result.push(VenueTemplateType.avet);
            }
        }
        return result;
    }

    #loadEntityExternalCapacity(user?: User, entity?: Entity): void {
        this.#entitySrv.clearEntityExternalCapacities();
        let entityId: number;
        if (this.$event()) {
            entityId = this.$event().entity.id;
        } else {
            if (AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.OPR_MGR])) {
                if (entity) {
                    entityId = entity.id;
                }
            } else {
                entityId = user?.entity.id;
            }
        }
        if (entityId) {
            this.#entitySrv.loadEntityExternalCapacities(entityId);
        }
    }

    #getTemplateEventType(eventType: EventType): VenueTemplateType {
        switch (eventType) {
            case EventType.activity:
                return VenueTemplateType.activity;
            case EventType.themePark:
                return VenueTemplateType.themePark;
            case EventType.avet:
                return VenueTemplateType.avet;
            default: // EventType.normal
                return VenueTemplateType.normal;
        }
    }
}
