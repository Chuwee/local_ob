import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { EventAccommodations, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EntitiesBaseService, EntityAccommodationsVendors } from '@admin-clients/shared/common/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnDestroy, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, UntypedFormGroup, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { filter, first, map, shareReplay, takeUntil, tap, withLatestFrom } from 'rxjs/operators';

@Component({
    imports: [
        ReactiveFormsModule,
        TranslatePipe,
        FlexLayoutModule,
        MaterialModule,
        FormControlErrorsComponent
    ],
    selector: 'app-event-accommodations',
    templateUrl: './event-accommodations.component.html',
    styleUrls: ['./event-accommodations.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventAccommodationsComponent implements OnInit, OnDestroy {
    private readonly _fb = inject(FormBuilder);
    private readonly _eventsService = inject(EventsService);
    private readonly _entitiesService = inject(EntitiesBaseService);

    private readonly _onDestroy = new Subject<void>();

    readonly entityAccommodationsVendors$ = this._entitiesService.getEntity$()
        .pipe(
            first(Boolean),
            map(entity => entity.settings?.accommodations?.allowed_vendors)
        );

    readonly isAccommodationsEnabled$ = this._entitiesService.getEntity$()
        .pipe(
            first(Boolean),
            map(entity => !!entity.settings?.accommodations?.enabled),
            tap(isAccommodationsEnabled => isAccommodationsEnabled && this.accommodationsFormGroup.controls.enabled.enable()),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    readonly accommodationsFormGroup = this._fb.group({
        enabled: { value: false as boolean, disabled: true },
        value: [{ value: null as string, disabled: true }, Validators.required]
    });

    @Input() set form(value: UntypedFormGroup) {
        if (value.contains('accommodationsFormGroup')) {
            return;
        }
        value.addControl('accommodationsFormGroup', this.accommodationsFormGroup, { emitEvent: false });
    }

    readonly markForCheck = ((): () => void => {
        const cdr = inject(ChangeDetectorRef);
        return () => cdr.markForCheck();
    })();

    ngOnInit(): void {
        this.initFormHandlers();
        this.updateAccommodationsForm();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        const form = this.accommodationsFormGroup.parent as UntypedFormGroup;
        form.removeControl('accommodationsFormGroup', { emitEvent: false });
    }

    getValue(): EventAccommodations {
        const eventAccommodationsConfig = {
            enabled: this.accommodationsFormGroup.controls.enabled.value,
            vendor: EntityAccommodationsVendors.closer2Event,
            value: this.accommodationsFormGroup.controls.value.value
        };
        return eventAccommodationsConfig;
    }

    private initFormHandlers(): void {
        this.accommodationsFormGroup.get('enabled').valueChanges
            .pipe(
                withLatestFrom(this.isAccommodationsEnabled$),
                takeUntil(this._onDestroy)
            )
            .subscribe(([isEnabled, isEnabledByEntity]) => {
                if (isEnabledByEntity) {
                    if (isEnabled) {
                        this.accommodationsFormGroup.controls.value.enable();
                    } else {
                        this.accommodationsFormGroup.controls.value.disable();
                    }
                }
            });
    }

    private updateAccommodationsForm(): void {
        this._eventsService.event.get$()
            .pipe(
                filter(Boolean),
                takeUntil(this._onDestroy)
            )
            .subscribe(event => {
                this.accommodationsFormGroup.patchValue(event.settings?.accommodations ?? { enabled: false });
                this.accommodationsFormGroup.markAsPristine();
            });
    }
}
