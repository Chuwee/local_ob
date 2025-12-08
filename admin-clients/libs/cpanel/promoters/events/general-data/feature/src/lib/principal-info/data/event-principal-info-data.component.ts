import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EventFieldsRestriction, EventStatus, PutEvent, Event } from '@admin-clients/cpanel/promoters/events/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        ReactiveFormsModule,
        TranslatePipe,
        FormControlErrorsComponent
    ],
    selector: 'app-event-principal-info-data',
    templateUrl: './event-principal-info-data.component.html'
})
export class EventPrincipalInfoDataComponent implements OnInit {
    private readonly _destroyRef = inject(DestroyRef);
    private _event: Event;

    readonly eventStatus = EventStatus;
    readonly dataForm = inject(FormBuilder).nonNullable.group({
        name: [null as string, [
            Validators.required,
            Validators.maxLength(EventFieldsRestriction.eventNameLength)
        ]],
        reference: [null as string, [Validators.maxLength(EventFieldsRestriction.eventReferenceLength)]]
    });

    readonly operatorMode$ = inject(AuthenticationService).getLoggedUser$().pipe(
        filter(user => user !== null),
        map(user => AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.OPR_MGR])));

    @Input() putEventCtrl: FormControl<PutEvent>;
    @Input() eventStatusCtrl: FormControl<EventStatus>;
    @Input() form: FormGroup;
    @Input() set event(value: Event) {
        this._event = value;
        this.dataForm.reset({
            name: value.name,
            reference: value.reference
        }, { emitEvent: false });
    }

    get event(): Event {
        return this._event;
    }

    ngOnInit(): void {
        this.form.addControl('data', this.dataForm, { emitEvent: false });

        this.putEventCtrl.valueChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(putEvent => {
                if (this.form.invalid) return;

                const {
                    name,
                    reference
                } = this.dataForm.controls;
                if (name.dirty || reference.dirty || this.eventStatusCtrl.dirty) {
                    if (name.dirty) {
                        putEvent.name = name.value;
                    }

                    if (reference.dirty) {
                        putEvent.reference = reference.value;
                    }

                    if (this.eventStatusCtrl.dirty && this.eventStatusCtrl.enabled) {
                        putEvent.status = this.eventStatusCtrl.value;
                    }

                    this.putEventCtrl.setValue(putEvent, { emitEvent: false });
                }
            });
    }
}
