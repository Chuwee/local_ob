import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable, Subject, throwError } from 'rxjs';
import { filter, map, takeUntil, tap } from 'rxjs/operators';
import {
    VenueSpaceCapacityType, VenuesService, PutVenueSpaceRequest, VenueSpaceDetails, VenueSpacesLoadCase
} from '@admin-clients/cpanel/venues/data-access';
import { BreadcrumbsService, EphemeralMessageService, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { VenueSpacesStateMachine } from '../venue-spaces-state-machine';

@Component({
    selector: 'app-venue-space-details',
    templateUrl: './venue-space-details.component.html',
    styleUrls: ['./venue-space-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class VenueSpaceDetailsComponent implements OnInit, OnDestroy, WritingComponent {
    private _onDestroy = new Subject<void>();
    private _venueId: number;
    private _venueSpaceId: number;

    readonly venueSpaceCapacityType = VenueSpaceCapacityType;

    form: UntypedFormGroup;
    isInProgress$: Observable<boolean>;
    venueSpaceId$: Observable<number>;

    private get _breadcrumb(): string | undefined {
        return this._route.snapshot.data['breadcrumb'];
    }

    constructor(
        private _fb: UntypedFormBuilder,
        private _venueSpacesSM: VenueSpacesStateMachine,
        private _venuesService: VenuesService,
        private _breadcrumbsService: BreadcrumbsService,
        private _msgDialogService: MessageDialogService,
        private _ephemeralMessage: EphemeralMessageService,
        private _route: ActivatedRoute
    ) { }

    ngOnInit(): void {
        this.initForm();
        this.initFormHandlers();

        this._venuesService.getVenueSpace$()
            .pipe(
                filter(venueSpace => !!venueSpace),
                tap(venueSpace => {
                    this._venueId = venueSpace.venue_id;
                    this._venueSpaceId = venueSpace.id;
                    this._breadcrumbsService.addDynamicSegment(this._breadcrumb, venueSpace.name);
                }),
                takeUntil(this._onDestroy)
            )
            .subscribe(venueSpace => this.updateFormValues(venueSpace));

        this.initComponentModels();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    cancel(): void {
        this.reloadModels();
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const formValue = this.form.value;
            const request: PutVenueSpaceRequest = {
                name: formValue.name,
                capacity: { type: formValue.capacityType },
                notes: formValue.notes
            };
            if (this.form.get('capacityType').value === this.venueSpaceCapacityType.fixed) {
                request.capacity = {
                    ...request.capacity,
                    value: formValue.capacityValue
                };
            }
            return this._venuesService.saveVenueSpace(this._venueId, this._venueSpaceId, request)
                .pipe(tap(() => this._ephemeralMessage.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe(() => this.reloadModels());
    }

    private initForm(): void {
        this.form = this._fb.group({
            name: [null, Validators.required],
            capacityType: [null, Validators.required],
            capacityValue: [{ value: null, disabled: true }, [Validators.required, Validators.min(1)]],
            notes: [null, Validators.maxLength(1000)]
        });
    }

    private updateFormValues(venueSpace: VenueSpaceDetails): void {
        this.form.patchValue({
            name: venueSpace.name,
            capacityType: venueSpace.capacity?.type,
            capacityValue: venueSpace.capacity?.value,
            notes: venueSpace.notes
        });
        this.form.markAsPristine();
    }

    private initComponentModels(): void {
        this.venueSpaceId$ = this._venuesService.getVenueSpace$()
            .pipe(
                filter(venueSpace => !!venueSpace),
                map(venueSpace => venueSpace.id)
            );
        this.isInProgress$ = booleanOrMerge([
            this._venuesService.isVenueSpaceLoading$(),
            this._venuesService.isVenueSpaceSaving$()
        ]);
    }

    private initFormHandlers(): void {
        this.form.get('capacityType').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(value => {
                if (value === this.venueSpaceCapacityType.unlimited) {
                    this.form.get('capacityValue').disable();
                } else {
                    this.form.get('capacityValue').enable();
                }
            });
    }

    private reloadModels(): void {
        this.form.markAsPristine();
        this._venueSpacesSM.setCurrentState({
            state: VenueSpacesLoadCase.loadVenueSpace,
            idPath: this._venueSpaceId
        });
    }
}
