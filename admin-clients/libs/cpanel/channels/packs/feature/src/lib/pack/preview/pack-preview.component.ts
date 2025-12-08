
import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { PacksService } from '@admin-clients/cpanel/channels/packs/data-access';
import { LinkListComponent } from '@admin-clients/cpanel/shared/ui/components';
import { CopyTextComponent, LanguageBarComponent } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { map, filter, tap, shareReplay, BehaviorSubject, combineLatest } from 'rxjs';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, LanguageBarComponent, AsyncPipe, TranslatePipe,
        CopyTextComponent, MatIconModule, FlexLayoutModule, MatButtonModule,
        MatProgressSpinnerModule, LinkListComponent
    ],
    selector: 'app-pack-preview',
    templateUrl: './pack-preview.component.html',
    styleUrls: ['./pack-preview.component.scss']
})
export class PackPreviewComponent implements OnDestroy {
    readonly #channelsSrv = inject(ChannelsService);
    readonly #packsSrv = inject(PacksService);
    readonly #language = new BehaviorSubject<string>(null);

    readonly $loading = toSignal(this.#packsSrv.packPreviewLinks.loading$());
    readonly $channel = toSignal(this.#channelsSrv.getChannel$().pipe(filter(Boolean)));
    readonly $pack = toSignal(this.#packsSrv.pack.get$().pipe(
        filter(Boolean),
        tap(pack => {
            this.#packsSrv.packPreviewLinks.load(this.$channel().id, pack.id);
        })
    ));

    readonly $languageList = toSignal(this.#channelsSrv.getChannel$().pipe(
        filter(Boolean),
        tap(channel => {
            this.#language.next(channel.languages.default || channel.languages.selected[0]);
        }),
        map(channel => channel.languages.selected),
        shareReplay(1)
    )
    );

    readonly language$ = this.#language.asObservable();

    readonly packDetailPreviewLink$ =
        combineLatest([
            this.language$,
            this.#packsSrv.packPreviewLinks.get$()
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

    ngOnDestroy(): void {
        this.#packsSrv.packPreviewLinks.clear();
    }

    changeLanguage(newLanguage: string): void {
        this.#language.next(newLanguage);
    }
}
