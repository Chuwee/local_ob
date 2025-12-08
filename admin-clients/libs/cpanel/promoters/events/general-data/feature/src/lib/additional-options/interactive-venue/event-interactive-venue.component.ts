import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { EventInteractiveVenue, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EntitiesBaseService, InteractiveVenues } from '@admin-clients/shared/common/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { atLeastOneRequiredInFormGroup } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, Input, OnDestroy, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, UntypedFormGroup, ValidatorFn, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest } from 'rxjs';
import { filter, map } from 'rxjs/operators';

@Component({
    imports: [
        ReactiveFormsModule, TranslatePipe, MaterialModule, FormControlErrorsComponent
    ],
    selector: 'app-event-interactive-venue',
    templateUrl: './event-interactive-venue.component.html',
    styleUrls: ['./event-interactive-venue.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventInteractiveVenueComponent implements OnInit, OnDestroy {
    readonly #fb = inject(FormBuilder);
    readonly #eventsService = inject(EventsService);
    readonly #entityService = inject(EntitiesBaseService);
    readonly #destroyRef = inject(DestroyRef);

    readonly $interactiveVenues = toSignal(this.#entityService.getEntity$()
        .pipe(map(entity => entity.settings.interactive_venue?.allowed_venues || [])));

    readonly interactiveVenueFormGroup = this.#fb.group({
        enabled: { value: false, disabled: true },
        venueType: [{ value: null as InteractiveVenues, disabled: true }, Validators.required],
        venueOptions: this.#fb.group({
            venueViewEnabled: { value: false, disabled: true },
            venueSectorViewEnabled: { value: false, disabled: true },
            venueSeatViewEnabled: { value: false, disabled: true }
        }, { validators: atLeastOneRequiredInFormGroup('required') as ValidatorFn })
    });

    @Input() set form(value: UntypedFormGroup) {
        if (value.contains('interactiveVenueFormGroup')) {
            return;
        }
        value.addControl('interactiveVenueFormGroup', this.interactiveVenueFormGroup, { emitEvent: false });
    }

    readonly markForCheck = ((): () => void => {
        const cdr = inject(ChangeDetectorRef);
        return () => cdr.markForCheck();
    })();

    ngOnInit(): void {
        this.interactiveVenueFormGroup.controls.enabled.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(enabled => {
                if (enabled) {
                    this.interactiveVenueFormGroup.controls.venueType.enable();
                    this.interactiveVenueFormGroup.controls.venueOptions.controls.venueViewEnabled.enable();
                    this.interactiveVenueFormGroup.controls.venueOptions.controls.venueSectorViewEnabled.enable();
                    this.interactiveVenueFormGroup.controls.venueOptions.controls.venueSeatViewEnabled.enable();
                } else {
                    this.interactiveVenueFormGroup.controls.venueType.disable();
                    this.interactiveVenueFormGroup.controls.venueOptions.controls.venueViewEnabled.disable();
                    this.interactiveVenueFormGroup.controls.venueOptions.controls.venueSectorViewEnabled.disable();
                    this.interactiveVenueFormGroup.controls.venueOptions.controls.venueSeatViewEnabled.disable();
                }
            });

        combineLatest([
            this.#eventsService.event.get$(),
            this.#entityService.getEntity$()
        ])
            .pipe(
                filter(resp => resp.every(Boolean)),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(([event, entity]) => {
                if (entity.settings?.interactive_venue?.enabled) {
                    this.interactiveVenueFormGroup.patchValue({
                        enabled: event.settings.interactive_venue?.allow_interactive_venue ?? false,
                        venueType: event.settings.interactive_venue?.interactive_venue_type,
                        venueOptions: {
                            venueViewEnabled: event.settings.interactive_venue?.allow_venue_3d_view ?? false,
                            venueSectorViewEnabled: event.settings.interactive_venue?.allow_sector_3d_view ?? false,
                            venueSeatViewEnabled: event.settings.interactive_venue?.allow_seat_3d_view ?? false
                        }
                    });
                    this.interactiveVenueFormGroup.get('enabled').enable({ emitEvent: false });
                    const availableVenues = entity.settings.interactive_venue.allowed_venues || [];
                    if (availableVenues.length === 1 && !event.settings.interactive_venue?.interactive_venue_type) {
                        this.interactiveVenueFormGroup.get('venueType').setValue(entity.settings.interactive_venue.allowed_venues[0]);
                    }
                }
                this.interactiveVenueFormGroup.markAsPristine();
            });
    }

    ngOnDestroy(): void {
        const form = this.interactiveVenueFormGroup.parent as UntypedFormGroup;
        form.removeControl('interactiveVenueFormGroup', { emitEvent: false });
    }

    getValue(): EventInteractiveVenue {
        const interactiveVenueFormValues = this.interactiveVenueFormGroup.getRawValue();
        return {
            allow_interactive_venue: interactiveVenueFormValues.enabled,
            interactive_venue_type: interactiveVenueFormValues.venueType,
            allow_venue_3d_view: interactiveVenueFormValues.venueOptions.venueViewEnabled,
            allow_seat_3d_view: interactiveVenueFormValues.venueOptions.venueSeatViewEnabled,
            allow_sector_3d_view: interactiveVenueFormValues.venueOptions.venueSectorViewEnabled
        };
    }

}
