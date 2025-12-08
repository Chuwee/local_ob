
import { ChannelsService, ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { PackChannelRequestStatus, PacksService } from '@admin-clients/cpanel/packs/my-packs/data-access';
import { LinkListComponent } from '@admin-clients/cpanel/shared/ui/components';
import { CopyTextComponent, EmptyStateComponent, LanguageBarComponent } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, effect, inject, OnDestroy } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { map, filter, tap, shareReplay, BehaviorSubject, combineLatest } from 'rxjs';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, LanguageBarComponent, AsyncPipe, TranslatePipe,
        CopyTextComponent, MatIconModule, FlexLayoutModule, MatButtonModule,
        MatProgressSpinnerModule, LinkListComponent, EmptyStateComponent
    ],
    selector: 'app-pack-channel-preview',
    templateUrl: './pack-channel-preview.component.html',
    styleUrls: ['./pack-channel-preview.component.scss']
})
export class PackChannelPreviewComponent implements OnDestroy {
    readonly #channelsSrv = inject(ChannelsService);
    readonly #packsSrv = inject(PacksService);
    readonly #language = new BehaviorSubject<string>(null);
    readonly #router = inject(Router);

    readonly $loading = toSignal(this.#packsSrv.pack.previewLinks.loading$());
    readonly $channel = toSignal(this.#channelsSrv.getChannel$().pipe(filter(Boolean)));
    readonly $pack = toSignal(this.#packsSrv.pack.get$().pipe(filter(Boolean)));
    readonly $packChannels = toSignal(this.#packsSrv.pack.channels.getData$().pipe(filter(Boolean)));

    readonly $languageList = toSignal(this.#channelsSrv.getChannel$().pipe(
        filter(Boolean),
        tap(channel => {
            this.#language.next(channel.languages.default || channel.languages.selected[0]);
        }),
        map(channel => channel.languages.selected),
        shareReplay(1)
    ));

    readonly language$ = this.#language.asObservable();

    readonly packDetailPreviewLink$ =
        combineLatest([
            this.language$,
            this.#packsSrv.pack.previewLinks.get$()
        ]).pipe(
            filter(data => data.every(Boolean)),
            map(([language, links]) => {
                const preview = links.find(link => link.language === language) ?? { language };
                return [{
                    detail_link: preview.detail_link,
                    link: preview.select_link,
                    name: this.$pack().name,
                    enabled: true
                }];
            })
        );

    packChannelRequestStatus = PackChannelRequestStatus;

    readonly $selectedPackChannel = computed(() => {
        const packChannels = this.$packChannels();
        const currentChannel = this.$channel();
        if (!packChannels || !currentChannel) return null;
        return packChannels.find(channel => channel?.channel.id === currentChannel.id);
    });

    readonly #loadEffect = effect(() => {
        const pack = this.$pack();
        const channel = this.$channel();
        const packChannel = this.$selectedPackChannel();

        if (!pack || !channel || !packChannel) return;

        this.#packsSrv.pack.previewLinks.clear();

        if (channel.type === ChannelType.boxOffice || !pack?.active ||
            (packChannel.status.request !== this.packChannelRequestStatus.accepted)) {
            this.#router.navigate(['/packs', pack.id, 'channels', channel.id, 'general-data']);
        } else {
            this.#packsSrv.pack.previewLinks.load(pack.id, channel.id);
        }

    });

    ngOnDestroy(): void {
        this.#packsSrv.pack.previewLinks.clear();
    }

    changeLanguage(newLanguage: string): void {
        this.#language.next(newLanguage);
    }
}
