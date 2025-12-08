import { FORM_CONTROL_ERRORS, FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { DialogSize, EphemeralMessageService, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge, noDuplicateValuesValidatorFn } from '@admin-clients/shared/utility/utils';
import { VenueTemplateGate, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe, NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, EventEmitter, inject, input, OnInit, Output } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import {
    ReactiveFormsModule, UntypedFormArray, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators
} from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatButtonToggle } from '@angular/material/button-toggle';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatList, MatListItem } from '@angular/material/list';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter, pairwise, tap } from 'rxjs/operators';

@Component({
    selector: 'app-activity-venue-template-gates',
    templateUrl: './activity-venue-template-gates.component.html',
    styleUrls: ['./activity-venue-template-gates.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatFormField, MatInput, MatButton, MatIcon, MatList, MatListItem, MatButtonToggle, MatTooltip,
        AsyncPipe, FlexLayoutModule, ReactiveFormsModule, TranslatePipe, MatListItem, MatLabel, MatError,
        FormControlErrorsComponent, NgClass, MatIconButton
    ]
})
export class ActivityVenueTemplateGatesComponent implements OnInit {
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #formErrors = inject(FORM_CONTROL_ERRORS);
    readonly #destroyRef = inject(DestroyRef);

    #venueTemplateId: number;
    #gates: VenueTemplateGate[];
    gatesForm: UntypedFormGroup;
    gatesArrayForm: UntypedFormArray;
    newGateControl: UntypedFormControl;
    gates$: Observable<VenueTemplateGate[]>;
    loading$: Observable<boolean>;
    form = input<UntypedFormGroup>();
    @Output() dataChange = new EventEmitter<void>();

    ngOnInit(): void {
        this.loading$ = booleanOrMerge([
            this.#venueTemplatesSrv.isVenueTemplateGatesLoading$(),
            this.#venueTemplatesSrv.isVenueTemplateGatesSaving$()
        ]);
        this.#venueTemplatesSrv.venueTpl.get$()
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(venueTemplate => {
                this.#venueTemplateId = venueTemplate.id;
                this.#venueTemplatesSrv.loadVenueTemplateGates(this.#venueTemplateId);
            });
        this.gates$ = this.#venueTemplatesSrv.getVenueTemplateGates$()
            .pipe(tap(gates => this.#gates = gates));
        this.initForm();
        this.setFormBehaviors();
    }

    addNewGate(): void {
        if (this.newGateControl.valid && this.newGateControl.value) {
            this.#venueTemplatesSrv.addVenueTemplateGate(this.#venueTemplateId, {
                code: this.newGateControl.value,
                name: this.newGateControl.value,
                default: false
            })
                .subscribe(() => {
                    this.#ephemeralSrv.showSuccess({
                        msgKey: 'VENUE_TPLS.CREATE_GATE_SUCCESS',
                        msgParams: { name: this.newGateControl.value }
                    });
                    this.#venueTemplatesSrv.loadVenueTemplateGates(this.#venueTemplateId);
                    this.dataChange.emit();
                    this.newGateControl.patchValue('');
                    this.newGateControl.markAsPristine();
                });
        }
    }

    setDefaultGate(gate: VenueTemplateGate): void {
        const defaultedGate = Object.assign({}, gate);
        defaultedGate.default = true;
        this.#venueTemplatesSrv.updateVenueTemplateGate(this.#venueTemplateId, defaultedGate)
            .subscribe(() => {
                this.#ephemeralSrv.showSuccess({
                    msgKey: 'VENUE_TPLS.DEFAULT_GATE_SET_SUCCESS',
                    msgParams: { name: gate.name }
                });
                this.#venueTemplatesSrv.loadVenueTemplateGates(this.#venueTemplateId);
            });
    }

    deleteGate(gate: VenueTemplateGate): void {
        this.#msgDialogSrv.showWarn(
            {
                size: DialogSize.SMALL,
                title: 'TITLES.WARNING',
                message: 'VENUE_TPLS.DELETE_GATE_WARNING',
                messageParams: { name: gate.name }
            })
            .subscribe(success => {
                if (success) {
                    this.#venueTemplatesSrv.deleteVenueTemplateGate(this.#venueTemplateId, String(gate.id))
                        .subscribe(() => {
                            this.#ephemeralSrv.showSuccess({
                                msgKey: 'VENUE_TPLS.DELETE_GATE_SUCCESS',
                                msgParams: { name: gate.name }
                            });
                            this.#venueTemplatesSrv.loadVenueTemplateGates(this.#venueTemplateId);
                            this.dataChange.emit();
                        });
                }
            });
    }

    checkValidity(arrayIndex: number, gateInput: HTMLInputElement): void {
        const ctrl = this.gatesArrayForm.get(`${arrayIndex}`).get('name');
        if (ctrl.invalid) {
            this.#msgDialogSrv.showAlert({
                size: DialogSize.SMALL,
                title: 'TITLES.ERROR_DIALOG',
                message: this.#formErrors.getErrorMessage(ctrl.errors),
                messageParams: this.#formErrors.getErrorParameters(ctrl.errors)
            })
                .subscribe(() => gateInput.focus());
        }
    }

    private initForm(): void {
        this.gatesArrayForm = this.#fb.array([]);
        this.newGateControl =
            this.#fb.control('', [control => noDuplicateValuesValidatorFn(control, this.gatesArrayForm.controls.map(c => c.value.name))]);
        this.gatesForm = this.#fb.group({
            gates: this.gatesArrayForm,
            newGate: this.newGateControl
        });
        this.#venueTemplatesSrv.getVenueTemplateGates$()
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(gates => {
                this.gatesArrayForm.clear();
                gates.forEach(gate =>
                    this.gatesArrayForm.push(
                        this.#fb.group({
                            id: gate.id,
                            name: this.#fb.control(gate.name, {
                                updateOn: 'blur',
                                validators: [
                                    Validators.required,
                                    control => noDuplicateValuesValidatorFn(
                                        control,
                                        this.gatesArrayForm.controls.map(c => c.get('name')).filter(c => c !== control).map(c => c.value)
                                    )
                                ]
                            })
                        })
                    )
                );
            });
    }

    private setFormBehaviors(): void {
        this.form().valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => {
                if (this.form().dirty) {
                    this.gatesForm.disable();
                } else {
                    this.gatesForm.enable();
                }
            });
        // update gate name
        this.gatesForm.get('gates')
            .valueChanges
            .pipe(
                takeUntilDestroyed(this.#destroyRef),
                filter(() => !this.gatesForm.get('gates').invalid),
                pairwise()
            )
            .subscribe(([prev, current]: [VenueTemplateGate[], VenueTemplateGate[]]) => {
                if (prev.length === current.length) {
                    const modifIndex = current.findIndex(currentGate => !prev.find(prevGate => currentGate.name === prevGate.name));
                    const modifName = current[modifIndex]?.name;
                    if (modifIndex >= 0) {
                        const modifGate: VenueTemplateGate = {
                            id: this.#gates[modifIndex]?.id,
                            name: modifName
                        };
                        this.updateGate(modifGate);
                    }
                }
            });
    }

    private updateGate(gate: VenueTemplateGate): void {
        this.#venueTemplatesSrv.updateVenueTemplateGate(this.#venueTemplateId, gate)
            .subscribe(() => {
                this.#ephemeralSrv.showSuccess({ msgKey: 'FORMS.FEEDBACK.SAVE_SUCCESS' });
                this.#venueTemplatesSrv.loadVenueTemplateGates(this.#venueTemplateId);
                this.gatesForm.markAsPristine();
            });
    }
}
