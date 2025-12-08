import { Session } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import {
    ACTIVITY_PRICE_TYPES_GATES_SERVICE, SessionPriceType
} from '@admin-clients/shared/venues/data-access/activity-venue-tpls';
import {
    VenueTemplate, VenueTemplateGate, VenueTemplatePriceType, VenueTemplatesService
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, effect, inject, input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormGroup, ReactiveFormsModule, UntypedFormControl } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatSelect, MatOption } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { EMPTY, Observable } from 'rxjs';
import { shareReplay, switchMap, take, tap } from 'rxjs/operators';

@Component({
    selector: 'app-activity-venue-template-price-types-gates',
    templateUrl: './activity-venue-template-price-types-gates.component.html',
    styleUrls: ['./activity-venue-template-price-types-gates.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatFormField, MatSelect, MatOption, MatButton, MatIcon, MatTableModule, MatCheckbox, TranslatePipe,
        AsyncPipe, FlexLayoutModule, ReactiveFormsModule, MatInput, MatLabel
    ]
})
export class ActivityVenueTemplatePriceTypesGatesComponent implements OnInit {
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #venueTemplateSrv = inject(VenueTemplatesService);
    readonly #activityPriceTypesGatesService = inject(ACTIVITY_PRICE_TYPES_GATES_SERVICE);
    readonly #onDestroy = inject(DestroyRef);

    form = input<FormGroup>();
    isSga = input<boolean>();

    loading$: Observable<boolean>;
    gatesAssignControl: UntypedFormControl;
    columnNames = ['name', 'gate_id', 'restrictive_access'];
    priceTypes$: Observable<VenueTemplatePriceType[] | SessionPriceType[]>;
    gates$: Observable<VenueTemplateGate[]>;

    venueTemplate = input<VenueTemplate>();
    session = input<Session>();

    constructor() {
        effect(() => this.venueTemplate() && this.loadPriceTypes());
        effect(() => this.session() && this.loadPriceTypes());
    }

    ngOnInit(): void {
        this.loading$ = booleanOrMerge([
            this.#activityPriceTypesGatesService.isActivityPriceTypesGatesLoading$(),
            this.#activityPriceTypesGatesService.isActivityPriceTypesGatesSaving$()
        ]);
        this.priceTypes$ = this.#activityPriceTypesGatesService.getActivityPriceTypes$()
            .pipe(
                tap(priceTypes => {
                    this.initForm(priceTypes);
                    this.setFormValues(priceTypes);
                }),
                shareReplay(1)
            );
        this.gates$ = this.#venueTemplateSrv.getVenueTemplateGates$();
    }

    reset(): void {
        this.loadPriceTypes();
    }

    save(): Observable<void> {
        return this.#activityPriceTypesGatesService.getActivityPriceTypes$()
            .pipe(
                take(1),
                switchMap(priceTypes => {
                    const toSave: VenueTemplatePriceType[] = [];
                    priceTypes.forEach(priceType => {
                        const priceTypeControl = this.form().get(String(priceType.id));
                        if (priceTypeControl.dirty) {
                            toSave.push({
                                id: priceType.id,
                                additional_config: {
                                    gate_id: priceTypeControl.value.gateId,
                                    restrictive_access: priceTypeControl.value.restrictiveAccess
                                }
                            } as VenueTemplatePriceType);
                        }
                    });
                    if (toSave?.length) {
                        return this.#activityPriceTypesGatesService
                            .updatePriceTypesGates(
                                {
                                    venueTemplateId: this.venueTemplate()?.id,
                                    eventId: this.session()?.event?.id,
                                    sessionId: this.session()?.id
                                },
                                toSave);
                    } else {
                        return EMPTY;
                    }
                }),
                tap(() =>
                    this.#ephemeralMessageService.showSaveSuccess()
                )
            );
    }

    assingGate(): void {
        this.priceTypes$
            .pipe(take(1))
            .subscribe(priceTypes => {
                const gateToAssign = this.gatesAssignControl.value;
                priceTypes.forEach(priceType => {
                    const gateControl = this.form().get(priceType.id + '.gateId');
                    gateControl.setValue(gateToAssign);
                    gateControl.markAsDirty();
                });
            });
    }

    private loadPriceTypes(): void {
        this.#activityPriceTypesGatesService.loadActivityPriceTypes({
            venueTemplateId: this.venueTemplate()?.id || this.session()?.venue_template.id,
            eventId: this.session()?.event.id,
            sessionId: this.session()?.id
        });
    }

    private initForm(priceTypes: VenueTemplatePriceType[]): void {
        Object.keys(this.form().controls).forEach(controlKey => this.form().removeControl(controlKey));
        this.form().setValue({});
        this.form().markAsPristine();
        priceTypes?.forEach(priceType =>
            this.form().setControl(
                String(priceType.id),
                this.#fb.group({
                    gateId: { value: null, disabled: this.form().disabled },
                    restrictiveAccess: { value: null, disabled: this.form().disabled && !!this.session() }
                })));
        this.gatesAssignControl = this.#fb.control({ value: null, disabled: this.form().disabled });
        this.form().valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(() => {
                if (this.form().enabled) {
                    this.gatesAssignControl.enable();
                } else {
                    this.gatesAssignControl.disable();
                }
                if (this.session()) {
                    priceTypes?.map(priceType => this.form().get(priceType.id + '.restrictiveAccess'))
                        .filter(control => control?.enabled)
                        .forEach(control => control.disable());
                }
            });
    }

    private setFormValues(priceTypes: VenueTemplatePriceType[] | SessionPriceType[]): void {
        priceTypes?.forEach(priceType => {
            this.form().get(String(priceType.id)).setValue({
                gateId: priceType.additional_config.gate_id || null,
                restrictiveAccess: priceType.additional_config.restrictive_access || false
            });
        });
    }
}
