import { Channel, ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { ChannelMemberExternalService } from '@admin-clients/cpanel-channels-member-external-data-access';
import { ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { IdName } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { first } from 'rxjs';
import { RoleDialogComponent, RoleDialogData } from './dialog/roles-dialog.component';

@Component({
    selector: 'app-members-external-roles',
    templateUrl: './roles.component.html',
    styleUrls: ['./roles.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class RolesComponent implements OnInit {
    readonly #channelsSrv = inject(ChannelsService);
    readonly #channelMemberSrv = inject(ChannelMemberExternalService);
    readonly #matDialog = inject(MatDialog);

    #channel: Channel;

    readonly roles$ = this.#channelMemberSrv.roles.get$();
    readonly loading$ = this.#channelMemberSrv.roles.loading$();
    readonly error$ = this.#channelMemberSrv.roles.error$();
    readonly columns = ['id', 'name', 'actions'];

    ngOnInit(): void {
        this.#channelsSrv.getChannel$()
            .pipe(first())
            .subscribe(channel => {
                this.#channel = channel;
                this.#channelMemberSrv.roles.load(channel.id);
            });
    }

    edit(role: IdName): void {
        this.#matDialog.open<RoleDialogComponent, RoleDialogData, void>(
            RoleDialogComponent,
            new ObMatDialogConfig({ channel: this.#channel, role })
        );
    }

}
