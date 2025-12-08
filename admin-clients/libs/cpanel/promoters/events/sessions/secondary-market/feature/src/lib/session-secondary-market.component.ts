import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EventSessionsService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { SecondaryMarketService, SecondaryMarketConfig } from '@admin-clients/cpanel/promoters/secondary-market/data-access';
import { SecondaryMarketConfigComponent } from '@admin-clients/cpanel/promoters/secondary-market/feature';
import {
    DialogSize, EphemeralMessageService, HelpButtonComponent, MessageDialogConfig, MessageDialogService
} from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { CommonModule } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, ViewChild } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, delay, filter, map, Observable, of, startWith, switchMap, tap, throwError } from 'rxjs';

const unsavedChangesDialogData: MessageDialogConfig = {
    actionLabel: 'FORMS.ACTIONS.UPDATE',
    showCancelButton: true,
    message: 'SECONDARY_MARKET.STATUS_CHANGE_WARNING.DESCRIPTION',
    title: 'SECONDARY_MARKET.STATUS_CHANGE_WARNING.TITLE',
    size: DialogSize.MEDIUM
};
@Component({
    selector: 'ob-session-secondary-market',
    imports: [CommonModule, SecondaryMarketConfigComponent, FormContainerComponent, MatButtonModule, FlexLayoutModule,
        ReactiveFormsModule, MatProgressSpinnerModule, MatSlideToggleModule, TranslatePipe, MatExpansionModule, MatIconModule,
        MatTooltipModule, HelpButtonComponent],
    templateUrl: './session-secondary-market.component.html',
    styleUrls: ['./session-secondary-market.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SessionSecondaryMarketComponent implements AfterViewInit, OnDestroy {
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #destroy = inject(DestroyRef);
    readonly #sessionsService = inject(EventSessionsService);
    readonly #secondaryMarketSrv = inject(SecondaryMarketService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);

    @ViewChild(SecondaryMarketConfigComponent, { static: true })
    readonly configurationComponent: SecondaryMarketConfigComponent;

    resetButtonDisabled$: Observable<boolean>;
    #sessionId: number;

    // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
    get form() {
        return this.configurationComponent?.form;
    }

    loading$ = this.#secondaryMarketSrv.sessionConfiguration.loading$();

    ngAfterViewInit(): void {
        this.resetButtonDisabled$ = combineLatest([
            this.#secondaryMarketSrv.sessionConfiguration.get$().pipe(map(config => config?.type === 'SESSION')),
            this.form.valueChanges.pipe(startWith(null), map(() => !this.form?.dirty))
        ]).pipe(
            map(([isSession, isNotDirty]) => !isSession && isNotDirty)
        );

        this.#sessionsService.session.get$().pipe(
            filter(Boolean),
            tap(session => this.#secondaryMarketSrv.sessionConfiguration.load(session.id)),
            takeUntilDestroyed(this.#destroy)
        ).subscribe(session => {
            this.#sessionId = session.id;
        });

        this.#secondaryMarketSrv.sessionConfiguration.get$().pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#destroy)
        ).subscribe(settings => this.form.reset(settings));
    }

    ngOnDestroy(): void {
        this.#secondaryMarketSrv.sessionConfiguration.clear();
    }

    handleStatusChange(isActive: boolean): void {
        if (this.form.controls.commission?.dirty || this.form.controls.price?.dirty) {
            of(null).pipe(
                delay(100),
                tap(() => this.form.controls.enabled.setValue(!isActive)),
                switchMap(() =>
                    this.#msgDialogService.showWarn(unsavedChangesDialogData)
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

    resetConfig(): void {
        this.#msgDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'SECONDARY_MARKET.RESET_TO_EVENT_CONFIG.TITLE',
            message: 'SECONDARY_MARKET.RESET_TO_EVENT_CONFIG.BODY',
            actionLabel: 'FORMS.ACTIONS.RESTORE',
            showCancelButton: true
        })
            .pipe(filter(Boolean),
                switchMap(() => this.#secondaryMarketSrv.sessionConfiguration.delete(this.#sessionId)
                )).subscribe(() => {
                    this.#secondaryMarketSrv.sessionConfiguration.load(this.#sessionId);
                    this.#ephemeralSrv.showSuccess({ msgKey: 'SECONDARY_MARKET.RESET_TO_EVENT_CONFIG.SUCCESS' });
                });
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
        return this.#secondaryMarketSrv.sessionConfiguration.save(this.#sessionId, this.form.value as SecondaryMarketConfig).pipe(
            tap(() => this.#secondaryMarketSrv.sessionConfiguration.load(this.#sessionId)),
            tap(() => this.#ephemeralSrv.showSaveSuccess())
        );
    }

    cancel(): void {
        this.#secondaryMarketSrv.sessionConfiguration.load(this.#sessionId);
    }

}
