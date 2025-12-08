import { Restriction, RestrictionField, ChannelMemberExternalService, dynamicType } from '@admin-clients/cpanel-channels-member-external-data-access';
import { ChangeDetectionStrategy, Component, inject, Input, HostBinding } from '@angular/core';
import { FormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { defer, filter, map, of, tap } from 'rxjs';

@Component({
    selector: 'app-members-restriction-fields',
    templateUrl: './restriction-fields.component.html',
    styleUrls: ['./restriction-fields.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class MemberExternalRestrictionFieldsComponent {

    private _restriction!: Partial<Restriction>;
    private _fields!: RestrictionField[];

    private readonly _fb = inject(FormBuilder);
    private readonly _translate = inject(TranslateService);
    private readonly _membersSrv = inject(ChannelMemberExternalService);
    private readonly _sources = {
        ['ROLE_ID']: this._membersSrv.roles.get$(),
        ['MINIMUM_MAXIMUM']: of([
            { id: 'MAXIMUM', name: this._translate.instant('MEMBER_EXTERNAL.RESTRICTIONS.MAXIMUM') },
            { id: 'MINIMUM', name: this._translate.instant('MEMBER_EXTERNAL.RESTRICTIONS.MINIMUM') }
        ])
    };

    @HostBinding('attr.type') type: string;

    @Input() form: UntypedFormGroup;

    @Input() set restriction(value: Partial<Restriction>) {
        this._restriction = value;
        this.type = value.restriction_type;
    }

    fields$ = defer(() => this._membersSrv.restrictions.structure.fields$(this._restriction?.restriction_type).pipe(
        filter(fields => !!fields),
        tap(fields => this._fields = fields),
        map(fields => fields.map(field => ({
            ...field,
            label: `MEMBER_EXTERNAL.RESTRICTIONS.FIELDS.${field.id}`.toUpperCase(),
            placeholder: `MEMBER_EXTERNAL.RESTRICTIONS.PLACEHOLDER.${field.id}`.toUpperCase(),
            source$: this._sources[field.source],
            type: dynamicType(field),
            validators: dynamicType(field) === 'number' ? [Validators.min(0)] : []
        }))),
        tap(fields => this.setForm(fields))
    ));

    constructor() { }

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

    private setForm(fields: (RestrictionField & any)[]): void {
        return this.form.setControl('fields', this._fb.group(fields.reduce(
            (acc, field) => (acc[field.id] = this._fb.control({
                value: this._restriction?.fields?.[field.id],
                disabled: true
            }, { validators: field.validators }), acc), {}
        )));
    }

}
