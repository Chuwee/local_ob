import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { Event } from '@admin-clients/cpanel/promoters/events/data-access';
import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import { EntitiesBaseService, EventType } from '@admin-clients/shared/common/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { IdName, Venue } from '@admin-clients/shared/data-access/models';
import {
    PostVenueTemplateRequest, VenueTemplate, VenueTemplateFieldsRestrictions, VenueTemplateScope, VenueTemplatesService, VenueTemplateType
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, effect, ElementRef, inject, OnInit, signal, viewChild } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { catchError, combineLatest, fromEvent } from 'rxjs';
import { NewVenueTemplateDialogMode } from './models/new-venue-template-dialog-mode.enum';
import { NewVenueTplType } from './models/new-venue-tpl-type.enum';
import { NewVenueViewType } from './models/new-venue-view-type.enum';
import { TemplateSelectionComponent } from './template-selection/template-selection.component';
import { VenueSelectionComponent } from './venue-selection/venue-selection.component';

@Component({
    imports: [
        TranslatePipe, WizardBarComponent, MatDialogTitle, MatDialogContent, MatDialogActions, MatIcon,
        ReactiveFormsModule, VenueSelectionComponent, TemplateSelectionComponent, MatButton, AsyncPipe,
        MatProgressSpinner, MatIconButton
    ],
    selector: 'app-new-venue-template-dialog',
    templateUrl: './new-venue-template-dialog.component.html',
    styleUrls: ['./new-venue-template-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class NewVenueTemplateDialogComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #dialogRef = inject(MatDialogRef<NewVenueTemplateDialogComponent>);
    readonly #fb = inject(FormBuilder);
    readonly #auth = inject(AuthenticationService);
    readonly #venueTplsService = inject(VenueTemplatesService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #data = inject(MAT_DIALOG_DATA);

    private readonly _nextStepBtn = viewChild('nextStepBtn', { read: ElementRef });
    private readonly _prevStepBtn = viewChild('prevStepBtn', { read: ElementRef });
    private readonly _saveBtn = viewChild('saveBtn', { read: ElementRef });
    private readonly _wizardBar = viewChild(WizardBarComponent);

    #lastRepeatedName: string;

    readonly step1 = 'VENUES.VENUE_SELECTION';
    readonly step2 = 'VENUES.BASE_TEMPLATE_SELECTION';
    readonly reqInProgress$ = this.#venueTplsService.isVenueTemplateSaving$();
    readonly mode: NewVenueTemplateDialogMode = this.#data.mode;
    readonly event: Event = this.#data.event;
    prefixedEntityId: IdName;

    readonly $currentStep = signal(this.step1);

    readonly venueSelectionForm = this.#fb.group({
        entity: [null as IdName, Validators.required],
        country: null,
        city: null,
        keyword: null,
        selectedVenue: [null as Venue[], Validators.required]
    });

    readonly templateSelectionForm = this.#fb.group({
        venueSpace: [null, Validators.required],
        tplType: [null, Validators.required],
        newVenueTplType: [null, Validators.required],
        keyword: null,
        selectedTpl: [null, Validators.required],
        tplViewType: [null, Validators.required],
        newTplName: ['', [
            Validators.required, Validators.maxLength(VenueTemplateFieldsRestrictions.nameLength),
            () => this.#lastRepeatedName === this.templateSelectionForm?.get('newTplName')?.value
                && { nameConflict: this.#lastRepeatedName } || null
        ]],
        externalCapacity: [{ value: null, disabled: true }, Validators.required]
    });

    readonly form = this.#fb.group({
        venueSelection: this.venueSelectionForm,
        templateSelection: this.templateSelectionForm
    });

    constructor() {
        this.#dialogRef.addPanelClass([DialogSize.EXTRA_LARGE, 'no-padding']);
        effect(() => {
            fromEvent<MouseEvent>(this._nextStepBtn()?.nativeElement, 'click')
                .pipe(takeUntilDestroyed(this.#destroyRef))
                .subscribe(() => this.#setStep2());
            fromEvent<MouseEvent>(this._prevStepBtn()?.nativeElement, 'click')
                .pipe(takeUntilDestroyed(this.#destroyRef))
                .subscribe(() => this.#setStep1());
            fromEvent<MouseEvent>(this._saveBtn()?.nativeElement, 'click')
                .pipe(takeUntilDestroyed(this.#destroyRef))
                .subscribe(() => this.#save());
        });
    }

    ngOnInit(): void {
        if (this.mode === NewVenueTemplateDialogMode.eventTemplate) {
            this.#setEventBehaviours();
        } else if (this.mode === NewVenueTemplateDialogMode.promoterTemplate) {
            this.#setPromoterTemplateBehaviours();
        }
        this.#setPrefixedEntity();
    }

    close(newTplId?: number): void {
        this.#dialogRef.close(newTplId);
    }

    getSelectedVenue(): Venue {
        return this.venueSelectionForm.controls.selectedVenue.value?.length
            && this.venueSelectionForm.controls.selectedVenue.value[0];
    }

    #setPromoterTemplateBehaviours(): void {
        this.templateSelectionForm.controls.tplType.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(tplType => {
                if (tplType === VenueTemplateType.activity || tplType === VenueTemplateType.themePark) {
                    this.templateSelectionForm.controls.newVenueTplType.disable();
                    this.templateSelectionForm.controls.tplViewType.disable();
                    this.templateSelectionForm.controls.selectedTpl.disable();
                    this.templateSelectionForm.controls.externalCapacity.disable();
                } else if (tplType === VenueTemplateType.avet) {
                    this.templateSelectionForm.controls.externalCapacity.enable();
                    this.templateSelectionForm.controls.externalCapacity.setValue(null);
                    this.templateSelectionForm.controls.newVenueTplType.disable();
                    this.templateSelectionForm.controls.tplViewType.disable();
                    this.templateSelectionForm.controls.selectedTpl.disable();
                } else {
                    this.templateSelectionForm.controls.newVenueTplType.enable();
                    this.templateSelectionForm.controls.newVenueTplType.setValue(null);
                    this.templateSelectionForm.controls.tplViewType.enable();
                    this.templateSelectionForm.controls.selectedTpl.enable();
                    this.templateSelectionForm.controls.externalCapacity.disable();
                }
            });
        this.templateSelectionForm.controls.newVenueTplType.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(newVenueTplType => {
                this.templateSelectionForm.controls.selectedTpl.setValue(null);
                this.templateSelectionForm.controls.tplViewType.setValue(null);
                if (newVenueTplType === NewVenueTplType.new) {
                    if (this.templateSelectionForm.controls.tplType.value === VenueTemplateType.normal) {
                        this.templateSelectionForm.controls.tplViewType.enable();
                    }
                    this.templateSelectionForm.controls.selectedTpl.disable();
                } else if (newVenueTplType === NewVenueTplType.base) {
                    this.templateSelectionForm.controls.selectedTpl.enable();
                    this.templateSelectionForm.controls.tplViewType.disable();
                }
            });
    }

    #setEventBehaviours(): void {
        this.venueSelectionForm.controls.entity.setValidators(null);
        const eventType = this.event.type;
        this.templateSelectionForm.controls.tplType.setValue(
            (eventType === EventType.normal && VenueTemplateType.normal)
            || (eventType === EventType.activity && VenueTemplateType.activity)
            || (eventType === EventType.themePark && VenueTemplateType.themePark)
            || (eventType === EventType.avet && VenueTemplateType.avet)
        );
        this.templateSelectionForm.controls.newVenueTplType.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(newVenueTplType => {
                this.templateSelectionForm.controls.selectedTpl.setValue(null);
                if (newVenueTplType === NewVenueTplType.new) {
                    this.templateSelectionForm.controls.selectedTpl.disable();
                } else if (newVenueTplType === NewVenueTplType.base) {
                    this.templateSelectionForm.controls.selectedTpl.enable();
                }
            });
        if (eventType === EventType.activity || eventType === EventType.themePark) {
            this.templateSelectionForm.controls.tplViewType.disable();
        } else if (eventType === EventType.normal) {
            this.templateSelectionForm.controls.newVenueTplType.valueChanges
                .pipe(takeUntilDestroyed(this.#destroyRef))
                .subscribe(newVenueTplType => {
                    this.templateSelectionForm.controls.tplViewType.setValue(null);
                    if (newVenueTplType === NewVenueTplType.new) {
                        this.templateSelectionForm.controls.tplViewType.enable();
                    } else {
                        this.templateSelectionForm.controls.tplViewType.disable();
                    }
                });
        }
    }

    #setPrefixedEntity(): void {
        combineLatest([
            this.#auth.getLoggedUser$(),
            this.#auth.canReadMultipleEntities$()
        ]).subscribe(([user, canSelectEntity]) => {
            let entityToSet: IdName;
            if (this.mode === NewVenueTemplateDialogMode.eventTemplate) {
                entityToSet = this.event.entity;
            } else if (!canSelectEntity) {
                entityToSet = user?.entity;
            }
            if (entityToSet) {
                this.prefixedEntityId = entityToSet;
                this.venueSelectionForm.controls.entity.setValue(entityToSet);
            }
        });
    }

    #setStep1(): void {
        this._wizardBar().previousStep();
        this.$currentStep.set(this.step1);
        const currentTplType = this.templateSelectionForm.controls.tplType.value;
        this.templateSelectionForm.reset({ tplType: currentTplType });
        const currentEntity = this.venueSelectionForm.controls.entity.value;
        this.venueSelectionForm.reset({ entity: currentEntity });
    }

    #setStep2(): void {
        if (this.venueSelectionForm.controls.entity.value) {
            this.#entitiesSrv.loadEntity(this.venueSelectionForm.controls.entity.value.id);
        }
        this._wizardBar().nextStep();
        this.$currentStep.set(this.step2);
    }

    #save(): void {
        const req: PostVenueTemplateRequest = {
            name: this.templateSelectionForm.controls.newTplName.value,
            venue_id: this.venueSelectionForm.controls.selectedVenue.value[0].id,
            space_id: this.templateSelectionForm.controls.venueSpace.value.id,
            type: this.templateSelectionForm.controls.tplType.value,
            event_id: this.event?.id,
            scope: undefined,
            entity_id: this.venueSelectionForm.controls.entity.value.id || undefined,
            additional_config: {
                external_capacity_id: this.templateSelectionForm.controls.externalCapacity.enabled
                    ? this.templateSelectionForm.get('externalCapacity').value[0].id
                    : undefined,
                inventory_provider: (this.templateSelectionForm.controls.inventory?.value !== 'internal')
                    ? this.templateSelectionForm.controls.inventory?.value
                    : undefined,
                inventory_id: this.templateSelectionForm.controls.externalInventory.enabled
                    ? this.templateSelectionForm.controls.externalInventory.value[0].id
                    : undefined
            }
        };
        if (this.mode === NewVenueTemplateDialogMode.venueTemplate) {
            req.scope = VenueTemplateScope.archetype;
        } else if (this.mode === NewVenueTemplateDialogMode.promoterTemplate) {
            req.scope = VenueTemplateScope.standard;
        }
        if (this.templateSelectionForm.controls.newVenueTplType.enabled &&
            this.templateSelectionForm.controls.newVenueTplType.value === NewVenueTplType.base) {
            const selectedTpl = this.templateSelectionForm.controls.selectedTpl.value[0] as VenueTemplate;
            req.from_template_id = selectedTpl.id;
        } else if (this.templateSelectionForm.controls.tplType.value === VenueTemplateType.normal) {
            req.graphic = this.templateSelectionForm.controls.tplViewType.value === NewVenueViewType.graphic
                || this.templateSelectionForm.controls.inventory.value === 'SGA';
        }
        this.#venueTplsService.createVenueTemplate(req)
            .pipe(
                catchError(err => {
                    if (err.error.code === 'NAME_CONFLICT') {
                        const nameControl = this.templateSelectionForm.get('newTplName');
                        this.#lastRepeatedName = nameControl.value;
                        nameControl.updateValueAndValidity();
                    }
                    throw err;
                })
            )
            .subscribe(id => this.close(id.id));
    }
}
