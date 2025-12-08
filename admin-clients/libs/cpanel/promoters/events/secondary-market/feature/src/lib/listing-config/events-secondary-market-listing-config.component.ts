
import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { Event, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { SecondaryMarketService, SecondaryMarketConfig } from '@admin-clients/cpanel/promoters/secondary-market/data-access';
import { SecondaryMarketConfigComponent } from '@admin-clients/cpanel/promoters/secondary-market/feature';
import { DialogSize, EphemeralMessageService, MessageDialogConfig, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, ViewChild, AfterViewInit, OnDestroy } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { TranslatePipe } from '@ngx-translate/core';
import { throwError, Observable, first, tap, filter, of, delay, switchMap } from 'rxjs';

const unsavedChangesDialogData: MessageDialogConfig = {
    actionLabel: 'FORMS.ACTIONS.UPDATE',
    showCancelButton: true,
    message: 'SECONDARY_MARKET.STATUS_CHANGE_WARNING.DESCRIPTION',
    title: 'SECONDARY_MARKET.STATUS_CHANGE_WARNING.TITLE',
    size: DialogSize.MEDIUM
};
@Component({
    selector: 'ob-events-secondary-market-listing-config',
    imports: [FormContainerComponent, SecondaryMarketConfigComponent,
        ReactiveFormsModule, MatProgressSpinnerModule, MatSlideToggleModule, TranslatePipe, MatExpansionModule],
    templateUrl: './events-secondary-market-listing-config.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventsSecondaryMarketListingConfigComponent implements AfterViewInit, OnDestroy {
    readonly #destroy = inject(DestroyRef);
    readonly #eventSrv = inject(EventsService);
    readonly #secondaryMarketSrv = inject(SecondaryMarketService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #msgDialogSrv = inject(MessageDialogService);

    @ViewChild(SecondaryMarketConfigComponent, { static: true })
    readonly configurationComponent: SecondaryMarketConfigComponent;

    #event: Event;

    // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
    get form() {
        return this.configurationComponent?.form;
    }

    $loading = toSignal(this.#secondaryMarketSrv.eventConfiguration.loading$());

    ngAfterViewInit(): void {
        this.#eventSrv.event.get$().pipe(
            first(Boolean),
            tap(event => this.#secondaryMarketSrv.eventConfiguration.load(event.id)),
            takeUntilDestroyed(this.#destroy)
        ).subscribe(event => {
            this.#event = event;
        });

        this.#secondaryMarketSrv.eventConfiguration.get$().pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#destroy)
        ).subscribe(settings => this.form.reset(settings));
    }

    ngOnDestroy(): void {
        this.#secondaryMarketSrv.eventConfiguration.clear();
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<void> {
        if (!this.form.valid) {
            this.form.markAllAsTouched();
            this.form.patchValue(this.form.value);
            return throwError(() => scrollIntoFirstInvalidFieldOrErrorMsg());
        }
        return this.#secondaryMarketSrv.eventConfiguration.save(this.#event.id, this.form.value as SecondaryMarketConfig).pipe(
            tap(() => this.form.reset(this.form.value)),
            tap(() => this.#ephemeralSrv.showSaveSuccess())
        );
    }

    handleStatusChange(isActive: boolean): void {
        if (this.form.controls.commission?.dirty || this.form.controls.price?.dirty) {
            of(null).pipe(
                delay(100),
                tap(() => this.form.controls.enabled.setValue(!isActive)),
                switchMap(() =>
                    this.#msgDialogSrv.showWarn(unsavedChangesDialogData)
                ),
                switchMap(saveAccepted => {
                    if (saveAccepted) {
                        return this.save$();
                    } else {
                        this.cancel();
                        return of(false);
                    }
                }
                )
            ).subscribe();
        } else {
            this.save$().subscribe();
        }
    }

    cancel(): void {
        this.#secondaryMarketSrv.eventConfiguration.load(this.#event.id);
    }

}
