import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventRestriction, EventRestrictionsService } from '@admin-clients/cpanel/promoters/events/restrictions/data-access';
import { EphemeralMessageService, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { StdVenueTplService } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit, ViewChild, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject, catchError, first, takeUntil } from 'rxjs';
import { EventRestrictionFieldsComponent } from '../fields/event-restriction-fields.component';

@Component({
    imports: [
        AsyncPipe, NgIf, NgFor,
        ReactiveFormsModule,
        FlexLayoutModule,
        MaterialModule,
        TranslatePipe,
        SelectSearchComponent,
        EventRestrictionFieldsComponent
    ],
    selector: 'app-event-restriction',
    templateUrl: './event-restriction.component.html',
    styleUrls: ['./event-restriction.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventRestrictionComponent implements OnInit, OnDestroy, WritingComponent {
    private readonly _onDestroy = new Subject<void>();
    private readonly _fb = inject(FormBuilder);
    private readonly _ephemeralSrv = inject(EphemeralMessageService);
    private readonly _venueTplService = inject(StdVenueTplService);
    private readonly _eventsService = inject(EventsService);
    private readonly _eventRestrictionsService = inject(EventRestrictionsService);

    private readonly _form = this._fb.group({
        activated: this.restriction?.activated,
        venue_template_sectors: [this.restriction?.venue_template_sectors, Validators.required],
        fields: this.restriction?.fields
    });

    private _restriction: Partial<EventRestriction>;
    private _eventId: number;

    @ViewChild(EventRestrictionFieldsComponent)
    private _fields: EventRestrictionFieldsComponent;

    readonly loading$ = this._eventRestrictionsService.restrictions.loading$();
    readonly sectors$ = this._venueTplService.getSectors$();

    @Input() set form(value: FormGroup) {
        this._form.setParent(value);
    }

    get form(): FormGroup {
        return this._form;
    }

    @Input() set restriction(value: Partial<EventRestriction>) {
        if (this.equals(this._restriction, value)) return;

        this._restriction = value;

        this.form.reset(value);

        if (value.editing) {
            this.form.enable();
        } else {
            this.form.disable();
        }

        this.updateActivationStatus();
    }

    get restriction(): Partial<EventRestriction> {
        return this._restriction;
    }

    ngOnInit(): void {
        this._eventsService.event.get$()
            .pipe(
                first(Boolean),
                takeUntil(this._onDestroy)
            )
            .subscribe(event => this._eventId = event.id);
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    selectAll(select: MatSelect): void {
        if (this.allSelected(select)) {
            select.options.forEach(opt => opt.value && opt.select());
        } else {
            select.options.forEach(opt => opt.value && opt.deselect());
        }
    }

    allSelected(select: MatSelect): boolean {
        return select?.options?.filter(opt => !!opt.value).some(opt => !opt.selected);
    }

    save(): void {
        if (this.form.valid) {
            const restriction = {
                sid: this.restriction.sid,
                ...this.form.value,
                fields: this._fields.values()
            };
            this._eventRestrictionsService.restrictions.update(this._eventId, restriction)
                .subscribe(() => {
                    this.updateEditingStatus(false);
                    this._ephemeralSrv.showSaveSuccess();
                });
        }
    }

    cancel(): void {
        this.updateEditingStatus(false);
    }

    edit(): void {
        this.updateEditingStatus(true);
    }

    activate(activated: boolean): void {
        const restriction = { sid: this.restriction.sid, activated };
        this._eventRestrictionsService.restrictions.update(this._eventId, restriction)
            .pipe(catchError(error => {
                this.form.get('activated').reset(this.restriction.activated);
                throw error;
            }))
            .subscribe(() => {
                this.form.get('activated').markAsPristine();
                this._ephemeralSrv.showSuccess({
                    msgKey: `EVENTS.RESTRICTIONS.FORMS.FEEDBACKS.${this.form.get('activated').value ?
                        'RESTRICTION_ENABLED' : 'RESTRICTION_DISABLED'}`
                });
            });
    }

    private updateActivationStatus(): void {
        const restrictionFormValue = this.form.value;
        this._eventRestrictionsService.restrictions.structure.fields$(this.restriction.type).pipe(first(Boolean))
            .subscribe(fields => {
                const missingFields = (): boolean => fields.some(field =>
                    !Object.keys(restrictionFormValue.fields).includes(field.id) || !restrictionFormValue.fields[field.id]
                );
                if (!restrictionFormValue.fields || missingFields() || !restrictionFormValue.venue_template_sectors) {
                    this.form.get('activated').disable();
                } else {
                    this.form.get('activated').enable();
                }
            });
    }

    private updateEditingStatus(editing: boolean): void {
        this._eventRestrictionsService.restrictions.set({ sid: this.restriction.sid, editing });
    }

    private equals(a: Partial<EventRestriction>, b: Partial<EventRestriction>): boolean {
        return !!a && !!b &&
            a.editing === b.editing &&
            a.venue_template_sectors === b.venue_template_sectors &&
            JSON.stringify(a.fields) === JSON.stringify(b.fields);
    }
}
