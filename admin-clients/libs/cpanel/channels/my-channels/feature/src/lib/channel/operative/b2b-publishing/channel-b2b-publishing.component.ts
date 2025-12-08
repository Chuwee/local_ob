/* eslint-disable @typescript-eslint/naming-convention */
import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject, throwError } from 'rxjs';
import { filter, map, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-channel-b2b-publishing',
    templateUrl: './channel-b2b-publishing.component.html',
    styleUrls: ['./channel-b2b-publishing.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe,
        ReactiveFormsModule,
        FormContainerComponent,
        MatCheckbox,
        MatSpinner,
        TranslatePipe
    ]
})
export class ChannelB2BPublishingComponent implements OnInit, OnDestroy, WritingComponent {
    private readonly _onDestroy = new Subject<void>();
    private readonly _fb = inject(FormBuilder);
    private readonly _channelsService = inject(ChannelsService);
    private readonly _ephemeralMessageService = inject(EphemeralMessageService);
    private _channelId: number;
    private _channelName = '';
    readonly form = this._fb.group({ allow_B2B_publishing: false });
    readonly inProgress$ = this._channelsService.isChannelLoading$();

    ngOnInit(): void {
        this._channelsService.getChannel$()
            .pipe(
                takeUntil(this._onDestroy),
                filter(Boolean)
            ).subscribe(channel => {
                this._channelName = channel.name;
                this._channelId = channel.id;
                this.form.reset({ allow_B2B_publishing: channel.settings?.allow_B2B_publishing });
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    save$(): Observable<number> {
        if (this.form.valid && this.form.dirty) {
            return this._channelsService.saveChannel(this._channelId,
                {
                    // eslint-disable-next-line @typescript-eslint/naming-convention
                    settings: { allow_B2B_publishing: this.form.value.allow_B2B_publishing }
                })
                .pipe(
                    map(() => {
                        this._ephemeralMessageService.showSuccess({
                            msgKey: 'CHANNELS.UPDATE_SUCCESS',
                            msgParams: { channelName: this._channelName }
                        });
                        return this._channelId;
                    })
                );
        } else {
            this.form.markAllAsTouched();
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe(channelId => this._channelsService.loadChannel(channelId.toString()));
    }

    cancel(): void {
        this._channelsService.loadChannel(this._channelId.toString());
    }
}
