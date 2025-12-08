import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { Channel } from '@admin-clients/cpanel/channels/data-access';
import { MapValue, emptyMap } from '@admin-clients/cpanel/promoters/events/restrictions/data-access';
import { ChannelMemberExternalService, ConfigurationFields, DynamicConfiguration, DynamicField, dynamicType, dynamicValue } from '@admin-clients/cpanel-channels-member-external-data-access';
import { DialogSize, EphemeralMessageService, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { IdName } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormArray, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Observable, Subject, switchMap, takeUntil, tap } from 'rxjs';

export type ConfigurationData = {
    channel: Channel;
    configuration: DynamicConfiguration;
};

@Component({
    selector: 'app-configuration-dialog',
    templateUrl: './configuration-dialog.component.html',
    styleUrls: ['./configuration-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe,
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        ReactiveFormsModule,
        FormControlErrorsComponent
    ]
})
export class ConfigurationDialogComponent implements OnInit, OnDestroy {
    readonly #dialogRef = inject(MatDialogRef<ConfigurationDialogComponent>);
    readonly #channelMemberSrv = inject(ChannelMemberExternalService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #message = inject(MessageDialogService);
    readonly #headsup = inject(EphemeralMessageService);
    readonly #translate = inject(TranslateService);

    #onDestroy = new Subject<void>();

    data: ConfigurationData = inject(MAT_DIALOG_DATA);
    readonly loading$ = booleanOrMerge([
        this.#channelMemberSrv.channelConfigurations.loading$(),
        this.#channelMemberSrv.roles.loading$(),
        this.#channelMemberSrv.terms.loading$(),
        this.#channelMemberSrv.capacities.loading$(),
        this.#channelMemberSrv.periodicities.loading$()
    ]);

    readonly implementations$ = this.#channelMemberSrv.configurations.implementations$(this.data.configuration.operation_name);

    form: UntypedFormGroup;

    fields: ConfigurationFields<unknown>[];

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        const { channel: { id: channelId } } = this.data;
        this.#channelMemberSrv.capacities.load(channelId);
        this.#channelMemberSrv.roles.load(channelId);
        this.#channelMemberSrv.periodicities.load(channelId);
        this.#channelMemberSrv.terms.load(channelId);
    }

    ngOnInit(): void {
        const configuration = this.data.configuration;

        this.fields = this.parsefields(configuration.fields);

        this.form = this.#fb.group({
            implementation: [configuration.implementation, Validators.required],
            fields: this.fieldsForm()
        });

        this.form.get('implementation').valueChanges.pipe(
            switchMap(implementation => this.#channelMemberSrv.configurations.fields$(implementation)),
            tap(fields => {
                this.fields = this.parsefields(fields);
                this.form.setControl('fields', this.fieldsForm());
            }),
            takeUntil(this.#onDestroy)
        ).subscribe();
    }

    ngOnDestroy(): void {
        this.#onDestroy.next();
        this.#onDestroy.complete();
    }

    close(): void {
        if (this.form.dirty) {
            this.#message.defaultDiscardChangesWarn().subscribe(() => this.#dialogRef.close());
        } else {
            this.#dialogRef.close();
        }
    }

    save(): void {
        const { dirty, valid } = this.form;
        const { channel: { id: channelId } } = this.data;

        if (!dirty) {
            return this.#dialogRef.close();
        }
        if (!valid) {
            return this.invalid();
        }

        this.#channelMemberSrv.channelConfigurations.save(channelId, this.value()).subscribe(() => {
            this.#headsup.showSaveSuccess();
            this.#dialogRef.close();
        });
    }

    formArray(field: ConfigurationFields<MapValue[]>): UntypedFormArray {
        return this.form.get(['fields', field.id]) as UntypedFormArray;
    }

    deleteElement(field: ConfigurationFields<MapValue[]>, index: number): void {
        this.formArray(field).removeAt(index);
        this.formArray(field).markAsDirty();
    }

    private invalid = (): void => {
        this.form.markAllAsTouched();
        this.form.setValue(this.form.value);
        scrollIntoFirstInvalidFieldOrErrorMsg();
    };

    private fieldsForm(): UntypedFormGroup {
        return this.#fb.group(this.fields.reduce(
            (acc, field) => (acc[field.id] = this.valueForm(field), acc), {}
        ));
    }

    private valueForm({ value, container, source$ }: ConfigurationFields<unknown>): UntypedFormArray | UntypedFormControl {
        if (container === 'MAP' && Array.isArray(value)) {
            const formArray = this.#fb.array(value.map(value => this.#fb.group(value)));
            formArray.valueChanges
                .pipe(takeUntil(this.#onDestroy))
                .subscribe((values: MapValue[]) => {
                    const { target, source } = values.slice(-1)[0];
                    if (target !== null && source !== null) {
                        formArray.push(this.#fb.group(emptyMap));
                    }
                });
            return formArray;
        }
        if (container === 'LIST' && Array.isArray(value) && !source$) {
            const formArray = this.#fb.array(value?.map((value => this.#fb.control(value))));
            formArray.valueChanges
                .pipe(takeUntil(this.#onDestroy))
                .subscribe((values: string[]) => {
                    const value = values.slice(-1)[0];
                    if (value !== null) {
                        formArray.push(this.#fb.control(null));
                    }
                });
            return formArray;
        }
        return this.#fb.control(value);
    }

    private parsefields(fields: DynamicField[]): ConfigurationFields<unknown>[] {

        const label = (field: DynamicField): string => {
            const key = `MEMBER_EXTERNAL.ADVANCED.FIELD_LABELS.${field.id}`;
            const translateKey = this.#translate.instant(key);
            return translateKey !== key ? translateKey : field.id.toLowerCase();
        };
        const value = dynamicValue;
        const type = dynamicType;
        const options$ = (source: string): Observable<IdName[]> => {
            switch (source) {
                case 'ROLE_ID':
                    return this.#channelMemberSrv.roles.get$();
                case 'CAPACITY_ID':
                    return this.#channelMemberSrv.capacities.get$();
                case 'PERIODICITY_ID':
                    return this.#channelMemberSrv.periodicities.get$();
                case 'TERM_ID':
                    return this.#channelMemberSrv.terms.get$();
                default:
                    return null;
            }
        };

        return fields.map(field => ({
            id: field.id,
            container: field.container,
            value: value(field),
            label: label(field),
            source$: options$(field.source),
            target$: options$(field.target),
            type: type(field)
        }));

    }

    private value(): DynamicConfiguration {
        const { value } = this.form;
        return {
            ...this.data.configuration,
            implementation: value.implementation,
            fields: this.data.configuration.fields.map(field => ({
                ...field,
                value: this.fieldValue(field, value.fields[field.id])
            }))
        };
    }

    private fieldValue({ container }: DynamicField, value: unknown): unknown {
        if (container === 'MAP') {
            return (value as MapValue[])
                .filter(elem => elem?.target != null && elem?.source != null)
                .reduce((acc, elem) => (acc[elem.source] = elem.target, acc), {});
        }
        if (container === 'LIST') {
            return (value as []).filter(elem => elem != null);
        }
        return value;
    }

}
