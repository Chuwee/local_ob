import { Channel, ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { ChannelMemberExternalService } from '@admin-clients/cpanel-channels-member-external-data-access';
import { ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { IdName } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { first } from 'rxjs';
import { PeriodicityDialogComponent, PeriodicityDialogData } from './dialog/periodicity-dialog.component';

@Component({
    selector: 'app-members-external-periodicities',
    templateUrl: './periodicities.component.html',
    styleUrls: ['./periodicities.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class PeriodicitiesComponent implements OnInit {
    readonly #channelsSrv = inject(ChannelsService);
    readonly #channelMemberSrv = inject(ChannelMemberExternalService);
    readonly #matDialog = inject(MatDialog);

    #channel: Channel;

    readonly periodicities$ = this.#channelMemberSrv.periodicities.get$();
    readonly loading$ = this.#channelMemberSrv.periodicities.loading$();
    readonly error$ = this.#channelMemberSrv.periodicities.error$();
    readonly columns = ['id', 'name', 'actions'];

    ngOnInit(): void {
        this.#channelsSrv.getChannel$()
            .pipe(first())
            .subscribe(channel => {
                this.#channel = channel;
                this.#channelMemberSrv.periodicities.load(channel.id);
            });
    }

    edit(period: IdName): void {
        this.#matDialog.open<PeriodicityDialogComponent, PeriodicityDialogData, void>(
            PeriodicityDialogComponent,
            new ObMatDialogConfig({ channel: this.#channel, period })
        );
    }
}
