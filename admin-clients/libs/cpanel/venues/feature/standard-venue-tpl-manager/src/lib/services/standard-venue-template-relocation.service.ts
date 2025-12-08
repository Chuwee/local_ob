import { DestroyRef, inject, Injectable, OnDestroy, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { distinctUntilChanged, filter, first, map, withLatestFrom } from 'rxjs/operators';
import { VenueTemplateOriginOrderItem } from '../models/venue-template-origin-order-item';
import { StandardVenueTemplateState } from '../state/standard-venue-template.state';
import { StandardVenueTemplateBaseService } from './standard-venue-template-base.service';

const MAX_SELECTION_LIMIT = 100;

@Injectable()
export class StandardVenueTemplateRelocationService implements OnDestroy {
    readonly #destroyRef = inject(DestroyRef);
    readonly #stdVenueTplState = inject(StandardVenueTemplateState);
    readonly #stdVenueTplBaseSrv = inject(StandardVenueTemplateBaseService);

    readonly selectionLimit = signal(MAX_SELECTION_LIMIT);

    readonly selectedOriginSeatIds = Object.freeze({
        set: (seatIds: number[]) => this.#stdVenueTplState.selectedOriginSeatIds.setValue(seatIds),
        get$: () => this.#stdVenueTplState.selectedOriginSeatIds.getValue$(),
        clear: () => this.#stdVenueTplState.selectedOriginSeatIds.setValue(null)
    });

    readonly selectedOriginOrderItems = Object.freeze({
        get$: () => this.#stdVenueTplState.selectedOriginOrderItems.getValue$(),
        set: (orderItems: VenueTemplateOriginOrderItem[]) => this.#stdVenueTplState.selectedOriginOrderItems.setValue(orderItems),
        clear: () => this.#stdVenueTplState.selectedOriginOrderItems.setValue(null)
    });

    readonly selectedDestinationSeats = Object.freeze({
        get$: () => this.#stdVenueTplState.selectedDestinationSeats.getValue$(),
        set: (destinationSeats: { [originId: number]: number }) =>
            this.#stdVenueTplState.selectedDestinationSeats.setValue(destinationSeats),
        clear: () => this.#stdVenueTplState.selectedDestinationSeats.setValue(null)
    });

    constructor() {
        this.#stdVenueTplBaseSrv.relocation.getStatus$()
            .pipe(
                filter(Boolean),
                map(status => status.activeStep),
                distinctUntilChanged(),
                withLatestFrom(this.selectedOriginSeatIds.get$()),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(([activeStep, selectedOriginSeatIds]) => {
                if (activeStep === 0) {
                    this.selectionLimit.set(MAX_SELECTION_LIMIT);
                } else {
                    this.selectionLimit.set(selectedOriginSeatIds.length);
                    // When step 2(1) build the destination selected seats structure
                    this.#buildDestinationSelectedSeats();
                }
            });
    }

    ngOnDestroy(): void {
        this.clearAll();
    }

    clearAll(): void {
        this.selectedOriginSeatIds.clear();
        this.selectedOriginOrderItems.clear();
        this.selectedDestinationSeats.clear();
        this.#stdVenueTplBaseSrv.relocation.updateStatus({
            activeStep: 0,
            originSeatsSelected: false,
            canStartRelocation: false
        });
    }

    #buildDestinationSelectedSeats(): void {
        this.selectedOriginSeatIds.get$()
            .pipe(
                first(),
                withLatestFrom(this.selectedDestinationSeats.get$())
            )
            .subscribe(([selectedOriginSeatIds, selectedDestinationSeats]) => {
                const destinationSeats =
                    Object.fromEntries(selectedOriginSeatIds.map(originId => [originId, selectedDestinationSeats?.[originId]]));
                this.selectedDestinationSeats.set(destinationSeats);
            });
    }

}
