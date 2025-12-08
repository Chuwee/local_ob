import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { Session } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { MessageDialogService } from '@admin-clients/shared/common/ui/components';
import {
    ACTIVITY_GROUPS_SERVICE, ActivityGroupsComponentService, SessionActivityGroupsConfig
} from '@admin-clients/shared/venues/data-access/activity-venue-tpls';
import { ActivityGroupMaxType, ActivityGroupsConfig, VenueTemplate } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Inject, Input, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { AbstractControl, ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, ValidationErrors, Validators } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatDivider } from '@angular/material/divider';
import { MatFormField, MatLabel, MatError } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { TranslatePipe } from '@ngx-translate/core';
import { EMPTY, Observable, Subject } from 'rxjs';
import { debounceTime, filter, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-activity-venue-template-groups',
    templateUrl: './activity-venue-template-groups.component.html',
    styleUrls: ['./activity-venue-template-groups.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule, TranslatePipe, FlexLayoutModule, MatRadioGroup, MatRadioButton, MatFormField,
        FormControlErrorsComponent, MatInput, MatDivider, MatLabel, MatCheckbox, MatError
    ]
})
export class ActivityVenueTemplateGroupsComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _formChange = new Subject<void>();
    private _form: UntypedFormGroup;
    private _venueTemplate: VenueTemplate;
    private _session: Session;
    groupsForm: UntypedFormGroup;
    activityGroupMaxType = ActivityGroupMaxType;

    @Input()
    set form(value: UntypedFormGroup) {
        this._form = value;
        this.defineForm();
    }

    @Input()
    set venueTemplate(value: VenueTemplate) {
        this._venueTemplate = value;
    }

    @Input()
    set session(value: Session) {
        this._session = value;
        this._componentService.loadActivityGroupsConfig(this.getGroupsConfigIdentifier());
    }

    constructor(
        private _fb: UntypedFormBuilder,
        private _changeDet: ChangeDetectorRef,
        private _msgDialogSrv: MessageDialogService,
        @Inject(ACTIVITY_GROUPS_SERVICE) private _componentService: ActivityGroupsComponentService
    ) { }

    ngOnInit(): void {
        // form data set
        this._componentService.getActivityGroupsConfig$()
            .pipe(
                filter(groupsConfig => !!groupsConfig),
                takeUntil(this._onDestroy)
            )
            .subscribe(groupsConfig => this.initFormData(groupsConfig));
    }

    ngOnDestroy(): void {
        this._formChange.next();
        this._formChange.complete();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    reset(): void {
        this._componentService.loadActivityGroupsConfig(this.getGroupsConfigIdentifier());
    }

    valid(): boolean {
        if (this.groupsForm.invalid) {
            this.groupsForm.markAllAsTouched();
            this._changeDet.markForCheck();
        }
        return this.groupsForm.valid;
    }

    save(useTemplateConfig: boolean = null): Observable<void> {
        if (!this._form.invalid) {
            return this._componentService.updateActivityGroupsConfig(
                this.getGroupsConfigIdentifier(),
                this.getGroupsToSave(useTemplateConfig)
            );
        } else {
            this._form.markAllAsTouched();
            this._changeDet.markForCheck();
            return EMPTY;
        }
    }

    private getGroupsConfigIdentifier(): { venueTemplateId: number; eventId: number; sessionId: number } {
        return {
            venueTemplateId: this._venueTemplate?.id || this._session?.venue_template.id,
            eventId: this._session?.event.id,
            sessionId: this._session?.id
        };
    }

    private defineForm(): void {
        this._formChange.next();
        this.groupsForm = this._fb.group({
            id: null,
            limitType: [null, Validators.required],
            limit: [null, [Validators.required, Validators.min(0)]],
            attendees: this._fb.group(
                {
                    min: [null, [Validators.required, Validators.min(1)]],
                    maxType: [null, Validators.required],
                    max: [null, Validators.required]
                },
                { validators: fg => this.minMaxValidator(fg) }
            ),
            companions: this._fb.group(
                {
                    min: [null, [Validators.required, Validators.min(0)]],
                    maxType: [null, Validators.required],
                    max: [null, Validators.required]
                },
                { validators: fg => this.minMaxValidator(fg) }
            ),
            companionsOccupyCapacity: null
        });
        this._form.setControl('groups', this.groupsForm);
        // form side effects
        this.setNestedFieldBehavior('limitType', 'limit');
        this.setNestedFieldBehavior('attendees.maxType', 'attendees.max');
        this.setNestedFieldBehavior('companions.maxType', 'companions.max');
    }

    private minMaxValidator(control: AbstractControl): ValidationErrors | null {
        const group = control as UntypedFormGroup;
        const maxControl = group.get('max');
        const value: { min: number; maxType: ActivityGroupMaxType; max: number } = group.value;
        if (value?.maxType === ActivityGroupMaxType.limited && value.min !== null && value.max !== null && value.min > value.max) {
            const result = { min: value.min };
            maxControl.setErrors(Object.assign(maxControl.errors || {}, result));
            return result;
        } else {
            maxControl.updateValueAndValidity({ onlySelf: true, emitEvent: false });
            return null;
        }
    }

    private setNestedFieldBehavior(typeField: string, valueField: string): void {
        this.groupsForm.get(typeField).valueChanges
            .pipe(
                debounceTime(0),
                takeUntil(this._onDestroy)
            )
            .subscribe(limitType => {
                if (!limitType || limitType === ActivityGroupMaxType.unlimited || this.groupsForm.disabled) {
                    this.groupsForm.get(valueField).disable();
                } else {
                    this.groupsForm.get(valueField).enable();
                }
            });

    }

    private initFormData(groupsConfig: ActivityGroupsConfig): void {
        this.groupsForm.reset();
        this.groupsForm.patchValue({
            limitType: groupsConfig.limit?.type,
            limit: groupsConfig.limit?.value,
            attendees: {
                min: groupsConfig.attendees?.min,
                maxType: groupsConfig.attendees?.max?.type,
                max: groupsConfig.attendees?.max?.value
            },
            companions: {
                min: groupsConfig.companions?.min,
                maxType: groupsConfig.companions?.max?.type,
                max: groupsConfig.companions?.max?.value
            },
            companionsOccupyCapacity: groupsConfig.companions?.occupy_capacity
        });
    }

    private getGroupsToSave(useTemplateConfig: boolean): ActivityGroupsConfig | SessionActivityGroupsConfig {
        return {
            use_venue_template_group_config: useTemplateConfig !== null && useTemplateConfig || undefined, // only for sessions
            limit: {
                type: this.groupsForm.get('limitType').value,
                value: this.groupsForm.get('limitType').value
                    === ActivityGroupMaxType.limited ? this.groupsForm.get('limit').value : null
            },
            attendees: {
                min: this.groupsForm.get('attendees.min').value,
                max: {
                    type: this.groupsForm.get('attendees.maxType').value,
                    value: this.groupsForm.get('attendees.maxType').value === ActivityGroupMaxType.limited ?
                        this.groupsForm.get('attendees.max').value : null
                }
            },
            companions: {
                min: this.groupsForm.get('companions.min').value,
                max: {
                    type: this.groupsForm.get('companions.maxType').value,
                    value: this.groupsForm.get('companions.maxType').value === ActivityGroupMaxType.limited ?
                        this.groupsForm.get('companions.max').value : null
                },
                occupy_capacity: this.groupsForm.get('companionsOccupyCapacity').value
            }
        };
    }

}
