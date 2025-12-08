import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { Channel, ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { SeasonTicketChannelsService } from '@admin-clients/cpanel/promoters/season-tickets/channels/data-access';
import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { AsyncPipe } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, DestroyRef, effect, inject, input, OnInit, output
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatDialogContent } from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatOption, MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, first, map } from 'rxjs/operators';

@Component({
    selector: 'app-renewals-generate-csv-import-options',
    imports: [
        MatDialogContent, ReactiveFormsModule, MatFormField, TranslatePipe, MatSelect, MatLabel, MatOption,
        SelectSearchComponent, AsyncPipe, FormControlErrorsComponent, MatProgressSpinner
    ],
    templateUrl: './renewals-generate-csv-import-options.component.html',
    styleUrl: './renewals-generate-csv-import-options.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class RenewalsGenerateCsvImportOptionsComponent implements OnInit {
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #seasonTicketChannelsSrv = inject(SeasonTicketChannelsService);
    readonly #destroyRef = inject(DestroyRef);

    readonly $optionsFormGroup = input.required<FormGroup>({ alias: 'optionsFormGroup' });
    readonly $channelControlName = input.required<string>({ alias: 'channelControlName' });
    readonly isLoading = output<boolean>();

    readonly isLoading$ = this.#seasonTicketChannelsSrv.seasonTicketChannelList.loading$();
    readonly $seasonTicket = toSignal(this.#seasonTicketSrv.seasonTicket.get$().pipe(first(Boolean)));
    readonly channels$ = this.#seasonTicketChannelsSrv.seasonTicketChannelList.getData$()
        .pipe(
            filter(Boolean),
            map(seasonTicketChannels =>
                seasonTicketChannels
                    .map(seasonTicketChannel => seasonTicketChannel.channel)
                    .filter(channel => channel.type === ChannelType.web) || []
            )
        );

    constructor() {
        effect(() => {
            this.#seasonTicketChannelsSrv.seasonTicketChannelList.load(this.$seasonTicket().id, {
                limit: 999,
                sort: 'name:asc'
            });
        });
    }

    ngOnInit(): void {
        this.isLoading$
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(isLoading => this.isLoading.emit(isLoading));
    }

    compareById(option: Channel, option2: Channel): boolean {
        return option?.id === option2?.id;
    }
}
