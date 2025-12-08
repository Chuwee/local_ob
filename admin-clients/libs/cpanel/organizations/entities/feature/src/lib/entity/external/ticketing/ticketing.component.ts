import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { digitalTicketModes, EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { Entity } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnDestroy, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, firstValueFrom, Observable, throwError, tap } from 'rxjs';
import { CapacitiesTableComponent } from '../capacities-table/capacities-table.component';
import { ClubCodeSelectorComponent } from '../club-code-selector/club-code-selector.component';
import {
    CapacityNameType, Environment, ExternalEntityConfiguration, HttpProtocol,
    PartnerValidationType, WSConnectionVersion
} from '../models/configuration.model';
import { ExternalEntityService } from '../service/external.service';

type TicketingConfig = ExternalEntityConfiguration['ticketing'];

@Component({
    selector: 'app-entity-external-ticketing',
    imports: [
        CommonModule, FormsModule, ReactiveFormsModule, FlexLayoutModule, CapacitiesTableComponent,
        FormContainerComponent, TranslatePipe, FormControlErrorsComponent, ClubCodeSelectorComponent,
        MaterialModule
    ],
    templateUrl: './ticketing.component.html',
    styleUrls: ['./ticketing.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EntityExternalTicketingComponent implements OnInit, OnDestroy {
    readonly #fb = inject(FormBuilder);
    readonly #externalService = inject(ExternalEntityService);
    readonly #entitiesService = inject(EntitiesService);
    readonly #ephemeralMsg = inject(EphemeralMessageService);
    readonly #destroyRef = inject(DestroyRef);

    readonly connectionForm = this.#fb.group({
        name: null as string,
        username: null as string,
        password: null as string,
        protocol: null as HttpProtocol,
        ip: null as string,
        port: null as number,
        ping_requests_blocked: null as boolean,
        ws_connection_version: null as WSConnectionVersion
    });

    readonly operativeForm = this.#fb.group({
        partner_validation_type: null as PartnerValidationType,
        payment_method: null as number,
        generate_partner_ticket: null as boolean,
        scheduled: null as boolean,
        fixed_delay_in_minutes: null as number,
        check_partner_pin_regexp: null as boolean,
        partner_pin_regexp: null as string,
        send_id_number: null as boolean,
        id_number_max_length: null as number,
        digital_ticket_mode: null as string
    });

    readonly capacityForm = this.#fb.group({
        capacity_name_type: null as CapacityNameType,
        capacities: [null as number[]],
        season: null as string,
        members_capacity_id: null as number
    });

    readonly form = this.#fb.group({
        avet_ws_environment: null as Environment,
        avet_connection_type: null as ExternalEntityConfiguration['avet_connection_type'],
        connection: this.connectionForm,
        operative: this.operativeForm,
        capacity: this.capacityForm
    });

    readonly loading$: Observable<boolean> = booleanOrMerge([
        this.#externalService.clubCodes.loading$(),
        this.#externalService.clubCodes.linking$(),
        this.#externalService.configuration.loading$()
    ]);

    readonly versions = Object.values(WSConnectionVersion);
    readonly validationTypes = Object.values(PartnerValidationType);
    readonly capacityTypes = Object.values(CapacityNameType);
    readonly protocols = Object.values(HttpProtocol);
    readonly environments = Object.values(Environment);
    readonly digitalTicketModes = digitalTicketModes;

    entity: Entity;

    ngOnInit(): void {
        this.init();
    }

    ngOnDestroy(): void {
        this.#externalService.configuration.clear();
    }

    async init(): Promise<void> {
        this.entity = await firstValueFrom(this.#entitiesService.getEntity$());

        this.#externalService.configuration.load(this.entity.id);

        this.#externalService.configuration.get$().pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(({ ticketing, club_code: code, avet_ws_environment: avetWsEnvironment, avet_connection_type: avetConnectionType }) => {
            this.form.reset({
                avet_ws_environment: avetWsEnvironment,
                ...ticketing,
                operative: {
                    ...ticketing.operative,
                    fixed_delay_in_minutes: ticketing.operative.fixed_delay_ms / 1000 / 60
                },
                avet_connection_type: avetConnectionType
            });
            if (!code) {
                this.form.disable({ emitEvent: false });
            }
        });

        this.operativeForm.controls.id_number_max_length.addValidators([
            Validators.min(5),
            Validators.max(30),
            Validators.required
        ]);

        this.operativeForm.controls.partner_pin_regexp.addValidators([Validators.required]);

        this.operativeForm.controls.scheduled.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(checked => {
                const control = this.form.get(['operative', 'fixed_delay_in_minutes']);
                checked && this.form.enabled ? control.enable({ emitEvent: false }) : control.disable({ emitEvent: false });
            });

        this.operativeForm.controls.send_id_number.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(checked => {
                const control = this.form.get(['operative', 'id_number_max_length']);
                checked && this.form.enabled ? control.enable({ emitEvent: false }) : control.disable({ emitEvent: false });
            });

        this.operativeForm.controls.check_partner_pin_regexp.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(checked => {
                const control = this.form.get(['operative', 'partner_pin_regexp']);
                checked && this.form.enabled ? control.enable({ emitEvent: false }) : control.disable({ emitEvent: false });
            });

        this.form.controls.avet_connection_type.valueChanges.pipe(
            takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => {
                if (this.form.controls.avet_connection_type.status !== 'DISABLED') {
                    this.form.enable({ emitEvent: false });
                    this.connectionForm.controls.name.enable({ emitEvent: false });
                    if (!this.operativeForm.controls.send_id_number.value) {
                        this.operativeForm.controls.id_number_max_length.disable({ emitEvent: false });
                    }
                    if (!this.operativeForm.controls.check_partner_pin_regexp.value) {
                        this.operativeForm.controls.partner_pin_regexp.disable({ emitEvent: false });
                    }
                }
            });
    }

    save(): void {
        this.save$().subscribe(() => this.cancel());
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            if (this.form.get('avet_connection_type').value === 'SOCKET') {
                return this.#externalService.configuration.save(this.entity.id, {
                    avet_connection_type: this.form.value.avet_connection_type
                }).pipe(tap(() => this.cancel()));
            }
            const { operative: { fixed_delay_in_minutes: delayInSecs } } = this.form.value;
            return this.#externalService.configuration.save(this.entity.id, {
                avet_ws_environment: this.form.value.avet_ws_environment,
                ticketing: {
                    connection: { ...this.form.value.connection },
                    capacity: { ...this.form.value.capacity },
                    operative: {
                        ...this.form.value.operative,
                        fixed_delay_ms: delayInSecs * 1000 * 60
                    }
                },
                avet_connection_type: this.form.value.avet_connection_type
            }).pipe(tap(() => {
                this.form.controls.avet_connection_type.dirty
                    ? this.#ephemeralMsg.showSuccess({ msgKey: 'ENTITY.EXTERNAL.CONNECTION.SAVE_SUCCESS' })
                    : this.#ephemeralMsg.showSaveSuccess();
            }));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg();
            return throwError(() => 'invalid form');
        }
    }

    async cancel(): Promise<void> {
        this.form.markAsPristine();
        this.#externalService.configuration.reload(this.entity.id);
    }

}
