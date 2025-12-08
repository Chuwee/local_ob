import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { EventChannelsService } from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EphemeralMessageService, MessageDialogService, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { PrefixPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatSelectChange } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, Observable, combineLatestWith, filter, first, map, shareReplay, switchMap, tap, throwError } from 'rxjs';

@Component({
    selector: 'app-event-channel-publish',
    templateUrl: './event-channel-publish.component.html',
    styleUrls: ['./event-channel-publish.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, CommonModule, TranslatePipe, SelectSearchComponent,
        PrefixPipe, MaterialModule, ReactiveFormsModule
    ],
    providers: [PrefixPipe.provider('EVENTS.CHANNEL.PUBLISH.')]
})

export class EventChannelPublishComponent implements WritingComponent {

    private readonly _venueTplsSrv = inject(VenueTemplatesService);
    private readonly _eventSrv = inject(EventsService);
    private readonly _eventChannelService = inject(EventChannelsService);
    private readonly _fb = inject(FormBuilder);
    private readonly _msgDialogSrv = inject(MessageDialogService);
    private readonly _ephemeralMsgSrv = inject(EphemeralMessageService);

    private readonly _venuTemplateId = new BehaviorSubject<number>(null);

    readonly form = this._fb.group({
        enabled: null as boolean,
        published_seat_quota_id: [null as number, Validators.required]
    });

    readonly venueTplForm = this._fb.control(null as number);

    readonly event$ = this._eventSrv.event.get$();

    readonly loading$ = booleanOrMerge([
        this._eventChannelService.b2bPublishConfiguration.inProgress$(),
        this._venueTplsSrv.isVenueTemplateQuotasLoading$()
    ]);

    readonly quotas$ = this._venuTemplateId.asObservable().pipe(
        filter(Boolean),
        switchMap(id => {
            this._venueTplsSrv.loadVenueTemplateQuotas(id);
            return this._venueTplsSrv.getVenueTemplateQuotas$();
        }),
        shareReplay(1)
    );

    readonly venueTemplates$ = this.event$.pipe(
        map(event => event.venue_templates),
        tap(venueTpls => {
            const firstVenueTemplate = venueTpls?.at(0);
            if (firstVenueTemplate) {
                this.venueTplForm.setValue(firstVenueTemplate.id);
                this._venuTemplateId.next(firstVenueTemplate.id);
            }
        })
    );

    readonly data$ = this._venuTemplateId.asObservable().pipe(
        filter(Boolean),
        combineLatestWith(this._eventChannelService.eventChannel.get$().pipe(
            tap(() => this.form.markAsPristine()),
            filter(eventChannel => eventChannel?.channel.type === ChannelType.webB2B))
        ),
        switchMap(([venueTplId, { channel, event }]) => {
            this._eventChannelService.b2bPublishConfiguration.load(event.id, channel.id, venueTplId);
            return this._eventChannelService.b2bPublishConfiguration.get$();
        }),
        map(config => config || { enabled: null, published_seat_quota: { id: null } }),
        tap(({ enabled, published_seat_quota: { id } }) => this.form.reset({
            enabled,
            published_seat_quota_id: id
        }))
    );

    changeVenueTpl(change: MatSelectChange): void {
        if (this.form.dirty) {
            this._msgDialogSrv.defaultUnsavedChangesWarn().subscribe(result => {
                if (result) {
                    this._venuTemplateId.next(change.value);
                } else {
                    this.venueTplForm.setValue(this._venuTemplateId.value);
                }
            });
        } else {
            this._venuTemplateId.next(change.value);
        }
    }

    cancel(): void {
        this._eventChannelService.eventChannel.get$().pipe(first(Boolean)).subscribe(({ channel, event }) =>
            this._eventChannelService.b2bPublishConfiguration.load(event.id, channel.id, this.venueTplForm.value)
        );
    }

    save(): void {
        this.save$().subscribe(() => this.cancel());
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const value = this.form.getRawValue();
            return this._eventChannelService.eventChannel.get$().pipe(
                first(Boolean),
                switchMap(({ channel, event }) =>
                    this._eventChannelService.b2bPublishConfiguration.save(event.id, channel.id, this.venueTplForm.value, value)
                ),
                tap(() => {
                    this._ephemeralMsgSrv.showSaveSuccess();
                    this.form.markAsPristine();
                })
            );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }

    }

}

export default EventChannelPublishComponent;
