import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { EventSessionsService, SessionLiveStreaming, SessionType } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { EntitiesBaseService, EntityLiveStreamVendors } from '@admin-clients/shared/common/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject } from 'rxjs';
import { filter, first, map, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-session-live-streaming',
    changeDetection: ChangeDetectionStrategy.OnPush,
    styleUrls: ['../session-other-settings.component.scss'],
    templateUrl: './session-live-streaming.component.html',
    imports: [
        ReactiveFormsModule, FlexLayoutModule, TranslatePipe, MaterialModule,
        CommonModule, FormControlErrorsComponent, EllipsifyDirective
    ]
})
export class SessionLiveStreamingComponent implements OnInit, OnDestroy {
    private readonly _entitiesSrv = inject(EntitiesBaseService);
    private readonly _sessionsService = inject(EventSessionsService);
    private readonly _onDestroy = new Subject<void>();
    private readonly _sessionTypes = SessionType;

    readonly entityVendors$ = this._entitiesSrv.getEntity$()
        .pipe(
            filter(value => !!value),
            map(entity => entity.settings?.live_streaming?.vendors)
        );

    readonly liveStreamingFormGroup = inject(UntypedFormBuilder)
        .group({
            enable: { value: false, disabled: true },
            vendor: [null, Validators.required],
            value: [null, Validators.required]
        });

    @Input() set form(value: UntypedFormGroup) {
        if (value.contains('liveStreaming')) {
            return;
        }
        value.addControl('liveStreaming', this.liveStreamingFormGroup, { emitEvent: false });
    }

    readonly markForCheck = ((): () => void => {
        const cdr = inject(ChangeDetectorRef);
        return () => cdr.markForCheck();
    })();

    ngOnInit(): void {
        this.liveStreamingFormChangeHandler();
        this.updateLiveStreamingForm();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        const form = this.liveStreamingFormGroup.parent as UntypedFormGroup;
        form.removeControl('liveStreaming', { emitEvent: false });
    }

    getValue(): SessionLiveStreaming {
        return this.liveStreamingFormGroup.value ? {
            enable: this.liveStreamingFormGroup.value.enable,
            vendor: this.liveStreamingFormGroup.value.vendor,
            value: this.liveStreamingFormGroup.value.value
        } : undefined;
    }

    private liveStreamingFormChangeHandler(): void {
        (this.liveStreamingFormGroup.get('enable').valueChanges as Observable<boolean>)
            .pipe(takeUntil(this._onDestroy))
            .subscribe(isEnabled => {
                if (isEnabled) {
                    this.liveStreamingFormGroup.get('vendor').enable({ emitEvent: false });
                    this.liveStreamingFormGroup.get('value').enable({ emitEvent: false });
                } else {
                    this.liveStreamingFormGroup.get('vendor').disable({ emitEvent: false });
                    this.liveStreamingFormGroup.get('value').disable({ emitEvent: false });
                }
            });
    }

    private updateLiveStreamingForm(): void {
        this._sessionsService.session.get$()
            .pipe(filter(session => !!session))
            .subscribe(session => {

                this._entitiesSrv.getEntity$()
                    .pipe(first())
                    .subscribe(entity => {
                        const showLiveStreaming = entity.settings?.live_streaming?.enabled;
                        const isSession = session.type === this._sessionTypes.session;
                        if (showLiveStreaming && isSession) {
                            this.liveStreamingFormGroup.get('enable').enable({ onlySelf: true });
                        } else {
                            this.liveStreamingFormGroup.get('enable').disable({ onlySelf: true });
                        }
                    });

                this.liveStreamingFormGroup.patchValue({
                    enable: session.settings?.live_streaming && session.settings.live_streaming.enable,
                    vendor: session.settings?.live_streaming ? session.settings.live_streaming.vendor : EntityLiveStreamVendors.custom,
                    value: session.settings?.live_streaming?.value
                }, { onlySelf: true });
                this.liveStreamingFormGroup.markAsPristine();
                this.liveStreamingFormGroup.markAsUntouched();
            });
    }
}
