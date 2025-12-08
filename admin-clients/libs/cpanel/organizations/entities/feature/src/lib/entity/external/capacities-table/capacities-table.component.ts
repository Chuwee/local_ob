import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { Entity } from '@admin-clients/shared/common/data-access';
import {
    MessageDialogService, EphemeralMessageService, MessageType, DialogSize
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormControl, FormControlDirective, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, filter, firstValueFrom, map, Observable, switchMap, tap } from 'rxjs';
import { ExternalCapacity } from '../models/external-capacity.model';
import { ExternalEntityService } from '../service/external.service';

@Component({
    selector: 'app-capacities-table',
    imports: [
        CommonModule, MaterialModule, TranslatePipe, FormsModule, ReactiveFormsModule, FlexLayoutModule
    ],
    templateUrl: './capacities-table.component.html',
    styleUrls: ['./capacities-table.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CapacitiesTableComponent implements OnInit {
    readonly #externalService = inject(ExternalEntityService);
    readonly #entitiesService = inject(EntitiesService);
    readonly #message = inject(MessageDialogService);
    readonly #ephemeral = inject(EphemeralMessageService);
    readonly #formControl = inject(FormControlDirective);
    readonly #destroyRef = inject(DestroyRef);
    #pristineCapacities: number[] = [];

    entity: Entity;

    readonly form = new FormGroup<Record<string, FormControl<boolean>>>({});
    @Input() defaultCapacity: FormControl<number>;
    readonly columns = ['default', 'connection', 'id', 'name', 'enabled', 'status', 'import', 'actions'];

    readonly capacities$ = this.#externalService.capacities.get$().pipe(
        filter(capacities => !!capacities),
        tap(capacities => capacities.forEach(cap => this.form.setControl(
            `${cap.id}`, new FormControl({ value: cap.enabled, disabled: cap.loaded })
        ))),
        tap(() => this.form.markAsPristine()),
        tap(() => this.#formControl.control?.reset(this.capacities())),
        tap(() => this.#pristineCapacities = this.capacities())
    );

    readonly loading$ = combineLatest([
        this.#externalService.capacities.loading$(),
        this.#externalService.configuration.loading$()
    ]).pipe(
        takeUntilDestroyed(this.#destroyRef),
        map(([capLoading, configLoading]) => capLoading && !configLoading)
    );

    readonly isHandsetOrTablet$: Observable<boolean> = inject(BreakpointObserver)
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    ngOnInit(): void {
        this.init();
    }

    readonly capacities = (): number[] =>
        Object.keys(this.form.getRawValue()).map(id => this.form.getRawValue()[id] && +id).filter(id => !!id);

    async init(): Promise<void> {
        this.entity = await firstValueFrom(this.#entitiesService.getEntity$());

        this.#externalService.capacities.clear();

        this.form.valueChanges.pipe(
            tap(() => {
                this.#formControl.control.setValue(this.capacities());
                this.#formControl.control.markAsDirty();
                FormControlHandler.checkAndRefreshDirtyState(this.#formControl.control, this.#pristineCapacities);
            }),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe();

        this.#externalService.configuration.get$().pipe(
            filter(Boolean),
            tap(() => this.#externalService.capacities.reload(this.entity.id)),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe();
    }

    import(capacity: ExternalCapacity): void {
        let warning: Observable<boolean>;

        if (this.form.dirty) {
            warning = this.showWarningSaveImport().pipe(switchMap(() => this.save()));
        } else {
            warning = this.showWarningImport();
        }

        warning.pipe(
            switchMap(() => this.#externalService.capacities.import(this.entity.id, capacity.id)),
            tap(() => this.#externalService.capacities.reload(this.entity.id)),
            tap(() => this.#ephemeral.show({
                type: MessageType.info,
                msgKey: 'ENTITY.EXTERNAL.CAPACITIES.IMPORT_IN_PROGRESS',
                duration: 60 * 1000,
                hideCloseBtn: false
            }))
        ).subscribe();
    }

    delete(capacity: ExternalCapacity): void {
        this.#message.showWarn({
            title: 'ENTITY.EXTERNAL.CAPACITIES.DELETE_TITLE',
            message: 'ENTITY.EXTERNAL.CAPACITIES.DELETE_DESCRIPTION',
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true,
            size: DialogSize.SMALL
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this.#externalService.capacities.delete(this.entity.id, capacity.id)),
                tap(() => this.#ephemeral.show({
                    type: MessageType.info,
                    msgKey: 'ENTITY.EXTERNAL.CAPACITIES.DELETE_IN_PROGRESS',
                    duration: 60 * 1000,
                    hideCloseBtn: false
                }))
            )
            .subscribe();
    }

    remap(capacity: ExternalCapacity): void {
        this.#message.showWarn({
            size: DialogSize.SMALL,
            title: 'ENTITY.EXTERNAL.CAPACITIES.REMAP_TITLE',
            message: 'ENTITY.EXTERNAL.CAPACITIES.REMAP_DESCRIPTION',
            messageParams: { capacityName: capacity.name },
            actionLabel: 'FORMS.ACTIONS.REMAP',
            showCancelButton: true
        }).subscribe(success => {
            if (success) {
                this.#externalService.capacities.mapping(this.entity.id, capacity.id).subscribe(() => this.#ephemeral.showSuccess({
                    msgKey: 'ENTITY.EXTERNAL.CAPACITIES.MAPPING_SUCCESS',
                    msgParams: { capacityName: capacity.name }
                }));
            }
        });
    }

    reload(): void {
        this.#externalService.configuration.reload(this.entity.id);
    }

    setDefault(index: number): void {
        this.defaultCapacity.setValue(index);
        this.#formControl.control.setValue(this.capacities());
        this.#formControl.control.markAsDirty();
    }

    refreshCapacity(capacityId: number, capacityName: string): void {
        this.#externalService.capacities.refresh(this.entity.id, capacityId).subscribe(() => this.#ephemeral.showSuccess({
            msgKey: 'ENTITY.EXTERNAL.CAPACITIES.REFRESH_SUCCESS',
            msgParams: { capacity: capacityName }
        }));
    }

    private save(): Observable<boolean> {
        return this.#externalService.configuration.save(this.entity.id, {
            ticketing: {
                capacity: {
                    capacities: this.capacities(),
                    members_capacity_id: this.defaultCapacity.value
                }
            }
        }).pipe(map(() => true));
    }

    private showWarningImport(): Observable<boolean> {
        return this.#message.showWarn({
            size: DialogSize.SMALL,
            title: 'ENTITY.EXTERNAL.CAPACITIES.IMPORT_TITLE',
            message: 'ENTITY.EXTERNAL.CAPACITIES.IMPORT_DESCRIPTION',
            actionLabel: 'ENTITY.EXTERNAL.CAPACITIES.IMPORT'
        })
            .pipe(filter(Boolean));
    }

    private showWarningSaveImport(): Observable<boolean> {
        return this.#message.showWarn({
            size: DialogSize.MEDIUM,
            title: 'ENTITY.EXTERNAL.CAPACITIES.IMPORT_TITLE',
            message: 'ENTITY.EXTERNAL.CAPACITIES.SAVE_IMPORT_DESCRIPTION',
            actionLabel: 'ENTITY.EXTERNAL.CAPACITIES.SAVE_AND_IMPORT'
        })
            .pipe(filter(Boolean));
    }

}
