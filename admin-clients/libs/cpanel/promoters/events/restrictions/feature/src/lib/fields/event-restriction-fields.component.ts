import { ExternalEntityService } from '@admin-clients/cpanel/organizations/entities/feature';
import {
    EventConfigurationFields, EventRestriction, EventRestrictionField, EventRestrictionSourceType,
    EventRestrictionsService, eventRestrictionDynamicType
} from '@admin-clients/cpanel/promoters/events/restrictions/data-access';
import { SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { StdVenueTplService } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { AsyncPipe, NgFor, NgSwitch, NgSwitchCase } from '@angular/common';
import { ChangeDetectionStrategy, Component, HostBinding, Input, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, ValidatorFn, Validators } from '@angular/forms';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Observable, defer, filter, map, of, tap } from 'rxjs';

type EventRestrictionFieldVm = Omit<EventRestrictionField, 'type'> & {
    label: string;
    placeholder: string;
    source$: Observable<unknown>;
    type: EventConfigurationFields<unknown>['type'];
    validators: ValidatorFn[];
};

@Component({
    imports: [
        AsyncPipe, NgFor, NgSwitch, NgSwitchCase,
        ReactiveFormsModule,
        MaterialModule,
        TranslatePipe,
        SelectSearchComponent
    ],
    selector: 'app-event-restriction-fields',
    templateUrl: './event-restriction-fields.component.html',
    styleUrls: ['./event-restriction-fields.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventRestrictionFieldsComponent {
    private readonly _fb = inject(FormBuilder);
    private readonly _translate = inject(TranslateService);
    private readonly _eventRestrictionsService = inject(EventRestrictionsService);
    private readonly _externalService = inject(ExternalEntityService);
    private readonly _venueTplService = inject(StdVenueTplService);

    private readonly _sources = {
        [EventRestrictionSourceType.roleId]: this._externalService.roles.get$().pipe(
            filter(Boolean),
            map(roles => roles.map(role => ({ ...role, namedOption: `${role.id}: ${role.name}` })))
        ),
        [EventRestrictionSourceType.quotaId]: this._externalService.periodicities.get$().pipe(
            filter(Boolean),
            map(periodicities => periodicities.map(periodicity => ({
                ...periodicity, namedOption: `${periodicity.id}: ${periodicity.name}`
            })))
        ),
        [EventRestrictionSourceType.termId]: this._externalService.terms.get$().pipe(
            filter(Boolean),
            map(terms => terms.map(term => ({
                ...term
            })))
        ),
        [EventRestrictionSourceType.oneOfAll]: of([
            { id: 'AT_LEAST_ONE_OF', name: this._translate.instant('EVENTS.RESTRICTIONS_FIELDS.AT_LEAST_ONE_OF') },
            { id: 'ALL_OF', name: this._translate.instant('EVENTS.RESTRICTIONS_FIELDS.ALL_OF') }
        ]),
        [EventRestrictionSourceType.timeUnit]: of([
            { id: 'MINUTES', name: this._translate.instant('EVENTS.RESTRICTIONS_FIELDS.MINUTES') },
            { id: 'HOURS', name: this._translate.instant('EVENTS.RESTRICTIONS_FIELDS.HOURS') },
            { id: 'DAYS', name: this._translate.instant('EVENTS.RESTRICTIONS_FIELDS.DAYS') }
        ]),
        [EventRestrictionSourceType.timeLapse]: of([
            { id: 'BEFORE', name: this._translate.instant('EVENTS.RESTRICTIONS_FIELDS.BEFORE') },
            { id: 'AFTER', name: this._translate.instant('EVENTS.RESTRICTIONS_FIELDS.AFTER') }
        ]),
        [EventRestrictionSourceType.capacityId]: this._externalService.capacities.get$().pipe(
            filter(Boolean),
            map(capacities => capacities.map(capacity => ({
                ...capacity,
                namedOption: `${capacity.id}: ${capacity.name}`
            })))
        ),
        [EventRestrictionSourceType.sectorId]: this._venueTplService.getSectors$()
    };

    private _restriction: Partial<EventRestriction>;
    private _fields: EventRestrictionField[];

    @HostBinding('attr.type') type: string;

    @Input() form: FormGroup;

    @Input() set restriction(value: Partial<EventRestriction>) {
        this._restriction = value;
        this.type = value.type;
    }

    fields$ = defer(() => this._eventRestrictionsService.restrictions.structure.fields$(this._restriction?.type).pipe(
        filter(Boolean),
        tap(fields => this._fields = fields),
        map(fields => fields.map(field => ({
            ...field,
            label: `EVENTS.RESTRICTIONS_FIELDS.${field.id}`.toUpperCase(),
            placeholder: `EVENTS.RESTRICTIONS_FIELDS.PLACEHOLDER.${field.id}`.toUpperCase(),
            source$: this._sources[field.source],
            type: eventRestrictionDynamicType(field),
            validators: [Validators.required]
        }))),
        tap(fields => this.setForm(fields))
    ));

    values = (): Record<string, unknown> => Object.keys(this.form.value.fields || {}).reduce<Record<string, unknown>>((curr, key) => {
        const value = this.form.value.fields[key];
        const field = this._fields.find(field => field.id === key);
        if (field?.type === 'INTEGER' && field?.container === 'SINGLE') {
            curr[key] = parseInt(value);
        } else {
            curr[key] = value;
        }
        return curr;
    }, {});

    private setForm(fields: EventRestrictionFieldVm[]): void {
        this.form.setControl('fields', this._fb.group(fields.reduce(
            (acc, field) => (acc[field.id] = this._fb.control({
                value: this._restriction?.fields?.[field.id],
                disabled: true
            }, { validators: field.validators }), acc), {}
        )));
    }
}
