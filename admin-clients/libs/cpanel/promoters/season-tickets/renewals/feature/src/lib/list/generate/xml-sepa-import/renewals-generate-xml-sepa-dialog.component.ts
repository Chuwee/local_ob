import { ChannelType } from '@admin-clients/cpanel/channels/data-access';
import {
    SeasonTicketChannelsService
} from '@admin-clients/cpanel/promoters/season-tickets/channels/data-access';
import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    PostSeasonTicketRenewalsGeneration, seasonTicketRenewalsProviders
} from '@admin-clients/cpanel/promoters/season-tickets/renewals/data-access';
import { DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialogActions, MatDialogContent, MatDialogTitle } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatLabel } from '@angular/material/input';
import { MatFormField, MatOption, MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { map, filter, first } from 'rxjs';

@Component({
    selector: 'app-renewals-generate-xml-sepa-dialog',
    templateUrl: './renewals-generate-xml-sepa-dialog.component.html',
    styleUrls: ['./renewals-generate-xml-sepa-dialog.component.scss'],
    imports: [
        MatDialogActions, MatDialogTitle, MatDialogContent, MatIcon, MatButton, MatIconButton, TranslatePipe, MatSelect, MatOption,
        ReactiveFormsModule, MatFormField, MatLabel
    ],
    providers: [seasonTicketRenewalsProviders],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class RenewalsGenerateXmlSepaDialogComponent
    extends ObDialog<RenewalsGenerateXmlSepaDialogComponent, null, PostSeasonTicketRenewalsGeneration>
    implements OnInit {
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #fb = inject(FormBuilder);
    readonly #seasonTicketChannelsSrv = inject(SeasonTicketChannelsService);

    readonly #$seasonTicket = toSignal(this.#seasonTicketSrv.seasonTicket.get$().pipe(first(Boolean)));
    readonly $channelsList = toSignal(this.#seasonTicketChannelsSrv.seasonTicketChannelList.getData$()
        .pipe(
            filter(Boolean),
            map(seasonTicketChannels =>
                seasonTicketChannels
                    .map(seasonTicketChannel => seasonTicketChannel.channel)
                    .filter(channel => channel.type === ChannelType.web) || []
            )
        ));

    readonly form = this.#fb.group({
        channelId: [null as number, Validators.required]
    });

    constructor() {
        super(DialogSize.MEDIUM);
    }

    ngOnInit(): void {
        this.#seasonTicketChannelsSrv.seasonTicketChannelList.load(this.#$seasonTicket().id, {
            limit: 999,
            sort: 'name:asc'
        });
    }

    close(): void {
        this.dialogRef.close();
    }

    generate(): void {
        this.dialogRef.close({ channelId: this.form.value.channelId });
    }
}