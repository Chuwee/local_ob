import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { Entity } from '@admin-clients/shared/common/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FlexModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, firstValueFrom, Observable, Subject, takeUntil, throwError, tap } from 'rxjs';
import { ExternalEntityService } from '../service/external.service';

@Component({
    selector: 'app-entity-external-smart-booking',
    imports: [
        MaterialModule,
        FormContainerComponent,
        TranslatePipe,
        ReactiveFormsModule,
        FlexModule,
        FormControlErrorsComponent,
        NgIf,
        AsyncPipe
    ],
    templateUrl: './smart-booking.component.html',
    styleUrls: ['./smart-booking.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SmartBookingComponent implements OnInit, OnDestroy {
    private readonly _fb = inject(FormBuilder);
    private readonly _externalService = inject(ExternalEntityService);
    private readonly _entitiesService = inject(EntitiesService);
    private readonly _destroy = new Subject<void>();
    private _entity: Entity;

    readonly form = this._fb.group({
        url: this._fb.control<string>(null, Validators.required),
        credentials: this._fb.group({
            username: this._fb.control<string>(null),
            password: this._fb.control<string>(null)
        })
    });

    readonly statusCtrl = this._fb.control<boolean>({ value: null, disabled: true });
    readonly loading$ = this._externalService.configuration.loading$();

    ngOnInit(): void {
        this.init();
    }

    ngOnDestroy(): void {
        this._externalService.configuration.clear();
        this._destroy.next(null);
        this._destroy.complete();
    }

    async init(): Promise<void> {
        this._entity = await firstValueFrom(this._entitiesService.getEntity$());

        this._externalService.configuration.load(this._entity.id);

        this._externalService.configuration.get$().pipe(
            filter(Boolean),
            takeUntil(this._destroy)
        ).subscribe(configuration => {
            this.form.patchValue(configuration?.smart_booking?.connection);
            this.statusCtrl.setValue(configuration?.smart_booking?.enabled);
        });

        this.form.valueChanges
            .pipe(takeUntil(this._destroy))
            .subscribe(() => this.form.valid ? this.statusCtrl.enable() : this.statusCtrl.disable());
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            return this._externalService.configuration.save(this._entity.id, { smart_booking: { connection: this.form.value } }).pipe(
                tap(() => this.cancel())
            );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg();
            return throwError(() => 'invalid form');
        }
    }

    saveSmartBookingStatus(enabled: boolean): void {
        this._externalService.configuration.save(this._entity.id, { smart_booking: { enabled } })
            .subscribe(() => this.cancel());
    }

    cancel(): void {
        this.form.markAsPristine();
        this._externalService.configuration.reload(this._entity.id);
    }
}
